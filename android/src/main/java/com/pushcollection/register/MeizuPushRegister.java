package com.pushcollection.register;

import com.facebook.react.bridge.Callback;
import com.meizu.cloud.pushsdk.PushManager;
import com.pushcollection.BasicPushRegister;
import com.pushcollection.PushClient;
import com.pushcollection.config.MeizuPushConfig;
import com.xiaomi.mipush.sdk.MiPushClient;

public class MeizuPushRegister extends BasicPushRegister {
  public MeizuPushRegister(PushClient client) { super(client); }

  @Override
  public void register(Callback callback) {
    String t = getDeviceToken();
    if (t == null || t.contentEquals("")) {
      t = PushManager.getPushId(getContext());
      setDeviceToken(t);
    }
    if (t == null || t.contentEquals("")) {
      MeizuPushConfig f = (MeizuPushConfig)getPushConfig();
      PushManager.register(getContext(), f.appId, f.appKey);
      callback.invoke();
    } else {
      callback.invoke(t);
    }
  }

  @Override
  public void unregister(Callback callback) {
    MeizuPushConfig f = (MeizuPushConfig)getPushConfig();
    PushManager.unRegister(getContext(), f.appId, f.appKey);
    setDeviceToken(null);
    callback.invoke();
  }
}
