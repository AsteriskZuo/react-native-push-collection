package com.pushcollection.register;

import com.facebook.react.bridge.Callback;
import com.pushcollection.BasicPushRegister;
import com.pushcollection.PushClient;
import com.pushcollection.PushError;
import com.pushcollection.PushErrorCode;
import com.pushcollection.config.XiaomiPushConfig;
import com.xiaomi.mipush.sdk.MiPushClient;

public class XiaomiPushRegister extends BasicPushRegister {
  String token;

  public XiaomiPushRegister(PushClient client) { super(client); }

  @Override
  public void prepare(Callback callback) {
    MiPushClient.enablePush(getContext());
    MiPushClient.turnOnPush(getContext(), new MiPushClient.UPSTurnCallBack() {
      @Override
      public void onResult(MiPushClient.CodeResult codeResult) {
        if (codeResult.getResultCode() == 0) {
          callback.invoke();
        } else {
          callback.invoke(new PushError(PushErrorCode.PREPARE_ERROR, "Mi push prepare is failed."));
        }
      }
    });
  }

  @Override
  public void register(Callback callback) {
    setDeviceToken(MiPushClient.getRegId(getContext()));
    String t = getDeviceToken();
    if (t == null || t.contentEquals("")) {
      XiaomiPushConfig f = (XiaomiPushConfig)getPushConfig();
      MiPushClient.registerPush(getContext(), f.appId, f.appKey);
      callback.invoke();
    } else {
      callback.invoke(t);
    }
  }

  @Override
  public void unregister(Callback callback) {
    MiPushClient.unregisterPush(getContext());
    setDeviceToken(null);
    callback.invoke();
  }

  @Override
  public String getDeviceToken() {
    String t = super.getDeviceToken();
    if (t == null || t.contentEquals("")) {
      setDeviceToken(MiPushClient.getRegId(getContext()));
    }
    return super.getDeviceToken();
  }
}
