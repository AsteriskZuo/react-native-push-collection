package com.pushcollection.listener;

import android.content.Context;
import android.content.Intent;
import com.meizu.cloud.pushsdk.MzPushMessageReceiver;
import com.meizu.cloud.pushsdk.handler.MzPushMessage;
import com.meizu.cloud.pushsdk.notification.PushNotificationBuilder;
import com.meizu.cloud.pushsdk.platform.message.PushSwitchStatus;
import com.meizu.cloud.pushsdk.platform.message.RegisterStatus;
import com.meizu.cloud.pushsdk.platform.message.SubAliasStatus;
import com.meizu.cloud.pushsdk.platform.message.SubTagsStatus;
import com.meizu.cloud.pushsdk.platform.message.UnRegisterStatus;
import com.pushcollection.PushClient;
import com.pushcollection.ToMapUitl;

public class MeizuListener extends MzPushMessageReceiver {

  @Override
  public void onMessage(Context context, String s) {}

  @Override
  public void onMessage(Context context, Intent intent) {}

  @Override
  public void onMessage(Context context, String s, String s1) {}

  @Override
  public void onNotificationClicked(Context context, MzPushMessage mzPushMessage) {
    PushClient.getInstance().onClickedNotification(ToMapUitl.toMap(mzPushMessage));
  }

  @Override
  public void onNotificationArrived(Context context, MzPushMessage mzPushMessage) {
    PushClient.getInstance().onReceivePushMessage(ToMapUitl.toMap(mzPushMessage));
  }

  @Override
  public void onNotificationDeleted(Context context, MzPushMessage mzPushMessage) {}

  @Override
  public void onNotifyMessageArrived(Context context, String s) {}

  @Override
  public void onUpdateNotificationBuilder(PushNotificationBuilder pushNotificationBuilder) {}

  @Override
  public void onRegisterStatus(Context context, RegisterStatus registerStatus) {}

  @Override
  public void onUnRegisterStatus(Context context, UnRegisterStatus unRegisterStatus) {}

  @Override
  public void onPushStatus(Context context, PushSwitchStatus pushSwitchStatus) {}

  @Override
  public void onSubTagsStatus(Context context, SubTagsStatus subTagsStatus) {}

  @Override
  public void onSubAliasStatus(Context context, SubAliasStatus subAliasStatus) {}
}
