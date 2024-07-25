package com.pushcollection;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ProcessLifecycleOwner;

public class PushActivityLifecycleCallbacks implements Application.ActivityLifecycleCallbacks {
  private final PushAppLifecycleListener pushAppLifecycleListener;

  public PushActivityLifecycleCallbacks() { this.pushAppLifecycleListener = new PushAppLifecycleListener(); }

  @Override
  public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle bundle) {
    Log.d("LifecycleCallbacks", "onActivityCreated");
    ProcessLifecycleOwner.get().getLifecycle().addObserver(pushAppLifecycleListener);
  }

  @Override
  public void onActivityStarted(@NonNull Activity activity) {
    Log.d("LifecycleCallbacks", "onActivityStarted");
  }

  @Override
  public void onActivityResumed(@NonNull Activity activity) {
    Log.d("LifecycleCallbacks", "onActivityResumed");
  }

  @Override
  public void onActivityPaused(@NonNull Activity activity) {
    Log.d("LifecycleCallbacks", "onActivityPaused");
  }

  @Override
  public void onActivityStopped(@NonNull Activity activity) {
    Log.d("LifecycleCallbacks", "onActivityStopped");
  }

  @Override
  public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle bundle) {
    Log.d("LifecycleCallbacks", "onActivitySaveInstanceState");
  }

  @Override
  public void onActivityDestroyed(@NonNull Activity activity) {
    Log.d("LifecycleCallbacks", "onActivityDestroyed");
    ProcessLifecycleOwner.get().getLifecycle().removeObserver(pushAppLifecycleListener);
  }
}
