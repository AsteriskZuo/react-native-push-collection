package com.pushcollection;

import androidx.annotation.NonNull;

public enum PushType {
  FCM("fcm"),

  HUAWEI("huawei"),

  HONOR("honor"),

  XIAOMI("xiaomi"),

  VIVO("vivo"),

  OPPO("oppo"),

  MEIZU("meizu"),

  OTHERS("unknown"),
  ;

  private final String name;

  PushType(String name) { this.name = name; }

  public String getName() { return name; }

  @NonNull
  @Override
  public String toString() {
    return this.getName();
  }
}
