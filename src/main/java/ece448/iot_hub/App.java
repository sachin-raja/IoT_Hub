package ece448.iot_hub;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;

@SpringBootApplication
public class App {
	@Autowired
	public PlugsModel plugs;
	
	@Autowired
	public App(Environment env) throws Exception{

	}

	@Autowired
	private Environment env;

	@Bean(destroyMethod = "disconnect")
	public MqttClient mqttClient(Environment env) throws Exception {

		String broker = env.getProperty("mqtt.broker");
		String clientId = env.getProperty("mqtt.clientId");
		MqttClient mqtt = new MqttClient(broker, clientId, new MemoryPersistence());
		mqtt.connect();
		logger.info("MqttClient {} connected: {}", clientId, broker);
		String topicPrefix = env.getProperty("mqtt.topicPrefix");
		mqtt.subscribe(topicPrefix+ "update/#", this::handleUpdate);
		return mqtt;
	}


	protected  final HashMap<String, String> states = new HashMap<>();
	protected  final HashMap<String, String> powers = new HashMap<>();
//updating the state and power of a plug
	synchronized protected void handleUpdate(String topic, MqttMessage msg) {
		String topicPrefix = env.getProperty("mqtt.topicPrefix");
		String clientId = env.getProperty("mqtt.clientId");
		logger.info("MqttCtl {} : {} {}", clientId, topic,msg);
		
		String[] topicUpdate = topic.substring(topicPrefix.length()).split("/");
		if ((topicUpdate.length != 3) || !topicUpdate[0].equals("update"))
			return; // ignore unknown format
		System.out.println(topicUpdate[2]);
		switch (topicUpdate[2])
		{
		case "state":
			states.put(topicUpdate[1], msg.toString());
			break;
		case "power":
			powers.put(topicUpdate[1], msg.toString());
			break;
		default:
			return;
		}
	}

//publish our action on the plug to the broker
		synchronized public void publishAction(String plugName, String action, MqttClient mqtt) {
			String topicPrefix = env.getProperty("mqtt.topicPrefix");
			String clientId = env.getProperty("mqtt.clientId");

			String topic = topicPrefix+"action/"+plugName+"/"+action;
			try
			{
				mqtt.publish(topic, new MqttMessage());
				System.out.println("MqttCtl {"+clientId+"}: {"+topic+"} published");
			}
			catch (Exception e)
			{
				System.out.println("MqttCtl {"+clientId+"}: {"+topic+"} fail to publish");
			}
		}

//get the state of a single plug
		synchronized public String getState(String plugName) {
			return (states.get(plugName) == null)? "off" : states.get(plugName)  ;
		}
//get the power of a single plug
		synchronized public String getPower(String plugName) {
			return (powers.get(plugName) == null)? "0.000": powers.get(plugName) ;
		}

//get the state of a group of plug
		synchronized public Map<String, String> getStates() {
			return new TreeMap<>(states);
		}
//get the power of a group of plug
		synchronized public Map<String, String> getPowers() {
			return new TreeMap<>(powers);
		}
	private static final Logger logger = LoggerFactory.getLogger(App.class);
}
