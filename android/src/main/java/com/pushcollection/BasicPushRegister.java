package com.pushcollection;

import android.content.Context;
import com.facebook.react.bridge.Callback;
import com.google.firebase.messaging.FirebaseMessaging;
import com.huawei.hms.push.HmsMessaging;

public abstract class BasicPushRegister implements PushRegister {
  protected String token;
  protected PushClient client;

  public BasicPushRegister(PushClient client) {
    this.client = client;
    FirebaseMessaging.getInstance().setAutoInitEnabled(false);
    HmsMessaging.getInstance(client.getApplicationContext()).setAutoInitEnabled(false);
  }

  @Override
  public String getDeviceToken() {
    return this.token;
  }

  protected void setDeviceToken(String token) { this.token = token; }

  protected PushConfig getPushConfig() { return this.client.getPushConfig(); }

  protected Context getContext() { return this.client.getApplicationContext(); }

  @Override
  public void prepare(Callback callback) {
    callback.invoke();
  }
}
