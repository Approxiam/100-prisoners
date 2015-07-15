/**
 * A modification of the {@link ProtocolSingleCounter} strategy,
 * in which even the drones count how many transitions they have seen,
 * where each transition refers to a change in the bulb's state from being turned off to on.
 */
public class ProtocolSCWithSmartDrones extends Protocol {
	private static final int ROLE_DRONE = 0;
	private static final int ROLE_COUNTER = 1;

	public ProtocolSCWithSmartDrones() {
		super("Egy lampaoltogato elore kivalasztva");
	}

	public void simulate(Warden warden) {
		Bulb light = new Bulb();
		Prisoner[] prisoners = new Prisoner[warden.getNumberOfPrisoners()];
		for (int i = 0; i < prisoners.length; i++) {
			prisoners[i] = new Prisoner();
			prisoners[i].setRole(ROLE_DRONE);
		}
		prisoners[0].setRole(ROLE_COUNTER); // select a pre-agreed counter

		Prisoner prisoner;
		do {
			prisoner = prisoners[warden.pickNextPrisoner()]; // Select the next prisoner that visits the yard.
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
					if (!prisoner.isLastSeenLight() && light.isOn()) {
						prisoner.count(1);
					}
					if (light.isOff() && (prisoner.getTurnOnsRemaining() > 0)) {
						// If the light is off and the prisoner hasn't turned it on yet
						prisoner.turnOn(light);
					}
					prisoner.setLastSeenLight(light.isOn());
					if (prisoner.getCounted() == warden.getNumberOfPrisoners() - 1) {
						// The counter wouldn't be counted otherwise.
						prisoner.count(1);
					}
					break;
			}
		} while (prisoner.getCounted() != warden.getNumberOfPrisoners());
		setDaysUntilVictory(warden.daysPassed());
		if (prisoner.getRole() == ROLE_DRONE) {
			System.out.println("A gyozelmet nem a szamlalo hirdette ki, hanem az egyik okos rab.");
		}
	}
}
