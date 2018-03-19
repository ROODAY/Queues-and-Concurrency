
public class Request {

	private Double enteredWaitingQueue;
	//private double startedProcessing;
	//private double endedProcessing;
	
	private Double tw;
	private Double tq;
	
	public Request(double startTime) {
		// TODO Auto-generated constructor stub
		this.enteredWaitingQueue = startTime;
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
