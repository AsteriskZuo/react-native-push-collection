package com.pushcollection.config;

import com.pushcollection.PushConfig;
import com.pushcollection.PushType;

public class HuaweiPushConfig extends PushConfig {
  public String appId;

  public HuaweiPushConfig(String appId) {
    this.type = PushType.HUAWEI;
    this.appId = appId;
  }
}
