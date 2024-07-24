package com.pushcollection.register;

import android.content.Context;
import androidx.annotation.NonNull;
import com.facebook.react.bridge.Callback;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.messaging.FirebaseMessaging;
import com.pushcollection.PushConfig;
import com.pushcollection.PushError;
import com.pushcollection.PushErrorCode;
import com.pushcollection.PushRegister;
import java.util.Objects;

public class FcmPushRegister implements PushRegister {
  private String token;

  public FcmPushRegister(Context context) { FirebaseApp.initializeApp(context); }

  @Override
  public void register(PushConfig config, Callback callback) {
    FirebaseMessaging.getInstance()
      .getToken()
      .addOnCompleteListener(new OnCompleteListener<String>() {
        @Override
        public void onComplete(@NonNull Task<String> task) {
          if (!task.isSuccessful()) {
            String reason = Objects.requireNonNull(task.getException()).getMessage();
            callback.invoke(new PushError(PushErrorCode.REGISTER_ERROR, reason));
            return;
          }
          token = task.getResult();
          callback.invoke(token);
        }
      })
      .addOnFailureListener(new OnFailureListener() {
        @Override
        public void onFailure(@NonNull Exception e) {
          String reason = Objects.requireNonNull(e).getMessage();
          callback.invoke(new PushError(PushErrorCode.REGISTER_ERROR, reason));
        }
      });
  }

  @Override
  public void unregister(Callback callback) {
    FirebaseMessaging.getInstance()
      .deleteToken()
      .addOnFailureListener(new OnFailureListener() {
        @Override
        public void onFailure(@NonNull Exception e) {
          String reason = Objects.requireNonNull(e).getMessage();
          callback.invoke(new PushError(PushErrorCode.UNREGISTER_ERROR, reason));
        }
      })
      .addOnCompleteListener(new OnCompleteListener<Void>() {
        @Override
        public void onComplete(@NonNull Task<Void> task) {
          token = null;
          callback.invoke();
        }
      });
  }

  @Override
  public String getDeviceToken() {
    return token;
  }
}
