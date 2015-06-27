import java.util.Scanner;
import java.io.*;

public class Main {
	private static int numberApplied = 0;
	private static int protocolNo = 0;
	public static Warden W;
	public static Scanner input = new Scanner(System.in);
	
	public static void main(String[] args) throws IOException {
		//choose strategy
		System.out.println("Valaszhato strategiak:"
				+ "\n1. Egy lampaoltogato"
				+ "\n2. Dinamikusan valsztott lampaoltogato");
		System.out.print("Melyik strategiat hasznaljuk? ");
		while(!input.hasNextInt()) {
			input.next();
		}
		protocolNo = input.nextInt();
		
		//choose input method
		System.out.print("Milyen modon valasszuk a rabok sorrendjet?"
				+ "\n1. Véletlen sorrendben"
				+ "\n2. Elore meghatarozott sorrendben (fajlbol olvasva)"
				+ "\n");
		while(!input.hasNextInt()) {
			input.next();
		}
		int selection = input.nextInt();
		
		if(selection == 1) {
			//choose number of prisoners
			System.out.print("Hany rab legyen? ");
			while(!input.hasNextInt()) {
				input.next();
			}
			W = new Warden(input.nextInt());
		} else {
			//choose input file name
			System.out.print("Mi a fajl neve? ");
			W = new Warden(input.next());	
		}
		
		//choose number applied
		System.out.print("Hanyszor alkalmazzuk? ");
		while(!input.hasNextInt()) {
			input.next();
		}
		numberApplied = input.nextInt();
		
		double sum = 0;
		double min = Long.MAX_VALUE;
		double max = Long.MIN_VALUE;
		
		Protocol prot;
		switch(protocolNo) {
			case 1: prot = new ProtocolSingleCounter();
					break;
			case 2: prot = new ProtocolDynamicCounter();
					break;
			default: prot = new ProtocolSingleCounter();
					System.out.println("Nem értelmezhetõ protokoll.");
					System.exit(0);
					break;
		}
		
		for(int i = 0; i < numberApplied; i++){
				prot.simulate(W);
				int specday = prot.getDaysUntilVictory();
				if(specday < min){
					min = specday;
					}
				if(specday > max){
					max = specday;
					}
				sum += specday;
				W.eraseMemory();
		}
		
		System.out.println("Atlag:   " + sum/numberApplied + " nap, azaz " + sum/(numberApplied*365) + " ev");
		System.out.println("Minimum: " + min + " nap, azaz " + min/365 + " ev");
		System.out.println("Maximum: " + max + " nap, azaz " + max/365 + " ev");
	}

}
