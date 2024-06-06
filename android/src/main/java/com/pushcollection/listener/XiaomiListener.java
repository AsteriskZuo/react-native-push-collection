package com.pushcollection.listener;

import android.content.Context;
import com.pushcollection.PushClient;
import com.pushcollection.ToMapUitl;
import com.xiaomi.mipush.sdk.ErrorCode;
import com.xiaomi.mipush.sdk.MiPushClient;
import com.xiaomi.mipush.sdk.MiPushCommandMessage;
import com.xiaomi.mipush.sdk.MiPushMessage;
import com.xiaomi.mipush.sdk.PushMessageReceiver;
import java.util.List;

public class XiaomiListener extends PushMessageReceiver {
  @Override
  public void onNotificationMessageClicked(Context context, MiPushMessage miPushMessage) {
    PushClient.getInstance().onClickedNotification(ToMapUitl.toMap(miPushMessage));
  }

  @Override
  public void onNotificationMessageArrived(Context context, MiPushMessage miPushMessage) {
    PushClient.getInstance().onReceivePushMessage(ToMapUitl.toMap(miPushMessage));
  }

  @Override
  public void onCommandResult(Context context, MiPushCommandMessage message) {
    String command = message.getCommand();
    List<String> arguments = message.getCommandArguments();
    String cmdArg1 = ((arguments != null && arguments.size() > 0) ? arguments.get(0) : null);
    String cmdArg2 = ((arguments != null && arguments.size() > 1) ? arguments.get(1) : null);
    if (MiPushClient.COMMAND_REGISTER.equals(command)) {
      if (message.getResultCode() == ErrorCode.SUCCESS) {
        PushClient.getInstance().onReceivePushToken(cmdArg1);
      }
    }
  }

  @Override
  public void onReceiveRegisterResult(Context context, MiPushCommandMessage message) {
    String command = message.getCommand();
    List<String> arguments = message.getCommandArguments();
    String cmdArg1 = ((arguments != null && arguments.size() > 0) ? arguments.get(0) : null);
    String cmdArg2 = ((arguments != null && arguments.size() > 1) ? arguments.get(1) : null);
    if (MiPushClient.COMMAND_REGISTER.equals(command)) {
      if (message.getResultCode() == ErrorCode.SUCCESS) {
        PushClient.getInstance().onReceivePushToken(cmdArg1);
      }
    }
  }
}
