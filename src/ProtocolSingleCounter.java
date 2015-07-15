/**
 * The prisoners designate one of themselves as the counter, and all the rest become drones.
 * Drones turn on the light the very first time they enter the yard with the light off,
 * otherwise they do nothing. Whenever the counter enters the yard with the light on, he/she turns it off.
 * If the counter has turned off the light (n-1) times (where n is the number of prisoners),
 * then he/she declares victory.
 */
public class ProtocolSingleCounter extends Protocol {
	private static final int ROLE_DRONE = 0;
	private static final int ROLE_COUNTER = 1;

	public ProtocolSingleCounter() {
		super("Egy lampaoltogato elore kivalasztva");
	}

	public void simulate(Warden warden) {
		Bulb light = new Bulb();
		Prisoner[] prisoners = new Prisoner[warden.getNumberOfPrisoners()];
		for (int i = 0; i < prisoners.length; i++) {
			prisoners[i] = new Prisoner();
			prisoners[i].setRole(ROLE_DRONE);
		}
		prisoners[warden.pickRandomPrisoner()].setRole(ROLE_COUNTER);

		Prisoner prisoner;
		do {
			prisoner = prisoners[warden.pickNextPrisoner()]; // select the next prisoner who visits the yard.
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
						// if the light is off and the prisoner hasn't turned it on yet
						prisoner.turnOn(light);
					}
					break;
			}
			// Repeat until victory can be declared, this can only happen when the counter visits the yard.
		} while (prisoner.getCounted() != warden.getNumberOfPrisoners());
		setDaysUntilVictory(warden.daysPassed());
	}
}
