public class ProtocolBadgesAndCrowns extends Protocol {
	
	private boolean crownGivenAway;

	public ProtocolBadgesAndCrowns() {
		super("uj algoritmus");
		crownGivenAway = false;
	}
	
	public void simulate(Warden warden){
		int n = warden.getNumberOfPrisoners();
		Bulb light = new Bulb();
		Prisoner[] prisoners = new Prisoner[n];
		Prisoner prisoner = null;
		preset(warden, prisoners);
		doSelectionStage(warden, light, prisoners); //lasts 41 days
		do {
			prisoner = prisoners[warden.pickNextPrisoner()];
			switch(stageSelection(warden.daysPassed())){
				case 1: doFirstStage(light, prisoner);
						break;
				case 3: doFirstStageLastDay(light, prisoner);
						//checkSum(prisoners, warden, light);
						break;
				case 2: doSecondStage(light, prisoner);
						break;
				default:	doSecondStageLastDay(light, prisoner);
							//checkSum(prisoners, warden, light);
			}
			if(warden.daysPassed() > 10000) {
				System.out.println("Hiba");
				//System.exit(1);
				break;
			}
		} while(prisoner.recall("tokens") != n); 
		setDaysUntilVictory(warden.daysPassed());
		System.out.println(warden.daysPassed());
	}
	
	public void preset(Warden warden, Prisoner[] prisoners){
		crownGivenAway = false;
		for (int i = 0; i < prisoners.length; i++) {
			prisoners[i] = new Prisoner("" + i);
			prisoners[i].note("tokens",1);
			prisoners[i].note("badges",0);
			prisoners[i].note("crowns",0);
		}
	}
	
	private void doSelectionStage(Warden warden, Bulb light, Prisoner[] prisoners) {
		Prisoner prisoner = null;
		for(int i = 0; i < 10; i++){
			//Day 1 of the 4-day selection cycle
			prisoner = prisoners[warden.pickNextPrisoner()];
			if(light.isOn()){
				prisoner.modify("tokens", 3);
				prisoner.modify("badges", 1);
				
				if(!crownGivenAway){
					prisoner.modify("crowns", 1);
					crownGivenAway = true;
				}
			}
			if((prisoner.recall("tokens") > 0) || (prisoner.recall("badges") > 0)){
				prisoner.turnOn(light);
				prisoner.modify("tokens", -1);
			} else if((prisoner.recall("tokens") == 0) && (prisoner.recall("badges") == 0)){
				prisoner.turnOff(light);
				prisoner.modify("badges", 1);
				if(!crownGivenAway){
				prisoner.modify("crowns", 1);
					crownGivenAway = true;
				}
			}
			//Day 2 of the 4-day selection cycle
			prisoner = prisoners[warden.pickNextPrisoner()];
			if(light.isOn()){
				if((prisoner.recall("tokens") > 0) || (prisoner.recall("badges") > 0)){
					prisoner.modify("tokens", -1);
				} else if((prisoner.recall("tokens") == 0) && (prisoner.recall("badges") == 0)){
					prisoner.turnOff(light);
					prisoner.modify("badges", 1);
					prisoner.modify("tokens", 1);
					if(!crownGivenAway){
					prisoner.modify("crowns", 1);
						crownGivenAway = true;
					}
				}
			}
			//Day 3 of the 4-day selection cycle
			prisoner = prisoners[warden.pickNextPrisoner()];
			if(light.isOn()){
				if((prisoner.recall("tokens") > 0) || (prisoner.recall("badges") > 0)){
					prisoner.modify("tokens", -1);
				} else if((prisoner.recall("tokens") == 0) && (prisoner.recall("badges") == 0)){
					prisoner.turnOff(light);
					prisoner.modify("badges", 1);
					prisoner.modify("tokens", 2);
					if(!crownGivenAway){
					prisoner.modify("crowns", 1);
						crownGivenAway = true;
					}
				}
			}
			//Day 4 of the 4-day selection cycle
			prisoner = prisoners[warden.pickNextPrisoner()];
			if(light.isOn()){
				if(prisoner.recall("badges") == 0){
					prisoner.turnOff(light);
					prisoner.modify("badges", 1);
					prisoner.modify("tokens", 3);
					if(!crownGivenAway){
						prisoner.modify("crowns", 1);
							crownGivenAway = true;
					}
				}
			}
		}
		prisoner = prisoners[warden.pickNextPrisoner()];
		if(light.isOn()){
			prisoner.modify("tokens", 3);
			prisoner.modify("badges", 1);
			prisoner.turnOff(light);
		}
		doFirstStage(light, prisoner);
	}
	
