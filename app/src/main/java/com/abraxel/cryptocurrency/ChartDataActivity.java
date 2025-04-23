package com.abraxel.cryptocurrency;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.IBinder;
import android.os.Handler;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import com.abraxel.cryptocurrency.constants.Constants;
import com.abraxel.cryptocurrency.formatter.LineChartXAxisFormatter;
import com.abraxel.cryptocurrency.model.ChartData;
import com.abraxel.cryptocurrency.model.CryptoCurrencies;
import com.abraxel.cryptocurrency.websocket.WebSocketService;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import org.jetbrains.annotations.Nullable;
import org.json.JSONArray;
import org.json.JSONException;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;

public class ChartDataActivity extends AppCompatActivity implements WebSocketService.WebSocketCallback {
    private final Logger logger = Logger.getLogger(ChartDataActivity.class.getName());

    private LineChart lineChart;
    private final List<Entry> lineList = new ArrayList<>();
    private static RequestQueue requestQueue;
    protected static List<ChartData> chartDataList = new ArrayList<>();
    
    // WebSocket ile ilgili değişkenler
    private WebSocketService webSocketService;
    private boolean webSocketBound = false;
    private CryptoCurrencies cryptoData;
    private String currentPair;
    
    // Detay sayfası için TextView'lar
    private TextView coinNameText, priceText, highLowText, volumeText, percentText;

