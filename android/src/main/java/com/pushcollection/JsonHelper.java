package com.pushcollection;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class JsonHelper {
}

class FcmPushConfigHelper {
  public static FcmPushConfig fromJson(JSONObject json) throws JSONException {
    return new FcmPushConfig(json.getString("senderId"));
  }
  public static JSONObject toJson(FcmPushConfig config) throws JSONException {
    return new JSONObject(FcmPushConfigHelper.toMap(config));
  }
  public static FcmPushConfig fromMap(HashMap json) throws JSONException {
//    return FcmPushConfigHelper.fromJson(new JSONObject(json));
    return new FcmPushConfig((String) json.get("senderId"));
  }
  public static HashMap toMap(FcmPushConfig config) {
    HashMap<String, Object> map = new HashMap<>();
    map.put("type", "fcm");
    map.put("senderId", config.senderId);
    return map;
  }
}

class HuaweiPushConfigHelper {
  public static HuaweiPushConfig fromJson(JSONObject json) throws JSONException {
    return new HuaweiPushConfig(json.getString("appId"));
  }
  public static JSONObject toJson(HuaweiPushConfig config) throws JSONException {
    return new JSONObject(HuaweiPushConfigHelper.toMap(config));
  }
  public static HuaweiPushConfig fromMap(HashMap json) {
    return new HuaweiPushConfig((String) json.get("appId"));
  }
  public static HashMap toMap(HuaweiPushConfig config) {
    HashMap<String, Object> map = new HashMap<>();
    map.put("type", "huawei");
    map.put("appId", config.appId);
    return map;
  }
}

class HonorPushConfigHelper {
  public static HonorPushConfig fromJson(JSONObject json) throws JSONException {
    return new HonorPushConfig(json.getString("appId"));
  }
  public static JSONObject toJson(HonorPushConfig config) throws JSONException {
    return new JSONObject(HonorPushConfigHelper.toMap(config));
  }
  public static HonorPushConfig fromMap(HashMap json) {
    return new HonorPushConfig((String) json.get("appId"));
  }
  public static HashMap toMap(HonorPushConfig config) {
    HashMap<String, Object> map = new HashMap<>();
    map.put("type", "honor");
    map.put("appId", config.appId);
    return map;
  }
}

class XiaomiPushConfigHelper {
  public static XiaomiPushConfig fromJson(JSONObject json) throws JSONException {
    return new XiaomiPushConfig(json.getString("appId"), json.getString("appKey"));
  }
  public static JSONObject toJson(XiaomiPushConfig config) throws JSONException {
    return new JSONObject(XiaomiPushConfigHelper.toMap(config));
  }
  public static XiaomiPushConfig fromMap(HashMap json) {
    return new XiaomiPushConfig((String) json.get("appId"), (String) json.get("appKey"));
  }
  public static HashMap toMap(XiaomiPushConfig config) {
    HashMap<String, Object> map = new HashMap<>();
    map.put("type", "xiaomi");
    map.put("appId", config.appId);
    map.put("appKey", config.appKey);
    return map;
  }
}

class MeizuPushConfigHelper {
  public static MeizuPushConfig fromJson(JSONObject json) throws JSONException {
    return new MeizuPushConfig(json.getString("appId"), json.getString("appKey"));
  }
  public static JSONObject toJson(MeizuPushConfig config) throws JSONException {
    return new JSONObject(MeizuPushConfigHelper.toMap(config));
  }
  public static MeizuPushConfig fromMap(HashMap json) {
    return new MeizuPushConfig((String) json.get("appId"), (String) json.get("appKey"));
  }
  public static HashMap toMap(MeizuPushConfig config) {
    HashMap<String, Object> map = new HashMap<>();
    map.put("type", "meizu");
    map.put("appId", config.appId);
    map.put("appKey", config.appKey);
    return map;
  }
}

class VivoPushConfigHelper {
  public static VivoPushConfig fromJson(JSONObject json) throws JSONException {
    return new VivoPushConfig(json.getString("appId"), json.getString("appKey"));
  }
  public static JSONObject toJson(VivoPushConfig config) throws JSONException {
    return new JSONObject(VivoPushConfigHelper.toMap(config));
  }
  public static VivoPushConfig fromMap(HashMap json) {
    return new VivoPushConfig((String) json.get("appId"), (String) json.get("appKey"));
  }
  public static HashMap toMap(VivoPushConfig config) {
    HashMap<String, Object> map = new HashMap<>();
    map.put("type", "vivo");
    map.put("appId", config.appId);
    map.put("appKey", config.appKey);
    return map;
  }
}

