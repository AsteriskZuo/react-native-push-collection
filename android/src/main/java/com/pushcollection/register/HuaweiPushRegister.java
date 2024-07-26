package com.pushcollection.register;

import com.facebook.react.bridge.Callback;
import com.huawei.hms.aaid.HmsInstanceId;
import com.huawei.hms.common.ApiException;
import com.huawei.hms.push.HmsMessaging;
import com.pushcollection.BasicPushRegister;
import com.pushcollection.PushClient;
import com.pushcollection.PushError;
import com.pushcollection.PushErrorCode;
import com.pushcollection.config.HuaweiPushConfig;

public class HuaweiPushRegister extends BasicPushRegister {

  public HuaweiPushRegister(PushClient client, Callback callback) {
    super(client);
    if (!HmsMessaging.getInstance(client.getApplicationContext()).isAutoInitEnabled()) {
      HmsMessaging.getInstance(client.getApplicationContext()).setAutoInitEnabled(true);
    }
    callback.invoke();
  }

  @Override
  public void prepare(Callback callback) {
    HmsMessaging.getInstance(client.getApplicationContext())
      .turnOnPush()
      .addOnCompleteListener(task -> {
        if (!task.isSuccessful()) {
          callback.invoke(new PushError(PushErrorCode.INIT_ERROR, task.getException().getMessage()));
        } else {
          callback.invoke();
        }
      })
      .addOnFailureListener(
        e -> { callback.invoke(new PushError(PushErrorCode.INIT_ERROR, "Huawei init is failed.")); });
  }

  @Override
  public void register(Callback callback) {
    HuaweiPushConfig pushConfig = (HuaweiPushConfig)getPushConfig();
    try {
      setDeviceToken(
        HmsInstanceId.getInstance(getContext()).getToken(pushConfig.appId, HmsMessaging.DEFAULT_TOKEN_SCOPE));
      callback.invoke(getDeviceToken());
    } catch (ApiException e) {
      callback.invoke(new PushError(PushErrorCode.REGISTER_ERROR, e.getMessage()));
    }
  }

  @Override
  public void unregister(Callback callback) {
    HuaweiPushConfig f = (HuaweiPushConfig)getPushConfig();
    try {
      HmsInstanceId.getInstance(getContext()).deleteToken(f.appId);
      setDeviceToken(null);
      callback.invoke();
    } catch (ApiException e) {
      callback.invoke(new PushError(PushErrorCode.UNREGISTER_ERROR, e.getMessage()));
    }
  }
}
