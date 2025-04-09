package com.pushcollection;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.pushcollection.PushClient;

import java.util.HashMap;

public class NotificationClickActivity extends Activity {
  final static String TAG = "NotificationClickActivi";
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    Log.d(TAG, "onCreate");
    if (getIntent().getData() != null) {
      Bundle bundle = getIntent().getExtras();
      HashMap<String, String> map = new HashMap<>();
      if (bundle != null) {
        for (String key : bundle.keySet()) {
          String content = bundle.getString(key);
          map.put(key, content);
        }
        PushClient.getInstance().onClickedNotification(map);
      }
      PushClient.getInstance().onClickedNotification(map);
    }
    finish();
  }
}
