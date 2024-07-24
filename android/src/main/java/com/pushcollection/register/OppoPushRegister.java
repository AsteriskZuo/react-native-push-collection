package com.pushcollection.register;

import android.content.Context;
import com.facebook.react.bridge.Callback;
import com.heytap.msp.push.HeytapPushManager;
import com.heytap.msp.push.callback.ICallBackResultService;
import com.pushcollection.PushClient;
import com.pushcollection.PushConfig;
import com.pushcollection.PushError;
import com.pushcollection.PushErrorCode;
import com.pushcollection.PushRegister;
import com.pushcollection.config.OppoPushConfig;
import org.json.JSONObject;

public class OppoPushRegister implements PushRegister {
  boolean isSupport;

  private String token;

  public OppoPushRegister(Context context, Callback callback) {
    this.isSupport = HeytapPushManager.isSupportPush(context);
    if (!isSupport) {
      callback.invoke(new PushError(PushErrorCode.INIT_ERROR, "Oppo init is failed."));
      return;
    }
    HeytapPushManager.init(context, true);
    callback.invoke();
  }

  @Override
  public void register(PushConfig config, Callback callback) {
    if (!isSupport) {
      callback.invoke(new PushError(PushErrorCode.NO_SUPPROT_ERROR, "Oppo Push is not supported"));
      return;
    }
    OppoPushConfig f = (OppoPushConfig)config;
    HeytapPushManager.register(
      PushClient.getInstance().getApplicationContext(), f.appKey, f.secret, new ICallBackResultService() {
        @Override
        public void onRegister(int responseCode, String registerID, String packageName, String miniPackageName) {
          if (responseCode == 0) {
            token = registerID;
            callback.invoke(token);
          } else {
            callback.invoke(new PushError(PushErrorCode.REGISTER_ERROR, "Oppo push register error"));
          }
        }

        @Override
        public void onUnRegister(int responseCode, String packageName, String miniProgramPkg) {}

        @Override
        public void onSetPushTime(int i, String s) {}

        @Override
        public void onGetPushStatus(int i, int i1) {}

        @Override
        public void onGetNotificationStatus(int i, int i1) {}

        @Override
        public void onError(int errorCode, String message, String packageName, String miniProgramPkg) {
          callback.invoke(new PushError(PushErrorCode.fromCode(errorCode),
                                        "{"
                                          + "\"code\":" + errorCode + ",\"packageName\":" + packageName +
                                          "\"miniProgramPkg\":" + miniProgramPkg + "}"));
        }
      });
  }

  @Override
  public void unregister(Callback callback) {
    if (!isSupport) {
      callback.invoke(new PushError(PushErrorCode.NO_SUPPROT_ERROR, "Oppo Push is not supported"));
      return;
    }
    OppoPushConfig f = (OppoPushConfig)PushClient.getInstance().getPushConfig();
    HeytapPushManager.unRegister(
      PushClient.getInstance().getApplicationContext(), f.appKey, f.secret, new JSONObject(),
      new ICallBackResultService() {
        @Override
        public void onRegister(int i, String s, String s1, String s2) {}

        @Override
        public void onUnRegister(int responseCode, String s, String s1) {
          if (responseCode == 0) {
            token = null;
            callback.invoke();
          } else {
            callback.invoke(new PushError(PushErrorCode.UNREGISTER_ERROR, "Oppo push unregister error"));
          }
        }

        @Override
        public void onSetPushTime(int i, String s) {}

        @Override
        public void onGetPushStatus(int i, int i1) {}

        @Override
        public void onGetNotificationStatus(int i, int i1) {}

        @Override
        public void onError(int i, String s, String s1, String s2) {
          callback.invoke(new PushError(PushErrorCode.UNREGISTER_ERROR, "{"
                                                                          + "\"code\":" + i + ",\"message\":" + s +
                                                                          ",\"packageName\":" + s1 +
                                                                          "\"miniProgramPkg\":" + s2 + "}"));
        }
      });
  }

  @Override
  public String getDeviceToken() {
    return token;
  }
}
