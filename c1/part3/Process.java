import java.util.ArrayList;

public class Process {
  // Keeps track of which devices this process has visited
  public ArrayList<Request> history = new ArrayList<Request>();
   // 0 = cpu, 1 = i/o
  public int type;

  public Process(int type) {
    this.type = type;
  }
  public void addStep(Request req) {
    history.add(req);
  }

  // Sums Tq's of requests in history
  public double totalTq() {
    double sum = 0;
    for (Request r:history) {
      if (!r.isWaiting()){
        sum += r.getTq();
      }
    }
    return sum;
  }

  // Sums Tw's of requests in history
  public double totalTw() {
    double sum = 0;
    for (Request r:history) {
      if (!r.isWaiting()){
        sum += r.getTw();
      }
    }
    return sum;
  }
}