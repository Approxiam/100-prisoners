public class ProtocolDynamicCounter extends Protocol{

	/*
	 * Brief description of the strategy: 
	 * The strategy is similar to the ProtocolSingleCounter one, 
	 * the difference lies in the method of selecting the counter 
	 * who is assigned during the first n days of captivity (where n is the number of prisoners).
	 * Generally, the first person to visit the yard for the second time is to be the counter, 
	 * his/her task is to turn on the light and count k-1 prisoners (where k is the day he was selected as the counter).
	 * The light remains on until day n, letting people know that the counter has been selected.
	 * From day n+1 the single counter protocol runs, but everyone who visited the yard and seen the light off
	 * does nothing. 
	 * 
	 * Roles:
	 *	0: Drone
	 *	1: Counter
	 */
	
	public ProtocolDynamicCounter(){ 
		super("Egy lampaoltogato dinamikusan valasztva");
	}
	
	public void simulate(Warden W){
		int n = W.getNumberOfPrisoners();
		Bulb b = new Bulb();			
		Prisoner[] p = new Prisoner[n];	
		for(int i = 0; i < n; i++){
			p[i]=new Prisoner();
		}
		int selected = -1;	
		
		for(int i = 0; i < n; i++){			//counter selection round
			selected = W.nextPrisoner();
			if(!b.getLight()){
				if(p[selected].getTimesInYard() == 0){
					p[selected].visitYard();
					victoryTreshold = W.days();
					p[selected].setTurnOnsRemaining(0);
				} else if(p[selected].getTimesInYard() == 1){
					p[selected].visitYard();
					p[selected].setRole(1);				//The counter is selected and he/she notes 
					p[selected].setPrisonersCounted(i);	//that exactly 'i' different prisoners have visited the yard already 
					p[selected].turnON(b);
				}
			}
		}
		
		if(!b.getLight()){	//If the counter hasn't been assigned during the selection rounds, then declare victory!
			daysUntilVictory = W.days();
		} else {
			p[selected].turnOFF(b);
			do{
				selected = W.nextPrisoner();
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
				else{																//A drone is selected.
					if(!(b.getLight()) && (p[selected].getTurnOnsRemaining() > 0)){	//If the light is off and the prisoner hasn't turned it on yet,
						p[selected].turnON(b);										//then he/she turns it on.
					}	
				}
			} while(!(p[selected].getPrisonersCounted() == n));	//Repeat until victory can be declared.
		}
		daysUntilVictory = W.days();
	}

}
