public class Prisoner {
	
	int turnOnsRemaining;	//ennyiszer fogja meg felkapcsolni a lampat
	int prisonersCounted;	//ennyi rabot szamlalt ossze, meg a drone-ok is csinalhatnak naiv (lucky) szamlalast
	int timesInYard;		//ennyi alkalommal jart az udvaron
	int role;				//a rab szerepe, ez a stratégiáknál kerül meghatározásra
	boolean lastSeenLight;	//milyen allasban volt a lámpa mikor utoljara latta true = ON, false = OFF
	
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