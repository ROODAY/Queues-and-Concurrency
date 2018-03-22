
public class Request {

	private Double enteredWaitingQueue;
	private Double tw;
	private Double tq;

  public Process process;
	
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
		return tw;
	}
	
	public double getTq() {
		return tq;
	}
	
	public boolean isWaiting(){
		return tw == null;
	}
}
