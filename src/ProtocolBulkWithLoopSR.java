import java.util.*;
import java.io.*;

/**
 * FIXME add description
 */
public class ProtocolBulkWithLoopSR extends Protocol {
	private static final int ROLE_DRONE = 0;
	/** Assistant Counters are responsible for n bulk counts (n > 0), their role is >= 1 */
	private static final int ROLE_ASSISTANT_COUNTER_DEFAULT = 1;
	private static final int ROLE_HEAD_COUNTER = -1;

	private final int stageOneLength;
	private final int cycleLength;
	private final int bulkSize;
	private int goal;

	public ProtocolBulkWithLoopSR(int stageOneLength, int stageTwoLength, int bulkSize) {
		super("Ketfazisu szamlalas");
		this.stageOneLength = stageOneLength;
		this.cycleLength = stageOneLength + stageTwoLength;
		this.bulkSize = bulkSize;
		this.goal = 0;
	}

	public void simulate(Warden warden) {
		goal = bulkSize;
		int n = warden.getNumberOfPrisoners();

		Bulb light = new Bulb();
		Prisoner[] prisoners = new Prisoner[n];
		reset(warden, prisoners);
		doSelectionStage(warden, light, prisoners);
		Prisoner prisoner;
		do {
			prisoner = prisoners[warden.pickNextPrisoner()];

			if (prisoner.getTimesInYard() == 0) {
				setVictoryThreshold(warden.daysPassed());
			}
			prisoner.visitYard();
			if ((warden.daysPassed() % cycleLength > 0) && (warden.daysPassed() % cycleLength < stageOneLength)) {
				doFirstStage(light, prisoner); // except for the last day
			} else if (warden.daysPassed() % cycleLength == stageOneLength) {
				doFirstStageLastDay(light, prisoner);
			} else if (warden.daysPassed() % cycleLength != 0) {
				doSecondStage(light, prisoner); // except for the last day
			} else if (warden.daysPassed() % cycleLength == 0) {
				// last day of second stage (i.e. the cycle)
				doSecondStageLastDay(light, prisoner);
			}
		} while (prisoner.getCounted() != n);    // repeat until victory can be declared
		setDaysUntilVictory(warden.daysPassed());
	}

