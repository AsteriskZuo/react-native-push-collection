package com.pushcollection.register;

import android.content.Context;
import com.pushcollection.PushConfig;
import com.pushcollection.PushRegister;
import com.pushcollection.config.VivoPushConfig;
import com.vivo.push.IPushActionListener;
import com.vivo.push.PushClient;
import com.vivo.push.listener.IPushQueryActionListener;
import com.vivo.push.util.VivoPushException;

public class VivoPushRegister implements PushRegister {
  private String token;

  public VivoPushRegister(Context context) {
    com.vivo.push.PushConfig config = new com.vivo.push.PushConfig.Builder().agreePrivacyStatement(true).build();
    try {
      PushClient.getInstance(context).initialize(config);
    } catch (VivoPushException e) {
      com.pushcollection.PushClient.getInstance().onError(new Error("Vivo push init failed:" + e));
    }
    PushClient.getInstance(com.pushcollection.PushClient.getInstance().getApplicationContext())
      .turnOnPush(new com.vivo.push.IPushActionListener() {
        @Override
        public void onStateChanged(int state) {
          if (state != 0) {
            com.pushcollection.PushClient.getInstance().onError(new Error("Vivo push turn on error"));
          }
        }
      });
  }
  @Override
  public void register(PushConfig config) {
    VivoPushConfig f = (VivoPushConfig)config;
    PushClient.getInstance(com.pushcollection.PushClient.getInstance().getApplicationContext())
      .getRegId(new IPushQueryActionListener() {
        @Override
        public void onSuccess(String s) {
          token = s;
          if (token != null) {
            com.pushcollection.PushClient.getInstance().onReceivePushToken(token);
          }
        }

        @Override
        public void onFail(Integer integer) {
          com.pushcollection.PushClient.getInstance().onError(new Error("Vivo push register error:" + integer));
        }
      });
  }

  @Override
  public void unregister() {
    PushClient.getInstance(com.pushcollection.PushClient.getInstance().getApplicationContext())
      .deleteRegid(new IPushActionListener() {
        @Override
        public void onStateChanged(int i) {
          if (i != 0) {
            com.pushcollection.PushClient.getInstance().onError(new Error("Vivo push unregister error"));
          } else {
            token = null;
          }
        }
      });
  }

  @Override
  public String getDeviceToken() {
    return token;
  }
}
