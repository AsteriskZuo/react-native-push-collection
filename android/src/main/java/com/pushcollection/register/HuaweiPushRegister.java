package com.pushcollection.register;

import android.content.Context;
import com.facebook.react.bridge.Callback;
import com.huawei.hms.aaid.HmsInstanceId;
import com.huawei.hms.common.ApiException;
import com.huawei.hms.push.HmsMessaging;
import com.pushcollection.PushClient;
import com.pushcollection.PushConfig;
import com.pushcollection.PushError;
import com.pushcollection.PushErrorCode;
import com.pushcollection.PushRegister;
import com.pushcollection.config.HuaweiPushConfig;

public class HuaweiPushRegister implements PushRegister {
  String token;

  public HuaweiPushRegister(Context context, Callback callback) throws PushError {
    HmsMessaging.getInstance(context)
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
  public void register(PushConfig config, Callback callback) {
    HuaweiPushConfig pushConfig = (HuaweiPushConfig)config;
    try {
      token = HmsInstanceId.getInstance(PushClient.getInstance().getApplicationContext())
                .getToken(pushConfig.appId, HmsMessaging.DEFAULT_TOKEN_SCOPE);
      callback.invoke(token);
    } catch (ApiException e) {
      callback.invoke(new PushError(PushErrorCode.REGISTER_ERROR, e.getMessage()));
    }
  }

  @Override
  public void unregister(Callback callback) {
    HuaweiPushConfig f = (HuaweiPushConfig)PushClient.getInstance().getPushConfig();
    try {
      HmsInstanceId.getInstance(PushClient.getInstance().getApplicationContext()).deleteToken(f.appId);
      token = null;
      callback.invoke();
    } catch (ApiException e) {
      callback.invoke(new PushError(PushErrorCode.UNREGISTER_ERROR, e.getMessage()));
    }
  }

  @Override
  public String getDeviceToken() {
    return token;
  }
}
