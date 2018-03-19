import java.util.LinkedList;
import java.util.Queue;


public class Event implements Comparable<Event>{
	
	private double time;
	
	private static final double LAMBDA = 60;
	private static final double TS = 0.015;
  private static final double K = 3;
  private static final boolean constantTs = false;
	
	private EventType eventType;
		

	
	public double getTime(){
		return time;
	}
	
	
	/**
	 * Auto increment id for bith events
	 * @param time when the event starts
	 * @param eventType Monitor or Birth
	 * @param controller links to the specific controller instance i am working with
	 */
	public Event(double time, EventType eventType){
		
		this.time = time;
		this.eventType = eventType;
		
	}

		
	/**
	 * 
	 * @param schedule contains all the scheduled future events
	 * @param queue holds pending requests
	 * @param timestamp current time in the discrete simulation
	 */
	public void function(LinkedList<Event> schedule, Queue<Request> queue, double timestamp, LinkedList<Request> log, LinkedList<double[]> QandW, int[] rejections){		
		schedule.remove(this);
		
		switch (eventType){
			case DEATH:
				/**
				 * request has finished and left the mm1
				 */
				Request req = queue.remove();
				req.setEndedProcessing(timestamp);
				log.add(req);
				
				/**
				 * look for another blocked event in the queue that wants to execute and schedule it's death.
				 * at this time the waiting request enters processing time.
				 */
				if(queue.size() > 0){
					double timeOfNextDeath = timestamp + getTimeOfNextDeath();
					queue.peek().setStartedProcessing(timestamp);
					Event event = new Event(timeOfNextDeath, EventType.DEATH);
					schedule.add(event);
				}
			break;
			
			
			case BIRTH:
				/**
				 * a request has been added to the queue, it's now waiting to execute
				 */
				Request request = new Request(timestamp);
        rejections[1]++;
        if (queue.size() < K) {
          queue.add(request);
          
          
          /**
           * if the queue is empty then start executing directly there is no waiting time.
           */
          if(queue.size() == 1){
            
            request.setStartedProcessing(timestamp);
            schedule.remove(this);
            Event event = new Event(timestamp + getTimeOfNextDeath(), EventType.DEATH);
            schedule.add(event);
          }
        } else {
          rejections[0]++;
        }
  				
				
				/**
				 * schedule the next arrival
				 */
				double time = getTimeOfNextBirth();
				Event event = new Event(timestamp + time, EventType.BIRTH);
				schedule.add(event);
			break;
			
			
			case MONITOR:
				/**
				 * every fixed interval of time this function is called.
				 * Do some logs...
				 * Schedule another monitor event with the same interval.
				 */
				double Tw = 0;
				double Tq = 0;

				for(Request r: log){
					Tw += r.getTw();
					Tq += r.getTq();
				}
				Tq = Tq/log.size();
				Tw = Tw/log.size();
				
				//count the number of waiting requests
				double w = 0;
				for(Request r: queue){
					if(r.isWaiting()) w++;
				}
				
				/*System.out.println("Monitor Event at time:" + timestamp);
				System.out.println("Tw: "+ Tw);
				System.out.println("Tq: "+ Tq);
				System.out.println("q : "+ queue.size());
				System.out.println("w : "+ w);
				
				System.out.println("---------------------");*/
				double[] qAndW = new double[4];
				qAndW[0] = queue.size();
				qAndW[1] = w;
        qAndW[2] = Tq;
        qAndW[3] = Tw;
				
				QandW.add(qAndW);
				schedule.add(new Event(timestamp + Controller.MONITOR_INTERVAL, EventType.MONITOR));				
			break;
		}
	}
	
	/**
	 * used to be able to sort according to start time
	 * used indirectly by  {@link Controller#getNextEvent(LinkedList<Event> schedule)} 
	 */
	@Override
	public int compareTo(Event other) {
		if(this.time == other.getTime()) return 0;
		else if(this.time > other.getTime()) return 1;
		else return -1;
	}
	
	
	/**
	 * exponential distribution
	 * used by {@link #getTimeOfNextBirth()} and {@link #getTimeOfNextDeath()} 
	 * @param rate
	 * @return
	 */
	private static double exp(double rate){
		return (- Math.log(1.0 - Math.random()) / rate);
	}
	
	/**
	 * 
	 * @return time for the next birth event
	 */
	public static double getTimeOfNextBirth(){
		return exp(LAMBDA);
	}
	/**
	 * 
	 * @return time for the next death event
	 */
	public static double getTimeOfNextDeath(){
		return constantTs ? TS : exp(1.0/TS);
	}
}
