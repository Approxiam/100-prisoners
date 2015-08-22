import java.util.*;

/**
 * FIXME add description
 */

public class ProtocolBulkWithLoopSRWM extends Protocol {
	private static final int ROLE_DRONE = 0;
	private static final int ROLE_ASSISTANT_COUNTER = 1;
	private static final int ROLE_HEAD_COUNTER = 2;

	private final int stageOneLength;
	private final int cycleLength;
	private final int bulkSize;
	private int currentStageOneLength;
	private int currentCycleLength;

	public ProtocolBulkWithLoopSRWM(int stageOneLength, int stageTwoLength, int bulkSize) {
		super("Ketfazisu szamlalas");
		this.stageOneLength = stageOneLength;
		this.cycleLength = stageOneLength + stageTwoLength;
		this.bulkSize = bulkSize;
		this.currentCycleLength = cycleLength;
		this.currentStageOneLength = stageOneLength;
	}

	public void simulate(Warden warden) {
		int n = warden.getNumberOfPrisoners();
		Bulb light = new Bulb();
		Prisoner[] prisoners = new Prisoner[n];
		preset(warden, prisoners);
		doSelectionStage(warden, light, prisoners);
		Prisoner prisoner;
		do {
			prisoner = prisoners[warden.pickNextPrisoner()];
			if (prisoner.getTimesInYard() == 0) {
				setVictoryThreshold(warden.daysPassed());
			}
			prisoner.visitYard();
			if(warden.daysPassed() > 10000000){
				System.out.println("Hiba!");
				break;
			}
			if ((warden.daysPassed() % currentCycleLength > 0) && (warden.daysPassed() % currentCycleLength < currentStageOneLength)) {
				doFirstStage(light, prisoner); // except for the last day
			} else if (warden.daysPassed() % currentCycleLength == currentStageOneLength) {
				doFirstStageLastDay(light, prisoner);
			} else if (warden.daysPassed() % currentCycleLength != 0) {
				doSecondStage(light, prisoner); // except for the last day
			} else if (warden.daysPassed() % currentCycleLength == 0) {
				// last day of second stage (i.e. the cycle)
				doSecondStageLastDay(light, prisoner);
				shortenCycle(n);
			}
		} while (prisoner.getCounted() != n);    // repeat until victory can be declared
		setDaysUntilVictory(warden.daysPassed());
		//System.out.println(warden.daysPassed());
	}

	private void preset(Warden warden, Prisoner[] prisoners) {
		currentCycleLength = cycleLength;
		currentStageOneLength = stageOneLength;
		int n = warden.getNumberOfPrisoners();
		assert (n - 1) % bulkSize == 0 : "bulkSize=" + bulkSize + " is not the divisor of n-1=" + (n - 1);
		final int numberOfAssistants = (n - 1) / bulkSize; // bulkSize was made to be a divisor of n-1 at input
		for (int i = 0; i < prisoners.length; i++) {
			prisoners[i] = new Prisoner();
			prisoners[i].setRole(ROLE_DRONE);
		}
		LinkedList<Integer> prisonerReferences = new LinkedList<>();
		for (int i = 0; i < prisoners.length; i++) {
			prisonerReferences.add(i); // index of each prisoner
		}
		Collections.shuffle(prisonerReferences, warden.getRandom()); // for the sake of randomness

		//pick the head counter
		Prisoner headCounter = prisoners[prisonerReferences.pop()];
		headCounter.setRole(ROLE_HEAD_COUNTER);
		headCounter.setTurnOnsRemaining(0);
		headCounter.note("goal", bulkSize);

		//pick all but one assistants, because the last assistant will be selected during the selection stage 
		for (int i = 0; i < numberOfAssistants - 2; i++) {
			Prisoner assistant = prisoners[prisonerReferences.pop()];
			assistant.setRole(ROLE_ASSISTANT_COUNTER);
			assistant.note("goal", bulkSize);
		}
	}

	private void doSelectionStage(Warden warden, Bulb light, Prisoner[] prisoners) {
		Prisoner prisoner = null;
		for (int i = 0; i < bulkSize; i++) {
			prisoner = prisoners[warden.pickNextPrisoner()];
			if (light.isOff()) {
				if (prisoner.getTimesInYard() == 1) {
					prisoner.visitYard();
					light.turnOn();
					//System.out.println("Selection day: " + i);
					switch(prisoner.getRole()){
					case ROLE_HEAD_COUNTER:
						prisoner.setCounted(i);
						prisoner.modify("goal", bulkSize - i);
						break;
					case ROLE_ASSISTANT_COUNTER:
						prisoner.setCounted(i);
						prisoner.modify("goal", bulkSize);
						break;
					default: //Drone
						prisoner.setCounted(i);
						prisoner.setRole(ROLE_ASSISTANT_COUNTER);
						prisoner.note("goal", bulkSize);
						break;
					} 
				} else if(i == bulkSize - 1){
					//System.out.println("Selection day: last");
					//light.turnOn();
					setVictoryThreshold(warden.daysPassed());
					prisoner.visitYard();
					switch(prisoner.getRole()){
					case ROLE_HEAD_COUNTER:
						prisoner.setCounted(bulkSize);
						prisoner.modify("goal", 1);
						break;
					case ROLE_ASSISTANT_COUNTER:
						prisoner.setCounted(bulkSize);
						prisoner.modify("goal", bulkSize);
						break;
					default: //Drone
						prisoner.setCounted(bulkSize);
						prisoner.setRole(ROLE_ASSISTANT_COUNTER);
						prisoner.note("goal", bulkSize);
						break;
					} 
				} else {
					prisoner.visitYard();
					setVictoryThreshold(warden.daysPassed());
					switch(prisoner.getRole()){
					case ROLE_HEAD_COUNTER:	prisoner.count(-1);
											prisoner.modify("goal", 1);
											break;
					case ROLE_ASSISTANT_COUNTER:	prisoner.count(-1);
													break;
					default:	prisoner.setTurnOnsRemaining(0);	//Drone
								break;
					}
				}
			}
			if(light.isOn()){
				if (prisoner.getTimesInYard() == 0) {
					prisoner.visitYard();
					setVictoryThreshold(warden.daysPassed());
				}
			}
		}
		light.turnOff();
	}
	
