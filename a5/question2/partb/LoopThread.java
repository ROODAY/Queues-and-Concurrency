import java.util.Arrays;

class LoopThread extends Thread {
  public static final int N = 5;
  private int id;
  private static volatile boolean[] choosing = new boolean[N];
  private static volatile int[] ticket = new int[N];

  public LoopThread(int id) {
   this.id = id;
  }
  
  public void run() {
    try {
      for (int i = 0; i < 5; i++) {
        choosing[this.id] = true;
        ticket[this.id] = Arrays.stream(ticket).max().getAsInt() + 1;
        choosing[this.id] = false;

        for (int j = 0; j < N; j++) {
          while( choosing[j] ) {
            Thread.sleep(1);
          }
          while( ticket[j] != 0 && ( (ticket[j] < ticket[this.id]) || (ticket[j] == ticket[this.id] && j < this.id) ) ) {
            Thread.sleep(1);
          }
        }

        System.out.println("Thread " + this.id + " is starting iteration " + i +".");
        Thread.sleep((long) Math.random() * 20);

        System.out.println("We hold these truths to be self-evident, that all men are created equal,");
        Thread.sleep((long) Math.random() * 20);

        System.out.println("that they are endowed by their Creator with certain unalienable Rights,");
        Thread.sleep((long) Math.random() * 20);

        System.out.println("that among these are Life, Liberty and the pursuit of Happiness.");
        Thread.sleep((long) Math.random() * 20);

        System.out.println("Thread " + this.id + " is done with iteration " + i +".");

        ticket[this.id] = 0;
      }
    } catch(InterruptedException e) {
      e.printStackTrace();
    }
  }
}