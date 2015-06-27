import java.util.Collections;
import java.util.Stack;


public class ProtocolBulkWithRestart extends Protocol {
	int stageOneLength;
	int stageTwoLength;
	public int cycleLength;
	public int bulkSize; //TODO should be a divisor of 'numberofprisoners-1', should be done in main
	public boolean reset = true;
	
	public ProtocolBulkWithRestart(int stageOneLength, int stageTwoLength, int bulkSize){ 
		super("BulkWithRestart-ReadyToBeNamed");
		this.stageOneLength = stageOneLength;
		this.stageTwoLength = stageTwoLength;
		cycleLength = this.stageOneLength + this.stageTwoLength;
		this.bulkSize = bulkSize;
	}
	
	public void simulate(Warden W){
		int n = W.getNumberOfPrisoners();
		Bulb b = new Bulb();			
		int numberOfAssistants = (n-1)/bulkSize; //bulkSize was made to be a divisor of n-1 (in main)
		Prisoner[] p = new Prisoner[n];
		Stack<Integer> stackForIntegers = new Stack<>();
		for(int i = 0; i<n; i++){
			stackForIntegers.add(i);
		}
		Collections.shuffle(stackForIntegers); //for the sake of randomness
		int selected = -1;
		
		do{
			if(reset){
				int j = -1;
				for(int i = 0; i < n; i++){//drones, role: 0
					p[i] = new Prisoner();
				}
				
				j = stackForIntegers.pop();// 1 HeadCounter, role: 1
				p[j].setRole(1);
				
				for(int i = 0; i < numberOfAssistants ; i++){ //AssistantCounters, role: 2
					j = stackForIntegers.pop();
					p[j] = new Prisoner();
					p[j].setRole(2);
				}
				
				reset = false;
			}
			
			selected = W.nextPrisoner();
			
			if(p[selected].getTimesInYard() == 0){
				victoryTreshold = W.days();
			}
			p[selected].visitYard();
			
			//first stage except for the last day of first stage
			if( ( W.days() % cycleLength > 0 ) && ( W.days() % cycleLength < stageOneLength ) ){
				if( p[selected].getRole() == 1 ){ //HeadCounter
					p[selected].doNothing();
				}
				else if( p[selected].getRole() == 2 ){ //Assistant Counter
					if(b.getLight() && p[selected].getPrisonersCounted() < bulkSize ){
						p[selected].turnOFF(b);
						p[selected].count(1);
					}
					else {
						p[selected].doNothing();
					}
				}
				else{//drone
					if( p[selected].getTurnOnsRemaining() > 0 && !b.getLight() ){
						p[selected].turnON(b);
						p[selected].setTurnOnsRemaining(0);
					}
					else {
						p[selected].doNothing();
					}
				}		
			}
			//last day of first stage
			else if( W.days() % cycleLength == stageOneLength ){
				if( p[selected].getRole() == 1 ){ //HeadCounter
					p[selected].doNothing();
				}
				else if( p[selected].getRole() == 2 ){ //Assistant Counter
					if( b.getLight() &&  p[selected].getPrisonersCounted() == bulkSize - 1 ){
						p[selected].setPrisonersCounted(0);
					}
					else if( !b.getLight() && p[selected].getPrisonersCounted() == bulkSize ){//TODO bulb-check needed?
						p[selected].turnON(b);
						p[selected].setPrisonersCounted(0);
					}
					else if(b.getLight()){
						p[selected].turnOFF(b);
						p[selected].count(1);
					} else {
						p[selected].doNothing();
					}
				}
				else{ //drone
					p[selected].turnOFF(b);
				}
			}
			//days of second stage, except for the last day of second stage
			else if( W.days() % cycleLength != 0){
				if( p[selected].getRole() == 1 ){ //HeadCounter
					if(b.getLight()){
						p[selected].count(bulkSize);//counts in bulkSize-sized units
						p[selected].turnOFF(b);
					}
				}
				else if( p[selected].getRole() == 2 ){ //Assistant Counter
					if( !b.getLight() && p[selected].getPrisonersCounted() == bulkSize ){
						p[selected].turnON(b);
						p[selected].setPrisonersCounted(0);
					}
					else {
						p[selected].doNothing();
					}
				}
				else{ //drone
					p[selected].doNothing();
					}	
			}
			//last day of second stage (i.e. the cycle)
			else if( W.days() % cycleLength == 0){
				reset = true;
			}
			
		} while( !(p[selected].getPrisonersCounted() == n) );	//repeat until victory can be declared
		daysUntilVictory = W.days();
	}
		
}

