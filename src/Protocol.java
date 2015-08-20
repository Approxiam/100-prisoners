
public abstract class Protocol {
	/** The day the visiting prisoner declared victory. */
	private int daysUntilVictory;
	/** The first day when every prisoner has been in the yard. */
	private int victoryThreshold;
	private String name;

	public Protocol(String txt) {
		daysUntilVictory = 0;
		name = txt;
	}

	public abstract void simulate(Warden W);

	public int getDaysUntilVictory() {
		return daysUntilVictory;
	}

	public void setDaysUntilVictory(int daysUntilVictory) {
		this.daysUntilVictory = daysUntilVictory;
	}

	public int getVictoryThreshold() {
		return victoryThreshold;
	}

	// TODO unused
	// FIXME doesn't mean what the doc says: it's assigned the 1st time *a* prisoner is visiting the yard the 2nd time
	public void setVictoryThreshold(int victoryThreshold) {
		this.victoryThreshold = victoryThreshold;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public static SimulationResult runSimulation(Warden warden, Protocol protocol, int iterationCount) {
		SimulationResult result = new SimulationResult();

		for (int i = 0; i < iterationCount; i++) {
			protocol.simulate(warden);
			result.accumulate(protocol.getDaysUntilVictory());
			warden.eraseMemory();
		}

		return result;
	}
}
