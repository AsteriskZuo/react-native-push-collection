package com.pushcollection;

import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.LifecycleOwner;

public class PushAppLifecycleListener implements LifecycleEventObserver {

  @Override
  public void onStateChanged(@NonNull LifecycleOwner source, @NonNull Lifecycle.Event event) {
    if (event == Lifecycle.Event.ON_START) {
      PushClient.getInstance().onAppForeground();
    } else if (event == Lifecycle.Event.ON_STOP) {
      PushClient.getInstance().onAppBackground();
    }
  }
}
