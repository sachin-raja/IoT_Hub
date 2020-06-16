package ece448.iot_hub;

// import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.http.client.fluent.Request;
import org.apache.http.entity.ContentType;

import ece448.iot_sim.SimConfig;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;

public class PlugsTests {

    @Autowired
	PlugsModel plugs;
	
	@Autowired
	GroupsModel grroups;

    @Autowired
    MqttClient mqttclient;

    @Autowired
    public Environment env;

    @Autowired
	public App app;

	
	private static final String broker = "tcp://127.0.0.1";
	private static final String topicPrefix = "grade_p4/iot_ece448";
	private static final List<String> plugNames = Arrays.asList("a", "b", "c");
	private static final List<String> plugNamesEx = Arrays.asList("d", "e", "f", "g");
	private static final List<String> allPlugNames = Arrays.asList("a", "b", "c", "d", "e", "f", "g");
	private static final List<String> groupNames = Arrays.asList("x", "y", "z");

	private static final ObjectMapper mapper = new ObjectMapper();
	private String getSim(String pathParams) throws Exception {
		return Request.Get("http://127.0.0.1:8080" + pathParams)
			.userAgent("Mozilla/5.0").connectTimeout(1000)
			.socketTimeout(1000).execute().returnContent().asString();
	}

	private String getSimEx(String pathParams) throws Exception {
		return Request.Get("http://127.0.0.1:8081" + pathParams)
			.userAgent("Mozilla/5.0").connectTimeout(1000)
			.socketTimeout(1000).execute().returnContent().asString();
	}

	private String getHub(String pathParams) throws Exception {
		return Request.Get("http://127.0.0.1:8088" + pathParams)
			.userAgent("Mozilla/5.0").connectTimeout(1000)
			.socketTimeout(1000).execute().returnContent().asString();
	}

	private void postGroup(String group, List<String> members) throws Exception {
		Request.Post("http://127.0.0.1:8088/api/groups/" + group)
			.bodyByteArray(mapper.writeValueAsBytes(members), ContentType.APPLICATION_JSON)
			.userAgent("Mozilla/5.0").connectTimeout(1000)
			.socketTimeout(1000).execute();
	}

	private void delGroup(String group) throws Exception {
		Request.Delete("http://127.0.0.1:8088/api/groups/" + group)
			.userAgent("Mozilla/5.0").connectTimeout(1000)
			.socketTimeout(1000).execute();
	}

	private String getStates1() throws Exception {
		TreeMap<String, String> states = new TreeMap<>();
		HashSet<String> known = new HashSet<>(allPlugNames);

		List<Map<String, Object>> plugs = mapper.readValue(getHub("/api/plugs"),
			new TypeReference<List<Map<String, Object>>>() {});
		for (Map<String, Object> plug: plugs)
		{
			String name = (String)plug.get("name");
			String state = (String)plug.get("state");
			if (!known.contains(name))
				throw new Exception("invalid plug " + name);
			known.remove(name);
			states.put(name, "off".equals(state)? "0": "1");
		}
		if (!known.isEmpty())
			throw new Exception("missing plugs");
		return String.join("", states.values());
	}

	private String getStates2() throws Exception {
		TreeMap<String, String> states = new TreeMap<>();
		for (String name: allPlugNames)
		{
			Map<String, Object> plug = mapper.readValue(getHub("/api/plugs/" + name),
				new TypeReference<Map<String, Object>>() {});
			if (!name.equals((String)plug.get("name")))
				throw new Exception("invalid name " + name);
			states.put(name, "off".equals((String)plug.get("state"))? "0": "1");
		}
		return String.join("", states.values());
	}

	private String getStates3() throws Exception {
		TreeMap<String, String> states = new TreeMap<>();
		for (String name: plugNames)
		{
			String ret = getSim("/"+name);
			if ((ret.indexOf(name+" is off") != -1) && (ret.indexOf(name+" is on") == -1))
			{
				states.put(name, "0");
			}
			else
			{
				states.put(name, "1");
			}
		}
		for (String name: plugNamesEx)
		{
			String ret = getSimEx("/"+name);
			if ((ret.indexOf(name+" is off") != -1) && (ret.indexOf(name+" is on") == -1))
			{
				states.put(name, "0");
			}
			else
			{
				states.put(name, "1");
			}
		}
		return String.join("", states.values());
	}

	private String getStates4() throws Exception {
		TreeMap<String, String> states = new TreeMap<>();
		for (String name: allPlugNames)
		{
			states.put(name, "off".equals(app.getState(name))? "0": "1");
		}
		return String.join("", states.values());
	}

