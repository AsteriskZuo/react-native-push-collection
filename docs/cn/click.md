# 介绍

除了 小米、vivo、魅族之外，华为、荣耀、oppo、fcm 没有直接的提供点击回调通知。

## 荣耀

#### 荣耀推送通知点击事件处理方案

与华为推送类似，荣耀推送也需要通过自定义 `Activity` 来处理点击事件。以下是具体实现步骤：

1. **创建通知点击处理 Activity**：

```java
package com.pushcollection.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import com.pushcollection.PushClient;
import org.json.JSONObject;

public class HonorNotificationClickActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            // 获取通知数据
            String pushData = getIntent().getStringExtra("push_data");
            if (pushData != null) {
                PushClient.getInstance().onClickedNotification(
                    new JSONObject(pushData).toMap()
                );
            }
            // 跳转主界面
            Intent launchIntent = getPackageManager().getLaunchIntentForPackage(getPackageName());
            if (launchIntent != null) {
                startActivity(launchIntent);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        finish();
    }
}
```

2. **创建通知构建工具类**：

```java
package com.pushcollection.notification;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import androidx.core.app.NotificationCompat;
import com.pushcollection.activity.HonorNotificationClickActivity;
import org.json.JSONObject;
import java.util.Map;

public class HonorNotificationHelper {
    public static void showNotification(Context context, Map<String, String> data) {
        NotificationManager notificationManager =
            (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // 创建通知点击 Intent
        Intent intent = new Intent(context, HonorNotificationClickActivity.class);
        intent.putExtra("push_data", new JSONObject(data).toString());
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        PendingIntent pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        // 构建通知
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "default")
            .setContentTitle(data.get("title"))
            .setContentText(data.get("content"))
            .setSmallIcon(context.getApplicationInfo().icon)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent);

        notificationManager.notify(System.currentTimeMillis(), builder.build());
    }
}
```

3. **在 AndroidManifest.xml 中注册 Activity**：

```xml
<activity
    android:name=".activity.HonorNotificationClickActivity"
    android:excludeFromRecents="true"
    android:exported="true"
    android:launchMode="singleTask"
    android:taskAffinity=""
    android:theme="@android:style/Theme.Translucent.NoTitleBar" />
```

4. **修改 HonorListener 添加消息处理**：

```java
package com.pushcollection.listener;

public class HonorListener extends HonorMessageService {
    // ...existing code...

    @Override
    public void onMessageReceived(HonorPushDataMsg honorPushDataMsg) {
        Map<String, String> data = ToMapUitl.toMap(honorPushDataMsg);
        PushClient.getInstance().onReceivePushMessage(data);
        // 显示通知
        HonorNotificationHelper.showNotification(getApplicationContext(), data);
    }
}
```

5. **React Native 层使用方式**：

```javascript
// 在 React Native 代码中使用
import PushNotification from 'react-native-push-collection';

PushNotification.addNotificationClickedListener((message) => {
  console.log('Honor通知被点击:', message);
  // 处理跳转逻辑
});
```

> **注意事项**：
>
> - 确保通知数据中包含必要的参数
> - Activity 需要在 AndroidManifest.xml 中正确配置
> - 通知 channel 需要根据 Android 版本适配
> - 推荐在通知数据中包含跳转相关的参数

这样实现后，荣耀设备的通知点击处理机制就与小米推送的表现一致了。

## 华为

#### 华为推送通知点击事件处理方案

对于华为设备，由于 `HmsMessageService` 确实没有提供点击通知的回调方法，我们需要通过以下方式来实现：

1. **在通知发送时配置 Intent**：

```java
public class HuaweiNotification {
    public static void createNotification(Map<String, String> data) {
        Intent intent = new Intent(context, NotificationClickActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("push_data", new JSONObject(data).toString());

        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        // 设置通知点击意图
        builder.setContentIntent(pendingIntent);
    }
}
```

2. **创建接收点击事件的 Activity**：

```java
public class NotificationClickActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 获取通知数据
        String pushData = getIntent().getStringExtra("push_data");
        if (pushData != null) {
            // 发送点击事件到 RN
            PushClient.getInstance().onClickedNotification(
                new JSONObject(pushData).toMap()
            );
        }
        // 跳转到主界面
        Intent launchIntent = getPackageManager().getLaunchIntentForPackage(getPackageName());
        if (launchIntent != null) {
            startActivity(launchIntent);
        }
        finish();
    }
}
```

3. **在 AndroidManifest.xml 中注册 Activity**：

```xml
<activity
    android:name=".activity.NotificationClickActivity"
    android:excludeFromRecents="true"
    android:exported="true"
    android:launchMode="singleTask"
    android:taskAffinity=""
    android:theme="@android:style/Theme.Translucent.NoTitleBar" />
```

4. **React Native 层使用方式不变**：

```javascript
import PushNotification from 'react-native-push-collection';

PushNotification.addNotificationClickedListener((message) => {
  console.log('通知被点击:', message);
});
```

> **注意事项**：
>
> - 这种方式需要创建一个透明的中转 Activity
> - Activity 需要在 AndroidManifest.xml 中正确配置
> - 建议在通知数据中包含必要的跳转参数
> - 确保应用退出后仍能正常处理点击事件

这样就可以实现与小米推送类似的通知点击事件处理机制。

## oppo

#### OPPO 推送通知点击事件处理方案

针对 OPPO 设备的推送通知点击事件，需要通过以下步骤实现：

1. **创建通知点击处理 Activity**：

