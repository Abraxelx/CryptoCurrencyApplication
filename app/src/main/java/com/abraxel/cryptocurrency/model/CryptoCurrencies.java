package com.abraxel.cryptocurrency.model;


import android.util.Log;

import com.abraxel.cryptocurrency.R;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class CryptoCurrencies implements Serializable {
    private String coinName;   // Kripto paranın ismi (Bitcoin, Ethereum, vs.)
    private String pair;       // Kripto para çifti (BTC-TRY, ETH-TRY, vs.)
    private String last;       // Son işlem fiyatı
    private String high;       // 24 saatlik en yüksek fiyat
    private String low;        // 24 saatlik en düşük fiyat
    private String dailyPercent; // Günlük değişim yüzdesi
    private int logoResourceId; // Logo kaynak kimliği
    
    // Yeni alanlar
    private String askPrice;   // Satış fiyatı (alıcı için)
    private String bidPrice;   // Alış fiyatı (satıcı için)
    private String volume;     // 24 saatlik işlem hacmi
    
    private static final String TAG = "CryptoCurrencies";
    
    /**
     * Son fiyatı biçimlendirilmiş olarak döndürür
     * @return Biçimlendirilmiş fiyat
     */
    public String getFormattedPrice() {
        if (last == null || last.isEmpty() || last.equals("0")) {
            return "0.00";
        }
        
        try {
            double price = Double.parseDouble(last);
            if (price == 0) return "0.00";
            
            // Debug için log ekle
            Log.d(TAG, "Fiyat formatlanıyor: " + last + " -> " + price);
            
            // 1'den küçük değerler için 4 ondalık basamak
            // 100'den küçük değerler için 2 ondalık basamak
            // 100'den büyük değerler için 0 ondalık basamak
            DecimalFormat formatter;
            if (price < 1) {
                formatter = new DecimalFormat("#,##0.0000");
            } else if (price < 100) {
                formatter = new DecimalFormat("#,##0.00");
            } else {
                formatter = new DecimalFormat("#,##0");
            }
            
            String formattedResult = formatter.format(price);
            Log.d(TAG, "Formatlanmış fiyat: " + formattedResult);
            return formattedResult;
        } catch (NumberFormatException e) {
            Log.e(TAG, "Fiyat formatlanırken hata: " + e.getMessage() + " için değer: " + last);
            return "0.00";
        }
    }
    
    // Alış fiyatını formatlanmış olarak döndürür
    public String getFormattedBidPrice() {
        if (bidPrice == null || bidPrice.isEmpty() || bidPrice.equals("0")) {
            return "0.00";
        }
        
        try {
            double price = Double.parseDouble(bidPrice);
            if (price == 0) return "0.00";
            
            // Fiyat formatını getFormattedPrice() ile aynı mantıkta yap
            DecimalFormat formatter;
            if (price < 1) {
                formatter = new DecimalFormat("#,##0.0000");
            } else if (price < 100) {
                formatter = new DecimalFormat("#,##0.00");
            } else {
                formatter = new DecimalFormat("#,##0");
            }
            
            return formatter.format(price);
        } catch (NumberFormatException e) {
            Log.e("CryptoCurrencies", "Alış fiyatı formatlanırken hata: " + e.getMessage());
            return "0.00";
        }
    }
    
    // Satış fiyatını formatlanmış olarak döndürür
    public String getFormattedAskPrice() {
        if (askPrice == null || askPrice.isEmpty() || askPrice.equals("0")) {
            return "0.00";
        }
        
        try {
            double price = Double.parseDouble(askPrice);
            if (price == 0) return "0.00";
            
            // Fiyat formatını getFormattedPrice() ile aynı mantıkta yap
            DecimalFormat formatter;
            if (price < 1) {
                formatter = new DecimalFormat("#,##0.0000");
            } else if (price < 100) {
                formatter = new DecimalFormat("#,##0.00");
            } else {
                formatter = new DecimalFormat("#,##0");
            }
            
            return formatter.format(price);
        } catch (NumberFormatException e) {
            Log.e("CryptoCurrencies", "Satış fiyatı formatlanırken hata: " + e.getMessage());
            return "0.00";
        }
    }
    
    /**
     * Hacim değerini biçimlendirilmiş olarak döndürür
     * @return Biçimlendirilmiş hacim
     */
    public String getFormattedVolume() {
        if (volume == null || volume.isEmpty() || volume.equals("0")) {
            return "0";
        }
        
        try {
            double volumeValue = Double.parseDouble(volume);
            if (volumeValue == 0) return "0";
            
            DecimalFormat formatter;
            if (volumeValue > 1_000_000) {
                return new DecimalFormat("#,##0.00").format(volumeValue / 1_000_000) + "M";
            } else if (volumeValue > 1_000) {
                return new DecimalFormat("#,##0.00").format(volumeValue / 1_000) + "K";
            } else {
                return new DecimalFormat("#,##0.00").format(volumeValue);
            }
        } catch (NumberFormatException e) {
            Log.e("CryptoCurrencies", "Hacim formatlanırken hata: " + e.getMessage());
            return "0";
        }
    }
    
    // Günlük değişim yüzdesini + veya - işaretiyle döndürür
    public String getFormattedDailyPercent() {
        if (dailyPercent == null || dailyPercent.isEmpty()) {
            return "0.00";
        }
        
        // Değer zaten '-' içeriyorsa olduğu gibi döndür
        if (dailyPercent.contains("-")) {
            return dailyPercent;
        }
        
        // Pozitif değerlere '+' işareti ekle
        return "+" + dailyPercent;
    }
    
    // Değerleri debug için özet olarak döndürür
    @Override
    public String toString() {
        return "CryptoCurrency{" +
               "name='" + coinName + '\'' +
               ", pair='" + pair + '\'' +
               ", price=" + last + " TRY" +
               ", ask=" + askPrice + " TRY" +
               ", bid=" + bidPrice + " TRY" +
               ", dailyChange=" + dailyPercent + "%" +
               '}';
    }
    
    // Statik listedeki veriler için - kripto para sembol listesi
    public static final String[] AVAILABLE_CRYPTO_SYMBOLS = {
        "BTC-TRY", "ETH-TRY", "BNB-TRY", "ADA-TRY", "XRP-TRY", 
        "DOGE-TRY", "DOT-TRY", "SOL-TRY", "LTC-TRY", "LINK-TRY",
        "MATIC-TRY", "UNI-TRY", "ALGO-TRY", "XLM-TRY", "ATOM-TRY"
    };
    
    // Statik veri oluşturma
    public static List<CryptoCurrencies> createStaticCryptoList() {
        List<CryptoCurrencies> cryptoList = new ArrayList<>();
        
        for (String symbol : AVAILABLE_CRYPTO_SYMBOLS) {
            CryptoCurrencies crypto = new CryptoCurrencies();
            crypto.setPair(symbol);
            crypto.setCoinName(getCoinNameFromSymbol(symbol));
            
            // Başlangıç için varsayılan değerler
            crypto.setLast("0.00");
            crypto.setHigh("0.00");
            crypto.setLow("0.00");
            crypto.setDailyPercent("0.00");
            crypto.setAskPrice("0.00");
            crypto.setBidPrice("0.00");
            crypto.setVolume("0.00");
            
            // Logo ataması
            crypto.setLogoResourceId(getDefaultLogoResourceId(crypto.getCoinName()));
            
            cryptoList.add(crypto);
        }
        
        return cryptoList;
    }
    
    // Sembolden para birimi adı almak için yardımcı metot
    private static String getCoinNameFromSymbol(String symbol) {
        if (symbol == null) return "Unknown";
        
        // TRY çiftini temizle
        String coinSymbol = symbol.split("-")[0];
        
        switch (coinSymbol) {
            case "BTC": return "Bitcoin";
            case "ETH": return "Ethereum";
            case "BNB": return "Binance Coin";
            case "ADA": return "Cardano";
            case "XRP": return "Ripple";
            case "DOGE": return "Dogecoin";
            case "DOT": return "Polkadot";
            case "SOL": return "Solana";
            case "LTC": return "Litecoin";
            case "LINK": return "Chainlink";
            case "MATIC": return "Polygon";
            case "UNI": return "Uniswap";
            case "ALGO": return "Algorand";
            case "XLM": return "Stellar";
            case "ATOM": return "Cosmos";
            default: return coinSymbol;
        }
    }
    
    // Varsayılan logo kaynağını belirlemek için yardımcı metot
    private static int getDefaultLogoResourceId(String coinName) {
        // Tüm kripto paralar için varsayılan olarak btc logosunu kullan
        // drawable resource bulunamama hatasını önlemek için
        return R.drawable.btc;
    }
    
    // Para birimini formatlanmış şekilde göster (TRY olarak)
    public String getFormattedCurrency() {
        return "TRY";
    }
    
    // Pair değerini döndür - artık dönüşüm yapmamıza gerek yok
    public String getTRYPair() {
        return pair;
    }
}
