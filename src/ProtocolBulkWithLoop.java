import java.util.Collections;
import java.util.Stack;

/*
 * Roles:
 * 	-1: Head Counter
 * 	 n: Assistant Counter responsible for n bulk counts (n > 0)
 * 	 0: Drone
 * 
 */

public class ProtocolBulkWithLoop extends Protocol {
	int stageOneLength;
	int stageTwoLength;
	public int cycleLength;
	public int bulkSize; //TODO should be a divisor of 'numberofprisoners-1', should be done in main
	public boolean reset = true;
	
	public ProtocolBulkWithLoop(int stageOneLength, int stageTwoLength, int bulkSize){ 
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
		for(int i = 0; i < n; i++){//drones, role: 0
			p[i] = new Prisoner();
		}
		int selected = -1;
		Stack<Integer> stackForIntegers = new Stack<>();
		for(int i = 0; i < n; i++){
			stackForIntegers.add(i);
		}
		Collections.shuffle(stackForIntegers); //for the sake of randomness
		int j = -1;
		j = stackForIntegers.pop();// 1 HeadCounter, role: 1
		p[j].setRole(-1);
		p[j].setTurnOnsRemaining(0);
		for(int i = 0; i < numberOfAssistants ; i++){ //AssistantCounters, role: 2
			j = stackForIntegers.pop();
			p[j].setRole(1);
		}
		do{
			selected = W.nextPrisoner();
			if(p[selected].getTimesInYard() == 0){
				victoryTreshold = W.days();
			}
			p[selected].visitYard();
			//first stage except for the last day of first stage
			if( ( W.days() % cycleLength > 0 ) && ( W.days() % cycleLength < stageOneLength ) ){
				if(p[selected].getRole() == -1){	//HeadCounter
					if(p[selected].getTurnOnsRemaining() > 0 && !b.getLight()){
						p[selected].turnON(b);
					} else {
						p[selected].doNothing();
					}
				}
				else if(p[selected].getRole() >= 1){	//Assistant Counter
					if(b.getLight() && p[selected].getPrisonersCounted() < p[selected].getRole()*bulkSize){
						p[selected].turnOFF(b);
						p[selected].count(1);
					}
					else {
						p[selected].doNothing();
					}
				}
				else{	//Drone
					if( p[selected].getTurnOnsRemaining() > 0 && !b.getLight() ){
						p[selected].turnON(b);
					}
					else {
						p[selected].doNothing();
					}
				}		
			}
			//last day of first stage
			else if( W.days() % cycleLength == stageOneLength ){
				if(p[selected].getRole() == -1){ //HeadCounter
					if(b.getLight()){
						p[selected].setTurnOnsRemaining(p[selected].getTurnOnsRemaining() + 1);
					} else {
						p[selected].doNothing();
					}
				}
				else if(p[selected].getRole() >= 1){ //Assistant Counter
					if(b.getLight()){
						if(p[selected].getPrisonersCounted() < bulkSize - 1){
							p[selected].turnOFF(b);
							p[selected].count(1);
						}
						else if(p[selected].getPrisonersCounted() >= bulkSize - 1) {
							p[selected].count(1 - bulkSize);
							p[selected].setRole(p[selected].getRole() - 1);
							p[selected].turnON(b);
							if(p[selected].getRole() == 0){
								p[selected].setTurnOnsRemaining(p[selected].getPrisonersCounted());
							}
						}
					}
					else {	//The light is OFF.
						if(p[selected].getPrisonersCounted() >= bulkSize) {
							p[selected].count(-bulkSize);
							p[selected].setRole(p[selected].getRole() - 1);
							p[selected].turnON(b);
							if(p[selected].getRole() == 0){
								p[selected].setTurnOnsRemaining(p[selected].getPrisonersCounted());
							}
						} else {
							p[selected].doNothing();
						}
					}
				}
				else{ //drone
					if(b.getLight()){
						p[selected].turnOFF(b);
						p[selected].setTurnOnsRemaining(p[selected].getTurnOnsRemaining() + 1);
					}
					else {
						p[selected].doNothing();
					}
				}
			}
			//days of second stage, except for the last day of second stage
			else if( W.days() % cycleLength != 0){
				if(p[selected].getRole() == -1){ //HeadCounter
					if(b.getLight()){
						p[selected].count(bulkSize);//counts in bulkSize-sized units
						p[selected].turnOFF(b);
					}
					else {
						p[selected].doNothing();
					}
				}
				else if(p[selected].getRole() >= 1){ //Assistant Counter
					if( !b.getLight() && p[selected].getPrisonersCounted() >= bulkSize ){
						p[selected].count(-bulkSize);
						p[selected].setRole(p[selected].getRole() - 1);
						p[selected].turnON(b);
						if(p[selected].getRole() == 0){
							p[selected].setTurnOnsRemaining(p[selected].getPrisonersCounted());
						}
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
				if(p[selected].getRole() == -1){ //Head Counter
					if(b.getLight()){
						p[selected].count(bulkSize);	//counts in bulkSize-sized units
						p[selected].turnOFF(b);
					}
					else {
						p[selected].doNothing();
					}
				}
				else {	//Assistant Counter or Drone
					if(b.getLight()){
						p[selected].count(bulkSize);
						p[selected].setRole(p[selected].getRole() + 1);
						p[selected].turnOFF(b);
					}
				}
			}
		} while( !(p[selected].getPrisonersCounted() == n) );	//repeat until victory can be declared
		daysUntilVictory = W.days();
	}
		
}

