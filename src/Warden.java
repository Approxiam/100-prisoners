import java.io.*;
import java.util.Random;
import java.util.ArrayList;

public class Warden {

	private String name;
	private Random random;
	private ArrayList<Integer> history;
	private int counter;
	private int numberOfPrisoners;
	
	public Warden(int n){
		random = new Random();
		history = new ArrayList<>();
		counter = 0;
		numberOfPrisoners = n;
	}
	
	public Warden(String source) throws IOException {
		random = new Random();
		history = new ArrayList<>();
		counter = 0;
		numberOfPrisoners = 0;
		BufferedReader input = new BufferedReader(new FileReader(source));
		String line = null;
		while((line = input.readLine()) != null){
			int prisoner = Integer.parseInt(line); 
			history.add(prisoner);
			if(prisoner > numberOfPrisoners - 1){
				numberOfPrisoners = prisoner + 1;
			}
		}
		input.close();
	}
	
	public int nextPrisoner(){
		int next = -1;
		if(counter < history.size()){
			next = history.get(counter);
		} 
		else {
			next = random.nextInt(numberOfPrisoners);
			history.add(next);
		}
		counter++;
		return next;
	}
	
	public void eraseMemory(){
		history.clear();
		counter = 0;
	}
	
	public int days(){
		return counter;
	}
	
	public int returnRandom(int max){
		return random.nextInt(max);  
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getCounter() {
		return counter;
	}

	public void setCounter(int counter) {
		this.counter = counter;
	}

	public int getNumberOfPrisoners() {
		return numberOfPrisoners;
	}

	public void setNumberOfPrisoners(int numberOfPrisoners) {
		this.numberOfPrisoners = numberOfPrisoners;
	} 
	
}
