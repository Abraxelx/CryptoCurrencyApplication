package com.abraxel.cryptocurrency;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import com.abraxel.cryptocurrency.interfaces.IVolley;
import com.abraxel.cryptocurrency.model.ChartData;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import java.util.List;

public class MyVolleyRequest {
    @SuppressLint("StaticFieldLeak")
    private static MyVolleyRequest mInstance;
    private RequestQueue requestQueue;
    private final Context context;
    private final IVolley iVolley;

    public MyVolleyRequest(Context context,  IVolley iVolley) {
        this.requestQueue = getRequestQueue(context);
        this.context = context;
        this.iVolley = iVolley;
    }


    public static synchronized MyVolleyRequest getInstance(Context context, List<ChartData> chartDataList, IVolley iVolley) {
        mInstance = new MyVolleyRequest(context, iVolley);
        return mInstance;
    }

    private RequestQueue getRequestQueue(Context context) {
        if (requestQueue == null)
            requestQueue = Volley.newRequestQueue(context.getApplicationContext());
        return requestQueue;
    }

    public <T> void addToRequestQueue(Request<T> request) {
        getRequestQueue(context).add(request);
    }

    public void getRequest(String url) {
        JsonObjectRequest getRequest = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                response -> iVolley.onResponse(response), error -> Log.i("Error : ", error.getMessage()));
        addToRequestQueue(getRequest);
    }

}
