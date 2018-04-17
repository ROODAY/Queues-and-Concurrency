public class TunnelMain {

	public static void main(String[] args) {
		int N = 20; // Number of threads/cars
		TunnelThread[] threads = new TunnelThread[N];

		for (int i = 0; i < N; i++) {
			threads[i] = new TunnelThread(i);
			threads[i].start();
		}
	}
}
