package com.pushcollection;

import android.util.Log;
import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.LifecycleOwner;

public class PushAppLifecycleListener implements LifecycleEventObserver {

  @Override
  public void onStateChanged(@NonNull LifecycleOwner source, @NonNull Lifecycle.Event event) {
    Log.d("LifecycleEventObserver", "onStateChanged" + event.name());
    if (event == Lifecycle.Event.ON_START) {
      PushClient.getInstance().onAppForeground();
    } else if (event == Lifecycle.Event.ON_STOP) {
      PushClient.getInstance().onAppBackground();
    }
  }
}
