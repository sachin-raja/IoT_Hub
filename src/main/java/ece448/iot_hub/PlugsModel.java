package ece448.iot_hub;

import java.util.ArrayList;

import java.util.stream.Collectors;


import org.eclipse.paho.client.mqttv3.MqttClient;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;


@Component
public class PlugsModel implements AutoCloseable  {

		@Autowired
		PlugsModel plugs;
	
		@Autowired
		MqttClient mqtt;
	
		@Autowired
		public Environment env;
	
		@Autowired
		public App app;
	

	public PlugsModel() throws Exception {
	}
//Get the plugs from MQTT broker
	synchronized public ArrayList<String> getPlugs() throws Exception {
		ArrayList<String> plugsList = app.getStates().keySet().stream().
		collect(Collectors.toCollection(ArrayList::new));
		
		return plugsList;
	}

//update the plugs
	synchronized void updatePlugs(String plugName,String action) throws Exception {
		// System.out.println(plugName+action);
						// mqtt.publishAction(plugName, action);
				app.publishAction(plugName,action,mqtt);

			// if(action.equals("on")){
			// 	app.publishAction(plugName,action,mqtt);
			// }

			// else if(action.equals("off")){
			// 	app.publishAction(plugName,action,mqtt);

			// }

			// else if(action.equals("toggle")){
			// }
						
	
		}

		@Override
		public void close() throws Exception {
		}

	// synchronized public String plugState(String plugName) {
	// 	// String state = "off";
	// 	// for(HashMap<String, Object>  plug: states){
	// 	// 	if(plug.containsKey(plugName)){
	// 	// 		state = plug.get(plugName).toString();
	// 	// 	}
	// 	// } 

	// 	String state = app.getState(plugName);
	// 	try {
	// 		return state;
	// 	} catch (Exception e) {
	// 		return "off";
	// 	}

	// } 
	// synchronized public ArrayList<String> getplugstates(){
	// 	ArrayList<String> str = new ArrayList<>();
	// 	Set<String> temp = app.getStates().keySet();
	// 	for(String k : temp){
	// 		str.add(app.getStates().get(k));
	// 	}
	// 	return str;  
	// }

	// synchronized public void groupState(List<String> plugsgroup, String action)
	// 		throws Exception {
	// 	// ArrayList<HashMap<String, Object>> groupstates = new ArrayList<>();
	// 	if(!plugsgroup.isEmpty()){
	// 		for(String plug: plugsgroup ){
	// 			// System.out.println(plug);
	// 			updatePlugs(plug, action);
	// 			// HashMap<String, Object> state = new HashMap<>();
	// 			// state.put("name",plug);
	// 			// state.put("state",plugState(plug));
	// 			// // state.put("state",action);
	// 			// // System.out.println(state);
	// 			// groupstates.add(state);
	// 		}
	// 	}

	// 	//Adding the states to the map
	// 	//Return Map
	// 	// return groupstates;
	// }


}