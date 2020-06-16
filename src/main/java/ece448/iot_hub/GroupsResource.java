package ece448.iot_hub;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class GroupsResource {
	@Autowired
	public Environment env;

	@Autowired
	public App app;

	@Autowired
	MqttClient mqtt;

	
	private final GroupsModel groups;
	public GroupsResource(GroupsModel groups) throws Exception {
		this.groups = groups;
	}

	//API request returns the groups available
	@GetMapping("/api/groups")
	public Collection<Object> getGroups() throws Exception {
		ArrayList<Object> ret = new ArrayList<>();
		for (String group: groups.getGroups()) {
			ret.add(makeGroup(group));
		}
		logger.info("Groups 1: {}", ret);
		return ret;
	}
	//API request returns the group members and their states and powers
	@GetMapping("/api/groups/{group}")
	public Object getGroup(
		@PathVariable("group") String group,
		@RequestParam(value = "action", required = false) String action) throws Exception {
		if (action == null) {
			Object ret = makeGroup(group);
			logger.info("Group {}: {}", group, ret);
			return ret;
		}

		// modify code below to control plugs by publishing messages to MQTT broker
		List<String> members = groups.getGroupMembers(group);
		logger.info("Group {}: action {}, {}", group, action, members);
		for(String member: members){
			app.publishAction(member, action, mqtt);
		}
		// System.out.println(plugs.groupState(members, action));
		return makeGroup(group);
	}
//API POST request to add a group to our repository
	@PostMapping("/api/groups/{group}")
	public void createGroup(
		@PathVariable("group") String group,
		@RequestBody List<String> members) {
		groups.setGroupMembers(group, members);
		logger.info("Group {}: created {}", group, members);
	}
//API DELETE request to remove a group from the repository
	@DeleteMapping("/api/groups/{group}")
	public void removeGroup(
		@PathVariable("group") String group) {
		//Delete a group
		groups.removeGroup(group);
		logger.info("Group {}: removed", group);
	}
//Making a group
	protected Object makeGroup(String group) {
		// modify code below to include plug states
		TreeMap<String, Object> ret = new TreeMap<>();
		//Adding states
		ArrayList<Map<String, Object>> states = new ArrayList<>();
		//Mapping the groups to its members
		ret.put("name", group);
		//getting the states of the plugs
		for(String member: groups.getGroupMembers(group) ){
			TreeMap<String, Object> state = new TreeMap<>();
			state.put("name",member);
			state.put("state",groups.getGroupMemberState(member));
			state.put("power",groups.getGroupMemberPower(member));
			states.add(state);
		}
		//Adding the states to the map
		ret.put("members", states);
		//Return Map
		return ret;
	}
	private static final Logger logger = LoggerFactory.getLogger(GroupsResource.class);	
}