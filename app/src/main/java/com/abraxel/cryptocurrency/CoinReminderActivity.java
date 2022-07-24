package com.abraxel.cryptocurrency;

import android.graphics.Color;
import android.widget.TextView;
import android.widget.Toast;

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

import java.util.ArrayList;
import java.util.List;

public class CoinReminderActivity extends AppCompatActivity {

    private LineChart lineChart;
    private TextView coinNameReminder;
    private String trying = "Değişim Trendi";
    private static RequestQueue requestQueue;
    public static List<ChartData> chartDataList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coin_reminder);
        getVolleyResponse();
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        lineChart = findViewById(R.id.line_chart);
        coinNameReminder = findViewById(R.id.remind_coin_name);

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
                            JSONArray jsonArray = arr.getJSONArray(i);
                            ChartData chartData = new ChartData();
                            chartData.setTimeStamp(jsonArray.getLong(0));
                            chartData.setCost((int) jsonArray.getDouble(1));
                            chartDataList.add(chartData);
                        }

                        List<Entry> lineList = new ArrayList<>();
                        for (int i = 0; i < chartDataList.size(); i++) {
                            if (i % 10 == 0) {
                                ChartData chartData = chartDataList.get(i);
                                lineList.add(new Entry(chartData.getTimeStamp(), (float) chartData.getCost()));
                            }

                        }
                        drawLineChart(lineList, getCoinName());

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> {
                    Toast.makeText(getApplicationContext(), "ERROR", Toast.LENGTH_LONG).show();
                });
        requestQueue.add(req);
        return chartDataList;

    }


    @Nullable
    private String getCoinName() {
        String coinName;
        Bundle extras = getIntent().getExtras();
        coinName = extras.getString(Constants.COIN_NAME);
        return coinName;
    }


    private void drawLineChart(List<Entry> lineList, String coinName) {
        coinNameReminder.setText(coinNameFormatter(coinName));
        Description description = new Description();
        description.setText(trying);
        lineChart.setDescription(description);
        lineChart.getXAxis().setValueFormatter(new LineChartXAxisFormatter());
        LineDataSet lineDataSet = new LineDataSet(lineList, coinNameFormatter(coinName) +" 7 Günlük Değişim Grafiği");
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

    private String coinNameFormatter(String coinName){
        return coinName.substring(0,1).toUpperCase() + coinName.substring(1);
    }

}