package com.pushcollection;

import com.facebook.react.bridge.Callback;

public interface PushRegister {
  void prepare(Callback callback);
  void register(Callback callback);
  void unregister(Callback callback);
  String getDeviceToken();
}
