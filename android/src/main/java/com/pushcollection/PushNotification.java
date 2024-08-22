package com.pushcollection;

import android.app.NotificationChannel;
import android.app.NotificationManager;

public class PushNotification {
  public static void createChannel(PushClient client) {
    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
      CharSequence name = BuildConfig.OPPO_CHANNEL_NAME;
      int importance = NotificationManager.IMPORTANCE_DEFAULT;
      NotificationChannel channel = new NotificationChannel(BuildConfig.OPPO_CHANNEL_ID, name, importance);
      NotificationManager notificationManager = client.getApplicationContext().getSystemService(NotificationManager.class);
      notificationManager.createNotificationChannel(channel);
    }
  }
}
