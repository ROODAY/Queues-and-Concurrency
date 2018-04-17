import java.util.*;
import java.util.concurrent.Semaphore;

public class TunnelThread extends Thread {

  final static int SIM_TIME = 20; // Number of times to run simulation (to test for starvation choose high number)
  final static int MAX_CARS = 4; // Max number of cars in tunnel
  final static int MAX_SKIP = 5; // Max number of times a car can be skipped before switching turn

	private int id; // ID of thread/car
	public int direction; // Direction current thread/car is moving. Either 0 or 1
  public int skipped = 0; // Number of times current thread/car has been skipped in queue

	public static volatile int turn = 0; // Decides which direction of threads/cars is allowed to travel
  public static volatile LinkedList<TunnelThread> queue = new LinkedList<TunnelThread>(); // Keeps track of waiting cars
	private static Semaphore sem = new Semaphore(MAX_CARS); // Manages access to tunnel resource
  private static Semaphore qsem = new Semaphore(1); // Manages access to waiting queue

	public TunnelThread(int id){
		this.id = id;
	}

	@Override
	public void run(){
    try{
      for (int i = 0; i < SIM_TIME; i++) {
  			direction = (Math.random() < 0.5) ? 0 : 1; // Direction car will be travelling
        System.out.println("Car: " + id + " arrived.");

        if (sem.availablePermits() == MAX_CARS) { // If tunnel is empty, go
          turn = direction;
        }

        qsem.acquire();
        queue.add(this); // Add car to waiting queue

        while (turn != direction && sem.availablePermits() < MAX_CARS) { // Wait until turn or tunnel is empty
          Thread.sleep((long) (Math.random()*50 + 10)); 
        }

        int index = queue.indexOf(this); // Remove self from waiting queue
        queue.remove(index);

        for (int j = 0; j < index; j++) { // Update skipped counter of any cars that may be skipped
          TunnelThread car = queue.get(j);
          if (car.direction != this.direction) {
            car.skipped++;
            if (car.skipped > MAX_SKIP) { // If a car has been skipped more than allowed, switch turn
              turn = car.direction;
            }
          }
        }
        qsem.release();

        while (turn != direction) { // Wait until turn to avoid crashes
          Thread.sleep((long) (Math.random()*50 + 10)); 
        }

        sem.acquire();
  			System.out.println("Car: " + id + " is entering in direction: " + direction + ".");
  			Thread.sleep((long) (Math.random()*50 + 10));
        System.out.println("Car: " + id + " is exiting.");
        sem.release();

        if (sem.availablePermits() == MAX_CARS) { // If tunnel is empty, switch directions
          turn = (turn == 0) ? 1 : 0;
        }
  		}
    } catch(InterruptedException e) {
      e.printStackTrace();
    }
	}
}