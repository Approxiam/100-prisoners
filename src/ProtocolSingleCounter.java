public class ProtocolSingleCounter extends Protocol{

	/*
	 * Brief description of the strategy: 
	 * The prisoners designate one of themselves as the counter, and all the rest become drones.
	 * Drones turn on the light the very first time they enter the yard with the light off, 
	 * otherwise they do nothing. Whenever the counter enters the yard with the light on, he/she turns it off.
	 * If the counter has turned off the light (n-1) times (where n is the number of prisoners),
	 * then he/she declares victory.
	 * 
	 * 	Roles:
	 *	0: Drone
	 *	1: Counter
	 */
	
	public ProtocolSingleCounter(){ 
		super("Egy lampaoltogato elore kivalasztva");
	}
	
	public void simulate(Warden W){
		int n = W.getNumberOfPrisoners();	//Initial setup
		Bulb b = new Bulb();			
		Prisoner[] p = new Prisoner[n];	
		for(int i = 0; i < n; i++){
			p[i]=new Prisoner();
		}
		int selected = W.returnRandom(n);	
		p[selected].setRole(1);		//Assign the role of the Counter to a randomly selected prisoner.
		
		
		do{
			selected = W.nextPrisoner();	//Select the next prisoner, that visits the yard.
			if(p[selected].getTimesInYard() == 0){
				victoryTreshold = W.days();
			}
			p[selected].visitYard();
			if(p[selected].getRole() == 1){		//The counter is selected.
				if(b.getLight()){				//If the light is on,
					p[selected].count(1);		//then the counter increments his/her count by 1
					p[selected].turnOFF(b);		//and turns the light off.
				}
			}
			else{	//A drone is selected.
				if(!(b.getLight()) && (p[selected].getTurnOnsRemaining() > 0)){	//If the light is off and the prisoner hasn't turned it on yet,
					p[selected].turnON(b);					//then he/she turns it on.
				}	
			}
		} while(!(p[selected].getPrisonersCounted() == n));	//Repeat until victory can be declared, this can only happen when the counter visits the yard.
		daysUntilVictory = W.days();
	}

}
