package com.pushcollection.register;

import android.content.Context;
import com.pushcollection.PushClient;
import com.pushcollection.PushConfig;
import com.pushcollection.PushRegister;
import com.pushcollection.config.XiaomiPushConfig;
import com.xiaomi.mipush.sdk.MiPushClient;

public class XiaomiPushRegister implements PushRegister {
  String token;

  public XiaomiPushRegister(Context context) {}

  @Override
  public void register(PushConfig config) {
    this.token = MiPushClient.getRegId(PushClient.getInstance().getApplicationContext());
    if (token == null || token.contentEquals("")) {
      XiaomiPushConfig f = (XiaomiPushConfig)config;
      MiPushClient.registerPush(PushClient.getInstance().getApplicationContext(), f.appId, f.appKey);
    } else {
      PushClient.getInstance().onReceivePushToken(token);
    }
  }

  @Override
  public void unregister() {
    MiPushClient.unregisterPush(PushClient.getInstance().getApplicationContext());
    this.token = null;
  }

  @Override
  public String getDeviceToken() {
    return token;
  }
}
