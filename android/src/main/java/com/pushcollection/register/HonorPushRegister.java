package com.pushcollection.register;

import android.content.Context;
import com.facebook.react.bridge.ReactApplicationContext;
import com.hihonor.push.sdk.HonorPushCallback;
import com.hihonor.push.sdk.HonorPushClient;
import com.pushcollection.PushClient;
import com.pushcollection.PushConfig;
import com.pushcollection.PushRegister;

public class HonorPushRegister implements PushRegister {
  boolean isSupport;
  String token;

  public HonorPushRegister(Context context) {
    this.isSupport = HonorPushClient.getInstance().checkSupportHonorPush(context);
    if (!isSupport) {
      PushClient.getInstance().onError(new Error("Honor Push is not supported"));
      return;
    }
    HonorPushClient.getInstance().init(context, true);
  }

  @Override
  public void register(PushConfig config) {
    if (!isSupport) {
      PushClient.getInstance().onError(new Error("Honor Push is not supported"));
      return;
    }
    HonorPushClient.getInstance().getPushToken(new HonorPushCallback<String>() {
      @Override
      public void onSuccess(String s) {
        token = s;
        if (token != null) {
          PushClient.getInstance().onReceivePushToken(token);
        }
      }

      @Override
      public void onFailure(int i, String s) {
        PushClient.getInstance().onError(new Error(s));
      }
    });
  }

  @Override
  public void unregister() {
    if (!isSupport) {
      PushClient.getInstance().onError(new Error("Honor Push is not supported"));
      return;
    }
    HonorPushClient.getInstance().deletePushToken(new HonorPushCallback<Void>() {
      @Override
      public void onSuccess(Void aVoid) {
        token = null;
      }

      @Override
      public void onFailure(int i, String s) {
        PushClient.getInstance().onError(new Error(s));
      }
    });
  }

  @Override
  public String getDeviceToken() {
    return token;
  }
}
