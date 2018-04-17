import java.util.*;

public class Driver {
    
    public static void main(String[] args) {
      PersonThread[] people = new PersonThread[50];
      ShuttleThread shuttle = new ShuttleThread(0);

      for(int i = 0; i < 50; i++) {
        people[i] = new PersonThread(i);
        people[i].start();
      }


      System.out.println("STARTING BUS");
      shuttle.start();
    }
}
