public class Prisoner {
	
	int turnOnsRemaining;	//The number of times the prisoner will/should turn on the light. May depend on strategy.
	int prisonersCounted;	//The prisoner knows, that at least this many prisoners visited the yard.
	int timesInYard;	//The prisoner visited the yard this many times.
	int role;		//The role of this prisoner. Depends on strategy.
	boolean lastSeenLight;	//The state of the light the last time the prisoner visited the room.
	
	public Prisoner(){ 
		turnOnsRemaining = 1;
		prisonersCounted = 1;
		timesInYard = 0;
		role = 0;
		lastSeenLight = false;
	}
	
	public void visitYard(){
		timesInYard++;
	}

	public void count(int a){
		prisonersCounted += a;
	}
	
	public void doNothing(){
	}
	
	public void turnON(Bulb b){ 
		b.setLight(true);
		turnOnsRemaining--;
	}
	
	public void turnOFF(Bulb b){
		b.setLight(false);
	}
	
	public String toString(){
		return "prisoner";
	}

	public int getTurnOnsRemaining() {
		return turnOnsRemaining;
	}

	public void setTurnOnsRemaining(int turnOnsRemaining) {
		this.turnOnsRemaining = turnOnsRemaining;
	}

	public int getPrisonersCounted() {
		return prisonersCounted;
	}

	public void setPrisonersCounted(int prisonersCounted) {
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
