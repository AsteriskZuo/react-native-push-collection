package com.pushcollection;

public abstract class PushConfig {
  PushDeviceType type;
}

class FcmPushConfig extends PushConfig {
  String senderId;

  public FcmPushConfig(String senderId) {
    this.type = PushDeviceType.FCM;
    this.senderId = senderId;
  }
}

class HuaweiPushConfig extends PushConfig {
  String appId;

  public HuaweiPushConfig(String appId) {
    this.type = PushDeviceType.HUAWEI;
    this.appId = appId;
  }
}

class HonorPushConfig extends PushConfig {
  String appId;

  public HonorPushConfig(String appId) {
    this.type = PushDeviceType.HONOR;
    this.appId = appId;
  }
}

class XiaomiPushConfig extends PushConfig {
  String appId;
  String appKey;

  public XiaomiPushConfig(String appId, String appKey) {
    this.type = PushDeviceType.XIAOMI;
    this.appId = appId;
    this.appKey = appKey;
  }
}

class MeizuPushConfig extends PushConfig {
  String appId;
  String appKey;

  public MeizuPushConfig(String appId, String appKey) {
    this.type = PushDeviceType.MEIZU;
    this.appId = appId;
    this.appKey = appKey;
  }
}

class VivoPushConfig extends PushConfig {
  String appId;
  String appKey;

  public VivoPushConfig(String appId, String appKey) {
    this.type = PushDeviceType.VIVO;
    this.appId = appId;
    this.appKey = appKey;
  }
}

class OppoPushConfig extends PushConfig {

  String appKey;
  String secret;

  public OppoPushConfig(String appKey, String secret) {
    this.type = PushDeviceType.OPPO;
    this.appKey = appKey;
    this.secret = secret;
  }
}
