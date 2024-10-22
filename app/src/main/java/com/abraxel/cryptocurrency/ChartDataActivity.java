package com.abraxel.cryptocurrency;

import android.graphics.Color;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.abraxel.cryptocurrency.constants.Constants;
import com.abraxel.cryptocurrency.formatter.LineChartXAxisFormatter;
import com.abraxel.cryptocurrency.model.ChartData;
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

public class ChartDataActivity extends AppCompatActivity {
    private Logger logger = Logger.getLogger(ChartDataActivity.class.getName());

    private LineChart lineChart;
    private TextView coinNameReminder;
    private final List<Entry> lineList = new ArrayList<>();
    private static RequestQueue requestQueue;
    protected static List<ChartData> chartDataList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart_data);
        getVolleyResponse();
        Objects.requireNonNull(this.getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        lineChart = findViewById(R.id.line_chart);
        coinNameReminder = findViewById(R.id.remind_coin_name);

        OnBackPressedCallback callback = new OnBackPressedCallback(true) {

            @Override
            public void handleOnBackPressed() {
                clearChartData();
                finish();
            }
        };
        getOnBackPressedDispatcher().addCallback(this, callback);

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
                error -> Toast.makeText(getApplicationContext(), "ERROR", Toast.LENGTH_LONG).show());
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
        coinNameReminder.setText(coinNameFormatter(coinName));
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