package com.example.kazim.myapplication;

import android.app.DownloadManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import static com.android.volley.Request.*;
import static com.android.volley.Request.Method.*;

public class MainActivity extends AppCompatActivity {
    Button btnRefresh;
    TextView txtTime;
    TextView txtBTCPrice;
    TextView txtBTCChange;
    TextView txtBTCHigh;
    TextView txtBTCLow;


    TextView txtETHPrice;
    TextView txtETHChange;
    TextView txtETHHigh;
    TextView txtETHLow;


    String URL;
    RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnRefresh = findViewById(R.id.button);
        txtTime = findViewById(R.id.txtTime);

        //BITCOIN SPACE
        txtBTCPrice = findViewById(R.id.txtBTCPrice);
        txtBTCChange = findViewById(R.id.txtBTCChange);
        txtBTCHigh = findViewById(R.id.txtBTCHigh);
        txtBTCLow = findViewById(R.id.txtBTCLow);


        //ETHEREUM SPACE

        txtETHPrice = findViewById(R.id.txtETHPrice);
        txtETHChange = findViewById(R.id.txtETHChange);
        txtETHHigh = findViewById(R.id.txtETHHigh);
        txtETHLow = findViewById(R.id.txtETHLow);



        requestQueue = Volley.newRequestQueue(this);
        URL = "https://koineks.com/ticker";

        DateFormat df = new SimpleDateFormat("d MMMM EEEE yyyy, HH:mm");
        String date = df.format(Calendar.getInstance().getTime());

        txtTime.setText(date);



        CallRest();

        btnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CallRest();
                DateFormat df = new SimpleDateFormat("d MMMM EEEE yyyy, HH:mm");
                String date = df.format(Calendar.getInstance().getTime());
                txtTime.setText(date);
            }
        });




    }



    public void CallRest()
    {
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());



        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                URL,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject jsonObject = response.getJSONObject("BTC");
                            JSONObject jsonObject1 = response.getJSONObject("ETH");

                          //  for(int i = 0; i<jsonObject.length();i++)
                         //   {
                               // JSONObject bpi = jsonArray.getJSONObject(i);
                                String BTCprice = jsonObject.getString("current");
                                String BTCchange = jsonObject.getString("change_percentage");
                                String BTChigh = jsonObject.getString("high");
                                String BTClow=jsonObject.getString("low");
                                txtBTCPrice.setText("Güncel Fiyat : "+ " " +BTCprice +" TL");
                                txtBTCChange.setText("Değişim(24s) : "+ " " + BTCchange + "%");
                                txtBTCHigh.setText("En Yüksek(24s) : "+ " " + BTChigh + " TL");
                                txtBTCLow.setText("En Düşük(24s) : "+ " " + BTClow + " TL");


                            String ETHprice = jsonObject1.getString("current");
                            String ETHchange = jsonObject1.getString("change_percentage");
                            String ETHhigh = jsonObject1.getString("high");
                            String ETHlow=jsonObject1.getString("low");
                            txtETHPrice.setText("Güncel Fiyat : "+ " " +ETHprice +" TL");
                            txtETHChange.setText("Değişim(24s) : "+ " " + ETHchange + "%");
                            txtETHHigh.setText("En Yüksek(24s) : "+ " " + ETHhigh + " TL");
                            txtETHLow.setText("En Düşük(24s) : "+ " " + ETHlow + " TL");
                           // }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } }},
                new ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("HATAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAa","Error :" + error.toString());
                    }
                });
        requestQueue.add(jsonObjectRequest);
    }
}
