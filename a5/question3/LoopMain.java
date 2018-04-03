public class LoopMain {
  public static void main(String[] args) {
    LoopThread[] processes = new LoopThread[LoopThread.K];

    for(int i = 0; i < LoopThread.K; i++) {
      processes[i] = new LoopThread(i);
      processes[i].start();
    }
  }
}