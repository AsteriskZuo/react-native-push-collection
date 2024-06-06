package com.pushcollection.register;

import android.content.Context;
import com.heytap.msp.push.HeytapPushManager;
import com.heytap.msp.push.callback.ICallBackResultService;
import com.pushcollection.PushClient;
import com.pushcollection.PushConfig;
import com.pushcollection.PushRegister;
import com.pushcollection.config.OppoPushConfig;

public class OppoPushRegister implements PushRegister {
  boolean isSupport;

  private String token;

  public OppoPushRegister(Context context) {
    this.isSupport = HeytapPushManager.isSupportPush(context);
    if (!isSupport) {
      PushClient.getInstance().onError(new Error("Oppo push is not supported"));
      return;
    }
    HeytapPushManager.init(context, true);
  }

  @Override
  public void register(PushConfig config) {
    if (!isSupport) {
      PushClient.getInstance().onError(new Error("Oppo push is not supported"));
      return;
    }
    OppoPushConfig f = (OppoPushConfig)config;
    HeytapPushManager.register(
      PushClient.getInstance().getApplicationContext(), f.appKey, f.secret, new ICallBackResultService() {
        @Override
        public void onRegister(int responseCode, String registerID, String packageName, String miniPackageName) {
          if (responseCode == 0) {
            token = registerID;
            if (token != null) {
              PushClient.getInstance().onReceivePushToken(token);
            }
          } else {
            PushClient.getInstance().onError(new Error("Oppo push register error"));
          }
        }

        @Override
        public void onUnRegister(int responseCode, String packageName, String miniProgramPkg) {
          if (responseCode == 0) {
            token = null;
          } else {
            PushClient.getInstance().onError(new Error("Oppo push unregister error"));
          }
        }

        @Override
        public void onSetPushTime(int i, String s) {}

        @Override
        public void onGetPushStatus(int i, int i1) {}

        @Override
        public void onGetNotificationStatus(int i, int i1) {}

        @Override
        public void onError(int errorCode, String message, String packageName, String miniProgramPkg) {
          PushClient.getInstance().onError(new Error("Oppo push error: " + errorCode + packageName + miniProgramPkg));
        }
      });
  }

  @Override
  public void unregister() {
    if (!isSupport) {
      PushClient.getInstance().onError(new Error("Oppo push is not supported"));
      return;
    }
    HeytapPushManager.unRegister();
    token = null;
  }

  @Override
  public String getDeviceToken() {
    return token;
  }
}
