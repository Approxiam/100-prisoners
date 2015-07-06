import java.util.ArrayList;
import java.util.Scanner;
import java.io.*;

public class Main {
	private static int numberApplied = 0;
	private static int protocolNo = 0;
	public static Warden W;
	public static Scanner input = new Scanner(System.in);
	
	public static void main(String[] args) throws IOException {
		while( true ){
			//defining default values
			numberApplied = -1;
			protocolNo = -1;
			
			//choose strategy
			System.out.println("Valaszhato strategiak:"
					+ "\n1. Egy lampaoltogato"
					+ "\n2. Egy lampaoltogato, okos rabok"
					+ "\n3. Dinamikusan valsztott lampaoltogato"
					+ "\n4. Ketfazisu szamlalas (ujrainditassal)"
					+ "\n5. Ketfazisu szamlalas"
					+ "\n0. Kilepes");
			System.out.print("Melyik strategiat hasznaljuk? ");
			while( !(protocolNo>=0 && protocolNo<=5) ){
				while(!input.hasNextInt()) {
					input.next();
				}
				protocolNo = input.nextInt();
			}
			
			//exit
			if(protocolNo == 0){ 
				System.exit(1);
			}	
			
			//choose input method
			System.out.print("Milyen modon valasszuk a rabok sorrendjet?"
					+ "\n1. Veletlen sorrendben"
					+ "\n2. Elore meghatarozott sorrendben (fajlbol olvasva)"
					+ "\n");
			int selection = 0;
			while( !(selection == 1 || selection == 2)){
				while(!input.hasNextInt()) {
					input.next();
				}
				selection = input.nextInt();
			}
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
			
			//specialize for 3rd protocol aka BulkWithRestart
			int stage1Length = 0;
			int stage2Length = 0;
			int bulkSize = 0;
			if(protocolNo >= 4){
				//stageOneLenght
				System.out.print("Hany napos legyen az elso szakasz? ");
				while( !(stage1Length >0) ){
					while(!input.hasNextInt()) {
						input.next();
					}
					stage1Length = input.nextInt();
				}
				//stageTwoLength
				System.out.print("Hany napos legyen a masodik szakasz? ");
				while( !(stage2Length >0) ){
					while(!input.hasNextInt()) {
						input.next();
					}
					stage2Length = input.nextInt();
				}
				//bulkSize
				System.out.print("Mekkora lepesekben szamoljon a foszamlalo? ");
					//preparations
					ArrayList<Integer> divisors = new ArrayList<>();
					divisors.add(1);
					int n = W.getNumberOfPrisoners();
					for(int i = 2; i<= n/2; i++){
						if( (n-1)%i == 0 ){
							divisors.add(i);
						}
					}
					divisors.add(n-1);
					System.out.println(divisors);
					/*alternative:
					 * for( int d : divisors){
						System.out.print(d + ", ");
					}*/
				while( !(divisors.contains(bulkSize)) ){
					while(!input.hasNextInt()) {
						input.next();
					}
					bulkSize = input.nextInt();
				}
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
				default: System.out.println("Nem ertelmezheto protokoll"); // eleg elegans-e?
					 System.exit(1);
				case 1: prot = new ProtocolSingleCounter();
						break;
				case 2: prot = new ProtocolSCWithSmartDrones();
						break;
				case 3: prot = new ProtocolDynamicCounter();
						break;
				case 4: prot = new ProtocolBulkWithRestart(stage1Length, stage2Length, bulkSize);
						break;
				case 5: prot = new ProtocolBulkWithLoop(stage1Length, stage2Length, bulkSize);
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

}
