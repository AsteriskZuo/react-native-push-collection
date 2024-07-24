package com.pushcollection;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class PushError extends Throwable {
  private final PushErrorCode code;
  private final String message;

  public PushError(PushErrorCode code, String message) {
    super(message);
    this.code = code;
    this.message = message;
  }

  public int getCode() { return code.getCode(); }

  @Nullable
  @Override
  public String getMessage() {
    return message;
  }

  @NonNull
  @Override
  public String toString() {
    return "{"
      + "\"code\":" + code.getCode() + ",\"message\":" + message + "}";
  }
}
