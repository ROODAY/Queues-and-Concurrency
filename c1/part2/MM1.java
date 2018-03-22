import java.util.LinkedList;
import java.util.Queue;
import java.util.HashMap;
import java.util.ArrayList;

public class MM1 extends Device {
	
	public final double LAMBDA;
	public final double TS;
	
	protected Queue<Request> queue;
	protected LinkedList<Event> schedule;
  protected ArrayList<Process> processes;
  protected HashMap<String,Device> deviceList; 
  protected LinkedList<double[]> wtotal;
	
	//saves all the previous requests
	protected LinkedList<Request> log;
	
	//saves the q and w of the queue, at the time the monitor event occurs
	LinkedList<double[]>QandW;
		
	
	public MM1(String name, LinkedList<Event> schedule, HashMap<String,Device> deviceList, ArrayList<Process> processes, LinkedList<double[]> wtotal, double lambda, double ts){
		super(name);
		
		this.queue = new LinkedList<>();
		this.QandW = new LinkedList<>();
		this.log = new LinkedList<>();
		
		this.schedule = schedule;
    this.deviceList = deviceList;
    this.processes = processes;
    this.wtotal = wtotal;
		this.LAMBDA = lambda;
		this.TS = ts;

	}
	
	public double getTS() {
		return TS;
	}
	public LinkedList<Request> getLog() {
		return log;
	}
	
	public Queue<Request> getQueue() {
		return queue;
	}

  public Device getNextDevice() {
    double prob = Controller.generator.nextDouble();
    Device device;
    if (this.name.equals("cpu")) {
      if (prob < 0.5) {
        device = null;
      } else if (prob < 0.9) {
        device = deviceList.get("net");
      } else {
        device = deviceList.get("disk");
      }
    } else if (this.name.equals("disk")) {
      if (prob < 0.5) {
        device = deviceList.get("cpu");
      } else {
        device = deviceList.get("net");
      }
    } else if (this.name.equals("net")) {
      device = deviceList.get("cpu");
    } else {
      device = null;
    }
    return device;
  }
	
	
	/**
	 * called when a death event happens
	 */
	public void onDeath(Event ev, double timestamp){
		/**
		 * request has finished and left the mm1
		 */
		Request req = queue.remove();
		req.setEndedProcessing(timestamp);
		log.add(req);
    ev.process.addStep(req);
		
		/**
		 * look for another blocked event in the queue that wants to execute and schedule it's death.
		 * at this time the waiting request enters processing time.
		 */
		if(queue.size() > 0){
			double timeOfNextDeath = timestamp + getTimeOfNextDeath();
			queue.peek().setStartedProcessing(timestamp);
			Event event = new Event(timeOfNextDeath, EventType.DEATH, this, queue.peek().process);
			schedule.add(event);
		}

    Device nextDevice = getNextDevice();
    if (nextDevice != null) {
      Event event = new Event(timestamp, EventType.BIRTH, nextDevice, ev.process);
      schedule.add(event);
    }
	}
	
	/**
	 * called when a birth event happens.
	 */
	public void onBirth(Event ev, double timestamp){
		Request request = new Request(timestamp, ev.process);
		queue.add(request);
		
		
		/**
		 * if the queue is empty then start executing directly there is no waiting time.
		 */
		if(queue.size() == 1){
			
			request.setStartedProcessing(timestamp);
			Event event = new Event(timestamp + getTimeOfNextDeath(), EventType.DEATH, this, request.process);
			schedule.add(event);
		}
		
		if(ev.getTag()){
			/**
			 * schedule the next arrival
			 */
      Process process = new Process(0);
      processes.add(process);
			double time = getTimeOfNextBirth();
			Event event = new Event(timestamp + time, EventType.BIRTH, this, process);
			event.setTag(true);
			schedule.add(event);
		}
	}
	
	/**
	 * called when a monitor event happens
	 */
	public void onMonitor(double timestamp, double startTime){
  
    if(timestamp < startTime) {
      //don't start lagging before the start time
      //clear the logs
      log.clear(); 
      return;
    }

    //count the number of waiting requests
    double wcpu = 0;
    double wio = 0;
    for(Request r: queue){
      if(r.isWaiting()) {
        if (r.process.type == 0) {
          wcpu++;
        } else {
          wio++;
        }
      }
    }
    
    //System.out.println("Monitor Event at time:" + timestamp);
    //System.out.println("---------------------");
    double[] qAndW = new double[3];
    qAndW[0] = queue.size();
    qAndW[1] = wcpu;
    qAndW[2] = wio;
    
    QandW.add(qAndW);
    wtotal.add(qAndW);
  }
	
	/**
	 * 
	 * @return time for the next birth event
	 */
	public double getTimeOfNextBirth(){
		return Event.exp(LAMBDA);
	}
	
	/**
	 * 
	 * @return time for the next death event
	 */
	public double getTimeOfNextDeath(){
		return Event.exp(1.0/TS);
	}

	
	/**
	 * initializes the device with an event
	 */
	@Override
	public void initializeScehduleWithOneEvent() {
		double time = getTimeOfNextBirth();
    Process process = new Process(0);
    processes.add(process);
		Event birthEvent = new Event(time, EventType.BIRTH, this, process);
		birthEvent.setTag(true);
		schedule.add(birthEvent);	
	}

	@Override
	public void printStats() {
		double Tw = 0;
		double Tq = 0;

		for(Request r: log){
			Tw += r.getTw();
			Tq += r.getTq();
		}
		Tq = Tq/log.size();
		Tw = Tw/log.size();
		
		
		double finalQ = 0;
    double finalWcpu = 0;
    double finalWio = 0;
    
    for(double[] qw: QandW){
      finalQ += qw[0];
      finalWcpu += qw[1];
      finalWio += qw[2];
    }
    
    finalQ = finalQ/QandW.size();
    finalWcpu = finalWcpu/QandW.size();
    finalWio = finalWio/QandW.size();
    
    System.out.println("************************************");
    System.out.println("************************************");
    System.out.println("************************************");
    
    System.out.println("Device name: " + getName());
    System.out.println("Tw: "+ Tw);
    System.out.println("Tq: "+ Tq);
    System.out.println("average q over the system is: " + finalQ);
    System.out.println("average wcpu over the system is: " + finalWcpu);
    System.out.println("average wio over the system is: " + finalWio);
    System.out.println("utilization: " + (finalQ - (finalWcpu + finalWio)));
	}

}
