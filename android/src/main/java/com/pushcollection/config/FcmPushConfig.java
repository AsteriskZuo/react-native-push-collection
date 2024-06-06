package com.pushcollection.config;

import com.pushcollection.PushConfig;
import com.pushcollection.PushType;

public class FcmPushConfig extends PushConfig {
  public String senderId;

  public FcmPushConfig(String senderId) {
    this.type = PushType.FCM;
    this.senderId = senderId;
  }
}
