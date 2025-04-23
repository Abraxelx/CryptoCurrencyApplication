package com.abraxel.cryptocurrency.websocket;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ServiceInfo;
import android.content.res.Resources;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.abraxel.cryptocurrency.BuildConfig;
import com.abraxel.cryptocurrency.MainActivity;
import com.abraxel.cryptocurrency.R;
import com.abraxel.cryptocurrency.constants.Constants;
import com.abraxel.cryptocurrency.model.CryptoCurrencies;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

public class WebSocketService extends Service {
    private static final String TAG = "WebSocketService";
    private static final int NOTIFICATION_ID = 1001;
    private static final String CHANNEL_ID = "websocket_channel";
    
    private WebSocket webSocket;
    private OkHttpClient client;
    private boolean isConnected = false;
    private final IBinder binder = new LocalBinder();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    private WebSocketListener webSocketListener;
    private List<WebSocketListener> listeners = new ArrayList<>();
    
    // Ping-pong için değişkenler
    private static final int PING_INTERVAL = 10000; // 10 saniye
    private static final int PONG_TIMEOUT = 30000; // 30 saniye
    private static final int MAX_RECONNECT_ATTEMPTS = 10; // Maksimum yeniden bağlanma denemesi
    private static final int INITIAL_RECONNECT_DELAY = 1000; // İlk deneme için 1 saniye bekle
    private final Handler pingHandler = new Handler(Looper.getMainLooper());
    private final Runnable pingRunnable = new Runnable() {
        @Override
        public void run() {
            if (webSocket != null && isConnected) {
                // Ping mesajı gönder
                webSocket.send("ping");
                
                // Sonraki ping için planla
                pingHandler.postDelayed(this, PING_INTERVAL);
            }
        }
    };
    
    // Veri akışı hız sınırlama (throttling) için değişkenler
    private static final int DATA_THROTTLE_INTERVAL = 2000; // 2 saniye
    private long lastDataSentTime = 0;
    private final List<CryptoCurrencies> bufferedData = new ArrayList<>();
    private final Handler throttleHandler = new Handler(Looper.getMainLooper());
    private final Runnable sendBufferedDataRunnable = new Runnable() {
        @Override
        public void run() {
            if (!bufferedData.isEmpty()) {
                sendBufferedDataToUI();
            }
            // Her 2 saniyede bir kontrol et
            throttleHandler.postDelayed(this, DATA_THROTTLE_INTERVAL);
        }
    };
    
    // Kripto para logoları için eşleme haritası
    private final Map<String, String> cryptoLogoMap = new HashMap<>();
    
    // Bu callback'i MainActivity'de kullanacağız
    private WebSocketCallback callback;
    
    // Reconnect için kullanılacak değişkenler
    private int reconnectAttempts = 0;
    
