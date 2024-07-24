package com.pushcollection;

import android.app.Application;
import android.content.Intent;
import android.util.Log;
import androidx.lifecycle.ProcessLifecycleOwner;

public class PushApplication extends Application {
  private static final String TAG = "PushApplication";

  @Override
  public void onCreate() {
    this.initializeLifecycle();
    super.onCreate();
  }

  private void launchMainActivity() {
    Intent intentLaunchMain = this.getPackageManager().getLaunchIntentForPackage(this.getPackageName());
    if (intentLaunchMain != null) {
      this.startActivity(intentLaunchMain);
    } else {
      Log.e(TAG, "Failed to get launch intent for package: " + this.getPackageName());
    }
  }

  private void initializeLifecycle() {
    ProcessLifecycleOwner.get().getLifecycle().addObserver(new PushAppLifecycleListener());
  }
}
