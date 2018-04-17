import java.util.*;
import java.util.concurrent.Semaphore;

public class PersonThread extends Thread {
  private final int N = 10;
  private final int K = 6;

  public int id;
  public int start;
  public int end;

  private Random rand = new Random();

  public static Semaphore[] sems = new Semaphore[50];

  public PersonThread(int id) {
    this.id = id;
  }

  public void run() {
    try {
      while (true) {
        sems[id] = new Semaphore(0);
        start = rand.nextInt(K);
        end = rand.nextInt(K);
        while (end == start) {
          end = rand.nextInt(K);
        }

        Thread.sleep((long) Math.random() * 20);

        while (ShuttleThread.boarding && ShuttleThread.currentStop == start) {
          Thread.sleep((long) Math.random() * 20);
        }

        ShuttleThread.terminalLock.acquire();

          ShuttleThread.terminals.get(start).add(this);
          System.out.println("Person: " + id + " entered line at stop: " + start);

        ShuttleThread.terminalLock.release();

        while (ShuttleThread.currentStop != start) {
          Thread.sleep((long) Math.random() * 20);
        }

        sems[id].acquire();

      }
    } catch(InterruptedException e) {
      e.printStackTrace();
    }
  }


    
}
