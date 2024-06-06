package com.pushcollection;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class ToMapUitl {
  public static Map<String, Object> toMap(Object object) {
    Map<String, Object> map = new HashMap<>();
    if (object == null) {
      return map;
    }
    Class<?> clazz = object.getClass();
    Field[] fields = clazz.getDeclaredFields();
    for (Field field : fields) {
      try {
        map.put(field.getName(), field.get(object));
      } catch (IllegalAccessException e) {
        e.printStackTrace();
      }
    }
    return map;
  }
}
