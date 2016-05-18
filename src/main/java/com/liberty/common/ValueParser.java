package com.liberty.common;


import org.jsoup.select.Elements;

import java.util.Optional;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ValueParser {

  public static Optional<Integer> parseInt(String toParse) {
    try {
      if (toParse.equals("???"))
        return Optional.empty();
      return Optional.of(Integer.parseInt(toParse));
    } catch (Exception e) {
      log.error(e.getMessage());
    }
    return Optional.empty();
  }

  public static Optional<Integer> parseInt(Elements toParse) {
    try {
      if (toParse != null && toParse.first() != null) {
        String text = toParse.first().text();
        if (!text.equals("???"))
          return Optional.of(Integer.parseInt(text));
      }
    } catch (Exception e) {
      log.error(e.getMessage());
    }
    return Optional.empty();
  }
}
