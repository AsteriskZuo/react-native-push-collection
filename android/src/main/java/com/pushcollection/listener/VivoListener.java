package com.pushcollection.listener;

import android.content.Context;
import com.pushcollection.PushClient;
import com.pushcollection.ToMapUitl;
import com.vivo.push.model.UPSNotificationMessage;
import com.vivo.push.sdk.OpenClientPushMessageReceiver;

public class VivoListener extends OpenClientPushMessageReceiver {
  @Override
  public void onReceiveRegId(Context context, String regId) {
    PushClient.getInstance().onReceivePushToken(regId);
  }

  @Override
  public void onNotificationMessageClicked(Context context, UPSNotificationMessage msg) {
    PushClient.getInstance().onClickedNotification(ToMapUitl.toMap(msg));
  }

  @Override
  public void onForegroundMessageArrived(UPSNotificationMessage msg) {
    PushClient.getInstance().onReceivePushMessage(ToMapUitl.toMap(msg));
  }
}
