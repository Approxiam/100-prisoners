public class ProtocolSCWithSmartDrones extends Protocol{

	/*
	 * A modification of the single counter strategy, 
	 * where even drones count how many transitions they have seen 
	 * when the light went form being off to being on.
	 * 
	 * 	Roles:
	 *	0: Drone
	 *	1: Counter
	 */
	
	public ProtocolSCWithSmartDrones(){ 
		super("Egy lampaoltogato elore kivalasztva");
	}
	
	public void simulate(Warden W){
		int n = W.getNumberOfPrisoners();
		Bulb b = new Bulb();			
		Prisoner[] p = new Prisoner[n];	
		for(int i = 0; i < n; i++){
			p[i]=new Prisoner();
		}
		int selected = W.returnRandom(n);	
		p[0].setRole(1);				//Assign the role of the Counter.
		
		
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
			else{																//A drone is selected.
				if(!p[selected].isLastSeenLight() && b.getLight()){
					p[selected].count(1);
				}
				if(!(b.getLight()) && (p[selected].getTurnOnsRemaining() > 0)){	//If the light is off and the prisoner hasn't turned it on yet,
					p[selected].turnON(b);										//then he/she turns it on.
				}
				p[selected].setLastSeenLight(b.getLight());
				if(p[selected].getPrisonersCounted() == n-1){		//The counter wouldn't be counted otherwise.
					p[selected].count(1);
				}
			}
		} while(!(p[selected].getPrisonersCounted() == n));
		daysUntilVictory = W.days();
		if(p[selected].getRole() == 0){
			System.out.println("A gyozelmet nem a szamlalo hirdette ki, hanem az egyik okos rab.");
		}
	}

}