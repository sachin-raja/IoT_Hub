package ece448.iot_hub;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.core.env.Environment;



@Component
public class GroupsModel {
	
	@Autowired
	public Environment env;

	@Autowired
	public App app;

	private HashMap<String, HashSet<String>> groups = new HashMap<>();
//retreving the name of the groups
	synchronized public List<String> getGroups() {
		return new ArrayList<>(groups.keySet());
	}
//retreiving the state of the groups
	synchronized public String getGroupMemberState(String group) {
		return (app.getState(group)==null) ? "off" :app.getState(group);
	}
//retreiving the power of the groups
synchronized public String getGroupMemberPower(String group) {
	return (app.getPower(group)==null) ? "0.000" :app.getPower(group);
}
//retreiving the group members
	synchronized public List<String> getGroupMembers(String group) {
		HashSet<String> members = groups.get(group);
		return (members == null)? new ArrayList<>(): new ArrayList<>(members);
	}
//making groups
	synchronized public void setGroupMembers(String group, List<String> members) {
		groups.put(group, new HashSet<>(members));
	}
//remove the groups
	synchronized public void removeGroup(String group) {
		groups.remove(group);
	}
}