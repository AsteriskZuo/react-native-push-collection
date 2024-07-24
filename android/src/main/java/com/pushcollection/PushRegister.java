package com.pushcollection;

import com.facebook.react.bridge.Callback;

public interface PushRegister {
  void register(PushConfig config, Callback callback);
  void unregister(Callback callback);
  String getDeviceToken();
}
