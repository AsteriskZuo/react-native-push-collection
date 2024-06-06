package com.pushcollection.register;

import android.content.Context;
import com.meizu.cloud.pushsdk.PushManager;
import com.pushcollection.PushClient;
import com.pushcollection.PushConfig;
import com.pushcollection.PushRegister;
import com.pushcollection.config.MeizuPushConfig;

public class MeizuPushRegister implements PushRegister {
  private String token;

  public MeizuPushRegister(Context context) {}

  @Override
  public void register(PushConfig config) {
    this.token = PushManager.getPushId(PushClient.getInstance().getApplicationContext());
    if (token == null || token.contentEquals("")) {
      MeizuPushConfig f = (MeizuPushConfig)config;
      PushManager.register(PushClient.getInstance().getApplicationContext(), f.appId, f.appKey);
    } else {
      PushClient.getInstance().onReceivePushToken(token);
    }
  }

  @Override
  public void unregister() {
    MeizuPushConfig f = (MeizuPushConfig)PushClient.getInstance().getPushConfig();
    PushManager.unRegister(PushClient.getInstance().getApplicationContext(), f.appId, f.appKey);
    token = null;
  }

  @Override
  public String getDeviceToken() {
    return token;
  }
}
