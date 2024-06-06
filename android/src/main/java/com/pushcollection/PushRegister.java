package com.pushcollection;

import android.content.Context;
import com.huawei.hms.common.ApiException;

public interface PushRegister {
  void register(PushConfig config);
  void unregister();
  String getDeviceToken();
}
