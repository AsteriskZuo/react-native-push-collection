package com.pushcollection.register;

import android.content.Context;
import com.facebook.react.bridge.Callback;
import com.pushcollection.PushClient;
import com.pushcollection.PushConfig;
import com.pushcollection.PushRegister;
import com.pushcollection.config.XiaomiPushConfig;
import com.xiaomi.mipush.sdk.MiPushClient;

public class XiaomiPushRegister implements PushRegister {
  String token;

  public XiaomiPushRegister(Context context) {}

  @Override
  public void register(PushConfig config, Callback callback) {
    this.token = MiPushClient.getRegId(PushClient.getInstance().getApplicationContext());
    if (token == null || token.contentEquals("")) {
      XiaomiPushConfig f = (XiaomiPushConfig)config;
      MiPushClient.registerPush(PushClient.getInstance().getApplicationContext(), f.appId, f.appKey);
      callback.invoke();
    } else {
      callback.invoke(token);
    }
  }

  @Override
  public void unregister(Callback callback) {
    MiPushClient.unregisterPush(PushClient.getInstance().getApplicationContext());
    this.token = null;
    callback.invoke();
  }

  @Override
  public String getDeviceToken() {
    if (token == null || token.contentEquals("")) {
      this.token = MiPushClient.getRegId(PushClient.getInstance().getApplicationContext());
    }
    return token;
  }
}
