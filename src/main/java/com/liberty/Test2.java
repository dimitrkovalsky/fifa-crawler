package com.liberty;

/**
 * @author Dmytro_Kovalskyi.
 * @since 24.10.2016.
 */
public class Test2 {

  //13-50  - 14-06
  public static void main(String[] args) {

    assert toArabic("LXXX") == 80;
    assert (toArabic("CCC") == 300);
    System.out.println("To enable assert add  -ea to VM options");
  }

  public static int toArabic(String number) {
    validate(number);
    if (number.isEmpty()) return 0;
    if (number.startsWith("M")) return 1000 + toArabic(number.substring(1));
    if (number.startsWith("CM")) return 900 + toArabic(number.substring(2));
    if (number.startsWith("D")) return 500 + toArabic(number.substring(1));
    if (number.startsWith("CD")) return 400 + toArabic(number.substring(2));
    if (number.startsWith("C")) return 100 + toArabic(number.substring(1));
    if (number.startsWith("XC")) return 90 + toArabic(number.substring(2));
    if (number.startsWith("L")) return 50 + toArabic(number.substring(1));
    if (number.startsWith("XL")) return 40 + toArabic(number.substring(2));
    if (number.startsWith("X")) return 10 + toArabic(number.substring(1));
    if (number.startsWith("IX")) return 9 + toArabic(number.substring(2));
    if (number.startsWith("V")) return 5 + toArabic(number.substring(1));
    if (number.startsWith("IV")) return 4 + toArabic(number.substring(2));
    if (number.startsWith("I")) return 1 + toArabic(number.substring(1));
    else return 0;
  }

  private static void validate(String number) {

  }
}
