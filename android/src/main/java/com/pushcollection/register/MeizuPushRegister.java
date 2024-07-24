package com.pushcollection.register;

import android.content.Context;
import com.facebook.react.bridge.Callback;
import com.meizu.cloud.pushsdk.PushManager;
import com.pushcollection.PushClient;
import com.pushcollection.PushConfig;
import com.pushcollection.PushRegister;
import com.pushcollection.config.MeizuPushConfig;

public class MeizuPushRegister implements PushRegister {
  private String token;

  public MeizuPushRegister(Context context) {}

  @Override
  public void register(PushConfig config, Callback callback) {
    this.token = PushManager.getPushId(PushClient.getInstance().getApplicationContext());
    if (token == null || token.contentEquals("")) {
      MeizuPushConfig f = (MeizuPushConfig)config;
      PushManager.register(PushClient.getInstance().getApplicationContext(), f.appId, f.appKey);
      callback.invoke();
    } else {
      callback.invoke(token);
    }
  }

  @Override
  public void unregister(Callback callback) {
    MeizuPushConfig f = (MeizuPushConfig)PushClient.getInstance().getPushConfig();
    PushManager.unRegister(PushClient.getInstance().getApplicationContext(), f.appId, f.appKey);
    token = null;
    callback.invoke();
  }

  @Override
  public String getDeviceToken() {
    if (this.token == null || this.token.contentEquals("")) {
      this.token = PushManager.getPushId(PushClient.getInstance().getApplicationContext());
    }
    return this.token;
  }
}