class OppoPushConfigHelper {
  public static OppoPushConfig fromJson(JSONObject json) throws JSONException {
    return new OppoPushConfig(json.getString("appKey"), json.getString("secret"));
  }
  public static JSONObject toJson(OppoPushConfig config) throws JSONException {
    return new JSONObject(OppoPushConfigHelper.toMap(config));
  }
  public static OppoPushConfig fromMap(HashMap json) {
    return new OppoPushConfig((String) json.get("appKey"), (String) json.get("secret"));
  }
  public static HashMap toMap(OppoPushConfig config) {
    HashMap<String, Object> map = new HashMap<>();
    map.put("type", "oppo");
    map.put("appKey", config.appKey);
    map.put("secret", config.secret);
    return map;
  }
}

class PushConfigHelper {
  public static PushConfig fromJson(JSONObject json) throws JSONException {
    String type = json.getString("type");
    switch (type) {
      case "fcm":
        return FcmPushConfigHelper.fromJson(json);
      case "huawei":
        return HuaweiPushConfigHelper.fromJson(json);
      case "honor":
        return HonorPushConfigHelper.fromJson(json);
      case "xiaomi":
        return XiaomiPushConfigHelper.fromJson(json);
      case "meizu":
        return MeizuPushConfigHelper.fromJson(json);
      case "vivo":
        return VivoPushConfigHelper.fromJson(json);
      case "oppo":
        return OppoPushConfigHelper.fromJson(json);
      default:
        throw new JSONException("Unknown push type: " + type);
    }
  }
  public static JSONObject toJson(PushConfig config) throws JSONException {
    switch (config.type) {
      case FCM:
        return FcmPushConfigHelper.toJson((FcmPushConfig) config);
      case HUAWEI:
        return HuaweiPushConfigHelper.toJson((HuaweiPushConfig) config);
      case HONOR:
        return HonorPushConfigHelper.toJson((HonorPushConfig) config);
      case XIAOMI:
        return XiaomiPushConfigHelper.toJson((XiaomiPushConfig) config);
      case MEIZU:
        return MeizuPushConfigHelper.toJson((MeizuPushConfig) config);
      case VIVO:
        return VivoPushConfigHelper.toJson((VivoPushConfig) config);
      case OPPO:
        return OppoPushConfigHelper.toJson((OppoPushConfig) config);
      default:
        throw new JSONException("Unknown push type: " + config.type);
    }
  }
  public static PushConfig fromMap(HashMap json) throws Exception {
    String type = (String) json.get("type");
    switch (type) {
      case "fcm":
        return FcmPushConfigHelper.fromMap(json);
      case "huawei":
        return HuaweiPushConfigHelper.fromMap(json);
      case "honor":
        return HonorPushConfigHelper.fromMap(json);
      case "xiaomi":
        return XiaomiPushConfigHelper.fromMap(json);
      case "meizu":
        return MeizuPushConfigHelper.fromMap(json);
      case "vivo":
        return VivoPushConfigHelper.fromMap(json);
      case "oppo":
        return OppoPushConfigHelper.fromMap(json);
      default:
        throw new Exception("Unknown push type: " + type);
    }
  }
  public static HashMap toMap(PushConfig config) throws Exception {
    switch (config.type) {
      case FCM:
        return FcmPushConfigHelper.toMap((FcmPushConfig) config);
      case HUAWEI:
        return HuaweiPushConfigHelper.toMap((HuaweiPushConfig) config);
      case HONOR:
        return HonorPushConfigHelper.toMap((HonorPushConfig) config);
      case XIAOMI:
        return XiaomiPushConfigHelper.toMap((XiaomiPushConfig) config);
      case MEIZU:
        return MeizuPushConfigHelper.toMap((MeizuPushConfig) config);
      case VIVO:
        return VivoPushConfigHelper.toMap((VivoPushConfig) config);
      case OPPO:
        return OppoPushConfigHelper.toMap((OppoPushConfig) config);
      default:
        throw new Exception("Unknown push type: " + config.type);
    }
  }
}
