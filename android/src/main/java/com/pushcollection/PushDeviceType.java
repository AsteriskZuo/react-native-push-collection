package com.pushcollection;

import androidx.annotation.NonNull;

public enum PushDeviceType {
  FCM("fcm"),

  HUAWEI("huawei"),

  HONOR("honor"),

  XIAOMI("xiaomi"),

  VIVO("vivo"),

  OPPO("oppo"),

  MEIZU("meizu"),

  OTHERS("others"),
  ;


  private final String name;

  PushDeviceType(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }

  @NonNull
  @Override
  public String toString() {
    return this.getName();
  }
}
