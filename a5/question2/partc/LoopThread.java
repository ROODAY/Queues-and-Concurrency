import java.util.Arrays;
import java.util.concurrent.Semaphore;

class LoopThread extends Thread {
  public static final int N = 5;
  private int id;
  private static Semaphore sem = new Semaphore(1);

  public LoopThread(int id) {
   this.id = id;
  }
  
  public void run() {
    try {
      for (int i = 0; i < 5; i++) {
        sem.acquire();

        System.out.println("Thread " + this.id + " is starting iteration " + i +".");
        Thread.sleep((long) Math.random() * 20);

        System.out.println("We hold these truths to be self-evident, that all men are created equal,");
        Thread.sleep((long) Math.random() * 20);

        System.out.println("that they are endowed by their Creator with certain unalienable Rights,");
        Thread.sleep((long) Math.random() * 20);

        System.out.println("that among these are Life, Liberty and the pursuit of Happiness.");
        Thread.sleep((long) Math.random() * 20);

        System.out.println("Thread " + this.id + " is done with iteration " + i +".");

        sem.release();
      }
    } catch(InterruptedException e) {
      e.printStackTrace();
    }
  }
}