```java
package com.pushcollection.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import com.pushcollection.PushClient;
import org.json.JSONObject;

public class OPPONotificationClickActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            String pushData = getIntent().getStringExtra("push_data");
            if (pushData != null) {
                PushClient.getInstance().onClickedNotification(
                    new JSONObject(pushData).toMap()
                );
            }
            // 打开主界面
            Intent launchIntent = getPackageManager().getLaunchIntentForPackage(getPackageName());
            if (launchIntent != null) {
                startActivity(launchIntent);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        finish();
    }
}
```

2. **修改 OppoPushRegister 添加通知处理方法**：

```java
// ...existing code...

private void createNotification(Map<String, String> data) {
    Context context = getContext();
    NotificationManager notificationManager =
        (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

    // 创建点击意图
    Intent intent = new Intent(context, OPPONotificationClickActivity.class);
    intent.putExtra("push_data", new JSONObject(data).toString());
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

    PendingIntent pendingIntent = PendingIntent.getActivity(
        context,
        0,
        intent,
        PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
    );

    // 构建通知
    NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "default")
        .setContentTitle(data.get("title"))
        .setContentText(data.get("content"))
        .setSmallIcon(context.getApplicationInfo().icon)
        .setAutoCancel(true)
        .setContentIntent(pendingIntent);

    notificationManager.notify(System.currentTimeMillis(), builder.build());
}
```

3. **在 AndroidManifest.xml 中注册 Activity**：

```xml
<activity
    android:name=".activity.OPPONotificationClickActivity"
    android:excludeFromRecents="true"
    android:exported="true"
    android:launchMode="singleTask"
    android:taskAffinity=""
    android:theme="@android:style/Theme.Translucent.NoTitleBar" />
```

4. **React Native 层使用示例**：

```javascript
import PushNotification from 'react-native-push-collection';

PushNotification.addNotificationClickedListener((message) => {
  console.log('OPPO通知点击:', message);
  // 处理跳转逻辑
});
```

> **注意事项**：
>
> - 确保通知数据中包含所需的业务参数
> - 配置正确的通知渠道（Channel）
> - Activity 需要在 AndroidManifest.xml 中正确注册
> - 考虑应用在后台和被杀死状态下的处理
> - 推荐在通知数据中包含页面跳转参数

通过以上配置，可以实现与其他厂商推送类似的通知点击事件处理机制。

## fcm

#### FCM 推送通知点击事件处理方案

针对 FCM 推送，需要通过自定义 PendingIntent 来实现点击事件处理。以下是具体实现步骤：

1. **创建通知点击处理 Activity**：

```java
package com.pushcollection.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import com.pushcollection.PushClient;
import org.json.JSONObject;

public class FCMNotificationClickActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            String pushData = getIntent().getStringExtra("push_data");
            if (pushData != null) {
                PushClient.getInstance().onClickedNotification(
                    new JSONObject(pushData).toMap()
                );
            }
            // 打开主界面
            Intent launchIntent = getPackageManager().getLaunchIntentForPackage(getPackageName());
            if (launchIntent != null) {
                startActivity(launchIntent);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        finish();
    }
}
```

2. **创建 FCM 通知构建工具**：

```java
package com.pushcollection.notification;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import androidx.core.app.NotificationCompat;
import com.google.firebase.messaging.RemoteMessage;
import com.pushcollection.activity.FCMNotificationClickActivity;
import org.json.JSONObject;
import java.util.Map;

public class FCMNotificationHelper {
    public static void showNotification(Context context, RemoteMessage remoteMessage) {
        NotificationManager notificationManager =
            (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // 创建点击意图
        Intent intent = new Intent(context, FCMNotificationClickActivity.class);
        intent.putExtra("push_data", new JSONObject(remoteMessage.getData()).toString());
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        PendingIntent pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        // 构建通知
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "default")
            .setContentTitle(remoteMessage.getNotification().getTitle())
            .setContentText(remoteMessage.getNotification().getBody())
            .setSmallIcon(context.getApplicationInfo().icon)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent);

        notificationManager.notify(System.currentTimeMillis(), builder.build());
    }
}
```

3. **修改 FCM Service 实现**：

```java
package com.pushcollection.listener;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.pushcollection.PushClient;
import com.pushcollection.notification.FCMNotificationHelper;

public class FCMListener extends FirebaseMessagingService {
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        // 处理消息
        PushClient.getInstance().onReceivePushMessage(remoteMessage.getData());
        // 显示通知
        FCMNotificationHelper.showNotification(getApplicationContext(), remoteMessage);
    }

    @Override
    public void onNewToken(String token) {
        super.onNewToken(token);
        PushClient.getInstance().onReceivePushToken(token);
    }
}
```

4. **在 AndroidManifest.xml 中注册 Activity**：

```xml
<activity
    android:name=".activity.FCMNotificationClickActivity"
    android:excludeFromRecents="true"
    android:exported="true"
    android:launchMode="singleTask"
    android:taskAffinity=""
    android:theme="@android:style/Theme.Translucent.NoTitleBar" />
```

5. **React Native 层使用示例**：

```javascript
import PushNotification from 'react-native-push-collection';

PushNotification.addNotificationClickedListener((message) => {
  console.log('FCM通知点击:', message);
  // 处理跳转逻辑
});
```

> **注意事项**：
>
> - FCM 通知需要确保设备已安装 Google Play Services
> - 正确配置 firebase-messaging 依赖
> - 确保通知数据中包含必要的业务参数
> - Activity 需要在 AndroidManifest.xml 中正确配置
> - 考虑应用在后台和被杀死状态下的处理

通过以上配置，可以实现与其他厂商推送类似的通知点击事件处理机制。
