package com.pushcollection;

public interface PushRegister {
  void register(PushConfig config);
  void unregister();
  String getDeviceToken();
}
