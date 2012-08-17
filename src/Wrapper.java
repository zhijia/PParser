
public class Wrapper {
	public static void main(String[] args) {

		long sum = 0;
		int iterations = 10;
		int warmupNum = 5;

		for(int i=0; i<iterations; i++){

		long start = System.nanoTime();
		Main.main(args);
		long end = System.nanoTime();

		if( i > warmupNum )
		sum += end - start;
		}

		System.out.println("ave: "+sum/(iterations-warmupNum));
	}
}
