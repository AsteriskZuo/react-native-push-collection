package com.pushcollection.config;

import com.pushcollection.PushConfig;
import com.pushcollection.PushType;

public class HonorPushConfig extends PushConfig {
  public String appId;

  public HonorPushConfig(String appId) {
    this.type = PushType.HONOR;
    this.appId = appId;
  }
}
