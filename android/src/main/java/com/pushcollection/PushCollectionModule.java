package com.pushcollection;

import android.util.Log;
import androidx.annotation.NonNull;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.module.annotations.ReactModule;
import java.util.HashMap;

@ReactModule(name = PushCollectionModule.NAME)
public class PushCollectionModule extends ReactContextBaseJavaModule {
  public static final String NAME = "PushCollection";
  private static final String TAG = "PushCollectionModule";

  public PushCollectionModule(ReactApplicationContext reactContext) { super(reactContext); }

  @Override
  public void initialize() {
    super.initialize();
    PushClient.getInstance().initialize(this.getReactApplicationContext());
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
  public void init(ReadableMap params, Promise promise) {
    HashMap<String, Object> hashParams = params.toHashMap();
    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
      String platform = (String)hashParams.getOrDefault("platform", null);
      String pushType = (String)hashParams.getOrDefault("pushType", null);

      if (platform == null) {
        ReturnUtil.fail(promise, new Error("Platform is required"));
        return;
      }
      if (!platform.equals("android")) {
        ReturnUtil.fail(promise, new Error("Platform is not supported"));
        return;
      }
      Log.i(TAG, "dev:deviceType" + pushType);
      boolean ret = PushClient.getInstance().setConfig(pushType);
      if (ret) {
        ReturnUtil.success(promise, null);
      } else {
        ReturnUtil.fail(promise, new Error("Device type is not supported"));
      }
    } else {
      ReturnUtil.fail(promise, new Error("Android version is not supported"));
    }
  }

  @ReactMethod
  public void registerPush(Promise promise) throws Exception {
    PushClient.getInstance().registerPush();
    ReturnUtil.success(promise, null);
  }

  @ReactMethod
  public void unregisterPush(Promise promise) {
    PushClient.getInstance().unregisterPush();
    ReturnUtil.success(promise, null);
  }

  @ReactMethod
  public void getPushConfig(Promise promise) {
    PushConfig ret = PushClient.getInstance().getPushConfig();
    if (ret != null) {
      try {
        ReturnUtil.success(promise, PushConfigHelper.toMap(ret));
      } catch (Exception e) {
        ReturnUtil.fail(promise, e);
      }
    } else {
      ReturnUtil.success(promise, null);
    }
  }

  @ReactMethod
  public void getToken(Promise promise) {
    ReturnUtil.success(promise, PushClient.getInstance().getDeviceToken());
  }

  @ReactMethod
  public void addListener(String methodType) {
    // Keep: Required for RN built in Event Emitter Calls.
  }

  @ReactMethod
  public void removeListeners(int count) {
    // Keep: Required for RN built in Event Emitter Calls.
  }

  @ReactMethod
  public void removeAllListeners(String methodType) {
    // Keep: Required for RN built in Event Emitter Calls.
  }
}
