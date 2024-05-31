package com.pushcollection;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.pushcollection.impl.HuaweiPushRegister;

import org.json.JSONObject;

public class PushClient implements PushListener {
  private DeviceEventManagerModule.RCTDeviceEventEmitter eventEmitter;
  private static PushClient instance;
  private String deviceToken;
  private PushConfig pushConfig;
  private PushRegister pushRegister;

  public static PushClient getInstance() {
    if (instance == null) {
      instance = new PushClient();
    }
    return instance;
  }

  PushClient() {

  }

  public void init(ReactApplicationContext reactContext) {
    this.eventEmitter = reactContext.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class);
  }

  public String getDeviceToken() {
    return this.deviceToken;
  }

  public void setPushConfig(PushConfig pushConfig) {
    this.pushConfig = pushConfig;
  }

  public PushConfig getPushConfig() {
    return this.pushConfig;
  }

  public void registerPush() {
    if (this.pushConfig instanceof FcmPushConfig) {

    } else if (this.pushConfig instanceof HuaweiPushConfig) {
      pushRegister = new HuaweiPushRegister();
    } else if (this.pushConfig instanceof HonorPushConfig) {

    } else {
      return;
    }
    pushRegister.register(this.pushConfig);
  }

  public void unregisterPush() {
    if (this.pushConfig != null) {
      pushRegister.unregister();
      pushRegister = null;
    }
  }

  public class InnerPushListener implements PushListener {
    @Override
    public void onReceivePushToken(String token) {
      // Implement this method
    }

    @Override
    public void onReceivePushMessage(JSONObject message) {
      // Implement this method
    }
  }

  @Override
  public void onReceivePushToken(String token) {
    this.deviceToken = token;
    this.eventEmitter.emit(Const.onReceivePushToken, token);
  }

  @Override
  public void onReceivePushMessage(JSONObject message) {
    this.eventEmitter.emit(Const.onReceivePushMessage, message);
  }
}
