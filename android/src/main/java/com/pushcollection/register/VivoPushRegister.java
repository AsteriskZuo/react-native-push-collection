package com.pushcollection.register;

import com.facebook.react.bridge.Callback;
import com.pushcollection.BasicPushRegister;
import com.pushcollection.PushError;
import com.pushcollection.PushErrorCode;
import com.pushcollection.config.VivoPushConfig;
import com.vivo.push.IPushActionListener;
import com.vivo.push.PushClient;
import com.vivo.push.listener.IPushQueryActionListener;
import com.vivo.push.util.VivoPushException;

public class VivoPushRegister extends BasicPushRegister {

  public VivoPushRegister(com.pushcollection.PushClient client, Callback callback) {
    super(client);
    com.vivo.push.PushConfig config = new com.vivo.push.PushConfig.Builder().agreePrivacyStatement(true).build();
    try {
      PushClient.getInstance(client.getApplicationContext()).initialize(config);
      callback.invoke();
    } catch (VivoPushException e) {
      callback.invoke(new PushError(PushErrorCode.INIT_ERROR, e.getMessage()));
    }
  }

  @Override
  public void prepare(Callback callback) {
    //    callback.invoke();
    //    return; // !!! 1003 vivo is error.  ref: https://dev.vivo.com.cn/documentCenter/doc/614
    PushClient.getInstance(getContext()).turnOnPush(new com.vivo.push.IPushActionListener() {
      @Override
      public void onStateChanged(int state) {
        if (state != 0) {
          // !!! 1003 vivo is error.  ref: https://dev.vivo.com.cn/documentCenter/doc/614
          callback.invoke(new PushError(PushErrorCode.INIT_ERROR, "Vivo turn on is failed." + state));
          return;
        }
        callback.invoke();
      }
    });
  }

  @Override
  public void register(Callback callback) {
    String t = getDeviceToken();
    if (t == null || t.contentEquals("")) {
      VivoPushConfig f = (VivoPushConfig)getPushConfig();
      PushClient.getInstance(getContext()).getRegId(new IPushQueryActionListener() {
        @Override
        public void onSuccess(String s) {
          setDeviceToken(s);
          callback.invoke(getDeviceToken());
        }

        @Override
        public void onFail(Integer integer) {
          callback.invoke(new PushError(PushErrorCode.REGISTER_ERROR, "Vivo push "
                                                                        + "register error" + integer));
        }
      });
    } else {
      callback.invoke(t);
    }
  }

  @Override
  public void unregister(Callback callback) {
    PushClient.getInstance(getContext()).deleteRegid(new IPushActionListener() {
      @Override
      public void onStateChanged(int i) {
        if (i != 0) {
          callback.invoke(new PushError(PushErrorCode.UNREGISTER_ERROR, "Vivo push "
                                                                          + "unregister error" + i));
          return;
        }
        setDeviceToken(null);
        callback.invoke();
      }
    });
  }
}
