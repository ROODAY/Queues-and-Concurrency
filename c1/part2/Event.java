import java.util.LinkedList;

public class Event implements Comparable<Event>{
	
	private double time;
	private EventType eventType;
	private Device device;	
  public Process process;

	/**
	 * tag used to differentiate between birth events generated from the device through 
	 * the lambda arrival rate or coming from another system
	 */
	private boolean tag;
	
	/**
	 * @return time of execution
	 */
	public double getTime(){
		return time;
	}
	
	/**
	 * create a regular event
	 * @param time
	 * @param eventType
	 */
	public Event(double time, EventType eventType){
		this.time = time;
		this.eventType = eventType;
		
	}
	
	/** connect an event with an mm1
	 * @param time when the event starts
	 * @param eventType Monitor, Birth or death
	 * @param mm1 
	 */
	public Event(double time, EventType eventType, Device device, Process process){
		this(time, eventType);
		this.device = device;	
    this.process = process;
	}

		
	/**
	 * 
	 * @param schedule contains all the scheduled future events
	 * @param queue holds pending requests
	 * @param timestamp current time in the discrete simulation
	 */
	public void function(LinkedList<Event> schedule, double timestamp){		
		schedule.remove(this);
		
		switch (eventType){
			case DEATH:
				//notify the device of a death
				device.onDeath(this, timestamp);
			break;
			
			
			case BIRTH:
				//notify the device of a birth
				device.onBirth(this, timestamp);
			break;
			
			
			case MONITOR:
				for(Device device: Controller.devices){
					//notify all the devices of a monitor event
					device.onMonitor(timestamp, Controller.LOGGING_START_TIME);
				}
				//schedule another event according to pasta
				schedule.add(new Event(timestamp + exp(1/Controller.MONITOR_INTERVAL), EventType.MONITOR));				
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
	public static double exp(double rate){
		return (- Math.log(1.0 - Controller.generator.nextDouble()) / rate);
	}

	public boolean getTag() {
		return tag;
	}

	public void setTag(boolean tag) {
		this.tag = tag;
	}
	
}
