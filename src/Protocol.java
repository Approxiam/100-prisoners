public abstract class Protocol {

	int daysUntilVictory;			//The day the visiting prisoner declared victory. 
	int victoryTreshold;			//The first day when every prisoner has been in the yard.
	private String protocolName;
	
	public Protocol(){
		daysUntilVictory = 0;
		protocolName = null;
	}
	
	public Protocol(String txt){
		daysUntilVictory = 0;
		protocolName = txt;
	}
	
	public abstract void simulate(Warden W);

	public int getDaysUntilVictory() {
		return daysUntilVictory;
	}

	public void setDaysUntilVictory(int daysUntilVictory) {
		this.daysUntilVictory = daysUntilVictory;
	}

	public int getVictoryTreshold() {
		return victoryTreshold;
	}

	public void setVictoryTreshold(int victoryTreshold) {
		this.victoryTreshold = victoryTreshold;
	}

	public String getProtocolName() {
		return protocolName;
	}

	public void setProtocolName(String protocolName) {
		this.protocolName = protocolName;
	}
	
}