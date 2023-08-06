package com.abraxel.cryptocurrency;

import android.content.Context;
import com.abraxel.cryptocurrency.model.CryptoCurrencies;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.List;

public class MethodServer extends MainActivity {
    Context mContext;

    MethodServer(Context ctx) {
        this.mContext = ctx;
    }

    public void cryptoSetter(JSONArray data, List<CryptoCurrencies> cryptoCurrenciesList) throws JSONException {


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
        JSONObject ETH = data.getJSONObject(1);
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
        JSONObject XRP = data.getJSONObject(2);
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
        JSONObject LTC = data.getJSONObject(3);
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
        JSONObject USDT = data.getJSONObject(4);
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
        JSONObject XLM = data.getJSONObject(9);
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
        JSONObject NEO = data.getJSONObject(11);
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
        JSONObject DASH = data.getJSONObject(15);
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
        JSONObject LINK = data.getJSONObject(17);
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
        JSONObject ATOM = data.getJSONObject(19);
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
        JSONObject XTZ = data.getJSONObject(21);
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
        JSONObject TRX = data.getJSONObject(23);
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
        JSONObject ADA = data.getJSONObject(25);
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
        JSONObject DOT = data.getJSONObject(27);
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
        JSONObject USDC = data.getJSONObject(29);
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
        JSONObject MKR = data.getJSONObject(35);
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
        JSONObject AVAX = data.getJSONObject(53);
        avaxTry.setCoinName("AVALANCHE");
        avaxTry.setPair(AVAX.getString("pair"));
        avaxTry.setLast(AVAX.getString("last"));
        avaxTry.setHigh(AVAX.getString("high"));
        avaxTry.setLow(AVAX.getString("low"));
        avaxTry.setDailyPercent(AVAX.getString("dailyPercent"));
        avaxTry.setLogoResourceId(getImageResourceId("avax"));

        cryptoCurrenciesList.add(avaxTry);


        //EOS SECTION
        CryptoCurrencies eosTry = new CryptoCurrencies();
        JSONObject EOS = data.getJSONObject(13);
        eosTry.setCoinName("EOS");
        eosTry.setPair(EOS.getString("pair"));
        eosTry.setLast(EOS.getString("last"));
        eosTry.setHigh(EOS.getString("high"));
        eosTry.setLow(EOS.getString("low"));
        eosTry.setDailyPercent(EOS.getString("dailyPercent"));
        eosTry.setLogoResourceId(getImageResourceId("eos"));

        cryptoCurrenciesList.add(eosTry);
    }

    public int getImageResourceId(String imageName) {
        Context context = mContext;
        return context.getResources().getIdentifier(imageName, "drawable", context.getPackageName());
    }

}
