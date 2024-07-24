package com.pushcollection.register;

import android.content.Context;
import com.facebook.react.bridge.Callback;
import com.pushcollection.PushConfig;
import com.pushcollection.PushError;
import com.pushcollection.PushErrorCode;
import com.pushcollection.PushRegister;
import com.pushcollection.config.VivoPushConfig;
import com.vivo.push.IPushActionListener;
import com.vivo.push.PushClient;
import com.vivo.push.listener.IPushQueryActionListener;
import com.vivo.push.util.VivoPushException;

public class VivoPushRegister implements PushRegister {
  private String token;

  public VivoPushRegister(Context context, Callback callback) {
    com.vivo.push.PushConfig config = new com.vivo.push.PushConfig.Builder().agreePrivacyStatement(true).build();
    try {
      PushClient.getInstance(context).initialize(config);
    } catch (VivoPushException e) {
      callback.invoke(new PushError(PushErrorCode.INIT_ERROR, e.getMessage()));
      return;
    }
    PushClient.getInstance(com.pushcollection.PushClient.getInstance().getApplicationContext())
      .turnOnPush(new com.vivo.push.IPushActionListener() {
        @Override
        public void onStateChanged(int state) {
          if (state != 0) {
            callback.invoke(new PushError(PushErrorCode.INIT_ERROR, "Vivo init is failed."));
            return;
          }
          callback.invoke();
        }
      });
  }
  @Override
  public void register(PushConfig config, Callback callback) {
    VivoPushConfig f = (VivoPushConfig)config;
    PushClient.getInstance(com.pushcollection.PushClient.getInstance().getApplicationContext())
      .getRegId(new IPushQueryActionListener() {
        @Override
        public void onSuccess(String s) {
          token = s;
          callback.invoke(token);
        }

        @Override
        public void onFail(Integer integer) {
          callback.invoke(new PushError(PushErrorCode.REGISTER_ERROR, "Vivo push "
                                                                        + "register error" + integer));
        }
      });
  }

  @Override
  public void unregister(Callback callback) {
    PushClient.getInstance(com.pushcollection.PushClient.getInstance().getApplicationContext())
      .deleteRegid(new IPushActionListener() {
        @Override
        public void onStateChanged(int i) {
          if (i != 0) {
            callback.invoke(new PushError(PushErrorCode.UNREGISTER_ERROR, "Vivo push "
                                                                            + "unregister error" + i));
            return;
          }
          token = null;
          callback.invoke();
        }
      });
  }

  @Override
  public String getDeviceToken() {
    return token;
  }
}
