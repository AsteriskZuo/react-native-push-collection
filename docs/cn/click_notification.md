# 关于 android 设备推送通知接收的建议

## 现有各个厂商的推送插件存在的较大差异

华为、荣耀这类厂商，官方推荐使用 Activity 接收点击通知消息。
小米、vivo 这类厂商，支持通过监听器 接收点击通知消息。

如果集成华为厂商的监听器示例:

```java
import com.huawei.hms.push.HmsMessageService;
import com.huawei.hms.push.RemoteMessage;

public class HuaweiListener extends HmsMessageService {
  @Override
  public void onNotificationMessageClicked(Context context, MiPushMessage miPushMessage) {
    // 华为不支持点击事件回调通知，没有这个方法，这样写是错误的。
    // 需要通过 Activity 来接收点击事件通知
  }
  @Override
  public void onMessageReceived(RemoteMessage remoteMessage) {
    // 处理消息
  }
}

```

如果集成小米厂商监听器示例:

```java
import com.xiaomi.mipush.sdk.PushMessageReceiver;

public class XiaomiListener extends PushMessageReceiver {
  @Override
  public void onNotificationMessageClicked(Context context, MiPushMessage miPushMessage) {
    // 小米支持点击事件回调通知
  }

  @Override
  public void onNotificationMessageArrived(Context context, MiPushMessage miPushMessage) {
    // 处理消息
  }
}

```

## 我们建议的解决方案

建议采用 Activity 的这种通用的方式进行统一处理。

以下示例展示如何接收环信消息并且进行消息处理。

_java 版本_

```java
public class MainActivity {
  final static String TAG = "MainActivity";

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    getIntentData(getIntent());
  }

  @Override
  public void onNewIntent(Intent intent) {
    super.onNewIntent(intent);
    setIntent(intent);
    getIntentData(intent);
  }

  private void getIntentData(Intent intent) {
    if (null != intent) {
      Bundle bundle = intent.getExtras();
      Log.d(TAG, "Bundle bundle: " + (bundle != null));
      if (bundle != null) {
        Log.d(TAG, "from" + bundle.getString("f"))
        Log.d(TAG, "to" + bundle.getString("t"))
        Log.d(TAG, "message id" + bundle.getString("m"))
        Log.d(TAG, "group id" + bundle.getString("g"))
        Log.d(TAG, "extra" + bundle.getString("e"))
      }
    } else {
      Log.i(TAG, "intent is null");
    }
  }
}
```

_kotlin 版本_

```kt
class MainActivity {
  companion object {
    const val TAG = "MainActivity"
  }

  override fun onNewIntent(intent: Intent?) {
    super.onNewIntent(intent)
    setIntent(intent) // 更新 Intent
    getIntentData(intent)
  }

  override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
    super.onCreate(savedInstanceState, persistentState)
    getIntentData(intent)
  }
  private fun getIntentData(intent: Intent?) {
    if (null != intent) {
      val bundle = intent.extras
      bundle?.keySet()?.forEach { key ->
        val value = bundle.get(key)
        when (value) {
          is String -> Log.i(TAG, "receive data from push, key = $key, content = ${bundle.getString(key)}")
          is Int -> Log.i(TAG, "receive data from push, key = $key, content = ${bundle.getInt(key)}")
          is Boolean -> Log.i(TAG, "receive data from push, key = $key, content = ${bundle.getBoolean(key)}")
          is Float -> Log.i(TAG, "receive data from push, key = $key, content = ${bundle.getFloat(key)}")
          is Double -> Log.i(TAG, "receive data from push, key = $key, content = ${bundle.getDouble(key)}")
          else -> Log.i(TAG, "receive data from push, key = $key, content = $value")
        }
      }
    } else {
      Log.i(TAG, "intent = null")
    }
  }
}

```

## 参考

[环信服务端接口说明](https://doc.easemob.com/document/server-side/push_extension.html#%E7%A6%BB%E7%BA%BF%E6%8E%A8%E9%80%81%E7%9B%B8%E5%85%B3%E7%9A%84%E6%89%A9%E5%B1%95%E5%AD%97%E6%AE%B5)
[android 解析消息示例](https://doc.easemob.com/document/android/push/push_parsing.html)

[华为官方说明](https://developer.huawei.com/consumer/cn/doc/HMSCore-Guides/andorid-basic-clickaction-0000001087554076)
[小米官方说明](https://dev.mi.com/console/doc/detail?pId=68)
