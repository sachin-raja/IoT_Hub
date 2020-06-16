package ece448.iot_sim;

import static org.junit.Assert.*;

import java.util.*;


import org.junit.Test;

import ece448.iot_sim.PlugSim.Observer;


public class MqttCommandsTests {

    public static class ObserverOne implements Observer {
        private String name, key, value;

        @Override
        public void update(String name, String key, String value) {
            this.name = name;
            this.value = value;
            this.key = key;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }

    @Test
    public void MqttCommandsTestTopic() {
        PlugSim plug = new PlugSim("PowerPlug");
        ArrayList<PlugSim> plugs = new ArrayList<>();
        plugs.add(plug);
        MqttCommands cmds = new MqttCommands(plugs, "update/");
        assertEquals(cmds.getTopic(), "update/action/#");
    }

    @Test
    public void MqttCommandsTestgetmessage() {
        PlugSim plug = new PlugSim("PowerPlug");
        ArrayList<PlugSim> plugs = new ArrayList<>();
        plugs.add(plug);
        MqttCommands cmds = new MqttCommands(plugs, "iot_448/");
        MqttUpdates cmdupt = new MqttUpdates("on");
        assertEquals(cmds.getMessage(cmdupt.getMessage("on")), "on");
    }

    @Test
    public void MqttCommandsTestHandleMessage() {
        PlugSim plug = new PlugSim("PowerPlug");
        ArrayList<PlugSim> plugs = new ArrayList<>();
        plugs.add(plug);
        MqttCommands cmds = new MqttCommands(plugs, "iot_448/");
        MqttUpdates cmdupt = new MqttUpdates("on");
        cmds.handleMessage("iot_448/action/a/on/", cmdupt.getMessage("on"));

    }
    @Test
    public void MqttCommandsTestHandleMessageoff() {
        PlugSim plug = new PlugSim("PowerPlug");
        ArrayList<PlugSim> plugs = new ArrayList<>();
        plugs.add(plug);
        MqttCommands cmds = new MqttCommands(plugs, "iot_448/");
        MqttUpdates cmdupt = new MqttUpdates("off");
        cmds.handleMessage("iot_448/action/a/off/", cmdupt.getMessage("off"));

    }
    @Test
    public void MqttCommandsTestHandleMessagetoggle() {
        PlugSim plug = new PlugSim("PowerPlug");
        ArrayList<PlugSim> plugs = new ArrayList<>();
        plugs.add(plug);
        MqttCommands cmds = new MqttCommands(plugs, "iot_448/");
        MqttUpdates cmdupt = new MqttUpdates("toggle");
          cmds.handleMessage("iot_448/action/a/toggle/", cmdupt.getMessage("toggle"));
    }
    
    
    @Test
    public void MqttUpdatesTest(){
        PlugSim plug = new PlugSim("PowerPlug");
        ArrayList<PlugSim> plugs =new ArrayList<>();
        plugs.add(plug);
        MqttUpdates cmds = new MqttUpdates("IOT_ECE448/");
        assertEquals(cmds.getTopic(plug.toString(), "state"), "IOT_ECE448/update/" +plug.toString()+"/state");
    }

    @Test
    public void MqttUpdatePowerTest(){
        PlugSim plug = new PlugSim("PowerPlug");
        ArrayList<PlugSim> plugs =new ArrayList<>();
        plugs.add(plug);
        MqttUpdates cmds = new MqttUpdates("IOT_ECE448/");
        assertEquals(cmds.getTopic(plug.toString(), "power"), "IOT_ECE448/update/" +plug.toString()+"/power");
    }
    @Test
    public void testObservers(){
        PlugSim plug = new PlugSim("a");

        ObserverOne obs = new ObserverOne();

        plug.addObserver(obs);
        plug.switchOn();
        assertEquals(obs.getName(), "a");
        assertEquals(obs.getKey(), "state");
        assertEquals(obs.getValue(), "on");
    }
    @Test
    public void testObserversoff(){
        PlugSim plug = new PlugSim("a");

        ObserverOne obs = new ObserverOne();

        plug.addObserver(obs);

        plug.switchOff();
        assertEquals("a",obs.getName() );
        assertEquals("state",obs.getKey() );
        assertEquals("off",obs.getValue() );
    }
    @Test
    public void testObserverstoggle(){
        PlugSim plug = new PlugSim("a");

        ObserverOne obs = new ObserverOne();

        plug.addObserver(obs);
        plug.toggle();
        assertEquals(obs.getName(), "a");
        assertEquals(obs.getKey(), "state");
        assertEquals(obs.getValue(), "on");
        plug.toggle();
        assertEquals(obs.getValue(), "off");

    }
    @Test
    public void testObserverspower(){
        PlugSim plug = new PlugSim("a.666");

        ObserverOne obs = new ObserverOne();
        plug.switchOn();
        plug.measurePower();
        plug.addObserver(obs);
        plug.getPower();
        assertEquals(obs.getName(), "a.666");
        assertEquals(obs.getKey(), "power");
        assertEquals("666.000",obs.getValue());

    }

}