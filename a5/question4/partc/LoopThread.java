import java.util.Arrays;
import java.util.LinkedList;
import java.util.concurrent.Semaphore;

class LoopThread extends Thread {
  public static final int N = 5;
  private int id;
  private static volatile Semaphore[] sems = new Semaphore[N];
  private static volatile LinkedList<Integer> processes = new LinkedList<>();
  private static volatile int counter = 0;

  public LoopThread(int id) {
   this.id = id;
   this.sems[id] = new Semaphore(0);
  }

  private void newWait(int i) throws InterruptedException {
    System.out.println("P" + this.id + " is requesting the CS.");
    counter++;
    if (counter > 1) {
      processes.add(this.id);
      sems[this.id].acquire();
    }
  }

  private void newSignal(int i) throws InterruptedException {
    counter--;
    if (counter > 0) {
      int index = processes.pop();
      System.out.println("P" + this.id + " is exiting the CS.\n");
      sems[index].release();
    } else {
      System.out.println("P" + this.id + " is exiting the CS.\n");
    }
  }
  
  public void run() {
    try {
      for (int i = 0; i < 5; i++) {
        newWait(this.id);

        System.out.println("\nP" + this.id + " is in the CS.");
        Thread.sleep((long) Math.random() * 20);

        newSignal(this.id);
      }
    } catch(InterruptedException e) {
      e.printStackTrace();
    }
  }
}