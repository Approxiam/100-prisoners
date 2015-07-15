/**
 * Light in the yard.
 */
public class Bulb {
	/** <code>true</code> means the light is on, while <code>false</code> means it is off. */
	private boolean state = false; // initially the light is off

	public boolean isOn() {
		return state;
	}
	public boolean isOff() {
		return !state;
	}
	public void turnOn() {
		state = true;
	}
	public void turnOff() {
		state = false;
	}
}
