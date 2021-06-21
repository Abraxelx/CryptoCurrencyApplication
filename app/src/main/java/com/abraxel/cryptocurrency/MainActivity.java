package com.abraxel.cryptocurrency;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.abraxel.cryptocurrency.adapter.CurrencyAdapter;
import com.abraxel.cryptocurrency.model.CryptoCurrencies;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {



    private final String URL = "https://api.btcturk.com/api/v2/ticker";
    private RequestQueue requestQueue;
    private DividerItemDecoration dividerItemDecoration;
    private LinearLayoutManager linearLayoutManager;
    private CurrencyAdapter currencyAdapter ;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private AdView mAdView;
    List<CryptoCurrencies> cryptoCurrenciesList;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        MobileAds.initialize(getApplicationContext(), new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        mAdView = new AdView(this);
        mAdView.setAdSize(AdSize.BANNER);
        mAdView.setAdUnitId("ca-app-pub-2313560120112536/8127489925");

        mAdView= findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        mAdView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
            }

            @Override
            public void onAdFailedToLoad(LoadAdError adError) {
                // Code to be executed when an ad request fails.
            }

            @Override
            public void onAdOpened() {
                // Code to be executed when an ad opens an overlay that
                // covers the screen.
            }

            @Override
            public void onAdClicked() {
                // Code to be executed when the user clicks on an ad.
            }

            @Override
            public void onAdClosed() {
                // Code to be executed when the user is about to return
                // to the app after tapping on an ad.
            }
        });



        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);

        requestQueue = Volley.newRequestQueue(this);



        cryptoCurrenciesList = CallRest();
        currencyAdapter = new CurrencyAdapter(cryptoCurrenciesList, getApplicationContext());
        linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView = findViewById(R.id.recycleView);
        dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), linearLayoutManager.getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(currencyAdapter);


        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                List<CryptoCurrencies> cryptoCurrenciesList = CallRest();
                currencyAdapter = new CurrencyAdapter(cryptoCurrenciesList, getApplicationContext());
                recyclerView.setAdapter(currencyAdapter);
                currencyAdapter.notifyDataSetChanged();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
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

    @Override
    public void onResume() {

        super.onResume();
        if (mAdView!= null) {
            AdRequest adRequest = new AdRequest.Builder().build();
            mAdView.loadAd(adRequest);
        }
    }


    public List<CryptoCurrencies> CallRest() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.show();
        requestQueue = Volley.newRequestQueue(getApplicationContext());
        final List<CryptoCurrencies> cryptoCurrenciesList = new ArrayList<>();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                URL,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray data = response.getJSONArray("data");



                            //BITCOIN SECTION
                            CryptoCurrencies btcTry = new CryptoCurrencies();
                            JSONObject BTC = data.getJSONObject(0);
                            btcTry.setCoinName("BITCOIN");
                            btcTry.setPair(BTC.getString("pair"));
                            btcTry.setLast(BTC.getString("last"));
                            btcTry.setHigh(BTC.getString("high"));
                            btcTry.setLow(BTC.getString("low"));
                            btcTry.setDailyPercent(BTC.getString("dailyPercent"));
                            btcTry.setLogoResourceId(getImageResourceId("btc"));

                            cryptoCurrenciesList.add(btcTry);


                            //ETHEREUM SECTION
                            CryptoCurrencies ethTry = new CryptoCurrencies();
                            JSONObject ETH = data.getJSONObject(2);
                            ethTry.setCoinName("ETHEREUM");
                            ethTry.setPair(ETH.getString("pair"));
                            ethTry.setLast(ETH.getString("last"));
                            ethTry.setHigh(ETH.getString("high"));
                            ethTry.setLow(ETH.getString("low"));
                            ethTry.setDailyPercent(ETH.getString("dailyPercent"));
                            ethTry.setLogoResourceId(getImageResourceId("eth"));

                            cryptoCurrenciesList.add(ethTry);



                            //RIPPLE SECTION
                            CryptoCurrencies xrpTry = new CryptoCurrencies();
                            JSONObject XRP = data.getJSONObject(3);
                            xrpTry.setCoinName("RIPPLE");
                            xrpTry.setPair(XRP.getString("pair"));
                            xrpTry.setLast(XRP.getString("last"));
                            xrpTry.setHigh(XRP.getString("high"));
                            xrpTry.setLow(XRP.getString("low"));
                            xrpTry.setDailyPercent(XRP.getString("dailyPercent"));
                            xrpTry.setLogoResourceId(getImageResourceId("xrp"));

                            cryptoCurrenciesList.add(xrpTry);


                            //LITECOIN SECTION
                            CryptoCurrencies ltcTry = new CryptoCurrencies();
                            JSONObject LTC = data.getJSONObject(4);
                            ltcTry.setCoinName("LITECOIN");
                            ltcTry.setPair(LTC.getString("pair"));
                            ltcTry.setLast(LTC.getString("last"));
                            ltcTry.setHigh(LTC.getString("high"));
                            ltcTry.setLow(LTC.getString("low"));
                            ltcTry.setDailyPercent(LTC.getString("dailyPercent"));
                            ltcTry.setLogoResourceId(getImageResourceId("ltc"));

                            cryptoCurrenciesList.add(ltcTry);


                            //TETHER SECTION
                            CryptoCurrencies usdtTry = new CryptoCurrencies();
                            JSONObject USDT = data.getJSONObject(5);
                            usdtTry.setCoinName("TETHER");
                            usdtTry.setPair(USDT.getString("pair"));
                            usdtTry.setLast(USDT.getString("last"));
                            usdtTry.setHigh(USDT.getString("high"));
                            usdtTry.setLow(USDT.getString("low"));
                            usdtTry.setDailyPercent(USDT.getString("dailyPercent"));
                            usdtTry.setLogoResourceId(getImageResourceId("usdt"));

                            cryptoCurrenciesList.add(usdtTry);


                            //STELLAR SECTION
                            CryptoCurrencies xlmTry = new CryptoCurrencies();
                            JSONObject XLM = data.getJSONObject(10);
                            xlmTry.setCoinName("STELLAR");
                            xlmTry.setPair(XLM.getString("pair"));
                            xlmTry.setLast(XLM.getString("last"));
                            xlmTry.setHigh(XLM.getString("high"));
                            xlmTry.setLow(XLM.getString("low"));
                            xlmTry.setDailyPercent(XLM.getString("dailyPercent"));
                            xlmTry.setLogoResourceId(getImageResourceId("xlm"));

                            cryptoCurrenciesList.add(xlmTry);


                            //NEO SECTION
                            CryptoCurrencies neoTry = new CryptoCurrencies();
                            JSONObject NEO = data.getJSONObject(15);
                            neoTry.setCoinName("NEO");
                            neoTry.setPair(NEO.getString("pair"));
                            neoTry.setLast(NEO.getString("last"));
                            neoTry.setHigh(NEO.getString("high"));
                            neoTry.setLow(NEO.getString("low"));
                            neoTry.setDailyPercent(NEO.getString("dailyPercent"));
                            neoTry.setLogoResourceId(getImageResourceId("neo"));

                            cryptoCurrenciesList.add(neoTry);


                            //DASH SECTION
                            CryptoCurrencies dashTry = new CryptoCurrencies();
                            JSONObject DASH = data.getJSONObject(21);
                            dashTry.setCoinName("DASH");
                            dashTry.setPair(DASH.getString("pair"));
                            dashTry.setLast(DASH.getString("last"));
                            dashTry.setHigh(DASH.getString("high"));
                            dashTry.setLow(DASH.getString("low"));
                            dashTry.setDailyPercent(DASH.getString("dailyPercent"));
                            dashTry.setLogoResourceId(getImageResourceId("dash"));

                            cryptoCurrenciesList.add(dashTry);


                            //LINK SECTION
                            CryptoCurrencies linkTry = new CryptoCurrencies();
                            JSONObject LINK = data.getJSONObject(24);
                            linkTry.setCoinName("CHAINLINK");
                            linkTry.setPair(LINK.getString("pair"));
                            linkTry.setLast(LINK.getString("last"));
                            linkTry.setHigh(LINK.getString("high"));
                            linkTry.setLow(LINK.getString("low"));
                            linkTry.setDailyPercent(LINK.getString("dailyPercent"));
                            linkTry.setLogoResourceId(getImageResourceId("link"));

                            cryptoCurrenciesList.add(linkTry);


                            //ATOM SECTION
                            CryptoCurrencies atomTry = new CryptoCurrencies();
                            JSONObject ATOM = data.getJSONObject(27);
                            atomTry.setCoinName("COSMOS");
                            atomTry.setPair(ATOM.getString("pair"));
                            atomTry.setLast(ATOM.getString("last"));
                            atomTry.setHigh(ATOM.getString("high"));
                            atomTry.setLow(ATOM.getString("low"));
                            atomTry.setDailyPercent(ATOM.getString("dailyPercent"));
                            atomTry.setLogoResourceId(getImageResourceId("atom"));

                            cryptoCurrenciesList.add(atomTry);



                            //TEZOS SECTION
                            CryptoCurrencies xtzTry = new CryptoCurrencies();
                            JSONObject XTZ = data.getJSONObject(30);
                            xtzTry.setCoinName("TEZOS");
                            xtzTry.setPair(XTZ.getString("pair"));
                            xtzTry.setLast(XTZ.getString("last"));
                            xtzTry.setHigh(XTZ.getString("high"));
                            xtzTry.setLow(XTZ.getString("low"));
                            xtzTry.setDailyPercent(XTZ.getString("dailyPercent"));
                            xtzTry.setLogoResourceId(getImageResourceId("xtz"));

                            cryptoCurrenciesList.add(xtzTry);



                            //TRON SECTION
                            CryptoCurrencies trxTry = new CryptoCurrencies();
                            JSONObject TRX = data.getJSONObject(33);
                            trxTry.setCoinName("TRON");
                            trxTry.setPair(TRX.getString("pair"));
                            trxTry.setLast(TRX.getString("last"));
                            trxTry.setHigh(TRX.getString("high"));
                            trxTry.setLow(TRX.getString("low"));
                            trxTry.setDailyPercent(TRX.getString("dailyPercent"));
                            trxTry.setLogoResourceId(getImageResourceId("trx"));

                            cryptoCurrenciesList.add(trxTry);



                            //CARDANO SECTION
                            CryptoCurrencies adaTry = new CryptoCurrencies();
                            JSONObject ADA = data.getJSONObject(36);
                            adaTry.setCoinName("CARDANO");
                            adaTry.setPair(ADA.getString("pair"));
                            adaTry.setLast(ADA.getString("last"));
                            adaTry.setHigh(ADA.getString("high"));
                            adaTry.setLow(ADA.getString("low"));
                            adaTry.setDailyPercent(ADA.getString("dailyPercent"));
                            adaTry.setLogoResourceId(getImageResourceId("ada"));

                            cryptoCurrenciesList.add(adaTry);



                            //POLKADOT SECTION
                            CryptoCurrencies dotTry = new CryptoCurrencies();
                            JSONObject DOT = data.getJSONObject(39);
                            dotTry.setCoinName("POLKADOT");
                            dotTry.setPair(DOT.getString("pair"));
                            dotTry.setLast(DOT.getString("last"));
                            dotTry.setHigh(DOT.getString("high"));
                            dotTry.setLow(DOT.getString("low"));
                            dotTry.setDailyPercent(DOT.getString("dailyPercent"));
                            dotTry.setLogoResourceId(getImageResourceId("dot"));

                            cryptoCurrenciesList.add(dotTry);



                            //USD_COIN SECTION
                            CryptoCurrencies usdcTry = new CryptoCurrencies();
                            JSONObject USDC = data.getJSONObject(42);
                            usdcTry.setCoinName("USD COIN");
                            usdcTry.setPair(USDC.getString("pair"));
                            usdcTry.setLast(USDC.getString("last"));
                            usdcTry.setHigh(USDC.getString("high"));
                            usdcTry.setLow(USDC.getString("low"));
                            usdcTry.setDailyPercent(USDC.getString("dailyPercent"));
                            usdcTry.setLogoResourceId(getImageResourceId("usdc"));

                            cryptoCurrenciesList.add(usdcTry);



                            //MAKER SECTION
                            CryptoCurrencies mkrTry = new CryptoCurrencies();
                            JSONObject MKR = data.getJSONObject(50);
                            mkrTry.setCoinName("MAKER");
                            mkrTry.setPair(MKR.getString("pair"));
                            mkrTry.setLast(MKR.getString("last"));
                            mkrTry.setHigh(MKR.getString("high"));
                            mkrTry.setLow(MKR.getString("low"));
                            mkrTry.setDailyPercent(MKR.getString("dailyPercent"));
                            mkrTry.setLogoResourceId(getImageResourceId("mkr"));

                            cryptoCurrenciesList.add(mkrTry);




                            //AVALANCHE SECTION
                            CryptoCurrencies avaxTry = new CryptoCurrencies();
                            JSONObject AVAX = data.getJSONObject(77);
                            avaxTry.setCoinName("AVALANCHE");
                            avaxTry.setPair(AVAX.getString("pair"));
                            avaxTry.setLast(AVAX.getString("last"));
                            avaxTry.setHigh(AVAX.getString("high"));
                            avaxTry.setLow(AVAX.getString("low"));
                            avaxTry.setDailyPercent(AVAX.getString("dailyPercent"));
                            avaxTry.setLogoResourceId(getImageResourceId("avax"));

                            cryptoCurrenciesList.add(avaxTry);



                        } catch (JSONException e) {
                            e.printStackTrace();
                            progressDialog.dismiss();
                        }
                    currencyAdapter.notifyDataSetChanged();
                        progressDialog.dismiss();
                    }},
                new ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                    }
                });
        jsonObjectRequest.setTag("T");
        requestQueue.add(jsonObjectRequest);
        return cryptoCurrenciesList;
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(requestQueue != null){
            requestQueue.cancelAll("T");
        }
    }

    private int getImageResourceId(String imageName){
        Context context = getApplicationContext();
        return context.getResources().getIdentifier(imageName, "drawable", context.getPackageName());
    }


}
