package com.pushcollection.register;

import androidx.annotation.NonNull;
import com.facebook.react.bridge.Callback;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.messaging.FirebaseMessaging;
import com.pushcollection.BasicPushRegister;
import com.pushcollection.PushClient;
import com.pushcollection.PushError;
import com.pushcollection.PushErrorCode;
import java.util.Objects;

public class FcmPushRegister extends BasicPushRegister {
  public FcmPushRegister(PushClient client, Callback callback) {
    super(client);
    if (!FirebaseMessaging.getInstance().isAutoInitEnabled()) {
      FirebaseMessaging.getInstance().setAutoInitEnabled(true);
    }

    if (FirebaseApp.initializeApp(client.getApplicationContext()) == null) {
      callback.invoke(new PushError(PushErrorCode.INIT_ERROR, "FCM init is failed."));
    } else {
      callback.invoke();
    }
  }

  @Override
  public void prepare(Callback callback) {
    if (!FirebaseMessaging.getInstance().isNotificationDelegationEnabled()) {
      FirebaseMessaging.getInstance().setNotificationDelegationEnabled(true);
    }
    callback.invoke();
  }

  @Override
  public void register(Callback callback) {
    String t = getDeviceToken();
    if (t == null || t.contentEquals("")) {
      FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
        @Override
        public void onComplete(@NonNull Task<String> task) {
          if (!task.isSuccessful()) {
            String reason = Objects.requireNonNull(task.getException()).getMessage();
            callback.invoke(new PushError(PushErrorCode.REGISTER_ERROR, reason));
          } else {
            setDeviceToken(task.getResult());
            callback.invoke(getDeviceToken());
          }
        }
      });
    } else {
      callback.invoke(t);
    }
  }

  @Override
  public void unregister(Callback callback) {
    FirebaseMessaging.getInstance().deleteToken().addOnCompleteListener(new OnCompleteListener<Void>() {
      @Override
      public void onComplete(@NonNull Task<Void> task) {
        if (!task.isSuccessful()) {
          String reason = Objects.requireNonNull(task.getException()).getMessage();
          callback.invoke(new PushError(PushErrorCode.UNREGISTER_ERROR, reason));
        } else {
          setDeviceToken(null);
          callback.invoke();
        }
      }
    });
  }
}
