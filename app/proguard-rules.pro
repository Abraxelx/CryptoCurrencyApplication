-keep class com.abraxel.cryptocurrency.MainActivity.*{ *; }
-keep class com.abraxel.cryptocurrency.MethodServer.*{ *; }
-keep class com.abraxel.cryptocurrency.adapter.CurrencyAdapter.*{ *; }
-keep class com.abraxel.cryptocurrency.model.CryptoCurrencies.*{ *; }
-keepnames class okhttp3.internal.publicsuffix.PublicSuffixDatabase


# OkHttp platform used only on JVM and when Conscrypt and other security providers are available.
-dontwarn org.conscrypt.**
-dontwarn org.bouncycastle.**
-dontwarn org.openjsse.**
-dontwarn javax.**
-dontwarn lombok.**
-dontwarn org.apache.**
-dontwarn com.sun.**
-dontwarn **retrofit**
