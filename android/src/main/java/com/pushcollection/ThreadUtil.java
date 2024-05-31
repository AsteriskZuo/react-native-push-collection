package com.pushcollection;

import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadUtil {
  public static void asyncExecute(Runnable runnable) {
    asyncThreadPool.execute(runnable);
  }

  public static void mainThreadExecute(Runnable runnable) {
    mainThreadHandler.post(runnable);
  }

  private static final String TAG = "ThreadUtil";
  private static final ExecutorService asyncThreadPool = Executors.newCachedThreadPool();
  private static final Handler mainThreadHandler = new Handler(Looper.getMainLooper());
}
