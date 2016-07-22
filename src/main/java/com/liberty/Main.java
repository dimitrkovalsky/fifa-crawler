package com.liberty;

import java.io.File;

/**
 * @author Dmytro_Kovalskyi.
 * @since 01.06.2016.
 */
public class Main {

  public static void main(String[] args) {
    File file = new File("../abc.txt");
    String absolutePath = file.getAbsolutePath();
    System.out.println(absolutePath);

    System.out.println("Working Directory = " +
        System.getProperty("user.dir"));
  }

}
