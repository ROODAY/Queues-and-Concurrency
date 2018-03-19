
public class Request {

	private Double enteredWaitingQueue;
	//private double startedProcessing;
	//private double endedProcessing;
	
	private Double tw;
	private Double tq;

  public Process process;
  public int index;
	
	public Request(double startTime, Process process) {
		// TODO Auto-generated constructor stub
		this.enteredWaitingQueue = startTime;
    this.process = process;
	}
	
	public Double getEnteredWaitingQueue() {
		return enteredWaitingQueue;
	}
	public void setStartedProcessing(double startedProcessing) {
		tw = new Double(startedProcessing - enteredWaitingQueue);
		
	}
	
	public void setEndedProcessing(double endedProcessing) {
		tq = new Double(endedProcessing - enteredWaitingQueue);
	}
	
	public double getTw() {
		return tw != null ? tw : 0.0;
	}
	
	public double getTq() {
		return tq != null ? tq : 0.0;
	}
	
	public boolean isWaiting(){
		return tw == null;
	}
}
