package ece448.iot_sim;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashMap;

import org.junit.Test;

public class HTTPCommandsTests{

	@Test
	public void testInit() {
		PlugSim plug = new PlugSim("PowerPlug");
        ArrayList<PlugSim> plugs =new ArrayList<>();
        plugs.add(plug);
        HTTPCommands cmds = new HTTPCommands(plugs);
        HashMap<String, String> params = new HashMap<>();
        assertTrue(cmds.handleGet("/", params).contains("PowerPlug"));
    }
    
    @Test
    public void TestWebPage(){
        PlugSim plug = new PlugSim("PowerPlug");
        ArrayList<PlugSim> plugs =new ArrayList<>();
        plugs.add(plug);
        HTTPCommands cmds = new HTTPCommands(plugs);
        HashMap<String, String> params = new HashMap<>();
        assertTrue(cmds.handleGet("/a", params) == null);
    }
    
    @Test
    public void TestPlugsim() {
		PlugSim plug = new PlugSim("PowerPlug");
        ArrayList<PlugSim> plugs =new ArrayList<>();
        plugs.add(plug);
        HTTPCommands cmds = new HTTPCommands(plugs);
        cmds.report(plug);
    }

    @Test
    public void TestPlugOn() {
		PlugSim plug = new PlugSim("PowerPlug");
        ArrayList<PlugSim> plugs =new ArrayList<>();
        plugs.add(plug);
        plug.switchOn();   
        assertTrue(plug.isOn());     
    }

    @Test
    public void TestSwitchOff(){
        PlugSim plug = new PlugSim("PowerPlug");
        ArrayList<PlugSim> plugs =new ArrayList<>();
        plugs.add(plug);
        plug.switchOff();   
        assertTrue(!plug.isOn()); 
    }

    
    @Test
    public void TestHTTPRequest(){
        PlugSim plug = new PlugSim("PowerPlug.100");
        ArrayList<PlugSim> plugs =new ArrayList<>();
        plugs.add(plug);
        HTTPCommands cmds = new HTTPCommands(plugs);
        plug.updatePower(plug.getPower());
        cmds.listPlugs();
    }



    @Test
    public void TestHandler(){
        PlugSim plug = new PlugSim("PowerPlug");
        ArrayList<PlugSim> plugs =new ArrayList<>();
        plugs.add(plug);
        // HTTPCommands cmds = new HTTPCommands(plugs);
        // HashMap<String, String> params = new HashMap<>();
        // String action = params.get("action");  
    }

    @Test
    public void TestupdatePower(){
        PlugSim plug = new PlugSim("PowerPlug.100");
        ArrayList<PlugSim> plugs =new ArrayList<>();
        plugs.add(plug);
        // HTTPCommands cmds = new HTTPCommands(plugs);
        // HashMap<String, String> params = new HashMap<>();
        // String action = params.get("action");  
        plug.updatePower(plug.getPower());
    }

    @Test
    public void TestActionRequest(){
        PlugSim plug = new PlugSim("PowerPlug");
        ArrayList<PlugSim> plugs =new ArrayList<>();
        plugs.add(plug);
        // HTTPCommands cmds = new HTTPCommands(plugs);
        // HashMap<String, String> params = new HashMap<>();
        // String action = params.get("action");  
    }

    @Test
    public void TestWebpageupdate(){
        PlugSim plug = new PlugSim("PowerPlug");
        ArrayList<PlugSim> plugs =new ArrayList<>();
        plugs.add(plug);
        // HTTPCommands cmds = new HTTPCommands(plugs);
    }
}