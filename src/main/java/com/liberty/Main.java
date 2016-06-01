package com.liberty;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * @author Dmytro_Kovalskyi.
 * @since 01.06.2016.
 */
public class Main {

  public static void main(String[] args) {
//    ExecutorService service = Executors.newCachedThreadPool();
//    service.submit(()-> System.out.println("Service"));
//    service.submit(()-> System.out.println("Service"));
//    service.submit(()-> System.out.println("Service"));
//    service.submit(()-> System.out.println("Service"));
//    service.submit(()-> System.out.println("Service"));
//    service.submit(()-> System.out.println("Service"));
//    service.submit(()-> System.out.println("Service"));
//    service.submit(()-> System.out.println("Service"));
//    service.submit(()-> System.out.println("Service"));

    final Executor executor = Executors.newSingleThreadExecutor();

      executor.execute(()-> System.out.println("Service"));
    //service.shutdown();
  }

  public static void test(Testable testable)  {
    try {
      testable.test();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  interface Testable {

    public void test() throws Exception;
  }

  static class Test implements Testable{

    @Override
    public void test() {
      System.out.println("Test class");
    }
  }
}
