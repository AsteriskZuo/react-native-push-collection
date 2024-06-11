package com.pushcollection.register;

import android.content.Context;
import androidx.annotation.NonNull;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.messaging.FirebaseMessaging;
import com.pushcollection.PushClient;
import com.pushcollection.PushConfig;
import com.pushcollection.PushRegister;
import com.pushcollection.ThreadUtil;

import java.util.Objects;

public class FcmPushRegister implements PushRegister {
  private String token;

  public FcmPushRegister(Context context) {
    ThreadUtil.mainThreadExecute(() -> {
      FirebaseApp.initializeApp(context);
    });
  }

  @Override
  public void register(PushConfig config) {
    ThreadUtil.mainThreadExecute(() -> {
      FirebaseMessaging.getInstance()
        .getToken()
        .addOnCompleteListener(new OnCompleteListener<String>() {
          @Override
          public void onComplete(@NonNull Task<String> task) {
            if (!task.isSuccessful()) {
              PushClient.getInstance().onError(new Error(Objects.requireNonNull(task.getException()).getMessage()));
              return;
            }
            token = task.getResult();
            if (token != null) {
              PushClient.getInstance().onReceivePushToken(token);
            }
          }
        })
        .addOnFailureListener(new OnFailureListener() {
          @Override
          public void onFailure(@NonNull Exception e) {
            PushClient.getInstance().onError(new Error(e.getMessage()));
          }
        });
    });
  }

  @Override
  public void unregister() {
    ThreadUtil.mainThreadExecute(() -> {
      FirebaseMessaging.getInstance()
        .deleteToken()
        .addOnFailureListener(new OnFailureListener() {
          @Override
          public void onFailure(@NonNull Exception e) {
            PushClient.getInstance().onError(new Error(e.getMessage()));
          }
        })
        .addOnCompleteListener(new OnCompleteListener<Void>() {
          @Override
          public void onComplete(@NonNull Task<Void> task) {
            token = null;
            PushClient.getInstance().onReceivePushToken(token);
          }
        });
    });
  }

  @Override
  public String getDeviceToken() {
    return token;
  }
}
