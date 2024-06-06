package com.pushcollection.listener;

import com.huawei.hms.push.HmsMessageService;
import com.huawei.hms.push.RemoteMessage;
import com.pushcollection.PushClient;

public class HuaweiListener extends HmsMessageService {
  @Override
  public void onMessageReceived(RemoteMessage remoteMessage) {
    PushClient.getInstance().onReceivePushMessage(remoteMessage.getDataOfMap());
  }

  @Override
  public void onNewToken(String s) {
    PushClient.getInstance().onReceivePushToken(s);
  }

  @Override
  public void onTokenError(Exception e) {
    PushClient.getInstance().onError(new Error(e.getMessage()));
  }
}
