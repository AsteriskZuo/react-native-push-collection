package com.pushcollection.config;

import com.pushcollection.PushConfig;
import com.pushcollection.PushType;

public class XiaomiPushConfig extends PushConfig {
  public String appId;
  public String appKey;

  public XiaomiPushConfig(String appId, String appKey) {
    this.type = PushType.XIAOMI;
    this.appId = appId;
    this.appKey = appKey;
  }
}
