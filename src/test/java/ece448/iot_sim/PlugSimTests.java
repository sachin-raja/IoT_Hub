package ece448.iot_sim;

import static org.junit.Assert.*;

import org.junit.Test;

public class PlugSimTests {
	@Test
	public void testName() {
		PlugSim plug = new PlugSim("PowerPlug");
		System.out.println("Test 0: Plug Name - " + plug.getName());
	}

	@Test
	public void testInit() {
		PlugSim plug = new PlugSim("PowerPlug");
		assertFalse(plug.isOn());//check if plug is off
		System.out.println("Test 1: Plug is Off");//print comments
	}

	@Test
	public void testSwitchOn() {
		PlugSim plug = new PlugSim("PowerPlug");
		plug.switchOn();//call method SwitchOn 
		assertTrue(plug.isOn());//check if plug is on
		System.out.println("Test 2: Plug Turns On");
	}

	@Test
	public void testSwitchOff() {
		PlugSim plug = new PlugSim("PowerPlug");
		plug.switchOn();
		plug.switchOff();
		assertFalse(plug.isOn());
		System.out.println("Test 3: Plug turned On and then turned Off");
	}

	@Test
	public void testtoggle() {
		PlugSim plug = new PlugSim("PowerPlug");
		plug.switchOn();
		plug.switchOff();
		plug.switchOn();
		plug.toggle();
		assertFalse(plug.isOn());
		System.out.println("Test 4: Toggle Test ");
	}

	@Test
	public void testtoggleSameState() {
		PlugSim plug = new PlugSim("PowerPlug");
		plug.switchOn();//call method SwitchOn 
		plug.switchOff();//call method SwitchOff 
		plug.switchOn();//call method SwitchOn 
		plug.toggle();//call method toggle
		plug.toggle();//call method toggle 
		assertTrue(plug.isOn());//check if power is on
		System.out.println("Test 5: Toggle Test 2 ");
	}

	@Test
	public void testmeasurePower(){
		PlugSim plug = new PlugSim("PowerPlug.300");
		plug.switchOn();
		plug.measurePower();
		assertEquals(300.00, plug.getPower(), 0.00);//check if power=300	
		System.out.println("Test 6: Measuring power when plug is On to be "+ plug.getPower());
	}

	@Test
	public void testmeasurePowerPlugOff(){
		PlugSim plug = new PlugSim("PowerPlug.1000");
		plug.switchOn(); //call method SwitchOn
		plug.measurePower();
		assertEquals(1000.00, plug.getPower(), 0.00);// check power =300
		System.out.println("Test 7: Measuring power when plug is On to be "+ plug.getPower());
		plug.switchOff();//call method Switchoff
		plug.measurePower();
		assertEquals(0.00, plug.getPower(), 0.00); //check power=0	
		System.out.println("Test 7: Measuring power when plug is Off to be "+ plug.getPower());
	}

	@Test
	public void MeasurePowerwhenswitchoffTest(){
	PlugSim plug = new PlugSim("PPP.500");
	//plug.switchOn();
	plug.measurePower(); //Measuring power 
	System.out.println(plug.getPower());
	assertEquals(0.00, plug.getPower(), 0.00);// check if no power when switch is off
	}

	
	@Test
	public void MeasurePowerTestlessthan100(){
	PlugSim plug = new PlugSim("PP.50");
	plug.switchOn();
	plug.measurePower(); 	
	System.out.println(plug.getPower());
	}

	@Test
	public void MeasurePowerTestgreaterthan300(){
	PlugSim plug = new PlugSim("PP.3000");
	plug.switchOn();
	plug.measurePower(); 	
	System.out.println(plug.getPower());
	}

	@Test
	public void MeasurePowerTestbetween100and300(){
	PlugSim plug = new PlugSim("PP.200");
	plug.switchOn();
	plug.measurePower(); 	
	System.out.println(plug.getPower());
	}

	@Test
	public void testmeasurePlugPower(){
		PlugSim plug = new PlugSim("PowerPlug.300");
		plug.switchOn();//call method SwitchOn 
		plug.measurePower();	//call method measure power
		plug.switchOff(); //call method SwitchOff
		plug.measurePower();//call method measure power
		System.out.println("Test 8: Does Plug name PowerPlug have 300 watts of power "+ (plug.getName().equals("PowerPlug.300") && !plug.isOn()));
	}
	@Test
	public void testSwitchOnoff() {
		PlugSim plug = new PlugSim("PowerPlug");
		plug.switchOn();//call method SwitchOn 
		plug.switchOff();//call method Switchoff
		plug.switchOn();//call method switchOn 
		assertTrue(plug.isOn());//check if plug is on
		System.out.println("Test 9: Plug Turns off and on");
	}

}
