package utils;

public class CommonUtils {

  public static void tinySleep() {
    try {Thread.sleep(10);} catch (InterruptedException e) {e.printStackTrace();}
  }

  public static void sleep() {
    try {Thread.sleep(30);} catch (InterruptedException e) {e.printStackTrace();}
  }

  public static void longSleep() {

  }

  public static void longlongSleep(int time) {
    try {Thread.sleep(time);} catch (InterruptedException e) {e.printStackTrace();}
  }

}
