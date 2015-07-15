public class Prisoner {
	/** The number of times the prisoner will/should turn on the light. May depend on strategy. */
	private int turnOnsRemaining;
	/** The prisoner knows, that at least this many prisoners visited the yard. */
	private int prisonersCounted;
	/** The prisoner visited the yard this many times. */
	private int timesInYard;
	/** The role of this prisoner. Depends on strategy. */
	private int role;
	/** The state of the light the last time the prisoner visited the room. */
	private boolean lastSeenLight;

	public Prisoner() {
		turnOnsRemaining = 1;
		prisonersCounted = 1;
		timesInYard = 0;
		role = 0;
		lastSeenLight = false;
	}

	public void visitYard() {
		timesInYard++;
	}

	public void count(int a) {
		prisonersCounted += a;
	}

	public void doNothing() {
	}

	public void turnOn(Bulb light) {
		light.turnOn();
		turnOnsRemaining--;
	}

	public void turnOff(Bulb light) {
		light.turnOff();
	}

	public String toString() {
		return "prisoner";
	}

	public int getTurnOnsRemaining() {
		return turnOnsRemaining;
	}

	public void setTurnOnsRemaining(int turnOnsRemaining) {
		this.turnOnsRemaining = turnOnsRemaining;
	}

	public int getCounted() {
		return prisonersCounted;
	}

	public void setCounted(int prisonersCounted) {
		this.prisonersCounted = prisonersCounted;
	}

	public int getTimesInYard() {
		return timesInYard;
	}

	public void setTimesInYard(int timesInYard) {
		this.timesInYard = timesInYard;
	}

	public boolean isLastSeenLight() {
		return lastSeenLight;
	}

	public void setLastSeenLight(boolean lastSeenLight) {
		this.lastSeenLight = lastSeenLight;
	}

	public int getRole() {
		return role;
	}

	public void setRole(int role) {
		this.role = role;
	}
}
