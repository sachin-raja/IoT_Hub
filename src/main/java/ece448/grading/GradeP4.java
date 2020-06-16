package ece448.grading;

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
import ece448.grading.GradeP3.MqttController;
import ece448.iot_hub.HubConfig;

public class GradeP4 implements AutoCloseable {

	private static final String broker = "tcp://127.0.0.1";
	private static final String topicPrefix = "grade_p4/iot_ece448";
	private static final List<String> plugNames = Arrays.asList("a", "b", "c");
	private static final List<String> plugNamesEx = Arrays.asList("d", "e", "f", "g");
	private static final List<String> allPlugNames = Arrays.asList("a", "b", "c", "d", "e", "f", "g");
	private static final List<String> groupNames = Arrays.asList("x", "y", "z");

	private static final ObjectMapper mapper = new ObjectMapper();

	private final MqttController mqtt;

	public GradeP4() throws Exception {
		this.mqtt = new MqttController(broker, "grader/iot_hub", topicPrefix);
		this.mqtt.start();
	}

	@Override
	public void close() throws Exception {
		mqtt.close();
	}

	public static void main(String[] args) throws Exception {
		SimConfig config = new SimConfig(8080, plugNames, broker, "testee/iot_sim", topicPrefix);
		SimConfig configEx = new SimConfig(8081, plugNamesEx, broker, "ex_testee/iot_sim", topicPrefix);
		HubConfig hubConfig = new HubConfig(8088, broker, "testee/iot_hub", topicPrefix);

		try (
			ece448.iot_sim.Main m = new ece448.iot_sim.Main(config);
			ece448.iot_sim.Main mex = new ece448.iot_sim.Main(configEx);
			ece448.iot_hub.Main hub = new ece448.iot_hub.Main(hubConfig, new String[0]);
			GradeP4 p4 = new GradeP4())
		{
			Grading.run(p4, 10);
		}
	}

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
			states.put(name, "off".equals(mqtt.getState(name))? "0": "1");
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

	public boolean testCase00() throws Exception {
		return verifyStates("0000000") && verifyGroups("");
	}

	public boolean testCase01() throws Exception {
		getHub("/api/plugs/a?action=on");
		getHub("/api/plugs/c?action=toggle");
		getHub("/api/plugs/e?action=on");
		getHub("/api/plugs/g?action=toggle");

		Thread.sleep(1000);
		return verifyStates("1010101");
	}

	public boolean testCase02() throws Exception {
		getHub("/api/plugs/a?action=toggle");
		getHub("/api/plugs/b?action=on");
		getHub("/api/plugs/c?action=off");
		getHub("/api/plugs/d?action=toggle");
		getHub("/api/plugs/e?action=on");
		getHub("/api/plugs/f?action=off");
		getHub("/api/plugs/g?action=toggle");

		Thread.sleep(1000);
		return verifyStates("0101100");
	}

	public boolean testCase03() throws Exception {
		getSim("/b?action=off");
		getSimEx("/d?action=off");
		mqtt.publishAction("e", "off");

		Thread.sleep(1000);
		return verifyStates("0000000");
	}	

	public boolean testCase04() throws Exception {
		postGroup("x", Arrays.asList("a", "c", "e", "g"));
		postGroup("y", Arrays.asList("b", "d", "f"));
		postGroup("z", Arrays.asList("a", "d"));

		Thread.sleep(1000);
		return verifyStates("0000000")
			&& verifyGroups("x.a0c0e0g0|y.b0d0f0|z.a0d0");
	}

	public boolean testCase05() throws Exception {
		getHub("/api/groups/x?action=on");
		getHub("/api/groups/y?action=off");

		Thread.sleep(1000);
		return verifyStates("1010101")
			&& verifyGroups("x.a1c1e1g1|y.b0d0f0|z.a1d0");
	}

	public boolean testCase06() throws Exception {
		getHub("/api/groups/z?action=toggle");

		Thread.sleep(1000);
		return verifyStates("0011101")
			&& verifyGroups("x.a0c1e1g1|y.b0d1f0|z.a0d1");
	}

	public boolean testCase07() throws Exception {
		getSim("/c?action=off");
		getSimEx("/d?action=off");
		mqtt.publishAction("e", "off");
		mqtt.publishAction("g", "toggle");

		Thread.sleep(1000);
		return verifyStates("0000000")
			&& verifyGroups("x.a0c0e0g0|y.b0d0f0|z.a0d0");
	}

	public boolean testCase08() throws Exception {
		delGroup("z");

		Thread.sleep(1000);
		return verifyStates("0000000")
			&& verifyGroups("x.a0c0e0g0|y.b0d0f0");
	}

	public boolean testCase09() throws Exception {
		postGroup("x", Arrays.asList("a", "b", "c"));
		postGroup("y", Arrays.asList("e", "f", "g"));

		Thread.sleep(500);
		getHub("/api/groups/x?action=toggle");
		getHub("/api/groups/y?action=toggle");
		getHub("/api/groups/x?action=toggle");

		Thread.sleep(1000);
		return verifyStates("0000111")
			&& verifyGroups("x.a0b0c0|y.e1f1g1");
	}
}
