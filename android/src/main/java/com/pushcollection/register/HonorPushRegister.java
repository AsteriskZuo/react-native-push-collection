package com.pushcollection.register;

import com.facebook.react.bridge.Callback;
import com.hihonor.push.sdk.HonorPushCallback;
import com.hihonor.push.sdk.HonorPushClient;
import com.pushcollection.BasicPushRegister;
import com.pushcollection.PushClient;
import com.pushcollection.PushError;
import com.pushcollection.PushErrorCode;

public class HonorPushRegister extends BasicPushRegister {
  boolean isSupport;
  public HonorPushRegister(PushClient client, Callback callback) {
    super(client);
    isSupport = HonorPushClient.getInstance().checkSupportHonorPush(client.getApplicationContext());
    if (!isSupport) {
      callback.invoke(new PushError(PushErrorCode.INIT_ERROR, "Honor init is failed."));
      return;
    }
    HonorPushClient.getInstance().init(client.getApplicationContext(), true);
    callback.invoke();
  }

  @Override
  public void prepare(Callback callback) {
    HonorPushClient.getInstance().turnOnNotificationCenter(new HonorPushCallback<Void>() {
      @Override
      public void onSuccess(Void unused) {
        callback.invoke();
      }

      @Override
      public void onFailure(int i, String s) {
        callback.invoke(new PushError(PushErrorCode.PREPARE_ERROR, "{"
                                                                     + "\"code\":" + i + ",\"message\":" + s + "}"));
      }
    });
  }

  @Override
  public void register(Callback callback) {
    if (!isSupport) {
      callback.invoke(new PushError(PushErrorCode.NO_SUPPROT_ERROR, "Honor Push is not supported"));
      return;
    }

    String t = getDeviceToken();
    if (t == null || t.contentEquals("")) {
      HonorPushClient.getInstance().getPushToken(new HonorPushCallback<String>() {
        @Override
        public void onSuccess(String s) {
          setDeviceToken(s);
          callback.invoke(getDeviceToken());
        }

        @Override
        public void onFailure(int i, String s) {
          callback.invoke(new PushError(PushErrorCode.REGISTER_ERROR, "{"
                                                                        + "\"code\":" + i + ",\"message\":" + s + "}"));
        }
      });
    } else {
      callback.invoke(t);
    }
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
        setDeviceToken(null);
        callback.invoke();
      }

      @Override
      public void onFailure(int i, String s) {
        callback.invoke(new PushError(PushErrorCode.UNREGISTER_ERROR, "{"
                                                                        + "\"code\":" + i + ",\"message\":" + s + "}"));
      }
    });
  }
}
