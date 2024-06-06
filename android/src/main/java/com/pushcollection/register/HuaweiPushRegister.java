package com.pushcollection.register;

import android.content.Context;
import com.facebook.react.bridge.ReactApplicationContext;
import com.huawei.hms.aaid.HmsInstanceId;
import com.huawei.hms.common.ApiException;
import com.huawei.hms.push.HmsMessaging;
import com.pushcollection.PushClient;
import com.pushcollection.PushConfig;
import com.pushcollection.PushRegister;
import com.pushcollection.ThreadUtil;
import com.pushcollection.config.HuaweiPushConfig;

public class HuaweiPushRegister implements PushRegister {
  String token;

  public HuaweiPushRegister(Context context) {
    HmsMessaging.getInstance(context)
      .turnOnPush()
      .addOnCompleteListener(task -> {
        if (!task.isSuccessful()) {
          PushClient.getInstance().onError(new Error(task.getException().getMessage()));
        }
      })
      .addOnFailureListener(e -> { PushClient.getInstance().onError(new Error(e.getMessage())); });
  }

  @Override
  public void register(PushConfig config) {
    HuaweiPushConfig pushConfig = (HuaweiPushConfig)config;
    ThreadUtil.asyncExecute(() -> {
      try {
        token = HmsInstanceId.getInstance(PushClient.getInstance().getApplicationContext())
                  .getToken(pushConfig.appId, HmsMessaging.DEFAULT_TOKEN_SCOPE);
        if (token != null) {
          PushClient.getInstance().onReceivePushToken(token);
        }
      } catch (ApiException e) {
        PushClient.getInstance().onError(new Error(e.getMessage()));
      }
    });
  }

  @Override
  public void unregister() {
    HuaweiPushConfig f = (HuaweiPushConfig)PushClient.getInstance().getPushConfig();
    ThreadUtil.asyncExecute(() -> {
      try {
        HmsInstanceId.getInstance(PushClient.getInstance().getApplicationContext()).deleteToken(f.appId);
        token = null;
      } catch (ApiException e) {
        PushClient.getInstance().onError(new Error(e.getMessage()));
      }
    });
  }

  @Override
  public String getDeviceToken() {
    return token;
  }
}
