package com.pushcollection.listener;

import com.hihonor.push.sdk.HonorMessageService;
import com.hihonor.push.sdk.HonorPushDataMsg;
import com.pushcollection.PushClient;
import com.pushcollection.ToMapUitl;

public class HonorListener extends HonorMessageService {

  @Override
  public void onNewToken(String token) {
    PushClient.getInstance().onReceivePushToken(token);
  }

  @Override
  public void onMessageReceived(HonorPushDataMsg honorPushDataMsg) {
    PushClient.getInstance().onReceivePushMessage(ToMapUitl.toMap(honorPushDataMsg));
  }
}
