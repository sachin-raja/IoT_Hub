package ece448.iot_hub;

import java.io.File;
import java.util.HashMap;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

import ece448.iot_sim.SimConfig;


public class Main implements AutoCloseable {

	public static void main(String[] args) throws Exception {
		// load configuration file
		String configFile = args.length > 0 ? args[0] : "hubConfig.json";
		HubConfig config = mapper.readValue(new File(configFile), HubConfig.class);
		String simConfigFile = args.length > 0 ? args[0] : "simConfig.json";
		SimConfig simConfig = mapper.readValue(new File(simConfigFile), SimConfig.class);
		String simExConfigFile = args.length > 0 ? args[0] : "simConfigEx.json";
		SimConfig simExConfig = mapper.readValue(new File(simExConfigFile), SimConfig.class);

		// try (Main m = new Main(config, args))
		try(
			ece448.iot_sim.Main m = new ece448.iot_sim.Main(simConfig);
			ece448.iot_sim.Main mex = new ece448.iot_sim.Main(simExConfig);
			ece448.iot_hub.Main hub = new ece448.iot_hub.Main(config, new String[0]);
		)
		{
			// loop forever
			for (;;)
			{
				Thread.sleep(60000);
			}
		}
	}

	public Main(HubConfig config, String[] args) throws Exception {
		// Spring app
		HashMap<String, Object> props = new HashMap<>();
		props.put("server.port", config.getHttpPort());
		props.put("mqtt.broker", config.getMqttBroker());
		props.put("mqtt.clientId", config.getMqttClientId());
		props.put("mqtt.topicPrefix", config.getMqttTopicPrefix());
		SpringApplication app = new SpringApplication(App.class);
		app.setDefaultProperties(props);
		this.appCtx = app.run(args);
	}

	@Override
	public void close() throws Exception {
		appCtx.close();
	}

	private final ConfigurableApplicationContext appCtx;

	private static final ObjectMapper mapper = new ObjectMapper();
}
