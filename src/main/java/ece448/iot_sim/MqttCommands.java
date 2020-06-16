package ece448.iot_sim;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ece448.iot_sim.MqttCommands;
import ece448.iot_sim.http_server.RequestHandler;

public class MqttCommands implements RequestHandler {
    
    private final String topicPrefix;

    private final TreeMap<String, PlugSim> plugs = new TreeMap<>();

    public MqttCommands(List<PlugSim> plugs, String topicPrefix) {
        for (PlugSim plug : plugs)
            this.plugs.put(plug.getName(), plug);
        this.topicPrefix = topicPrefix;
    }

    public String getTopic() {
        return topicPrefix + "action/#";
    }

    public String getMessage(MqttMessage msg) {
        return msg.toString();
    }

    public void handleMessage(String topic, MqttMessage msg) {

        try {
        logger.info("MqttCmd {} {}", topic, msg);
        // switch on, switch off, toggle
        String[] topicp = topic.split("action");
        String[] topicsplit=topicp[1].split("/");
        String action= topicsplit[topicsplit.length-1];

        String plugName = topicsplit[topicsplit.length-2];
        final PlugSim plug = plugs.get(plugName);
        
        //Check null values

        if (plug == null || action == null)
        {
            logger.info("NULL VALUE");
            return;
        } 
        if(action.equals("power") || topicsplit[topicsplit.length - 3].equals("update")){
            logger.info("Power :"+String.valueOf(plug.getPower()));

        }
        else{

            //handle on request
            if(action.equals("on")){
                plug.switchOn();
                // plug.measurePower();
            }
            //handle off request
            else if(action.equals("off")){
                plug.switchOff();
                // plug.updatePower(0.000);
            }
            //handle toggle request
            else if(action.equals("toggle")){
                plug.toggle();
            }   
            if(action.equals("state"))
            logger.info("{} is {}",plugName,plug.isOn()?"on":"off");
        }

        // switch(action){
        //     case "on":
        //     plug.switchOn();
        //     break;
        //     case "off":
        //     plug.switchOff();
        //     break;
        //     case "toggle":
        //     plug.toggle();

        // }}
        // if (plug == null || action == null)
        // {
        //     logger.info("NULL VALUE");
        // } 
        // else{

        //     //handle on request
        //     if(action.equals("on")){
        //         plug.updateState(true);
        //         // plug.measurePower();
        //     }
        //     //handle off request
        //     else if(action.equals("off")){
        //         plug.updateState(false);
        //         // plug.updatePower(0.000);
        //     }
        //     //handle toggle request
        //     else if(action.equals("toggle")){
        //         plug.toggle();

        //     } 
        // } }
        }
        catch (Exception e) {
            e.printStackTrace();
        }

    }
    private static final Logger logger = LoggerFactory.getLogger(MqttCommands.class);

    @Override
    public String handleGet(String path, Map<String, String> params) {
        return null;
    }

}