    private static final long UI_UPDATE_THROTTLE = 500; // 500ms
    private long lastUiUpdateTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart_data);
        
        // Kripto para verisini al
        cryptoData = (CryptoCurrencies) getIntent().getSerializableExtra("crypto_data");
        if (cryptoData != null) {
            currentPair = cryptoData.getPair();
        }
        
        // TextView referanslarını al
        coinNameText = findViewById(R.id.remind_coin_name);
        priceText = findViewById(R.id.price_text);
        highLowText = findViewById(R.id.high_low_text);
        volumeText = findViewById(R.id.volume_text);
        percentText = findViewById(R.id.percent_text);
        
        // Kripto para verilerini göster
        updateCryptoDetails();
        
        // WebSocket servisini bağla
        bindWebSocketService();
        
        getVolleyResponse();
        
        // ActionBar'ı güvenli bir şekilde ayarla
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        
        lineChart = findViewById(R.id.line_chart);

        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                clearChartData();
                finish();
            }
        };
        getOnBackPressedDispatcher().addCallback(this, callback);
    }
    
    /**
     * Kripto para detaylarını güncelle
     */
    private void updateCryptoDetails() {
        if (cryptoData == null) {
            Log.d("ChartDataActivity", "updateCryptoDetails: cryptoData null");
            return;
        }

        long currentTime = System.currentTimeMillis();
        if (currentTime - lastUiUpdateTime < UI_UPDATE_THROTTLE) {
            return; // Çok sık güncellemeyi engelle
        }
        lastUiUpdateTime = currentTime;

        runOnUiThread(() -> {
            try {
                String formattedPrice = cryptoData.getFormattedPrice();
                String formattedHigh = cryptoData.getHigh();
                String formattedLow = cryptoData.getLow();
                String formattedVolume = cryptoData.getFormattedVolume();
                
                Log.d("ChartDataActivity", "Arayüz güncelleniyor: " + cryptoData.getPair() + 
                      ", Fiyat: " + formattedPrice);
                
                // Para birimi TRY olduğundan emin ol
                String currency = " ₺";
                
                coinNameText.setText(cryptoData.getCoinName() + " (" + cryptoData.getPair() + ")");
                priceText.setText("Fiyat: " + formattedPrice + currency);
                highLowText.setText("En Yüksek: " + formattedHigh + currency + " / En Düşük: " + formattedLow + currency);
                volumeText.setText("Hacim: " + formattedVolume);
                
                String percentValue = cryptoData.getDailyPercent();
                
                // Pozitif/negatif değişime göre renklendirme
                if (percentValue != null && !percentValue.isEmpty()) {
                    boolean isPositive = !percentValue.contains("-");
                    
                    if (isPositive) {
                        percentText.setText("Değişim: +" + percentValue + "%");
                        percentText.setTextColor(Color.parseColor("#006400")); // Koyu yeşil
                    } else {
                        percentText.setText("Değişim: " + percentValue + "%");
                        percentText.setTextColor(Color.parseColor("#B71C1C")); // Koyu kırmızı
                    }
                } else {
                    percentText.setText("Değişim: 0.00%");
                    percentText.setTextColor(Color.parseColor("#616161")); // Gri
                }
            } catch (Exception e) {
                Log.e("ChartDataActivity", "UI güncelleme hatası: " + e.getMessage());
            }
        });
    }
    
    /**
     * WebSocket servisine bağlan
     */
    private void bindWebSocketService() {
        Intent intent = new Intent(this, WebSocketService.class);
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
        Log.d("ChartDataActivity", "WebSocket servisine bağlanılıyor...");
    }
    
    /**
     * WebSocket Service bağlantısı
     */
    private final ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            WebSocketService.LocalBinder binder = (WebSocketService.LocalBinder) service;
            webSocketService = binder.getService();
            webSocketBound = true;
            
            Log.d("ChartDataActivity", "WebSocket servisine bağlandı");
            
            // WebSocket callback'i ayarla
            webSocketService.setCallback(ChartDataActivity.this);
            
            // Eğer mevcut kripto para bilgisi varsa, WebSocket üzerinden veri talep et
            if (cryptoData != null && cryptoData.getPair() != null) {
                Log.d("ChartDataActivity", "Veri talebi gönderiliyor: " + cryptoData.getPair());
                
                // Eğer WebSocket bağlantısı yoksa, bağlantı kur ve talep gönder
                if (!webSocketService.isConnected()) {
                    Log.d("ChartDataActivity", "WebSocket bağlı değil, bağlanılıyor...");
                    webSocketService.connectWebSocket();
                    
                    // 1 saniye sonra veri talep et (bağlantı kurulana kadar bekle)
                    new Handler().postDelayed(() -> {
                        if (webSocketService != null && webSocketService.isConnected()) {
                            Log.d("ChartDataActivity", "WebSocket bağlandı, veri talep ediliyor: " + cryptoData.getPair());
                            boolean success = webSocketService.requestSymbolData(cryptoData.getPair());
                            Log.d("ChartDataActivity", "Veri talebi sonucu: " + (success ? "Başarılı" : "Başarısız"));
                        } else {
                            Log.e("ChartDataActivity", "WebSocket hala bağlı değil, varsayılan veriler kullanılacak");
                        }
                    }, 1000);
                } else {
                    // WebSocket zaten bağlıysa hemen veri talep et
                    boolean success = webSocketService.requestSymbolData(cryptoData.getPair());
                    Log.d("ChartDataActivity", "Veri talebi sonucu: " + (success ? "Başarılı" : "Başarısız"));
                }
            } else {
                Log.e("ChartDataActivity", "Kripto para verisi bulunamadı!");
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            webSocketBound = false;
            webSocketService = null;
            Log.d("ChartDataActivity", "WebSocket servisi bağlantısı kesildi");
        }
    };
    
    @Override
    protected void onDestroy() {
        // WebSocket servis bağlantısını kapat
        if (webSocketBound) {
            // Mevcut kripto para için aboneliği iptal et
            if (webSocketService != null && currentPair != null) {
                webSocketService.unsubscribeFromSymbol(currentPair);
                Log.d("ChartDataActivity", "WebSocket aboneliği iptal edildi: " + currentPair);
            }
            
            // Callback'i kaldır
            if (webSocketService != null) {
                webSocketService.setCallback(null);
            }
            
            // Servisi unbind et
            unbindService(serviceConnection);
            webSocketBound = false;
        }
        
        super.onDestroy();
    }
    
    @Override
    public void onDataReceived(List<CryptoCurrencies> data) {
        if (data == null || data.isEmpty() || cryptoData == null) {
            Log.d("ChartDataActivity", "Veri boş veya cryptoData null");
            return;
        }
        
        // Gelen verilerden şu anki kripto para ile eşleşeni bul
        for (CryptoCurrencies newCrypto : data) {
            if (newCrypto.getPair() != null && newCrypto.getPair().equals(currentPair)) {
                Log.d("ChartDataActivity", "Eşleşen veri bulundu: " + newCrypto.getPair() + 
                      ", Fiyat: " + newCrypto.getFormattedPrice());
                
                // Kripto para verilerini güncelle
                cryptoData = newCrypto;
                
                // UI'ı güncelle
                updateCryptoDetails();
                break;
            }
        }
    }
    
    @Override
    public void onConnectionStateChanged(boolean connected) {
        // Bağlantı durumu değiştiğinde
        runOnUiThread(() -> {
            // Eğer bağlantı kurulduysa ve kripto para verisi varsa, veri talep et
            if (connected && webSocketService != null && cryptoData != null && cryptoData.getPair() != null) {
                Log.d("ChartDataActivity", "Bağlantı durumu değişti, veri talep ediliyor: " + cryptoData.getPair());
                boolean success = webSocketService.requestSymbolData(cryptoData.getPair());
                Log.d("ChartDataActivity", "Veri talebi sonucu: " + (success ? "Başarılı" : "Başarısız"));
            }
        });
    }
    
    @Override
    public void onError(String message) {
        // Hata durumunda loglama yap
        Log.e("ChartDataActivity", "WebSocket hatası: " + message);
    }

    public List<ChartData> getVolleyResponse() {
        requestQueue = Volley.newRequestQueue(getApplicationContext());
        JsonObjectRequest req = new JsonObjectRequest(
                Request.Method.GET,
                urlCreator(getCoinName()),
                null,
                response -> {
                    try {
                        JSONArray arr = response.getJSONArray("prices");
                        for (int i = 0; i < arr.length(); i++) {
                            if (i % 15 == 0) {
                                JSONArray jsonArray = arr.getJSONArray(i);
                                ChartData chartData = new ChartData();
                                chartData.setTimeStamp(jsonArray.getLong(0));
                                chartData.setCost(jsonArray.getDouble(1));
                                chartDataList.add(chartData);
                            }
                        }

                        for (int i = 0; i < chartDataList.size(); i++) {
                            ChartData chartData = chartDataList.get(i);
                            float formattedVal = getFormattedVal(chartData);
                            lineList.add(new Entry(chartData.getTimeStamp(), formattedVal));

                        }
                        if (!lineList.isEmpty()) {
                            drawLineChart(lineList, getCoinName());
                        }

                    } catch (JSONException e) {
                        logger.severe(e.getMessage());
                    }
                },
                error -> Log.e("ChartDataActivity", "Grafik verisi yüklenirken hata oluştu: " + error.getMessage()));
        requestQueue.add(req);
        return chartDataList;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            clearChartData();
        }
        return super.onOptionsItemSelected(item);
    }

    @Nullable
    private String getCoinName() {
        String coinName;
        Bundle extras = getIntent().getExtras();
        coinName = extras.getString(Constants.COIN_NAME);
        if (coinName.equals("avalanche")) {
            return coinName.concat("-2");
        } else if (coinName.equals("usd coin")) {
            String[] s = coinName.split(" ");
            return s[0].concat("-").concat(s[1]);
        }
        return coinName;
    }

    private void drawLineChart(List<Entry> lineList, String coinName) {
        Description description = new Description();
        description.setText(Constants.CHANGE_TREND);
        lineChart.setDescription(description);
        lineChart.getXAxis().setValueFormatter(new LineChartXAxisFormatter());
        LineDataSet lineDataSet = new LineDataSet(lineList, coinNameFormatter(coinName) + " 7 Günlük Değişim Grafiği");
        lineDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        lineDataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        lineDataSet.setDrawFilled(true);
        lineDataSet.setFillColor(Color.GREEN);

        LineData lineData = new LineData(lineDataSet);
        lineChart.setData(lineData);
        lineData.setValueTextSize(10f);
        lineData.setValueTextColor(Color.BLACK);
        lineChart.invalidate();
    }

    private String urlCreator(String coinName) {
        String beginURL = "https://api.coingecko.com/api/v3/coins/";
        String finalQueue = "/market_chart?vs_currency=try&days=7";
        return beginURL + coinName + finalQueue;
    }

    private String coinNameFormatter(String coinName) {
        coinName = coinName.replace("-", " ");
        return coinName.substring(0, 1).toUpperCase() + coinName.substring(1);
    }

    private float getFormattedVal(ChartData chartData) {
        DecimalFormat decimalFormat = new DecimalFormat("#.###");
        decimalFormat.setRoundingMode(RoundingMode.CEILING);
        return (float) chartData.getCost();
    }

    private void clearChartData() {
        lineList.clear();
        chartDataList.clear();

        if (lineChart != null) {
            lineChart.clear();
            lineChart.invalidate();
        }
    }
}