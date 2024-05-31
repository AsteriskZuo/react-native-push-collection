package com.pushcollection;

import androidx.annotation.NonNull;

import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.module.annotations.ReactModule;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

@ReactModule(name = PushCollectionModule.NAME)
public class PushCollectionModule extends ReactContextBaseJavaModule {
  public static final String NAME = "PushCollection";

  public PushCollectionModule(ReactApplicationContext reactContext) {
    super(reactContext);
    PushClient.getInstance().init(reactContext);
  }

  @Override
  @NonNull
  public String getName() {
    return NAME;
  }


  // Example method
  // See https://reactnative.dev/docs/native-modules-android
  @ReactMethod
  public void multiply(double a, double b, Promise promise) {
    promise.resolve(a * b);
  }

  @ReactMethod
  public void init(Promise promise) {
    promise.resolve(null);
  }

  @ReactMethod
  public void registerPush(ReadableMap params, Promise promise) throws Exception {
    HashMap<String, Object> hashParams = params.toHashMap();
    PushConfig pushConfig = PushConfigHelper.fromMap(hashParams);
    PushClient.getInstance().setPushConfig(pushConfig);
    PushClient.getInstance().registerPush();
    promise.resolve(null);
  }

  @ReactMethod
  public void unregisterPush(Promise promise) {
    PushClient.getInstance().setPushConfig(null);
    PushClient.getInstance().unregisterPush();
    promise.resolve(null);
  }

  @ReactMethod
  public void getPushConfig(Promise promise) throws Exception {
    PushConfig ret = PushClient.getInstance().getPushConfig();
    if (ret != null) {
      promise.resolve(PushConfigHelper.toMap(ret));
    } else {
      promise.resolve(null);
    }
  }

  @ReactMethod
  public void getToken(Promise promise) {
    promise.resolve(PushClient.getInstance().getDeviceToken());
  }
}
