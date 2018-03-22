import java.util.LinkedList;
import java.util.Queue;
import java.util.HashMap;
import java.util.ArrayList;

public class MM2 extends MM1 {

  // For I/O bound processes
  public final double LAMBDA2;
  public final double TS2;
  protected Queue<Request> queue2;

  public MM2(String name, LinkedList<Event> schedule, HashMap<String,Device> deviceList, ArrayList<Process> processes, LinkedList<double[]> wtotal, double lambda, double ts, double lambda2, double ts2){
    super(name, schedule, deviceList, processes, wtotal, lambda, ts);
    this.LAMBDA2 = lambda2;
    this.TS2 = ts2;
    this.queue2 = new LinkedList<>();
  }

  public int serving = 0;
  public Request[] servers = new Request[2];
  public double[] deaths = new double[2];

  @Override
  public void onDeath(Event ev, double timestamp){
    /**
     * request has finished and left the mm1
     */
    Request req;
    if (deaths[0] == timestamp) {
      req = servers[0];
      servers[0] = null;
    } else {
      req = servers[1];
      servers[1] = null;
    }
    req.setEndedProcessing(timestamp);
    log.add(req);
    ev.process.addStep(req);
    serving--;
    
    /**
     * look for another blocked event in the queue that wants to execute and schedule it's death.
     * at this time the waiting request enters processing time.
     */
    while(queue2.size() > serving && serving < 2){
      serving++;
      Request request = queue2.remove();
      request.setStartedProcessing(timestamp);
      double time = timestamp + getTimeOfNextDeath(request.process);
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

    while(queue.size() > serving && serving < 2){
      serving++;
      Request request = queue.remove();
      request.setStartedProcessing(timestamp);
      double time = timestamp + getTimeOfNextDeath(request.process);
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
    if (req.process.type == 0) {
      queue.add(req);
    } else {
      queue2.add(req);
    }
    
    
    /**
     * if the queue is empty then start executing directly there is no waiting time.
     */
    if (queue2.size() == 1 && serving < 2){
      serving++;
      Request request = queue2.remove();
      request.setStartedProcessing(timestamp);
      double time = timestamp + getTimeOfNextDeath(request.process);
      if (servers[0] == null) {
        servers[0] = request;
        deaths[0] = time;
      } else if (servers[1] == null) {
        servers[1] = request;
        deaths[1] = time;
      }
      Event event = new Event(time, EventType.DEATH, this, request.process);
      schedule.add(event);
    } else if (queue.size() == 1 && serving < 2){
      serving++;
      Request request = queue.remove();
      request.setStartedProcessing(timestamp);
      double time = timestamp + getTimeOfNextDeath(request.process);
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
      Process process = new Process(ev.process.type);
      processes.add(process);
      double time = getTimeOfNextBirth(process);
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
    for(Request r: queue2){
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
    qAndW[0] = queue.size() + queue2.size() + serving;
    qAndW[1] = wcpu;
    qAndW[2] = wio;
    
    QandW.add(qAndW);
    wtotal.add(qAndW);
  }

  public double getTimeOfNextBirth(Process proc){
    return proc.type == 0 ? Event.exp(LAMBDA) : Event.exp(LAMBDA2);
  }
  
  public double getTimeOfNextDeath(Process proc){
    return proc.type == 0 ? Event.exp(1.0/TS) : Event.exp(1.0/TS2);
  }

  @Override
  public void initializeScehduleWithOneEvent() {
    Process cpuProcess = new Process(0);
    processes.add(cpuProcess);
    Event cpuBirthEvent = new Event(getTimeOfNextBirth(cpuProcess), EventType.BIRTH, this, cpuProcess);
    cpuBirthEvent.setTag(true);
    schedule.add(cpuBirthEvent); 

    Process ioProcess = new Process(1);
    processes.add(ioProcess);
    Event ioBirthEvent = new Event(getTimeOfNextBirth(ioProcess), EventType.BIRTH, this, ioProcess);
    ioBirthEvent.setTag(true);
    schedule.add(ioBirthEvent); 
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
    System.out.println("utilization: " + (finalQ - (finalWcpu + finalWio))/2);
  }
}