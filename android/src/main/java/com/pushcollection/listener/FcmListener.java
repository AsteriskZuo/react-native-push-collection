package com.pushcollection.listener;

import androidx.annotation.NonNull;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.pushcollection.PushClient;

/**
 * FcmListener
 * <p>
 * No configuration is required, and it will automatically jump to the application startup page.
 */
public class FcmListener extends FirebaseMessagingService {
  @Override
  public void onNewToken(@NonNull String token) {
    PushClient.getInstance().onReceivePushToken(token);
  }

  @Override
  public void onMessageReceived(@NonNull RemoteMessage message) {
    PushClient.getInstance().onReceivePushMessage(message.getData());
  }
}
