import java.util.*;
import java.util.concurrent.Semaphore;

public class ShuttleThread extends Thread {
  private final int N = 10;
  private final int K = 6;
  private final int MAX_SERVICED = 100;
  private int serviced = 0;

  public int id;
  public static volatile ArrayList<ArrayList<PersonThread>> terminals = new ArrayList<ArrayList<PersonThread>>();
  public static volatile ArrayList<PersonThread> seats = new ArrayList<PersonThread>();
  public static volatile int currentStop = 0;
  public static volatile boolean boarding = false;
  public static volatile boolean running = true;

  public static volatile Semaphore terminalLock = new Semaphore(1);


  public ShuttleThread(int id) {
    this.id = id;
    for (int i = 0; i < K; i++) {
      terminals.add(new ArrayList<PersonThread>());
    }
  }


  public void run() {
    try {
      while (serviced < MAX_SERVICED || running) {

        if (serviced >= MAX_SERVICED && seats.size() == 0) {
          running = false;
          System.out.println("BUS WILL STOP TAKING NEW PASSENGERS");
        }

        System.out.println("BUS AT: " + currentStop + ", CAPACITY: " + seats.size());

        for (Iterator<PersonThread> iterator = seats.iterator(); iterator.hasNext(); ) {
            PersonThread person = iterator.next();
            if (person.end == currentStop) {
              iterator.remove();
              PersonThread.sems[person.id].release();
              System.out.println("Person: " + person.id + " exited");
              serviced++;
            }
        }

        if (serviced < MAX_SERVICED) {
          terminalLock.acquire();

            if (terminals.get(currentStop).size() > 0) {
              boarding = true;

              while (seats.size() < N && terminals.get(currentStop).size() > 0) {
                PersonThread person = terminals.get(currentStop).remove(0);
                seats.add(person);
                System.out.println("Person: " + person.id + " entered bus, leaves at : " + person.end);
              }

              boarding = false;
            }

          terminalLock.release();
        }
          
        Thread.sleep((long) Math.random() * 20);
        currentStop = (currentStop + 1) % K;
      }
      System.out.println("BUS HAS STOPPED RUNNING");
      System.exit(0);
    } catch(InterruptedException e) {
      e.printStackTrace();
    }
  }
}
