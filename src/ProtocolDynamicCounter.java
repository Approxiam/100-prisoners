public class ProtocolDynamicCounter extends Protocol{

	/*	Roles:
	 *	0: Drone
	 *	1: Counter
	 */
	
	public ProtocolDynamicCounter(){ 
		super("Egy lampaoltogato dinamikusan valasztva");
	}
	
	public void simulate(Warden W){
		int n = W.getNumberOfPrisoners();
		Bulb b = new Bulb();			//Initiate a light bulb
		Prisoner[] p = new Prisoner[n];	//Line up the prisoners
		for(int i = 0; i < n; i++){
			p[i]=new Prisoner();
		}
		int selected = 0;	
		
		for(int i = 0; i < n; i++){	//counter selection rounds
			selected = W.nextPrisoner();
			if(!b.getLight()){
				if(p[selected].getTimesInYard() == 0){
					p[selected].visitYard();
					victoryTreshold = W.days();
					p[selected].setTurnOnsRemaining(0);
				} else if(p[selected].getTimesInYard() == 1){
					p[selected].visitYard();
					p[selected].setRole(1);		//the counter is selected
					p[selected].setPrisonersCounted(i);
					p[selected].turnON(b);
				}
			}
		}
		
		if(!b.getLight()){				//if after the selection round a counter has not been found, then declare victory
			daysUntilVictory = W.days();
		} else {
			p[selected].turnOFF(b);
			do{
				selected = W.nextPrisoner();		//a random integer between 0 and n-1
				if(p[selected].getTimesInYard() == 0){
					victoryTreshold = W.days();
				}
				p[selected].visitYard();
				if(p[selected].getRole() == 1){		//the selected prisoner is the counter
					if(b.getLight()){				//If the light is on,
						p[selected].count(1);		//then the counter counts one prisoner
						p[selected].turnOFF(b);		//and turns the light off
					}
				}
				else{																//the selected prisoner is a drone
					if(!(b.getLight()) && (p[selected].getTurnOnsRemaining() > 0)){	//the light is off, and the prisoner haven't turned it on yet
						p[selected].turnON(b);
					}	
				}
			} while(!(p[selected].getPrisonersCounted() == n));	//repeat until victory can be declared
		}
		daysUntilVictory = W.days();
	}

}