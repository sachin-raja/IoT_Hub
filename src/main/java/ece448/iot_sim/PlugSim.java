package ece448.iot_sim;

import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Simulate a smart plug with power monitoring.
 */
public class PlugSim {

	private final String name;
	String value,key,pname;
	private boolean on = false;
	
	// private boolean off = false;
	private double power = 0.000; // in watts
	private final ArrayList<Observer> observers = new ArrayList<>();

	synchronized public void addObserver(Observer observer){
		observers.add(observer);
		observer.update(name, "state", on? "on":"off");
		observer.update(name, "power",String.format("%.3f", power));
	}
	public PlugSim(String name) {
		this.name = name;
	}
	

	public static interface Observer{
		void update(String name, String key, String value);
	}



	/**
	 * No need to synchronize if read a final field.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Switch the plug on.
	 */
	synchronized public void switchOn() {
		// P1: add your code here
		on = true;//On state of plug
		updateState(true);
		// measurePower();
	}

	protected void updateState(boolean o){
		on=o;
		logger.info("Plug {}: state {}", name, on? "on":"off");
		for(Observer observer:observers){
			observer.update(name, "state", on? "on": "off");
		}
	}
	/**
	 * Switch the plug off.
	 */
	synchronized public void switchOff() {
		// P1: add your code here
		on = false;//off state of plug
		updateState(false);
		// measurePower();
	}
	/**
	 * Toggle the plug.
	 */
	synchronized public void toggle() {
		// P1: add your code here
		if(on)
		{
			on = false;
			updateState(false);
			// measurePower();
		}
		else
		{
			on = true;
			updateState(true);
			// measurePower();
		} 
	}
	/**
	 * Measure power.
	 */
	synchronized public void measurePower() {
		if (!on) {
			updatePower(0.000);
			return;
		}

		// a trick to help testing
		if (name.indexOf(".") != -1){
			double number = Double.parseDouble(name.split("\\.")[1]);

			updatePower(number);
		}
		else if (power < 100){
			updatePower(power + Math.random() * 100);
		}
		else if (power > 300){
			updatePower(power - Math.random() * 100);
		}
		else
		{
			updatePower(power + Math.random() * 40 - 20);
		}
	}

	protected void updatePower(double p) {

		try
		{
			power = p;
			logger.debug("Plug {}: power {}", name, power);

			for(Observer observer:observers){
				observer.update(name, "power", String.format("%.3f", getPower()));
			}
			// observer.update(name, "power",String.format("%.3f", getPower()));
			
		}
		catch(NullPointerException e){
			power = Math.random()*100;
			logger.debug("Plug {}: power {}", name, power);
		}
	}

	/**
	 * Getter: current state
	 */
	synchronized public boolean isOn() {
		return on;
	}

	// synchronized public boolean isOff(){
	// 	return off;
	// }

	/**
	 * Getter: last power reading
	 */
	synchronized public double getPower() {
		return power;
	}

	private static final Logger logger = LoggerFactory.getLogger(PlugSim.class);

}