	private void doFirstStage(Bulb light, Prisoner prisoner) {
		//If the light represents a token the prisoner needs, then he takes it out.
		if(light.isOn()){
			if(prisoner.recall("tokens") < 10*prisoner.recall("badges")){
				prisoner.turnOff(light);
				prisoner.modify("tokens", 1);
			}
		} else if(prisoner.recall("tokens") > 10*prisoner.recall("badges")){
			//If the prisoner has a token he doesn't need, then he puts it in.
			prisoner.turnOn(light);
			prisoner.modify("tokens", -1);
		}
	}
	
	private void doFirstStageLastDay(Bulb light, Prisoner prisoner) {
		//If the light represents a token the prisoner needs to take it out.
		if(light.isOn()){
			prisoner.turnOff(light);
			prisoner.modify("tokens", 1);
		}
		//If the prisoner can put in a 'bulkSize' (at this point fixed at value 10) amount of tokens, then he does that.
		//Notice this isn't an 'else if' statement. It is possible to take a token out and put in a 'bulkSize' amount.
		if((prisoner.recall("crowns") == 0) && (prisoner.recall("badges") > 0) && (prisoner.recall("tokens") >= 10)){
			prisoner.turnOn(light);
			prisoner.modify("tokens", -10);
			prisoner.modify("badges", -1);
		}
	}
	
	private void doSecondStage(Bulb light, Prisoner prisoner) {
		//If the prisoner is the head counter (he has the crown), ha takes 'bulkSize' amount of tokens.
		if(light.isOn()){
			if(prisoner.recall("crowns") > 0){
				prisoner.turnOff(light);
				prisoner.modify("tokens", 10);
				prisoner.modify("badges", 1);
			}
		} else if((prisoner.recall("crowns") == 0) && (prisoner.recall("badges") > 0) && (prisoner.recall("tokens") >= 10)){ 
			//If the light is off, and the prisoner is an assistant counter (he has at least one badge, but no crown) 
			//with enough tokens, then he puts in a 'bulkSize' amount.
				prisoner.turnOn(light);
				prisoner.modify("tokens", -10);
				prisoner.modify("badges", -1);
		}
	}
	
	private void doSecondStageLastDay(Bulb light, Prisoner prisoner) {
		//If the light is on, the visiting prisoner has to take the 'bulkSize' amount of tokens.
		//By taking the tokens the prisoner assumes the role of an assistant counter, unless he is already the head counter.
		if(light.isOn()){
			prisoner.turnOff(light);
			prisoner.modify("tokens", 10);
			prisoner.modify("badges", 1);
		}
		//If the prisoner can put in a single token, he does that.
		if(prisoner.recall("tokens") > 10*prisoner.recall("badges")){
			prisoner.turnOn(light);
			prisoner.modify("tokens", -1);
		}
	}
	
	private int stageSelection(int n){
		//Stage one consists of 2000 days and stage two lasts for 1500 days.
		//If the first cycle fails, then both stages are shortened to 300 days each.
		//Check whether if we are in the first cycle 
		if(n <= 3500){
			if(n == 3500){
				return 4;	//Last day of the second stage
			} else if(n == 2000) {
				return 3;	//Last day of the first stage
			} else if(n < 2000) {
				return 1;	//First stage
			}
			return 2;	//Second stage
		}
		if(((n-3500) % 600 < 300) && ((n-3500) % 600 > 0)){
			return 1;
		} else if((n-3500) % 600 == 300){
			return 3;
		} else if((n-3500) % 600 > 300){
			return 2;
		}
		return 4;
	}
	
	private boolean checkSum(Prisoner[] prisoners, Warden warden, Bulb light){
		int tokens = 0;
		int badges = 0;
		if(light.isOn()){
			switch(stageSelection(warden.daysPassed())){
				case 1: tokens++;
						break;
				case 3: tokens++;
						break;
				default:	tokens += 10;
							break;
			}
		}
		for(int i = 0; i < prisoners.length; i++){
			tokens += prisoners[i].recall("tokens");
			badges += prisoners[i].recall("badges");
		}
		System.out.println("On day #"
				+ warden.daysPassed()
				+ " Tokens: "
				+ tokens
				+ " Badges: "
				+ badges
				+ " Light: "
				+ light.isOn()
				);
	if(tokens < 100) {
		return false;
	}
	return true;
	}
}
