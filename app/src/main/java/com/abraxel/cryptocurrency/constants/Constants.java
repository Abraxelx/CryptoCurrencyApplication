package com.abraxel.cryptocurrency.constants;

public class Constants {
    private Constants() {
        throw new IllegalStateException("Constant class");
    }

    // Anahtar sabitleri
    public static final String COIN_NAME = "COIN_NAME";
    public static final String COIN_VALUE = "COIN_VALUE";
    public static final String COIN_LOGO = "COIN_LOGO";
    
    // URL sabitleri
    public static final String FIREBASE_URL = "https://currency-android-app-default-rtdb.europe-west1.firebasedatabase.app/";
    public static final String API_URL = "https://api.btcturk.com/api/v2/ticker";
    public static final String WEBSOCKET_URL = "wss://ws.okx.com:8443/ws/v5/public";
    
    // Bildirim sabitleri
    public static final String NOTIFICATION_CHANNEL_ID = "cryptocurrency";
    public static final String NOTIFICATION_CHANNEL_NAME = "abraxelReminderChannel";
    public static final String NOTIFICATION_CHANNEL_DESC = "Channel for Kripto Para";
    
    // Uygulama mesajları
    public static final String CHANGE_TREND = "Değişim Trendi";
    public static final String LOADING_MESSAGE = "Yükleniyor...";
    public static final String ERROR_DATA_PROCESSING = "Veri işlenirken bir hata oluştu: ";
    public static final String ERROR_DATA_LOADING = "Veri yüklenirken bir hata oluştu. Lütfen internet bağlantınızı kontrol edin.";
    
    // Request etiketleri
    public static final String REQUEST_TAG = "T";
    
    // Arama ipucu
    public static final String SEARCH_HINT = "ARA";
    
    // WebSocket sabitleri
    public static final String WEBSOCKET_CONNECTION_OK = "Gerçek zamanlı veri akışı aktif";
    public static final String WEBSOCKET_CONNECTION_ERROR = "Gerçek zamanlı veri akışı kesildi";
    public static final String WEBSOCKET_RECONNECTING = "Bağlantı yeniden kuruluyor...";
}
