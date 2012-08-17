import java.io.*;
import java.util.*;

public class Main {

	String input;
	Parser[] parsers;
	static int numThreads = 1;
	Remain[] remains;
	String[] inputs;

	void init(String fileName) {
		String line = "";
		try {
			Scanner scanner = new Scanner(new FileInputStream(fileName));
			scanner.useDelimiter("//Z");
			line += scanner.next();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		input = line;
		parsers = new Parser[numThreads];
		inputs = partition();
		remains = new Remain[numThreads];
		System.out.println("finished loading file.");
	}

	String[] partition() {
		String[] inputs = new String[numThreads];
		int length = input.length();
		int start = 0;
		int end = 0;
		int step = length / numThreads;
		for (int i = 0; i < numThreads; i++) {
			start = end;
			end = start + step;
			if (end >= length)
				end = length;
			else {
				// find a good partition point i.e. after '>'
				while (input.charAt(end) != '>') {
					end++;
				}
				end++;
			}
			inputs[i] = input.substring(start, end);
		}
		// for(int i=0; i<numThreads; i++)
		// System.out.println("input["+i+"]"+inputs[i]);

		return inputs;
	}

	void process() {
		for (int i = 0; i < numThreads; i++) {
			remains[i] = new Remain();
			parsers[i] = new Parser("parser[" + i + "]", inputs[i], remains[i]);
		}

		for (int i = 0; i < numThreads; i++) {
			parsers[i].start();
		}

		for (int i = 0; i < numThreads; i++) {
			try {
				parsers[i].join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		if (numThreads > 1) {
			long start = System.nanoTime();
			Node root = (new PostProcessor(remains)).process();
			System.out.println("postprocessing time: "
					+ (System.nanoTime() - start));
			// Parser.treePrintToDot(root, "tree.dot");
		}
		// else
		// {
		// Parser.treePrintToDot(remains[0].forest.get(0), "tree.dot");
		// }
	}

	public static void main(String[] args) {
		Main pparser = new Main();
		pparser.init(args[0]);

		long sum = 0;
		for (int i = 0; i < 10; i++) {
			long start = System.nanoTime();
			pparser.process();
			if (i > 5)
				sum += System.nanoTime() - start;
		}
		System.out.println("parsing time ave: " + (sum / (10 - 5)));
	}

}
