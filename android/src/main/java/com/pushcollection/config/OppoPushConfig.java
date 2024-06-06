package com.pushcollection.config;

import com.pushcollection.PushConfig;
import com.pushcollection.PushType;

public class OppoPushConfig extends PushConfig {

  public String appKey;
  public String secret;

  public OppoPushConfig(String appKey, String secret) {
    this.type = PushType.OPPO;
    this.appKey = appKey;
    this.secret = secret;
  }
}
