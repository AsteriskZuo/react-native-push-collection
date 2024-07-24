package com.pushcollection.register;

import android.content.Context;
import com.facebook.react.bridge.Callback;
import com.hihonor.push.sdk.HonorPushCallback;
import com.hihonor.push.sdk.HonorPushClient;
import com.pushcollection.PushConfig;
import com.pushcollection.PushError;
import com.pushcollection.PushErrorCode;
import com.pushcollection.PushRegister;

public class HonorPushRegister implements PushRegister {
  boolean isSupport;
  String token;

  public HonorPushRegister(Context context) throws PushError {
    this.isSupport = HonorPushClient.getInstance().checkSupportHonorPush(context);
    if (!isSupport) {
      throw new PushError(PushErrorCode.INIT_ERROR, "Honor init is failed.");
    }
    HonorPushClient.getInstance().init(context, true);
  }

  @Override
  public void register(PushConfig config, Callback callback) {
    if (!isSupport) {
      callback.invoke(new PushError(PushErrorCode.NO_SUPPROT_ERROR, "Honor Push is not supported"));
      return;
    }
    HonorPushClient.getInstance().getPushToken(new HonorPushCallback<String>() {
      @Override
      public void onSuccess(String s) {
        token = s;
        callback.invoke(token);
      }

      @Override
      public void onFailure(int i, String s) {
        callback.invoke(new PushError(PushErrorCode.REGISTER_ERROR, "{"
                                                                      + "\"code\":" + i + ",\"message\":" + s + "}"));
      }
    });
  }

  @Override
  public void unregister(Callback callback) {
    if (!isSupport) {
      callback.invoke(new PushError(PushErrorCode.NO_SUPPROT_ERROR, "Honor Push is not supported"));
      return;
    }
    HonorPushClient.getInstance().deletePushToken(new HonorPushCallback<Void>() {
      @Override
      public void onSuccess(Void aVoid) {
        token = null;
        callback.invoke();
      }

      @Override
      public void onFailure(int i, String s) {
        callback.invoke(new PushError(PushErrorCode.UNREGISTER_ERROR, "{"
                                                                        + "\"code\":" + i + ",\"message\":" + s + "}"));
      }
    });
  }

  @Override
  public String getDeviceToken() {
    return token;
  }
}
