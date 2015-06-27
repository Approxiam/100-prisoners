
public class Bulb {

	boolean light;		//True: ON, False: OFF
	
	public Bulb(){		//initial state is OFF
		light = false;
	}
	
	public boolean getLight(){
		return light;
	}
	
	public void setLight(boolean value){
		light = value;
	}
	
}