    @Override
    public void onCreate() {
        super.onCreate();
        initCryptoLogoMap();
        createWebSocketClient();
        createNotificationChannel();
        
        // Veri akış kontrol mekanizmasını başlat
        startDataThrottling();
    }
    
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Android 14 ve üzeri için foreground servis tipini DATA_SYNC olarak belirtiyoruz
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(NOTIFICATION_ID, createNotification("Bağlanıyor..."), 
                    ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC);
        } else {
            startForeground(NOTIFICATION_ID, createNotification("Bağlanıyor..."));
        }
        connectWebSocket();
        return START_STICKY;
    }
    
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }
    
    @Override
    public void onDestroy() {
        stopPingPong();
        stopDataThrottling();
        disconnectWebSocket();
        super.onDestroy();
    }
    
    // Binder sınıfı
    public class LocalBinder extends Binder {
        public WebSocketService getService() {
            return WebSocketService.this;
        }
    }
    
    // WebSocket bağlantısını kurma
    private void createWebSocketClient() {
        client = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(PONG_TIMEOUT, TimeUnit.MILLISECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .pingInterval(PING_INTERVAL, TimeUnit.MILLISECONDS)
                .retryOnConnectionFailure(true)
                .build();

        Request request = new Request.Builder()
                .url("wss://ws.okx.com:8443/ws/v5/public")
                .build();

        webSocket = client.newWebSocket(request, new WebSocketListener() {
            @Override
            public void onOpen(@NonNull WebSocket webSocket, @NonNull Response response) {
                isConnected = true;
                reconnectAttempts = 0;
                
                Log.d(TAG, "WebSocket bağlantısı kuruldu: " + response.message());
                
                // Bildirim göstermeden bağlantı durumunu güncelle
                startPingPong();
                notifyConnectionState(true);
            }

            @Override
            public void onMessage(@NonNull WebSocket webSocket, @NonNull String text) {
                Log.d(TAG, "WebSocket mesajı alındı: " + text);
                
                // Ping-pong mesajlarını kontrol et
                if (text.equals("ping") || text.equals("pong")) {
                    Log.d(TAG, "Ping-pong mesajı alındı: " + text);
                    return;
                }
                
                try {
                    JSONObject jsonObject = new JSONObject(text);
                    
                    // OKX API yanıtlarını işleme
                    if (jsonObject.has("event")) {
                        String event = jsonObject.getString("event");
                        Log.d(TAG, "Event mesajı: " + event);
                        
                        // Abonelik onayını kontrol et
                        if ("subscribe".equals(event) && jsonObject.has("arg")) {
                            JSONObject arg = jsonObject.getJSONObject("arg");
                            String instId = arg.optString("instId", "");
                            
                            Log.d(TAG, "Abonelik onaylandı: " + instId);
                            
                            // Abone olunan sembol için hemen veri talep et
                            if (!instId.isEmpty()) {
                                String getDataRequest = "{\"op\":\"get\",\"args\":[{\"channel\":\"tickers\",\"instId\":\"" + instId + "\"}]}";
                                boolean sent = webSocket.send(getDataRequest);
                                Log.d(TAG, "Anlık veri talebi gönderildi: " + sent);
                            }
                        }
                        return;
                    }
                    
                    // Ticker verisi kontrolü
                    if (jsonObject.has("data") && jsonObject.has("arg")) {
                        JSONObject arg = jsonObject.getJSONObject("arg");
                        String channel = arg.getString("channel");
                        String instId = arg.getString("instId");
                        
                        Log.d(TAG, "Veri alındı - Kanal: " + channel + ", InstId: " + instId);
                        
                        if ("tickers".equals(channel)) {
                            JSONArray dataArray = jsonObject.getJSONArray("data");
                            if (dataArray.length() > 0) {
                                JSONObject tickerData = dataArray.getJSONObject(0);
                                
                                // Detaylı veri loglaması sadece debug modunda yapılır
                                if (BuildConfig.DEBUG) {
                                    Log.d(TAG, "Ticker verisi: " + tickerData.toString());
                                }
                                
                                // OKX API'den gelen veriyi CryptoCurrencies nesnesine dönüştür
                                CryptoCurrencies cryptoCurrency = new CryptoCurrencies();
                                cryptoCurrency.setPair(instId);
                                cryptoCurrency.setCoinName(getCoinNameFromPair(instId));
                                
                                // Fiyat verilerini ayarla
                                String lastPrice = tickerData.optString("last", "0");
                                cryptoCurrency.setLast(lastPrice);
                                
                                cryptoCurrency.setHigh(tickerData.optString("high24h", "0"));
                                cryptoCurrency.setLow(tickerData.optString("low24h", "0"));
                                
                                // % değişim değerini al
                                String changePercent = tickerData.optString("changePercent24h", "0");
                                // % karakterini kaldır (API'den gelen değerde olabilir)
                                changePercent = changePercent.replace("%", "");
                                cryptoCurrency.setDailyPercent(changePercent);
                                
                                cryptoCurrency.setAskPrice(tickerData.optString("askPx", "0"));
                                cryptoCurrency.setBidPrice(tickerData.optString("bidPx", "0"));
                                cryptoCurrency.setVolume(tickerData.optString("vol24h", "0"));
                                
                                // Logo kaynak kimliğini ayarla
                                cryptoCurrency.setLogoResourceId(getLogoResourceId(cryptoCurrency.getCoinName().toUpperCase()));
                                
                                // Formatlanmış fiyat gösterimi için log
                                Log.d(TAG, "Dönüştürülen veri: " + cryptoCurrency.getPair() + 
                                      ", Fiyat: " + cryptoCurrency.getFormattedPrice() + 
                                      ", Değişim: " + cryptoCurrency.getFormattedDailyPercent() + "%");
                                
                                // Veriyi hız sınırlama (throttling) mekanizması için önbelleğe ekle
                                synchronized (bufferedData) {
                                    // Eğer aynı para birimi zaten önbellekte varsa güncelle
                                    boolean found = false;
                                    for (int i = 0; i < bufferedData.size(); i++) {
                                        if (bufferedData.get(i).getPair().equals(cryptoCurrency.getPair())) {
                                            bufferedData.set(i, cryptoCurrency);
                                            found = true;
                                            break;
                                        }
                                    }
                                    
                                    // Yoksa ekle
                                    if (!found) {
                                        bufferedData.add(cryptoCurrency);
                                    }
                                    
                                    // Hemen veriyi gönder
                                    List<CryptoCurrencies> dataToSend = new ArrayList<>();
                                    dataToSend.add(cryptoCurrency);
                                    notifyDataReceived(dataToSend);
                                }
                            }
                        }
                    }
                } catch (JSONException e) {
                    Log.e(TAG, "JSON ayrıştırma hatası: " + e.getMessage());
                }
            }

            @Override
            public void onFailure(@NonNull WebSocket webSocket, @NonNull Throwable t, @Nullable Response response) {
                Log.e(TAG, "WebSocket hatası: " + t.getMessage());
                if (response != null) {
                    Log.e(TAG, "Hata yanıtı: " + response.toString());
                }
                scheduleReconnect();
            }

            @Override
            public void onClosed(@NonNull WebSocket webSocket, int code, @NonNull String reason) {
                Log.d(TAG, "WebSocket kapatıldı: " + reason);
                notifyConnectionState(false);
            }
        });
    }
    
    // WebSocket bağlantısı kurma
    public void connectWebSocket() {
        if (webSocket != null) {
            // Zaten var olan bir WebSocket nesnesi varsa kapat
            webSocket.close(1000, "Reconnecting");
        }
        
        isConnected = false;
        
        if (client == null) {
            client = new OkHttpClient.Builder()
                    .connectTimeout(10, TimeUnit.SECONDS)
                    .readTimeout(PONG_TIMEOUT, TimeUnit.MILLISECONDS)
                    .writeTimeout(10, TimeUnit.SECONDS)
                    .pingInterval(PING_INTERVAL, TimeUnit.MILLISECONDS)
                    .retryOnConnectionFailure(true)
                    .build();
        }
        
        try {
            createWebSocketClient();
        } catch (Exception e) {
            Log.e(TAG, "WebSocket bağlantısı kurulurken hata oluştu: " + e.getMessage());
        }
    }
    
    // WebSocket bağlantısını kapatma
    public void disconnectWebSocket() {
        stopPingPong();
        
        if (webSocket != null) {
            webSocket.close(1000, "Service destroyed");
            webSocket = null;
        }
        isConnected = false;
    }
    
    // Ping-pong mekanizmasını başlatma
    private void startPingPong() {
        // Önceki ping-pong planlamasını iptal et
        stopPingPong();
        
        // Yeni ping-pong planla
        pingHandler.postDelayed(pingRunnable, PING_INTERVAL);
    }
    
    // Ping-pong mekanizmasını durdurma
    private void stopPingPong() {
        pingHandler.removeCallbacks(pingRunnable);
    }
    
    // Yeniden bağlanma
    private void scheduleReconnect() {
        if (reconnectAttempts < MAX_RECONNECT_ATTEMPTS) {
            reconnectAttempts++;
            
            // Üstel geri çekilme ile yeniden bağlanma gecikmesi
            int delay = Math.min(INITIAL_RECONNECT_DELAY * (1 << (reconnectAttempts - 1)), 30000);
            
            Log.d(TAG, "Yeniden bağlanma denemesi " + reconnectAttempts + "/" + MAX_RECONNECT_ATTEMPTS + 
                  " - " + delay + "ms sonra");
            
            mainHandler.postDelayed(() -> {
                if (!isConnected) {
                    updateNotification("Yeniden bağlanma denemesi " + reconnectAttempts + "/" + MAX_RECONNECT_ATTEMPTS);
                    connectWebSocket();
                }
            }, delay);
        } else {
            updateNotification("Bağlantı kurulamadı - Manuel yenileme gerekiyor");
            Log.e(TAG, "Maksimum yeniden bağlanma denemesi aşıldı");
            
            // Bağlantı durumunu güncelle
            notifyConnectionState(false);
            
            // 1 dakika sonra yeniden dene
            mainHandler.postDelayed(() -> {
                reconnectAttempts = 0;
                connectWebSocket();
            }, 60000);
        }
    }
    
    // Bağlantı durumunu kontrol etme
    public boolean isConnected() {
        return isConnected;
    }
    
    // Callback'i ayarlama
    public void setCallback(WebSocketCallback callback) {
        WebSocketCallback oldCallback = this.callback;
        this.callback = callback;
        
        // Yeni callback null değilse ve eskisinden farklıysa
        if (callback != null && callback != oldCallback) {
            Log.d(TAG, "Yeni callback ayarlandı: " + callback.getClass().getSimpleName());
            
            // Eğer zaten bağlıysak, callback'e hemen bildir
            if (isConnected) {
                mainHandler.post(() -> callback.onConnectionStateChanged(true));
                
                // Eğer önbellekte veri varsa hemen gönder
                synchronized (bufferedData) {
                    if (!bufferedData.isEmpty()) {
                        List<CryptoCurrencies> dataToSend = new ArrayList<>(bufferedData);
                        mainHandler.post(() -> callback.onDataReceived(dataToSend));
                        Log.d(TAG, "Önbellekten " + dataToSend.size() + " adet veri yeni callback'e gönderildi");
                    }
                }
            }
        }
    }
    
    // Listener ekleme
    public void addListener(WebSocketListener listener) {
        if (!listeners.contains(listener)) {
            listeners.add(listener);
        }
    }
    
    // Listener çıkarma
    public void removeListener(WebSocketListener listener) {
        listeners.remove(listener);
    }
    
    // Bildirim kanalı oluşturma
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "WebSocket Service",
                    NotificationManager.IMPORTANCE_LOW);
            channel.setDescription("WebSocket bağlantı durumu bildirimleri");
            
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
    
    // Bildirim oluşturma
    private Notification createNotification(String message) {
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE);
        
        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Kripto Para Takibi")
                .setContentText(message)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentIntent(pendingIntent)
                .setOngoing(true)
                .build();
    }
    
    // Bildirimi güncelleme
    private void updateNotification(String message) {
        NotificationManager notificationManager = 
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFICATION_ID, createNotification(message));
    }
    
    // Kripto para isimlerini logo kaynak isimleriyle eşleştirme
    private void initCryptoLogoMap() {
        cryptoLogoMap.put("BITCOIN", "btc");
        cryptoLogoMap.put("ETHEREUM", "eth");
        cryptoLogoMap.put("RIPPLE", "xrp");
        cryptoLogoMap.put("LITECOIN", "ltc");
        cryptoLogoMap.put("TETHER", "usdt");
        cryptoLogoMap.put("STELLAR", "xlm");
        cryptoLogoMap.put("NEO", "neo");
        cryptoLogoMap.put("DASH", "dash");
        cryptoLogoMap.put("CHAINLINK", "link");
        cryptoLogoMap.put("COSMOS", "atom");
        cryptoLogoMap.put("TEZOS", "xtz");
        cryptoLogoMap.put("TRON", "trx");
        cryptoLogoMap.put("CARDANO", "ada");
        cryptoLogoMap.put("POLKADOT", "dot");
        cryptoLogoMap.put("USD COIN", "usdc");
        cryptoLogoMap.put("MAKER", "mkr");
        cryptoLogoMap.put("AVALANCHE", "avax");
        cryptoLogoMap.put("EOS", "eos");
        // Diğer kripto paralar için gerekirse buraya eklenebilir
    }
    
    // Kripto para için logo kaynak kimliğini getirme
    private int getLogoResourceId(String coinName) {
        String logoName = cryptoLogoMap.getOrDefault(coinName, "default_crypto_logo");
        Resources resources = getResources();
        return resources.getIdentifier(logoName, "drawable", getPackageName());
    }
    
    // OKX API'sinden gelen çift adlarını kripto para isimlerine dönüştürme
    private String getCoinNameFromPair(String pair) {
        if (pair == null) return null;
        
        // Desteklenen kripto para çiftlerini kontrol et
        switch (pair) {
            case "BTC-TRY":
                return "Bitcoin";
            case "ETH-TRY":
                return "Ethereum";
            case "BNB-TRY":
                return "Binance Coin";
            case "XRP-TRY":
                return "Ripple";
            case "ADA-TRY":
                return "Cardano";
            case "DOGE-TRY":
                return "Dogecoin";
            case "DOT-TRY":
                return "Polkadot";
            case "SOL-TRY":
                return "Solana";
            default:
                Log.d(TAG, "Desteklenmeyen kripto para çifti: " + pair);
                return null;
        }
    }
    
    // Callback arayüzü
    public interface WebSocketCallback {
        void onDataReceived(List<CryptoCurrencies> data);
        void onConnectionStateChanged(boolean connected);
        void onError(String message);
    }
    
    // Manuel veri isteği gönderme
    public void sendManualDataRequest() {
        if (webSocket != null && isConnected) {
            // OKX API formatında manuel veri istekleri
            String btcRequest = "{\"op\":\"subscribe\",\"args\":[{\"channel\":\"tickers\",\"instId\":\"BTC-TRY\"}]}";
            String ethRequest = "{\"op\":\"subscribe\",\"args\":[{\"channel\":\"tickers\",\"instId\":\"ETH-TRY\"}]}";
            
            boolean btcSent = webSocket.send(btcRequest);
            boolean ethSent = webSocket.send(ethRequest);
            
            Log.d(TAG, "Manuel veri isteği gönderildi: BTC=" + btcSent + ", ETH=" + ethSent);
        } else {
            Log.d(TAG, "Manuel veri isteği gönderilemedi: WebSocket bağlı değil");
        }
    }

    private void updateConnectionNotification(boolean isConnected) {
        String message = isConnected ? "Bağlandı" : "Bağlantı kesildi";
        updateNotification(message);
    }

    private void notifyConnectionState(boolean isConnected) {
        this.isConnected = isConnected;
        if (callback != null) {
            mainHandler.post(() -> callback.onConnectionStateChanged(isConnected));
        }
    }

    private void notifyDataReceived(List<CryptoCurrencies> currencies) {
        if (callback != null) {
            // Verileri ana thread'de gönder
            mainHandler.post(() -> {
                // Son kontrol - callback değişmiş olabilir
                if (callback != null) {
                    callback.onDataReceived(currencies);
                }
            });
        }
    }

    private String getCoinNameFromSymbol(String symbol) {
        if (symbol == null) return null;
        
        // Desteklenen kripto para sembollerini kontrol et
        switch (symbol.toUpperCase()) {
            case "BTC":
                return "Bitcoin";
            case "ETH":
                return "Ethereum";
            case "BNB":
                return "Binance Coin";
            case "XRP":
                return "Ripple";
            case "ADA":
                return "Cardano";
            case "DOGE":
                return "Dogecoin";
            case "DOT":
                return "Polkadot";
            case "SOL":
                return "Solana";
            default:
                Log.d(TAG, "Desteklenmeyen kripto para sembolü: " + symbol);
                return null;
        }
    }

    // Veri akış hız sınırlama mekanizmasını başlat
    private void startDataThrottling() {
        throttleHandler.removeCallbacks(sendBufferedDataRunnable);
        throttleHandler.postDelayed(sendBufferedDataRunnable, DATA_THROTTLE_INTERVAL);
    }
    
    // Veri akış hız sınırlama mekanizmasını durdur
    private void stopDataThrottling() {
        throttleHandler.removeCallbacks(sendBufferedDataRunnable);
    }
    
    // Önbelleğe alınan verileri UI'a gönder
    private synchronized void sendBufferedDataToUI() {
        if (bufferedData.isEmpty()) {
            return;
        }
        
        // Önbellekteki verilerin bir kopyasını al
        List<CryptoCurrencies> dataToSend = new ArrayList<>(bufferedData);
        
        // Önbelleği temizle
        bufferedData.clear();
        
        // Verileri gönder
        notifyDataReceived(dataToSend);
        
        // Son gönderim zamanını güncelle
        lastDataSentTime = System.currentTimeMillis();
    }

    /**
     * Belirli bir kripto para sembolüne abone ol
     * @param symbol Kripto para sembolü (ör. "BTC-TRY")
     * @return Başarılı olup olmadığı
     */
    public boolean subscribeToSymbol(String symbol) {
        if (webSocket != null && isConnected) {
            String subscribeMessage = "{\"op\":\"subscribe\",\"args\":[{\"channel\":\"tickers\",\"instId\":\"" + symbol + "\"}]}";
            boolean sent = webSocket.send(subscribeMessage);
            // Sadece debug için log kaydı
            if (BuildConfig.DEBUG) {
                Log.d(TAG, "Abonelik mesajı gönderildi: " + sent + " - " + subscribeMessage);
            }
            return sent;
        }
        return false;
    }
    
    /**
     * Belirli bir kripto para sembolüne olan aboneliği iptal et
     * @param symbol Kripto para sembolü (ör. "BTC-TRY")
     * @return Başarılı olup olmadığı
     */
    public boolean unsubscribeFromSymbol(String symbol) {
        if (webSocket != null && isConnected) {
            String unsubscribeMessage = "{\"op\":\"unsubscribe\",\"args\":[{\"channel\":\"tickers\",\"instId\":\"" + symbol + "\"}]}";
            boolean sent = webSocket.send(unsubscribeMessage);
            Log.d(TAG, "Abonelik iptal mesajı gönderildi: " + sent + " - " + unsubscribeMessage);
            return sent;
        }
        return false;
    }
    
    /**
     * Birden fazla kripto para sembolüne abone ol
     * @param symbols Kripto para sembolleri listesi
     * @return Başarılı işlem sayısı
     */
    public int subscribeToSymbols(List<String> symbols) {
        int successCount = 0;
        if (symbols != null && !symbols.isEmpty()) {
            for (String symbol : symbols) {
                if (subscribeToSymbol(symbol)) {
                    successCount++;
                }
            }
        }
        return successCount;
    }
    
    /**
     * Bir kripto para için veri talep et ve sürekli abone kal
     * @param symbol Kripto para sembolü
     * @return Başarılı olup olmadığı
     */
    public boolean requestSymbolData(String symbol) {
        if (symbol == null || symbol.isEmpty()) {
            Log.e(TAG, "Geçersiz sembol: null veya boş");
            return false;
        }
        
        if (webSocket != null) {
            // Bağlantı yoksa önce bağlanmayı dene
            if (!isConnected) {
                Log.d(TAG, "WebSocket bağlı değil, önce bağlanılıyor...");
                connectWebSocket();
                
                // İstek başarısız olarak kabul et, servis bağlandığında tekrar deneyecek
                return false;
            }
            
            Log.d(TAG, "Veri talebi gönderiliyor - Sembol: " + symbol);
            
            try {
                // Önce anlık veri talebi gönder
                String snapshotRequest = "{\"op\":\"get\",\"args\":[{\"channel\":\"tickers\",\"instId\":\"" + symbol + "\"}]}";
                boolean snapshotSent = webSocket.send(snapshotRequest);
                Log.d(TAG, "Anlık veri talebi gönderildi: " + snapshotRequest + ", Başarılı: " + snapshotSent);
                
                // Sonra sürekli güncellemeler için abone ol
                String subscribeRequest = "{\"op\":\"subscribe\",\"args\":[{\"channel\":\"tickers\",\"instId\":\"" + symbol + "\"}]}";
                boolean subscribeSent = webSocket.send(subscribeRequest);
                Log.d(TAG, "Abonelik talebi gönderildi: " + subscribeRequest + ", Başarılı: " + subscribeSent);
                
                return snapshotSent && subscribeSent;
            } catch (Exception e) {
                Log.e(TAG, "Veri talebi gönderilirken hata oluştu: " + e.getMessage());
                return false;
            }
        }
        
        Log.e(TAG, "Veri talebi gönderilemedi - WebSocket null");
        return false;
    }
} 