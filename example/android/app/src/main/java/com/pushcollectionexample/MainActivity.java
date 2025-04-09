package com.pushcollectionexample;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.facebook.react.ReactActivity;
import com.facebook.react.ReactActivityDelegate;
import com.facebook.react.defaults.DefaultNewArchitectureEntryPoint;
import com.facebook.react.defaults.DefaultReactActivityDelegate;
import com.pushcollection.PushClient;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends ReactActivity {
  final static String TAG = "MainActivity";

  /**
   * Returns the name of the main component registered from JavaScript. This is used to schedule
   * rendering of the component.
   */
  @Override
  protected String getMainComponentName() {
    return "PushCollectionExample";
  }

  /**
   * Returns the instance of the {@link ReactActivityDelegate}. Here we use a util class {@link
   * DefaultReactActivityDelegate} which allows you to easily enable Fabric and Concurrent React
   * (aka React 18) with two boolean flags.
   */
  @Override
  protected ReactActivityDelegate createReactActivityDelegate() {
    return new DefaultReactActivityDelegate(
        this,
        getMainComponentName(),
        // If you opted-in for the New Architecture, we enable the Fabric Renderer.
        DefaultNewArchitectureEntryPoint.getFabricEnabled(), // fabricEnabled
        // If you opted-in for the New Architecture, we enable Concurrent React (i.e. React 18).
        DefaultNewArchitectureEntryPoint.getConcurrentReactEnabled() // concurrentRootEnabled
        );
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    String s = getManufacturer();
    Log.d(TAG, "Manufacturer: " + s);
    try {
      getIntentData(getIntent());
    } catch (Exception e) {
      e.printStackTrace();
    }
//    finish();
  }

  @Override
  public void onNewIntent(Intent intent) {
    super.onNewIntent(intent);
    setIntent(intent);
    getIntentData(intent);
  }

  private void getIntentData(Intent intent) {
    if (null != intent) {
      // Get the values from the data
      Bundle bundle = intent.getExtras();
      Log.d(TAG, "Bundle bundle: " + (bundle != null));
      if (bundle != null) {
        Log.d(TAG, "Bundle bundle: " + bundle.keySet().size());
        for (String key : bundle.keySet()) {
          Object value = bundle.get(key);
          Log.d(TAG, "value type:" + value.getClass().getName());
          if (value instanceof String) {
            String content = (String) value;
            Log.i(TAG, "receive data from push, key = " + key + ", content = " + content);
          } else {
            Log.i(TAG, "receive data from push, key = " + key + ", content is not a String");
          }
        }
      }
    } else {
      Log.i(TAG, "intent is null");
    }
  }

  public static String getManufacturer() {
    String manufacturer = android.os.Build.MANUFACTURER.toLowerCase();

    switch (manufacturer) {
      case "huawei":
        return "HUAWEI";
      case "xiaomi":
        return "XIAOMI";
      case "oppo":
        return "OPPO";
      case "vivo":
        return "VIVO";
      case "meizu":
        return "MEIZU";
      default:
        return manufacturer.toUpperCase();
    }
  }
}
