
public class Bulb {

	boolean light;		//True means the light is on, while false means it is off.
	
	public Bulb(){		//Initially the light is off..
		light = false;
	}
	
	public boolean getLight(){
		return light;
	}
	
	public void setLight(boolean value){
		light = value;
	}
	
}
