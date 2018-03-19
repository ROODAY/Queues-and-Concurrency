import java.util.ArrayList;

public class Process {
  public ArrayList<Request> history = new ArrayList<Request>();

  public void addStep(Request req) {
    history.add(req);
  }

  public double totalTq() {
    double sum = 0;
    for (Request r:history) {
      double val = r.getTq();
      if (val > 0) {
        sum += val;
      }
    }
    return sum;
  }

  public double totalTw() {
    double sum = 0;
    for (Request r:history) {
      double val = r.getTw();
      if (val > 0) {
        sum += val;
      }
    }
    return sum;
  }
}