	private void reset(Warden warden, Prisoner[] prisoners) {
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

		// pick 1 head counter
		Prisoner headCounterPrisoner = prisoners[prisonerReferences.pop()];
		headCounterPrisoner.setRole(ROLE_HEAD_COUNTER);
		headCounterPrisoner.setTurnOnsRemaining(0);

		// pick all but one assistants
		for (int i = 0; i < numberOfAssistants - 2; i++) {
			prisoners[prisonerReferences.pop()].setRole(ROLE_ASSISTANT_COUNTER_DEFAULT);
		}
		// one assistant selected during the selection stage
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
					case ROLE_HEAD_COUNTER:	prisoner.setCounted(i);
											goal += bulkSize - i;
											break;
					case ROLE_ASSISTANT_COUNTER_DEFAULT:	prisoner.setCounted(i);
															prisoner.setRole(2);
															break;
					default:	prisoner.setCounted(i);	//Drone
								prisoner.setRole(1);
								break;
					} 
				} else if(i == bulkSize - 1){
					//System.out.println("Selection day: last");
					light.turnOn();
					setVictoryThreshold(warden.daysPassed());
					prisoner.visitYard();
					switch(prisoner.getRole()){
					case ROLE_HEAD_COUNTER:	prisoner.setCounted(bulkSize);
											goal++;
											break;
					case ROLE_ASSISTANT_COUNTER_DEFAULT:	prisoner.setCounted(bulkSize);
															prisoner.setRole(2);
															break;
					default:	prisoner.setCounted(bulkSize);	//Drone
								prisoner.setRole(1);
								break;
					} 
				} else {
					prisoner.visitYard();
					setVictoryThreshold(warden.daysPassed());
					switch(prisoner.getRole()){
					case ROLE_HEAD_COUNTER:	prisoner.count(-1);
											goal++;
											break;
					case ROLE_ASSISTANT_COUNTER_DEFAULT:	prisoner.count(-1);
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
				} else if (goal > 0 && light.isOn()){
					prisoner.turnOff(light);
					prisoner.count(1);
					goal--;
				}
				break;
			case ROLE_DRONE:
				if (prisoner.getTurnOnsRemaining() > 0 && light.isOff()) {
					prisoner.turnOn(light);
				} else {
					prisoner.doNothing();
				}
				break;
			default: // assistant counters, see ROLE_ASSISTANT_COUNTER_DEFAULT
				if (light.isOn() && prisoner.getCounted() < prisoner.getRole() * bulkSize) {
					prisoner.turnOff(light);
					prisoner.count(1);
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
					if(goal == 0){
							prisoner.setTurnOnsRemaining(prisoner.getTurnOnsRemaining() + 1);
							prisoner.turnOff(light);
					} else {
						prisoner.turnOff(light);
						prisoner.count(1);
						goal--;
					}
				} else {
					prisoner.doNothing();
				}
				break;
			case ROLE_DRONE:
				if (light.isOn()) {
					prisoner.turnOff(light);
					prisoner.setTurnOnsRemaining(prisoner.getTurnOnsRemaining() + 1);
					prisoner.count(1);
				} else {
					prisoner.doNothing();
				}
				break;
			default: // assistant counters, see ROLE_ASSISTANT_COUNTER_DEFAULT
				if (light.isOn()) {
					if (prisoner.getCounted() < bulkSize - 1) {
						prisoner.turnOff(light);
						prisoner.count(1);
					} else if (prisoner.getCounted() >= bulkSize - 1) {
						prisoner.count(1 - bulkSize);
						prisoner.setRole(prisoner.getRole() - 1);
						prisoner.turnOn(light);
						if (prisoner.getRole() == ROLE_DRONE) {
							prisoner.setTurnOnsRemaining(prisoner.getCounted());
						}
					}
				} else { // the light is OFF
					if (prisoner.getCounted() >= bulkSize) {
						prisoner.count(-bulkSize);
						prisoner.setRole(prisoner.getRole() - 1);
						prisoner.turnOn(light);
						if (prisoner.getRole() == ROLE_DRONE) {
							prisoner.setTurnOnsRemaining(prisoner.getCounted());
						}
					} else {
						prisoner.doNothing();
					}
				}
				break;
		}
	}

	private void doSecondStage(Bulb light, Prisoner prisoner) {
		switch (prisoner.getRole()) {
			case ROLE_HEAD_COUNTER:
				if (light.isOn()) {
					prisoner.count(bulkSize); // counts in bulkSize-sized units
					prisoner.turnOff(light);
				} else {
					prisoner.doNothing();
				}
				break;
			case ROLE_DRONE:
				prisoner.doNothing();
				break;
			default: // assistant counters, see ROLE_ASSISTANT_COUNTER_DEFAULT
				if (light.isOff() && prisoner.getCounted() >= bulkSize) {
					prisoner.count(-bulkSize);
					prisoner.setRole(prisoner.getRole() - 1);
					prisoner.turnOn(light);
					if (prisoner.getRole() == ROLE_DRONE) {
						prisoner.setTurnOnsRemaining(prisoner.getCounted());
					}
				} else {
					prisoner.doNothing();
				}
				break;
		}
	}

	private void doSecondStageLastDay(Bulb light, Prisoner prisoner) {
		switch (prisoner.getRole()) {
			case ROLE_HEAD_COUNTER:
				if (light.isOn()) {
					prisoner.count(bulkSize); // counts in bulkSize-sized units
					prisoner.turnOff(light);
				} else {
					prisoner.doNothing();
				}
				break;
			case ROLE_DRONE:
			default: // assistant counters, see ROLE_ASSISTANT_COUNTER_DEFAULT
				if (light.isOn()) {
					prisoner.count(bulkSize);
					prisoner.setRole(prisoner.getRole() + 1);
					prisoner.turnOff(light);
				}
				break;
		}
	}
} 

