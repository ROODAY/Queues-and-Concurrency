import java.util.LinkedList;
import java.util.Queue;
import java.util.HashMap;
import java.util.ArrayList;

public class MM2 extends MM1 {

  public MM2(String name, LinkedList<Event> schedule, HashMap<String,Device> deviceList, ArrayList<Process> processes, double lambda, double ts){
    super(name, schedule, deviceList, processes, lambda, ts);
  }

  // Keeps track of how many cores are in use
  public int serving = 0;
  // Holds the requests currently being serviced
  public Request[] servers = new Request[2];
  // Saves when currently serviced requests should die so we remove them properly
  public double[] deaths = new double[2];

  @Override
  public void onDeath(Event ev, double timestamp){
    Request req;
    // Check the deaths to current time to ensure we remove the correct request
    if (deaths[0] == timestamp) {
      req = servers[0];
      servers[0] = null;
    } else {
      req = servers[1];
      servers[1] = null;
    }
    req.setEndedProcessing(timestamp);
    log.add(req);
    ev.process.addStep(req); // Inform process that request has finished with device
    serving--;
    
    /**
     * look for another blocked event in the queue that wants to execute and schedule it's death.
     * at this time the waiting request enters processing time.
     * makes sure that servers are free as well
     */
    while(queue.size() > serving && serving < 2){
      serving++;
      Request request = queue.remove();
      request.setStartedProcessing(timestamp);
      double time = timestamp + getTimeOfNextDeath();

      // Check which server is free and allocate it to request
      if (servers[0] == null) {
        servers[0] = request;
        deaths[0] = time;
      } else if (servers[1] == null) {
        servers[1] = request;
        deaths[1] = time;
      }
      Event event = new Event(time, EventType.DEATH, this, request.process);
      schedule.add(event);
    }

    // Determine next device to send finished request to and create birth event for that device
    Device nextDevice = getNextDevice();
    if (nextDevice != null) {
      Event event = new Event(timestamp, EventType.BIRTH, nextDevice, ev.process);
      schedule.add(event);
    }
  }
  
  /**
   * called when a birth event happens.
   */
  @Override
  public void onBirth(Event ev, double timestamp){
    Request req = new Request(timestamp, ev.process);
    queue.add(req);
    
    /**
     * if the queue is empty and servers are available then start executing directly there is no waiting time.
     */
    if (queue.size() == 1 && serving < 2){
      serving++;
      Request request = queue.remove();
      request.setStartedProcessing(timestamp);
      double time = timestamp + getTimeOfNextDeath();

      // Check to see which server is free
      if (servers[0] == null) {
        servers[0] = request;
        deaths[0] = time;
      } else if (servers[1] == null) {
        servers[1] = request;
        deaths[1] = time;
      }
      Event event = new Event(time, EventType.DEATH, this, request.process);
      schedule.add(event);
    }
    
    if(ev.getTag()){
      /**
       * schedule the next arrival
       */
      Process process = new Process(); // Create new process and add to list
      processes.add(process);
      double time = getTimeOfNextBirth();
      Event event = new Event(timestamp + time, EventType.BIRTH, this, process);
      event.setTag(true);
      schedule.add(event);
    }
  }

  @Override
  public void onMonitor(double timestamp, double startTime){
  
    if(timestamp < startTime) {
      //don't start lagging before the start time
      //clear the logs
      log.clear(); 
      return;
    }

    //count the number of waiting requests
    double w = 0;
    for(Request r: queue){
      if(r.isWaiting()) w++;
    }

    double[] qAndW = new double[2];
    qAndW[0] = queue.size() + serving; // Make sure to keep track of currently serviced requests
    qAndW[1] = w;
    
    QandW.add(qAndW);
  }

  @Override
  public void printStats() {
    double Tw = 0;
    double Tq = 0;
    double count = 0;

    for(Request r: log){
      if (!r.isWaiting()) { // make sure we only count completed requests
        Tw += r.getTw();
        Tq += r.getTq();
        count++;
      }
    }
    Tq = Tq/count;
    Tw = Tw/count;
    
    
    double finalQ = 0;
    double finalW = 0;
    
    for(double[] qw: QandW){
      finalQ += qw[0];
      finalW += qw[1];
    }
    
    finalQ = finalQ/QandW.size();
    finalW = finalW/QandW.size();
    
    System.out.println("************************************");
    System.out.println("************************************");
    System.out.println("************************************");
    
    System.out.println("Device name: " + getName());
    System.out.println("Tw: "+ Tw);
    System.out.println("Tq: "+ Tq);
    System.out.println("average q over the system is: " + finalQ);
    System.out.println("average w over the system is: " + finalW);
    System.out.println("utilization: " + (finalQ - finalW)/2);
  }
}