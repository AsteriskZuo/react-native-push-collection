package com.pushcollection;

import android.util.Log;
import androidx.annotation.NonNull;
import com.facebook.react.bridge.Callback;
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
    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
      String platform = (String)hashParams.get("platform");
      String pushType = (String)hashParams.get("pushType");

      if (pushType == null) {
        ReturnUtil.fail(promise, new PushError(PushErrorCode.PARAM_ERROR, "PushType param is required"));
        return;
      }
      if (platform == null) {
        ReturnUtil.fail(promise, new PushError(PushErrorCode.PARAM_ERROR, "Platform param is required"));
        return;
      }
      if (!platform.equals("android")) {
        ReturnUtil.fail(promise, new PushError(PushErrorCode.NO_SUPPROT_ERROR, "Platform is not supported"));
        return;
      }
      Log.i(TAG, "dev:deviceType" + pushType);

      PushClient.getInstance().init(pushType, objects -> {
        if (objects.length > 0 && objects[0] instanceof PushError) {
          ReturnUtil.fail(promise, (PushError)objects[0]);
        } else {
          ReturnUtil.success(promise, null);
        }
      });
    } else {
      ReturnUtil.fail(promise, new PushError(PushErrorCode.NO_SUPPROT_ERROR, "Android version is not supported"));
    }
  }

  @ReactMethod
  public void prepare(ReadableMap params, Promise promise) throws Exception {
    PushClient.getInstance().prepare(new Callback() {
      @Override
      public void invoke(Object... objects) {
        if (objects.length > 0 && objects[0] instanceof PushError) {
          ReturnUtil.fail(promise, (PushError)objects[0]);
        } else {
          ReturnUtil.success(promise, null);
        }
      }
    });
  }

  @ReactMethod
  public void registerPush(ReadableMap params, Promise promise) throws Exception {
    PushClient.getInstance().registerPush(objects -> {
      if (objects.length > 0) {
        if (objects[0] instanceof PushError) {
          ReturnUtil.fail(promise, (PushError)objects[0]);
        } else if (objects[0] instanceof String) {
          ReturnUtil.success(promise, (String)objects[0]);
        } else if (objects[0] instanceof Throwable) {
          ReturnUtil.fail(promise, (Throwable)objects[0]);
        } else {
          ReturnUtil.success(promise, null);
        }
      } else {
        ReturnUtil.success(promise, null);
      }
    });
  }

  @ReactMethod
  public void unregisterPush(Promise promise) {
    PushClient.getInstance().unregisterPush(objects -> {
      if (objects.length > 0 && objects[0] instanceof PushError) {
        ReturnUtil.fail(promise, (PushError)objects[0]);
      } else {
        ReturnUtil.success(promise, null);
      }
    });
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
  public void getTokenFlow(ReadableMap params, Promise promise) {
    PushClient.getInstance().getTokenFlow(promise);
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
