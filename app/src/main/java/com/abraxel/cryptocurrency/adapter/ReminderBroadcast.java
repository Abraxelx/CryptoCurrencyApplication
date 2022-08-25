package com.abraxel.cryptocurrency.adapter;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.abraxel.cryptocurrency.R;

public class ReminderBroadcast extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "cryptocurrency")
                .setSmallIcon(R.drawable.ic_stat_monetization_on)
                .setContentTitle("Kripto Para TL")
                .setContentTitle("Ayarlanan tutar bilgisi...")
                .setPriority(NotificationCompat.PRIORITY_HIGH);
        NotificationManagerCompat notificationCompat = NotificationManagerCompat.from(context);
        notificationCompat.notify(200, builder.build());
    }
}
