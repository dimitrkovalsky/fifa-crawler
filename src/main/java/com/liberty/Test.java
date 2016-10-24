package com.liberty;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Dmytro_Kovalskyi.
 * @since 24.10.2016.
 */
public class Test {

  //13-50  - 14-06
  public static void main(String[] args) {
    String sentence = "hello my friend I like to see you";
    final String DELIMITER = " ";

    List<String> collected = Arrays.stream(sentence.split(DELIMITER)).map(word -> {
      char[] chars = word.toCharArray();
      for (int i = 0; i < chars.length; i++) {
        if ((i + 1) % 3 == 0) {
          chars[i] = Character.toUpperCase(chars[i]);
        }
      }
      return new String(chars);
    }).collect(Collectors.toList());

    Collections.reverse(collected);
    System.out.println(collected);
  }
}
