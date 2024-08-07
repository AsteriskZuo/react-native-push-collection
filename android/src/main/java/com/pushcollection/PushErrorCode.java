package com.pushcollection;

public enum PushErrorCode {
  PARAM_ERROR(1),
  NO_SUPPROT_ERROR(2),
  JSON_PARSE_ERROR(3),
  INIT_ERROR(4),
  REGISTER_ERROR(5),
  UNREGISTER_ERROR(6),
  PREPARE_ERROR(7),
  UNKOWN_ERROR(1000),
  ;
  private int code;

  PushErrorCode(int code) { this.code = code; }

  public static PushErrorCode fromCode(int code) {
    for (PushErrorCode errorCode : values()) {
      if (errorCode.code == code) {
        return errorCode;
      }
    }
    PushErrorCode ret = PushErrorCode.UNKOWN_ERROR;
    ret.setCode(code);
    return ret;
  }

  private void setCode(int code) { this.code = code; }

  public int getCode() { return this.code; }
}