	private boolean verifyStates(String states) throws Exception {
		return states.equals(getStates1())
			&& states.equals(getStates2())
			&& states.equals(getStates3())
			&& states.equals(getStates4());
	}

	private String getGroups1() throws Exception {
		TreeMap<String, String> fields = new TreeMap<>();

		List<Map<String, Object>> groups = mapper.readValue(getHub("/api/groups"),
			new TypeReference<List<Map<String, Object>>>() {});
		for (Map<String, Object> group: groups)
		{
			String name = (String)group.get("name");
			StringBuilder field = new StringBuilder(name+".");
			
			@SuppressWarnings("unchecked")
			List<Map<String, Object>> members = (List<Map<String, Object>>)group.get("members");
			for (Map<String, Object> member: members)
			{
				field.append(member.get("name"));
				field.append("off".equals(member.get("state"))? "0": "1");
			}
			fields.put(name, field.toString());
		}
		return String.join("|", fields.values());
	}

	private String getGroups2() throws Exception {
		TreeMap<String, String> fields = new TreeMap<>();

		for (String name: groupNames)
		{
			Map<String, Object> group = mapper.readValue(getHub("/api/groups/"+name),
				new TypeReference<Map<String, Object>>() {});
			if (!name.equals((String)group.get("name")))
				throw new Exception("invalid name " + name);

			StringBuilder field = new StringBuilder(name+".");
			
			@SuppressWarnings("unchecked")
			List<Map<String, Object>> members = (List<Map<String, Object>>)group.get("members");
			for (Map<String, Object> member: members)
			{
				field.append(member.get("name"));
				field.append("off".equals(member.get("state"))? "0": "1");
			}
			if (!members.isEmpty())
				fields.put(name, field.toString());
		}
		return String.join("|", fields.values());
	}

	private boolean verifyGroups(String groups) throws Exception {
		return groups.equals(getGroups1())
			&& groups.equals(getGroups2());
	}
	
	@Test
	public void testCase01() throws Exception{

		SimConfig config = new SimConfig(8080, plugNames, broker, "testee/iot_sim", topicPrefix);
		SimConfig configEx = new SimConfig(8081, plugNamesEx, broker, "ex_testee/iot_sim", topicPrefix);
		HubConfig hubConfig = new HubConfig(8088, broker, "testee/iot_hub", topicPrefix);

		try (
			ece448.iot_sim.Main m = new ece448.iot_sim.Main(config);
			ece448.iot_sim.Main mex = new ece448.iot_sim.Main(configEx);
			ece448.iot_hub.Main hub = new ece448.iot_hub.Main(hubConfig, new String[0]);)
		{

		}
		// app.mqttClient(env);
		// PlugsResource p = new PlugsResource(plugs);
		// p.makePlug();
		// app.handleUpdate("/update/"+"xx"+"/"+"state",new MqttMessage("on".getBytes()) );
	}

	@Test
	public void testCase02() throws Exception {

		SimConfig config = new SimConfig(8080, plugNames, broker, "testee/iot_sim", topicPrefix);
		SimConfig configEx = new SimConfig(8081, plugNamesEx, broker, "ex_testee/iot_sim", topicPrefix);
		HubConfig hubConfig = new HubConfig(8088, broker, "testee/iot_hub", topicPrefix);

		try (
			ece448.iot_sim.Main m = new ece448.iot_sim.Main(config);
			ece448.iot_sim.Main mex = new ece448.iot_sim.Main(configEx);
			ece448.iot_hub.Main hub = new ece448.iot_hub.Main(hubConfig, new String[0]);)
		{
			getHub("/api/plugs/a?action=on");
			getHub("/api/plugs/c?action=toggle");
			getHub("/api/plugs/e?action=on");
		}


	}

	@Test
	public void testCase03() throws Exception {

		SimConfig config = new SimConfig(8080, plugNames, broker, "testee/iot_sim", topicPrefix);
		SimConfig configEx = new SimConfig(8081, plugNamesEx, broker, "ex_testee/iot_sim", topicPrefix);
		HubConfig hubConfig = new HubConfig(8088, broker, "testee/iot_hub", topicPrefix);

		try (
			ece448.iot_sim.Main m = new ece448.iot_sim.Main(config);
			ece448.iot_sim.Main mex = new ece448.iot_sim.Main(configEx);
			ece448.iot_hub.Main hub = new ece448.iot_hub.Main(hubConfig, new String[0]);)
		{
			postGroup("x", Arrays.asList("a", "c", "e", "g"));
			postGroup("y", Arrays.asList("b", "d", "f"));
			postGroup("z", Arrays.asList("a", "d"));
		}


	}

