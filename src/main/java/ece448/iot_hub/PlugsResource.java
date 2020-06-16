package ece448.iot_hub;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.core.env.Environment;


@RestController
public class PlugsResource {

	// private final MqttHubClient mqtt;
	@Autowired
	PlugsModel plugs;

	@Autowired
	MqttClient mqtt;

	@Autowired
	public Environment env;

	@Autowired
	public App app;



	public PlugsResource(PlugsModel plugs) throws Exception {
		this.plugs = plugs;
	}

	
//list plugs
	@GetMapping("/api/plugs")
	public Collection<Object> plugs() throws Exception {
		ArrayList<Object> ret = new ArrayList<>();
		for (String plug: plugs.getPlugs()) {
			ret.add(makePlug(plug));
		}
		// logger.info("Plugs: {}", ret);
		return ret;
	}
	
	
//creating the plug
	protected Object makePlug(String plugName) {
		HashMap<String, Object> plugDetails = new HashMap<>();
		plugDetails.put("name",plugName);
		plugDetails.put("state", app.getState(plugName));
		plugDetails.put("power", app.getPower(plugName));

		// plugSate.put("state", plug.isOn()? "on": "off");
        // plugSate.put("power", "");
        return plugDetails;
	}

//retreving the plug information
	@GetMapping("/api/plugs/{plugName:.+}")
	public Object getPlug(
		@PathVariable("plugName") String plugName,
		@RequestParam(value = "action", required = false) String action) throws Exception {
		if (action == null) {
			Object ret = makePlug(plugName);
			logger.info("PlugName {}: {}", plugName, ret);
			return ret;
		}

		// modify code below to control plugs by publishing messages to MQTT broker
		// List<String> members = plugs.getPlugMembers(plugName);
		// logger.info("PlugName {}: action {}, {}", plugName, action, members);
		plugs.updatePlugs(plugName, action);

		return makePlug(plugName);
	}

	private static final Logger logger = LoggerFactory.getLogger(PlugsResource.class);	
}