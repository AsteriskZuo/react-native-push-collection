package com.pushcollection;

import java.util.Map;
import org.json.JSONObject;

public class ToJsonUtil {
  public static JSONObject toJson(Map<?, ?> object) { return new JSONObject(object); }
}
