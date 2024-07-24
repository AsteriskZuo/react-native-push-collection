package com.pushcollection;

import android.util.Log;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import java.util.Iterator;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONObject;

public class ReturnUtil {

  public static void success(Promise promise, Object data) {
    if (data instanceof Map) {
      WritableMap ret = convertJsonObjectToMap(ToJsonUtil.toJson((Map<?, ?>)data));
      promise.resolve(ret);
    } else if (data instanceof String) {
      promise.resolve(data);
    } else {
      promise.resolve(null);
    }
  }
  public static void fail(Promise promise, int code, String message) {
    ReturnUtil.fail(promise, new PushError(PushErrorCode.fromCode(code), message));
  }
  public static void fail(Promise promise, Throwable e) { promise.reject(e, convertThrowableToMap(e)); }
  public static void onEvent(DeviceEventManagerModule.RCTDeviceEventEmitter eventEmitter, String eventName,
                             Object data) {
    if (data instanceof Map) {
      WritableMap ret = convertJsonObjectToMap(ToJsonUtil.toJson((Map<?, ?>)data));
      eventEmitter.emit(eventName, ret);
    } else if (data instanceof String) {
      eventEmitter.emit(eventName, data);
    } else {
      eventEmitter.emit(eventName, null);
    }
  }
  private static WritableMap convertJsonObjectToMap(JSONObject jsonObject) {
    WritableMap map = Arguments.createMap();
    try {
      Iterator<String> iterator = jsonObject.keys();
      while (iterator.hasNext()) {
        String key = iterator.next();
        Object value = jsonObject.get(key);
        if (value instanceof JSONObject) {
          map.putMap(key, convertJsonObjectToMap((JSONObject)value));
        } else if (value instanceof JSONArray) {
          map.putArray(key, convertJsonToArray((JSONArray)value));
          if (("option_values").equals(key)) {
            map.putArray("options", convertJsonToArray((JSONArray)value));
          }
        } else if (value instanceof Boolean) {
          map.putBoolean(key, (Boolean)value);
        } else if (value instanceof Integer) {
          map.putInt(key, (Integer)value);
        } else if (value instanceof Double) {
          map.putDouble(key, (Double)value);
        } else if (value instanceof String) {
          map.putString(key, (String)value);
        } else {
          map.putString(key, value.toString());
        }
      }
    } catch (Exception e) {
      Log.e("ToJsonUtil", "convertJsonObjectToMap error:" + e);
    }
    return map;
  }

  private static WritableArray convertJsonToArray(JSONArray jsonArray) {
    WritableArray array = Arguments.createArray();

    try {
      for (int i = 0; i < jsonArray.length(); i++) {
        Object value = jsonArray.get(i);
        if (value instanceof JSONObject) {
          array.pushMap(convertJsonObjectToMap((JSONObject)value));
        } else if (value instanceof JSONArray) {
          array.pushArray(convertJsonToArray((JSONArray)value));
        } else if (value instanceof Boolean) {
          array.pushBoolean((Boolean)value);
        } else if (value instanceof Integer) {
          array.pushInt((Integer)value);
        } else if (value instanceof Double) {
          array.pushDouble((Double)value);
        } else if (value instanceof String) {
          array.pushString((String)value);
        } else {
          array.pushString(value.toString());
        }
      }
    } catch (Exception e) {
      Log.e("ToJsonUtil", "convertJsonToArray error:" + e);
    }
    return array;
  }

  private static WritableMap convertThrowableToMap(Throwable throwable) {
    WritableMap map = Arguments.createMap();
    if (throwable instanceof PushError) {
      PushError error = (PushError)throwable;
      map.putInt("code", error.getCode());
    }
    map.putString("message", throwable.getMessage());
    return map;
  }
}
