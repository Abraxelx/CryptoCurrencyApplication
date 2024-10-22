package com.abraxel.cryptocurrency;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.view.View;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.abraxel.cryptocurrency.adapter.CurrencyAdapter;
import com.abraxel.cryptocurrency.model.CryptoCurrencies;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class MainActivity extends AppCompatActivity {
    Logger logger = Logger.getLogger(MainActivity.class.getName());

    private RequestQueue requestQueue;
    private CurrencyAdapter currencyAdapter;
    public ProgressBar progressBar;
    List<CryptoCurrencies> cryptoCurrenciesList;
    MethodServer methodServer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Context context = getApplicationContext();
        methodServer = new MethodServer(context);
        createNotificationChannel();


        SwipeRefreshLayout swipeRefreshLayout;
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        progressBar = findViewById(R.id.progress_circular);
        requestQueue = Volley.newRequestQueue(this);


        cryptoCurrenciesList = callRest();
        currencyAdapter = new CurrencyAdapter(cryptoCurrenciesList, getApplicationContext());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        RecyclerView recyclerView;
        recyclerView = findViewById(R.id.recycleView);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), linearLayoutManager.getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(currencyAdapter);


        swipeRefreshLayout.setOnRefreshListener(() -> {
            List<CryptoCurrencies> cryptoCurrenciesList = callRest();
            currencyAdapter = new CurrencyAdapter(cryptoCurrenciesList, getApplicationContext());
            recyclerView.setAdapter(currencyAdapter);
            currencyAdapter.notifyDataSetChanged();
            swipeRefreshLayout.setRefreshing(false);
        });
    }

    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        getMenuInflater().inflate(R.menu.search_menu, menu);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setQueryHint("ARA");

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                currencyAdapter.getFilter().filter(newText);
                return false;
            }
        });
        return true;
    }

    public List<CryptoCurrencies> callRest() {

        progressBar.setVisibility(View.VISIBLE);
        requestQueue = Volley.newRequestQueue(getApplicationContext());
        final List<CryptoCurrencies> ccList = new ArrayList<>();

        final String URL = "https://api.btcturk.com/api/v2/ticker";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                URL,
                null,
                response -> {
                    try {
                        JSONArray data = response.getJSONArray("data");
                        methodServer.cryptoSetter(data, ccList);
                    } catch (JSONException e) {
                        logger.warning(e.getMessage());
                        progressBar.setVisibility(View.GONE);
                    }
                    currencyAdapter.notifyDataSetChanged();
                    progressBar.setVisibility(View.GONE);
                },
                error -> progressBar.setVisibility(View.GONE));
        jsonObjectRequest.setTag("T");
        requestQueue.add(jsonObjectRequest);
        return ccList;
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (requestQueue != null) {
            requestQueue.cancelAll("T");
        }
    }

    private void createNotificationChannel() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            CharSequence name = "abraxelReminderChannel";
            String description = "Channel for Kripto Para";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel;
            channel = new NotificationChannel("cryptocurrency", name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

}