	private void doFirstStage(Bulb light, Prisoner prisoner) {
		switch (prisoner.getRole()) {
			case ROLE_HEAD_COUNTER:
				if (prisoner.getTurnOnsRemaining() > 0 && light.isOff()) {
					prisoner.turnOn(light);
				} else if (prisoner.recall("goal") > 0 && light.isOn()){
					prisoner.turnOff(light);
					prisoner.count(1);
					prisoner.modify("goal", -1);
				} else {
					prisoner.doNothing();
				}
				break;
			case ROLE_ASSISTANT_COUNTER: 
				if (light.isOn() && prisoner.getCounted() < prisoner.recall("goal")) {
					prisoner.turnOff(light);
					prisoner.count(1);
				} else {
					prisoner.doNothing();
				}
				break;
			default: //Drone
				if (prisoner.getTurnOnsRemaining() > 0 && light.isOff()) {
					prisoner.turnOn(light);
				} else {
					prisoner.doNothing();
				}
				break;
		}
	}

	private void doFirstStageLastDay(Bulb light, Prisoner prisoner) {
		switch (prisoner.getRole()) {
			case ROLE_HEAD_COUNTER:
				if (light.isOn()) {
					if(prisoner.recall("goal") == 0){
							prisoner.setTurnOnsRemaining(prisoner.getTurnOnsRemaining() + 1);
							prisoner.turnOff(light);
					} else {
						prisoner.turnOff(light);
						prisoner.count(1);
						prisoner.modify("goal", -1);
					}
				} else { //Light is OFF
					prisoner.doNothing();
				}
				break;
			case ROLE_ASSISTANT_COUNTER:
				if (light.isOn()) {
					if (prisoner.getCounted() < bulkSize - 1) {
						prisoner.turnOff(light);
						prisoner.count(1);
					} else if (prisoner.getCounted() >= bulkSize - 1) {
						prisoner.count(1 - bulkSize);
						prisoner.modify("goal", -bulkSize);
						prisoner.turnOn(light);
						if (prisoner.recall("goal") == 0) {
							prisoner.setRole(ROLE_DRONE);
							prisoner.setTurnOnsRemaining(prisoner.getCounted());
						}
					}
				} else { // the light is OFF
					if (prisoner.getCounted() >= bulkSize) {
						prisoner.count(-bulkSize);
						prisoner.modify("goal", -bulkSize);
						prisoner.turnOn(light);
						if (prisoner.recall("goal") == 0) {
							prisoner.setRole(ROLE_DRONE);
							prisoner.setTurnOnsRemaining(prisoner.getCounted());
						}
					} else {
						prisoner.doNothing();
					}
				}
				break;
				
			default: //Drone
				if (light.isOn()) {
					prisoner.turnOff(light);
					prisoner.setTurnOnsRemaining(prisoner.getTurnOnsRemaining() + 1);
					prisoner.count(1);
				} else {
					prisoner.doNothing();
				}
				break;
		}
	}

	private void doSecondStage(Bulb light, Prisoner prisoner) {
		switch (prisoner.getRole()) {
			case ROLE_HEAD_COUNTER:
				if (light.isOn()) {
					prisoner.count(bulkSize); //counts in bulkSize-sized units
					prisoner.turnOff(light);
				} else {
					prisoner.doNothing();
				}
				break;
			case ROLE_ASSISTANT_COUNTER:
				if (light.isOff() && prisoner.getCounted() >= bulkSize) {
					prisoner.count(-bulkSize);
					prisoner.modify("goal", -bulkSize);
					prisoner.turnOn(light);
					if (prisoner.recall("goal") == 0) {
						prisoner.setRole(ROLE_DRONE);
						prisoner.setTurnOnsRemaining(prisoner.getCounted());
					}
				} else {
					prisoner.doNothing();
				}
				break;
			default: //Drone
				prisoner.doNothing();
				break;	
		}
	}

	private void doSecondStageLastDay(Bulb light, Prisoner prisoner) {
		switch (prisoner.getRole()) {
			case ROLE_HEAD_COUNTER:
				if (light.isOn()) {
					prisoner.count(bulkSize); //counts in bulkSize-sized units
					prisoner.turnOff(light);
				} else {
					prisoner.doNothing();
				}
				break;
			default: //Assistant counter or drone
				if (light.isOn()) {
					prisoner.count(bulkSize);
					prisoner.modify("goal", bulkSize);
					prisoner.setRole(ROLE_ASSISTANT_COUNTER);
					prisoner.turnOff(light);
				} else {
					prisoner.doNothing();
				}
				break;
		}
	}
	
	private void shortenCycle(int lowerBound){
		if((currentStageOneLength/2 > lowerBound) && (currentCycleLength/2 - currentStageOneLength/2 > lowerBound)){
			this.currentStageOneLength = currentStageOneLength/2;
			this.currentCycleLength = currentCycleLength/2;
		}
	}
} 

