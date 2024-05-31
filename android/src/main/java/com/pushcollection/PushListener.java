package com.pushcollection;

import org.json.JSONObject;

public interface PushListener {
  void onReceivePushToken(String token);
  void onReceivePushMessage(JSONObject message);
}
