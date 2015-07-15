/**
 * The strategy is similar to the {@link ProtocolSingleCounter} one,
 * the difference lies in the method of selecting the counter
 * who is assigned during the first n days of captivity (where n is the number of prisoners).
 * Generally, the first person to visit the yard for the second time is to be the counter,
 * his/her task is to turn on the light and count k-1 prisoners (where k is the day he was selected as the counter).
 * The light remains on until day n, letting people know that the counter has been selected.
 * From day n+1 the single counter protocol runs, but everyone who visited the yard and seen the light off
 * does nothing.
 */
public class ProtocolDynamicCounter extends Protocol {
	private static final int ROLE_DRONE = 0;
	private static final int ROLE_COUNTER = 1;

	public ProtocolDynamicCounter() {
		super("Egy lampaoltogato dinamikusan valasztva");
	}

	public void simulate(Warden warden) {
		Bulb light = new Bulb();
		Prisoner[] prisoners = new Prisoner[warden.getNumberOfPrisoners()];
		for (int i = 0; i < prisoners.length; i++) {
			prisoners[i] = new Prisoner();
			prisoners[i].setRole(ROLE_DRONE);
		}

		Prisoner lastPrisonerInYard = doCounterSelectionStage(warden, light, prisoners);

		if (light.isOff()) { // If the counter hasn't been assigned during the selection rounds, then declare victory!
			setDaysUntilVictory(warden.daysPassed());
		} else {
			lastPrisonerInYard.turnOff(light);
			doSingleCounterStage(warden, light, prisoners);
		}
		setDaysUntilVictory(warden.daysPassed());
	}

	private Prisoner doCounterSelectionStage(Warden warden, Bulb light, Prisoner[] prisoners) {
		Prisoner prisoner = null;
		for (int i = 0; i < warden.getNumberOfPrisoners(); i++) {
			prisoner = prisoners[warden.pickNextPrisoner()];
			if (light.isOff()) {
				if (prisoner.getTimesInYard() == 0) {
					prisoner.visitYard();
					setVictoryThreshold(warden.daysPassed());
					prisoner.setTurnOnsRemaining(0);
				} else if (prisoner.getTimesInYard() == 1) {
					prisoner.visitYard();
					prisoner.setRole(ROLE_COUNTER); // The counter is selected and they note
					prisoner.setCounted(i); // that exactly 'i' different prisoners have visited the yard already
					prisoner.turnOn(light);
				}
			}
		}
		return prisoner;
	}

	private void doSingleCounterStage(Warden warden, Bulb light, Prisoner[] prisoners) {
		Prisoner prisoner;
		do {
			prisoner = prisoners[warden.pickNextPrisoner()];
			if (prisoner.getTimesInYard() == 0) {
				setVictoryThreshold(warden.daysPassed());
			}
			prisoner.visitYard();
			switch (prisoner.getRole()) {
				case ROLE_COUNTER:
					if (light.isOn()) {
						prisoner.count(1);
						prisoner.turnOff(light);
					}
					break;
				case ROLE_DRONE:
					if (light.isOff() && (prisoner.getTurnOnsRemaining() > 0)) {
						// The light is off and the prisoner hasn't turned it on yet,
						prisoner.turnOn(light);
					}
					break;
			}
			// repeat until victory can be declared.
		} while (prisoner.getCounted() != warden.getNumberOfPrisoners());
	}
}
