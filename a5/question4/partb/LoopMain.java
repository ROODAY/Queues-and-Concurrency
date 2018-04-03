public class LoopMain {
  public static void main(String[] args) {
    LoopThread[] processes = new LoopThread[LoopThread.N];

    for(int i = 0; i < LoopThread.N; i++) {
      processes[i] = new LoopThread(i);
      processes[i].start();
    }
  }
}