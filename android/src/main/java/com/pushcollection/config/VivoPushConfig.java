package com.pushcollection.config;

import com.pushcollection.PushConfig;
import com.pushcollection.PushType;

public class VivoPushConfig extends PushConfig {
  public String appId;
  public String appKey;

  public VivoPushConfig(String appId, String appKey) {
    this.type = PushType.VIVO;
    this.appId = appId;
    this.appKey = appKey;
  }
}
