package com.abraxel.cryptocurrency;

import android.graphics.Color;
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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CoinReminderActivity extends AppCompatActivity {

    private LineChart lineChart;
    private String trying = "Trying Cryptos";
    private static RequestQueue requestQueue;
    public static List<ChartData> chartDataList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coin_reminder);
        getVolleyResponse();

        lineChart = findViewById(R.id.line_chart);

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
                        for(int i = 0; i< chartDataList.size(); i++){
                            if(i % 10 ==0){
                                ChartData chartData = chartDataList.get(i);
                                lineList.add(new Entry(chartData.getTimeStamp(), (float) chartData.getCost()));
                            }

                        }
                        drawLineChart(lineList);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> {
                    Toast.makeText(getApplicationContext(), "ERROR", Toast.LENGTH_LONG).show();
                });
        requestQueue.add(req);
        return chartDataList;


        /*
        progressBar.setVisibility(View.VISIBLE);
        MyVolleyRequest.getInstance(CoinReminderActivity.this, chartDataList,
                        response -> {
                            chartDataList = new ArrayList<>();
                            ChartData chartData = new ChartData();
                            try {
                                JSONArray prices = response.getJSONArray("prices");
                                for(int i = 0; i< prices.length(); i++){
                                    JSONArray jsonArray = prices.getJSONArray(i);
                                    chartData.setTimeStamp(timeStampConverter(jsonArray.getString(0)));
                                    chartData.setCost(jsonArray.getDouble(1));
                                    chartDataList.add(chartData);
                        }

                            } catch (JSONException e) {
                                progressBar.setVisibility(View.GONE);
                                e.printStackTrace();
                            }
                            return null;
                        })
                .getRequest(urlCreator(getCoinName()));

         */
    }


    private void callChartList() {

      /*
        requestQueue = Volley.newRequestQueue(getApplicationContext());
        final List<ChartData> chartDataList = new ArrayList<>();
        RequestFuture<JSONArray> future = RequestFuture.newFuture();
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, urlCreator(getCoinName()), null, null, future);
        requestQueue.add(request);

        try {
            JSONArray response = null;
            while (response == null){
                try {
                    response = future.get();
                }catch (InterruptedException e){
                    Thread.currentThread().interrupt();
                }
            }
            Log.i("RESPONSE : ", response.getString(Integer.parseInt("prices")));
        }catch (ExecutionException executionException){
            ((Response.ErrorListener) error -> {
            }).onErrorResponse(new VolleyError(executionException));
        } catch (JSONException e) {
            e.printStackTrace();
        }




 */
    }


    @Nullable
    private String getCoinName() {
        String coinName;
        Bundle extras = getIntent().getExtras();
        coinName = extras.getString(Constants.COIN_NAME);
        return coinName;
    }


    private void drawLineChart(List<Entry> lineList) {
        Description description = new Description();
        description.setText(trying);
        lineChart.setDescription(description);
        LineDataSet lineDataSet = new LineDataSet(lineList, "Test");
        lineDataSet.setValueFormatter(new LineChartXAxisFormatter());
        lineDataSet.setFillAlpha(110);
        lineDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        lineDataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        lineDataSet.setDrawFilled(true);
        lineDataSet.setFillColor(Color.GREEN);

        LineData lineData = new LineData(lineDataSet);
        lineChart.setData(lineData);
        lineData.setValueTextSize(20f);
        lineData.setValueTextColor(Color.BLACK);
        lineChart.invalidate();
    }

    private String urlCreator(String coinName) {
        String beginURL = "https://api.coingecko.com/api/v3/coins/";
        String finalQueue = "/market_chart?vs_currency=try&days=7";
        return beginURL + coinName + finalQueue;
    }

}