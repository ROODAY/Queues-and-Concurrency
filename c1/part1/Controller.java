import java.util.Collections;
import java.util.LinkedList;
import java.util.HashMap;  
import java.util.ArrayList;
import java.util.Random;

public class Controller {

	/**
	 * Simulation constants
	 */
	public static final double MONITOR_INTERVAL = 0.03;
	public static final int SIMULATION_TIME = 200;
  public static final int SEED = 5612783;
	
	/**
	 * time elapsed time before starting to log
	 */
	public static final double LOGGING_START_TIME = 100;
	
	/**
	 * holds all the devices in the system
	 */
	public static LinkedList<Device> devices = new LinkedList<Device>();
  // Provides a mapping of device names to devices so devices can refer to each other
  public static HashMap<String,Device> deviceList = new HashMap<String,Device>();  
  // Keeps track of all processes moving through the system
  public static ArrayList<Process> processes = new ArrayList<Process>();
  // Random number generator that takes a seed
  public static Random generator = new Random(SEED);
	
	/**
	 * initialize the schedule with birth and a monitor events
	 * @return a schedule with events
	 */
	public static LinkedList<Event> initSchedule(){
		LinkedList<Event> schedule = new LinkedList<Event>();
		
		Device cpu = new MM2("cpu", schedule, deviceList, processes, 40, 0.02);
		cpu.initializeScehduleWithOneEvent();
		devices.add(cpu);
    deviceList.put("cpu", cpu);

    Device net = new MM1("net", schedule, deviceList, processes, 0, 0.025);
    devices.add(net);
    deviceList.put("net", net);

    Device disk = new MM1("disk", schedule, deviceList, processes, 0, 0.1);
    devices.add(disk);
    deviceList.put("disk", disk);
	
		schedule.add(new Event(MONITOR_INTERVAL, EventType.MONITOR));
		return schedule;
	}
	
	/**
	 * sorts the schedule according to time, and returns the earliest event.
	 * @param schedule
	 * @return the earliest event in the schedule
	 */
	public static Event getNextEvent(LinkedList<Event> schedule){
			Collections.sort(schedule);
			return schedule.getFirst();
	}
	
	
	public static void main(String[] args){
    // Run 20 trials to collect sample data for confidence intervals
    int trials = 20;
    double[] tqs = new double[trials];
    double[] slows = new double[trials];
    for (int i = 0; i < trials; i++) {
      LinkedList<Event> schedule = initSchedule();
      
      double time = 0, maxTime = SIMULATION_TIME;
      while(time < maxTime){
        Event event = getNextEvent(schedule);
        time = event.getTime();
        event.function(schedule, time);
      }
      
      for(Device device: devices){
        device.printStats();
      }

      double systemTq = 0;
      for (Process p:processes) {
        systemTq += p.totalTq();
      }
      systemTq /= processes.size();

      double systemTw = 0;
      for (Process p:processes) {
        systemTw += p.totalTw();
      }
      systemTw /= processes.size();

      System.out.println("************************************");
      System.out.println("************************************");
      System.out.println("************************************");
      System.out.println("System tq: " + systemTq);
      System.out.println("System tw: " + systemTw);
      System.out.println("System Slowdown: " + (systemTq/(systemTq - systemTw)));
      tqs[i] = systemTq;
      slows[i] = (systemTq/(systemTq - systemTw));
    }
    // calculate confidence intervals for turnaround time and slowdown
    double avgTq = 0.0;
    double avgSlow = 0.0;
    for (int i = 0; i < trials; i++) {
      avgTq += tqs[i];
      avgSlow += slows[i];
    }

    avgTq /= trials;
    avgSlow /= trials;

    double varTq = 0.0;
    double varSlow = 0.0;
    for (int i = 0; i < trials; i++) {
        varTq += Math.pow((tqs[i] - avgTq), 2);
        varSlow += Math.pow((slows[i] - avgSlow), 2);
    }
    varTq /= (trials - 1);
    varSlow /= (trials - 1);
    double sdTq = Math.sqrt(varTq);
    double sdSlow = Math.sqrt(varSlow);
    System.out.println("Average Turnaround Time 97% CI: " + avgTq + " +- " + (2.17 * sdTq));
    System.out.println("Average Slowdown 97% CI: " + avgSlow + " +- " + (2.17 * sdSlow));
    System.out.println("Seed used: "+ SEED);
	}
	
}
