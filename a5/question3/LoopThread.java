import java.util.Arrays;
import java.util.concurrent.Semaphore;

class LoopThread extends Thread {
  public static final int K = 12;
  private static final int N = 3;
  private int id;
  private static Semaphore sem = new Semaphore(N); // Explanation of semaphore usage in pdf
  private static volatile int batch = 0;
  private static volatile int counter = 0;

  public LoopThread(int id) {
   this.id = id;
  }
  
  public void run() {
    try {
      sem.acquire();
      System.out.println("Process " + this.id + " in batch " + batch + " is going through.");
      Thread.sleep((long) Math.random() * 20);

      counter++;
      if (counter == N) {
        counter = 0;
        batch++;
        System.out.println("");
        sem.release(N);
      }
    } catch(InterruptedException e) {
      e.printStackTrace();
    }
  }
}