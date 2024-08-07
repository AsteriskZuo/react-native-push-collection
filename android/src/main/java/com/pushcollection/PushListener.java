package com.pushcollection;

import java.util.Map;
import org.json.JSONObject;

public interface PushListener {
  void onReceivePushToken(String token);
  void onReceivePushMessage(Map<?, ?> message);
  void onClickedNotification(Map<?, ?> message);
  void onAppForeground();
  void onAppBackground();
  void onError(PushError message);
}
