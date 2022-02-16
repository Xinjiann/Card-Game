package utils;

public class CommonUtils {

  public static void sleep() {
    try {Thread.sleep(30);} catch (InterruptedException e) {e.printStackTrace();}
  }

  public static void longSleep() {
    try {Thread.sleep(100);} catch (InterruptedException e) {e.printStackTrace();}
  }

}