	@Test
	public void testCase04() throws Exception {

		SimConfig config = new SimConfig(8080, plugNames, broker, "testee/iot_sim", topicPrefix);
		SimConfig configEx = new SimConfig(8081, plugNamesEx, broker, "ex_testee/iot_sim", topicPrefix);
		HubConfig hubConfig = new HubConfig(8088, broker, "testee/iot_hub", topicPrefix);

		try (
			ece448.iot_sim.Main m = new ece448.iot_sim.Main(config);
			ece448.iot_sim.Main mex = new ece448.iot_sim.Main(configEx);
			ece448.iot_hub.Main hub = new ece448.iot_hub.Main(hubConfig, new String[0]);)
		{
			postGroup("x", Arrays.asList("a", "c", "e", "g"));
			postGroup("y", Arrays.asList("b", "d", "f"));
			postGroup("z", Arrays.asList("a", "d"));
			delGroup("z");

		}
	}
		@Test
	public void testCase05() throws Exception {

		SimConfig config = new SimConfig(8080, plugNames, broker, "testee/iot_sim", topicPrefix);
		SimConfig configEx = new SimConfig(8081, plugNamesEx, broker, "ex_testee/iot_sim", topicPrefix);
		HubConfig hubConfig = new HubConfig(8088, broker, "testee/iot_hub", topicPrefix);

		try (
			ece448.iot_sim.Main m = new ece448.iot_sim.Main(config);
			ece448.iot_sim.Main mex = new ece448.iot_sim.Main(configEx);
			ece448.iot_hub.Main hub = new ece448.iot_hub.Main(hubConfig, new String[0]);)
		{
			getHub("/api/groups/y?action=toggle");

		}


	}

	@Test
	public void testCase06() throws Exception {

		SimConfig config = new SimConfig(8080, plugNames, broker, "testee/iot_sim", topicPrefix);
		SimConfig configEx = new SimConfig(8081, plugNamesEx, broker, "ex_testee/iot_sim", topicPrefix);
		HubConfig hubConfig = new HubConfig(8088, broker, "testee/iot_hub", topicPrefix);

		try (
			ece448.iot_sim.Main m = new ece448.iot_sim.Main(config);
			ece448.iot_sim.Main mex = new ece448.iot_sim.Main(configEx);
			ece448.iot_hub.Main hub = new ece448.iot_hub.Main(hubConfig, new String[0]);)
		{

			postGroup("x", Arrays.asList("a", "b", "c"));
			postGroup("y", Arrays.asList("e", "f", "g"));

			verifyGroups("x.a0c0e0g0|y.b0d0f0");
		}


	}

	@Test
	public void testCase07() throws Exception {

		SimConfig config = new SimConfig(8080, plugNames, broker, "testee/iot_sim", topicPrefix);
		SimConfig configEx = new SimConfig(8081, plugNamesEx, broker, "ex_testee/iot_sim", topicPrefix);
		HubConfig hubConfig = new HubConfig(8088, broker, "testee/iot_hub", topicPrefix);

		try (
			ece448.iot_sim.Main m = new ece448.iot_sim.Main(config);
			ece448.iot_sim.Main mex = new ece448.iot_sim.Main(configEx);
			ece448.iot_hub.Main hub = new ece448.iot_hub.Main(hubConfig, new String[0]);)
		{

			postGroup("y", Arrays.asList("b", "d", "f"));
			postGroup("z", Arrays.asList("a", "d"));

		}


	}

	@Test
	public void testCase08() throws Exception {

		SimConfig config = new SimConfig(8080, plugNames, broker, "testee/iot_sim", topicPrefix);
		SimConfig configEx = new SimConfig(8081, plugNamesEx, broker, "ex_testee/iot_sim", topicPrefix);
		HubConfig hubConfig = new HubConfig(8088, broker, "testee/iot_hub", topicPrefix);

		try (
			ece448.iot_sim.Main m = new ece448.iot_sim.Main(config);
			ece448.iot_sim.Main mex = new ece448.iot_sim.Main(configEx);
			ece448.iot_hub.Main hub = new ece448.iot_hub.Main(hubConfig, new String[0]);)
		{

			getSim("/b?action=off");
			getSimEx("/d?action=off");

		}


	}

}
