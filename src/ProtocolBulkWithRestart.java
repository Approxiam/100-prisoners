import java.util.*;

/**
 * FIXME add description
 */
public class ProtocolBulkWithRestart extends Protocol {
	private static final int ROLE_DRONE = 0;
	private static final int ROLE_HEAD_COUNTER = 1;
	private static final int ROLE_ASSISTANT_COUNTER = 2;

	private final int stageOneLength;
	private final int cycleLength;
	private final int bulkSize;

	public ProtocolBulkWithRestart(int stageOneLength, int stageTwoLength, int bulkSize) {
		super("BulkWithRestart-ReadyToBeNamed");
		this.stageOneLength = stageOneLength;
		this.cycleLength = stageOneLength + stageTwoLength;
		this.bulkSize = bulkSize;
		// FIXME add if(condition) throw IllegalArgumentException() to validate ALL parameters
	}

	public void simulate(Warden warden) {
		int n = warden.getNumberOfPrisoners();

		Bulb light = new Bulb();
		Prisoner[] prisoners = new Prisoner[n];

		Prisoner prisoner;
		boolean reset = true;
		do {
			if (reset) {
				reset(warden, prisoners);
				reset = false;
			}

			prisoner = prisoners[warden.pickNextPrisoner()];

			if (prisoner.getTimesInYard() == 0) {
				setVictoryThreshold(warden.daysPassed());
			}
			prisoner.visitYard();

			if ((warden.daysPassed() % cycleLength > 0) && (warden.daysPassed() % cycleLength < stageOneLength)) {
				doFirstStage(light, prisoner); // except for tha last day
			} else if (warden.daysPassed() % cycleLength == stageOneLength) {
				doFirstStageLastDay(light, prisoner);
			} else if (warden.daysPassed() % cycleLength != 0) {
				doSecondStage(light, prisoner); // except for the last day
			} else if (warden.daysPassed() % cycleLength == 0) {
				// last day of second stage (i.e. the cycle)
				reset = true;
			}
		} while (prisoner.getCounted() != n);    // repeat until victory can be declared
		setDaysUntilVictory(warden.daysPassed());
	}

	private void reset(Warden warden, Prisoner[] prisoners) {
		int n = warden.getNumberOfPrisoners();
		if ((n - 1) % bulkSize != 0) {
			throw new IllegalArgumentException("bulkSize=" + bulkSize + " is not the divisor of n-1=" + (n - 1));
		}
		final int numberOfAssistants = (n - 1) / bulkSize;
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
		prisoners[prisonerReferences.pop()].setRole(ROLE_HEAD_COUNTER);

		// pick assistants
		for (int i = 0; i < numberOfAssistants; i++) {
			prisoners[prisonerReferences.pop()].setRole(ROLE_ASSISTANT_COUNTER);
		}

		// everyone else stays a drone
	}

	private void doFirstStage(Bulb light, Prisoner prisoner) {
		switch (prisoner.getRole()) {
			case ROLE_HEAD_COUNTER:
				prisoner.doNothing();
				break;
			case ROLE_ASSISTANT_COUNTER:
				if (light.isOn() && prisoner.getCounted() < bulkSize) {
					prisoner.turnOff(light);
					prisoner.count(1);
				} else {
					prisoner.doNothing();
				}
				break;
			case ROLE_DRONE:
				if (prisoner.getTurnOnsRemaining() > 0 && light.isOff()) {
					prisoner.turnOn(light);
					prisoner.setTurnOnsRemaining(0);
				} else {
					prisoner.doNothing();
				}
				break;
		}
	}

	private void doFirstStageLastDay(Bulb light, Prisoner prisoner) {
		switch (prisoner.getRole()) {
			case ROLE_HEAD_COUNTER:
				prisoner.doNothing();
				break;
			case ROLE_ASSISTANT_COUNTER:
				if (light.isOn() && prisoner.getCounted() == bulkSize - 1) {
					prisoner.setCounted(0);
				} else if (light.isOff() && prisoner.getCounted() == bulkSize) { // TODO light-check needed?
					prisoner.turnOn(light);
					prisoner.setCounted(0);
				} else if (light.isOn()) {
					prisoner.turnOff(light);
					prisoner.count(1);
				} else {
					prisoner.doNothing();
				}
				break;
			case ROLE_DRONE:
				prisoner.turnOff(light);
				break;
		}
	}

	private void doSecondStage(Bulb light, Prisoner prisoner) {
		switch (prisoner.getRole()) {
			case ROLE_HEAD_COUNTER:
				if (light.isOn()) {
					prisoner.count(bulkSize); // counts in bulkSize-sized units
					prisoner.turnOff(light);
				}
				break;
			case ROLE_ASSISTANT_COUNTER:
				if (light.isOff() && prisoner.getCounted() == bulkSize) {
					prisoner.turnOn(light);
					prisoner.setCounted(0);
				} else {
					prisoner.doNothing();
				}
				break;
			case ROLE_DRONE:
				prisoner.doNothing();
				break;
		}
	}
}
