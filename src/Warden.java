import java.util.*;

public class Warden {

	private final Random random;
	private final List<Integer> history;
	private int counter;
	private final int numberOfPrisoners;

	public Warden(int n) {
		this.random = new Random();
		this.history = new ArrayList<>();
		this.counter = 0;
		this.numberOfPrisoners = n;
	}

	public Warden(List<Integer> history) {
		if (history.isEmpty()) {
			throw new IllegalArgumentException("Cannot determine number of prisoners from an empty history.");
		}
		this.random = new Random();
		this.history = history;
		this.counter = 0;
		this.numberOfPrisoners = Collections.max(history) + 1;
	}

	public int pickNextPrisoner() {
		int next;
		if (hasPredeterminedHistory()) {
			next = history.get(counter);
		} else { // once the Warden runs out of predetermined prisoners to send, they will resort to random selection
			next = pickRandomPrisoner();
			history.add(next);
		}
		counter++;
		return next;
	}

	public void eraseMemory() {
		history.clear();
		counter = 0;
	}

	public boolean hasPredeterminedHistory() {
		return counter < history.size();
	}

	public int daysPassed() {
		return counter;
	}
	
	public void setCounter(int c){
		this.counter = c;
	}
	
	public int pickRandomPrisoner() {
		return random.nextInt(numberOfPrisoners);
	}

	public int getNumberOfPrisoners() {
		return numberOfPrisoners;
	}

	public Random getRandom() {
		return random;
	}
}
