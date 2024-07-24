package com.pushcollection;

import android.content.Context;
import android.util.Log;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.pushcollection.config.FcmPushConfig;
import com.pushcollection.config.HonorPushConfig;
import com.pushcollection.config.HuaweiPushConfig;
import com.pushcollection.config.MeizuPushConfig;
import com.pushcollection.config.OppoPushConfig;
import com.pushcollection.config.VivoPushConfig;
import com.pushcollection.config.XiaomiPushConfig;
import com.pushcollection.register.FcmPushRegister;
import com.pushcollection.register.HonorPushRegister;
import com.pushcollection.register.HuaweiPushRegister;
import com.pushcollection.register.MeizuPushRegister;
import com.pushcollection.register.OppoPushRegister;
import com.pushcollection.register.VivoPushRegister;
import com.pushcollection.register.XiaomiPushRegister;
import java.util.HashMap;
import java.util.Map;

public class PushClient implements PushListener {
  private static final String TAG = "PushClient";
  private static PushClient instance;
  private PushConfig pushConfig;
  private PushRegister pushRegister;
  private ReactApplicationContext reactContext;
  private String token;

  public static PushClient getInstance() {
    if (instance == null) {
      instance = new PushClient();
    }
    return instance;
  }

  public void initialize(ReactApplicationContext reactContext) {
    Log.i(TAG, "PushClient init");
    this.reactContext = reactContext;
  }

  private DeviceEventManagerModule.RCTDeviceEventEmitter getEventEmitter() {
    return this.reactContext.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class);
  }

  public void setConfig(String pushType, Callback callback) {
    ThreadUtil.mainThreadExecute(() -> {
      if (pushType.contentEquals("huawei")) {
        this.pushConfig = new HuaweiPushConfig(BuildConfig.HUAWEI_PUSH_APPID);
      } else if (pushType.contentEquals("honor")) {
        this.pushConfig = new HonorPushConfig(BuildConfig.HONOR_PUSH_APPID);
      } else if (pushType.contentEquals("xiaomi")) {
        this.pushConfig = new XiaomiPushConfig(BuildConfig.MI_PUSH_APPID, BuildConfig.MI_PUSH_APPKEY);
      } else if (pushType.contentEquals("vivo")) {
        this.pushConfig = new VivoPushConfig(BuildConfig.VIVO_PUSH_APPID, BuildConfig.VIVO_PUSH_APPKEY);
      } else if (pushType.contentEquals("meizu")) {
        this.pushConfig = new MeizuPushConfig(BuildConfig.MEIZU_PUSH_APPID, BuildConfig.MEIZU_PUSH_APPKEY);
      } else if (pushType.contentEquals("oppo")) {
        this.pushConfig = new OppoPushConfig(BuildConfig.OPPO_PUSH_APPKEY, BuildConfig.OPPO_PUSH_APPSECRET);
      } else if (pushType.contentEquals("fcm")) {
        this.pushConfig = new FcmPushConfig(BuildConfig.FCM_SENDERID);
      }

      if (this.pushConfig == null) {
        callback.invoke(new PushError(PushErrorCode.NO_SUPPROT_ERROR, "PushType is not supported"));
        return;
      }

      try {
        if (this.pushConfig instanceof FcmPushConfig) {
          this.pushRegister = new FcmPushRegister(this.getApplicationContext());
        } else if (this.pushConfig instanceof HuaweiPushConfig) {
          this.pushRegister = new HuaweiPushRegister(this.getApplicationContext(), callback);
          return;
        } else if (this.pushConfig instanceof HonorPushConfig) {
          this.pushRegister = new HonorPushRegister(this.getApplicationContext());
        } else if (this.pushConfig instanceof XiaomiPushConfig) {
          this.pushRegister = new XiaomiPushRegister(this.getApplicationContext());
        } else if (this.pushConfig instanceof VivoPushConfig) {
          this.pushRegister = new VivoPushRegister(this.getApplicationContext(), callback);
          return;
        } else if (this.pushConfig instanceof MeizuPushConfig) {
          this.pushRegister = new MeizuPushRegister(this.getApplicationContext());
        } else if (this.pushConfig instanceof OppoPushConfig) {
          this.pushRegister = new OppoPushRegister(this.getApplicationContext(), callback);
          return;
        }

        if (this.pushRegister == null) {
          callback.invoke(new PushError(PushErrorCode.INIT_ERROR, "Construct PushRegister failed"));
          return;
        }
        callback.invoke();
      } catch (PushError e) {
        callback.invoke(e);
      }
    });
  }

  public String getDeviceToken() {
    String ret = this.pushRegister.getDeviceToken();
    if (ret != null) {
      return ret;
    }
    return token;
  }

  public PushConfig getPushConfig() { return this.pushConfig; }

  public Context getApplicationContext() { return this.reactContext.getApplicationContext(); }

  public void registerPush(Callback callback) {
    if (this.pushRegister != null) {
      ThreadUtil.mainThreadExecute(() -> {
        this.pushRegister.register(this.pushConfig, new Callback() {
          @Override
          public void invoke(Object... objects) {
            if (objects.length > 0 && objects[0] instanceof String) {
              token = (String)objects[0];
            }
            callback.invoke(objects);
          };
        });
      });
    }
  }

  public void unregisterPush(Callback callback) {
    if (this.pushRegister != null) {
      ThreadUtil.mainThreadExecute(() -> {
        pushRegister.unregister(new Callback() {
          @Override
          public void invoke(Object... objects) {
            if (objects.length == 0) {
              token = null;
            }
            callback.invoke(objects);
          }
        });
      });
    }
  }

  public void sendEvent(String methodType, Map<String, Object> params) {
    DeviceEventManagerModule.RCTDeviceEventEmitter eventEmitter = this.getEventEmitter();
    if (eventEmitter != null) {
      ThreadUtil.mainThreadExecute(() -> {
        params.put("type", methodType);
        ReturnUtil.onEvent(eventEmitter, Const.onNativeNotification, params);
      });
    }
  }

  @Override
  public void onReceivePushToken(String token) {
    this.token = token;
    HashMap<String, Object> map = new HashMap<>();
    map.put("token", token);
    this.sendEvent(Const.onReceivePushToken, map);
  }

  @Override
  public void onReceivePushMessage(Map<?, ?> message) {
    HashMap<String, Object> map = new HashMap<>();
    map.put("message", message);
    this.sendEvent(Const.onReceivePushMessage, map);
  }

  @Override
  public void onClickedNotification(Map<?, ?> message) {
    HashMap<String, Object> map = new HashMap<>();
    map.put("message", message);
    this.sendEvent(Const.onNotificationClick, map);
  }

  @Override
  public void onAppBackground() {
    this.sendEvent(Const.onAppBackground, new HashMap<>());
  }

  @Override
  public void onAppForeground() {
    this.sendEvent(Const.onAppForeground, new HashMap<>());
  }

  @Override
  public void onError(Throwable message) {
    HashMap<String, Object> map = new HashMap<>();
    map.put("error", message);
    this.sendEvent(Const.onError, map);
  }
}
