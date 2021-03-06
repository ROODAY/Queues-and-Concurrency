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
  public static final int SEED = 43251;
	
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
  // Keeps track of how long each type of process waits in queue
  public static LinkedList<double[]> overallW = new LinkedList<double[]>();
  // Random number generator that takes a seed
  public static Random generator = new Random(SEED);
	
  /**
   * initialize the schedule with birth and a monitor events
   * @return a schedule with events
   */
	public static LinkedList<Event> initSchedule(){
		LinkedList<Event> schedule = new LinkedList<Event>();
		
		Device cpu = new MM2("cpu", schedule, deviceList, processes, overallW, 2, 0.248, 38, 0.008);
		cpu.initializeScehduleWithOneEvent();
		devices.add(cpu);
    deviceList.put("cpu", cpu);

    Device net = new MM1("net", schedule, deviceList, processes, overallW, 0, 0.025);
    devices.add(net);
    deviceList.put("net", net);

    Device disk = new MM1("disk", schedule, deviceList, processes, overallW, 0, 0.1);
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
    double[] cputqs = new double[trials];
    double[] cpuslows = new double[trials];
    double[] iotqs = new double[trials];
    double[] ioslows = new double[trials];
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

      double cpuTq = 0;
      double cpuTw = 0;
      double ioTq = 0;
      double ioTw = 0;
      for (Process p:processes) {
        if (p.type == 0) {
          cpuTq += p.totalTq();
          cpuTw += p.totalTw();
        } else {
          ioTq += p.totalTq();
          ioTw += p.totalTw();
        }
      }
      cpuTq /= processes.size();
      cpuTw /= processes.size();
      ioTq /= processes.size();
      ioTw /= processes.size();

      System.out.println("************************************");
      System.out.println("************************************");
      System.out.println("************************************");
      System.out.println("CPU tq: " + cpuTq);
      System.out.println("CPU tw: " + cpuTw);
      System.out.println("CPU Slowdown: " + (cpuTq/(cpuTq - cpuTw)));
      System.out.println("IO tq: " + ioTq);
      System.out.println("IO tw: " + ioTw);
      System.out.println("IO Slowdown: " + (ioTq/(ioTq - ioTw)));
      cputqs[i] = cpuTq;
      cpuslows[i] = (cpuTq/(cpuTq - cpuTw));
      iotqs[i] = ioTq;
      ioslows[i] = (ioTq/(ioTq - ioTw));
    }
    // calculate confidence intervals for turnaround time and slowdown for both types of processes
    // as well as average time in queue for both types of processes
    double cpuavgTq = 0.0;
    double cpuavgSlow = 0.0;
    double ioavgTq = 0.0;
    double ioavgSlow = 0.0;
    for (int i = 0; i < trials; i++) {
      cpuavgTq += cputqs[i];
      cpuavgSlow += cpuslows[i];
      ioavgTq += iotqs[i];
      ioavgSlow += ioslows[i];
    }

    cpuavgTq /= trials;
    cpuavgSlow /= trials;
    ioavgTq /= trials;
    ioavgSlow /= trials;

    double cpuvarTq = 0.0;
    double cpuvarSlow = 0.0;
    double iovarTq = 0.0;
    double iovarSlow = 0.0;
    for (int i = 0; i < trials; i++) {
        cpuvarTq += Math.pow((cputqs[i] - cpuavgTq), 2);
        cpuvarSlow += Math.pow((cpuslows[i] - cpuavgSlow), 2);
        iovarTq += Math.pow((iotqs[i] - ioavgTq), 2);
        iovarSlow += Math.pow((ioslows[i] - ioavgSlow), 2);
    }
    cpuvarTq /= (trials - 1);
    cpuvarSlow /= (trials - 1);
    iovarTq /= (trials - 1);
    iovarSlow /= (trials - 1);
    double cpusdTq = Math.sqrt(cpuvarTq);
    double cpusdSlow = Math.sqrt(cpuvarSlow);
    double iosdTq = Math.sqrt(iovarTq);
    double iosdSlow = Math.sqrt(iovarSlow);
    System.out.println("Average CPU Bound Process Turnaround Time 97% CI: " + cpuavgTq + " +- " + (2.17 * cpusdTq));
    System.out.println("Average CPU Bound Process Slowdown 97% CI: " + cpuavgSlow + " +- " + (2.17 * cpusdSlow));
    System.out.println("Average I/O Bound Process Turnaround Time 97% CI: " + ioavgTq + " +- " + (2.17 * iosdTq));
    System.out.println("Average I/O Bound Process Slowdown 97% CI: " + ioavgSlow + " +- " + (2.17 * iosdSlow));

    double finalWcpu = 0;
    double finalWio = 0;
    
    for(double[] qw: overallW){
      finalWcpu += qw[1];
      finalWio += qw[2];
    }
    
    finalWcpu = finalWcpu/overallW.size();
    finalWio = finalWio/overallW.size();
    System.out.println("average wcpu over the system is: " + finalWcpu);
    System.out.println("average wio over the system is: " + finalWio);
    System.out.println("Seed used: "+ SEED);
	}
	
}
