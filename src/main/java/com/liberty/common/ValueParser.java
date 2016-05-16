package com.liberty.common;


import static com.liberty.common.LoggingUtil.error;

/**
 * @author Dmytro_Kovalskyi.
 * @since 16.05.2016.
 */
public class ValueParser {

  public static int parseInt(String toParse) {
    try {
      return Integer.parseInt(toParse);
    } catch (Exception e) {
      error(ValueParser.class, e);
    }
    return 0;
  }
}
