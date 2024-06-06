package com.pushcollection.config;

import com.pushcollection.PushConfig;
import com.pushcollection.PushType;

public class MeizuPushConfig extends PushConfig {
  public String appId;
  public String appKey;

  public MeizuPushConfig(String appId, String appKey) {
    this.type = PushType.MEIZU;
    this.appId = appId;
    this.appKey = appKey;
  }
}
