import java.util.Collections;
import java.util.LinkedList;
import java.util.Queue;


public class Controller {

	
	/**
	 * constants that define the mm1 controller
	 */
	public static final double MONITOR_INTERVAL = 10;
	public static final int SIMULATION_TIME = 1000;
	
	
	
	/**
	 * holds the processes in the mm1
	 */
	static Queue<Request> queue = new LinkedList<>();
	static LinkedList<Request> log = new LinkedList<Request>();
	static LinkedList<double[]> qAndW = new LinkedList<double[]>();
  static int[] rejections = new int[]{0,0};
	/**
	 * initialize the schedule with a birth event and a monitor event
	 * @return a schedule with two events
	 */
	public static LinkedList<Event> initSchedule(){
		LinkedList<Event> schedule = new LinkedList<Event>();
		
		double time = Event.getTimeOfNextBirth();
		schedule.add(new Event(time, EventType.BIRTH));
	
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

  public static double[] confidenceInterval(LinkedList<double[]> data, int index) {
      double sum = 0;

      for(double[] qw: data){
        sum += qw[index];
      }
      
      double avg = sum / data.size();
      double variance = 0.0;

      for(double[] qw: data){
        variance += Math.pow(qw[index] - avg, 2);
      }

      variance = variance / (data.size() - 1);
      double std = (double)Math.round((Math.sqrt(variance)) * 100000) / 100000;
      return new double[]{avg, 1.96 * std};
  }
	
	
	public static void main(String[] args){
		LinkedList<Event> schedule = initSchedule();
		
		double time = 0, maxTime = SIMULATION_TIME;
		while(time < maxTime){
			Event event = getNextEvent(schedule);
			time = event.getTime();
			event.function(schedule, queue, time, log, qAndW, rejections);
		}
		
		double finalQ = 0;
		double finalW = 0;
    double finalTq = 0;
    double finalTw = 0;
    double rej = 0;
		
		for(double[] qw: qAndW){
			finalQ += qw[0];
			finalW += qw[1];
      finalTq += qw[2];
      finalTw += qw[3];
		}
		
		finalQ = finalQ/qAndW.size();
		finalW = finalW/qAndW.size();
    finalTq = (double)Math.round((finalTq/qAndW.size()) * 100000) / 100000;
    finalTw = (double)Math.round((finalTw/qAndW.size()) * 100000) / 100000;
    rej = (double)Math.round(((double)rejections[0]/rejections[1]) * 100000) / 100000;
		
		System.out.println("************************************");
		System.out.println("************************************");
		System.out.println("************************************");

    double[] qconf = confidenceInterval(qAndW, 0);
    double[] tqconf = confidenceInterval(qAndW, 2);

		System.out.println("average q over the system is: " + finalQ + ". Conf: " + qconf[0] + " +- " + qconf[1]);
		System.out.println("average w over the system is: " + finalW);
    System.out.println("average Tq over the system is: " + finalTq + ". Conf: " + tqconf[0] + " +- " + tqconf[1]);
    System.out.println("average Tw over the system is: " + finalTw);
    System.out.println("average rejections over the system is: " + rej);
		
		
		System.out.println("---------------------");
	}
	
}
