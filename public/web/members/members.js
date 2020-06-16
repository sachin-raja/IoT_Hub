/**
 * A model for managing members in groups.
 */

var member_states = new Map(); // list of plugs with states
var member_powers = new Map();

function create_members_model(groups) {
	// create the data structure
	var all_members = new Set(); // all unique member names
	var group_names = [];
	var group_members = new Map(); // group_name to set of group members 
	for (var group of groups) {	
		group_names.push(group.name);
		// var members = new Set(group.members.name);
		var members = new Set();
		var memberstates = new Set();
		for( var item of group.members){
			if(!member_states.has(item.name)){
				member_states.set(item.name,item.state);
				member_powers.set(item.name,item.power);
			}
			// console.log("item", item.name)
			members.add(item.name);
		}
		console.log(member_states);
		group_members.set(group.name, members);
		members.forEach(member => all_members.add(member));
	}
	console.log(memberstates);
	var member_names = Array.from(all_members);
	group_names.sort();
	member_names.sort(); 

	// create the object
	var that = {}
	that.get_member_states = () => member_states;
	that.get_member_state = (name) => member_states.get(name);
	that.get_group_names = () => group_names;
	that.get_member_names = () => member_names;
	that.is_member_in_group = (member_name, group_name) =>
		!group_members.has(group_name)? false:
			group_members.get(group_name).has(member_name);
	that.get_group_members = group_name => group_members.get(group_name);

	console.debug("Members Model",
		groups, group_names, member_names, group_members,member_states);

	return that;
}

/**
 * The Members controller holds the state of groups.
 * It creates its view in render().
 */
class Members extends React.Component {

	constructor(props) {
		super(props);
		console.info("Members constructor()");
		this.state = {
			members: create_members_model([]),
			inputName: "",
			inputMembers: "",
			memberState: "off",
		};
	}

	componentDidMount() {
		console.info("Members componentDidMount()");
		this.getGroups();
		setInterval(this.Members, 1000);
	}

	render() {
		//console.info("Members render()");
		return (<MembersTable members={this.state.members}
			inputName={this.state.inputName} inputMembers={this.state.inputMembers}
			memberState={this.state.memberState}
			onMemberChange={this.onMemberChange}
			onDeleteGroup={this.onDeleteGroup}
			onStateGroupChange = {this.onStateGroupChange}

			onMemberStateChange={this.onMemberStateChange}
			onInputNameChange={this.onInputNameChange}
			onInputMembersChange={this.onInputMembersChange}
			onAddGroup={this.onAddGroup}
			onRemoveMemberFromAllGroups = {this.onRemoveMemberFromAllGroups}

			onAddMemberToAllGroups={this.onAddMemberToAllGroups}
			onStateofMemberChange={this.onStateofMemberChange} />);
	}

	getGroups = () => {
		console.debug("RESTful: get groups");
		fetch("api/groups")
			.then(rsp => rsp.json())
			.then(groups => this.showGroups(groups))
			.catch(err => console.error("Members: getGroups", err));
	}

	showGroups = groups => {
		this.setState({
			members: create_members_model(groups)
		});
	}

	createGroup = (groupName, groupMembers) => {
		console.info("RESTful: create group "+groupName
			+" "+JSON.stringify(groupMembers));
		
		var postReq = {
			method: "POST",
			headers: {"Content-Type": "application/json"},
			body: JSON.stringify(groupMembers)
		};
		fetch("api/groups/"+groupName, postReq)
			.then(rsp => this.getGroups())
			.catch(err => console.error("Members: createGroup", err));
	}

	createManyGroups = groups => {
		console.info("RESTful: create many groups "+JSON.stringify(groups));
		var pendingReqs = groups.map(group => {
			var postReq = {
				method: "POST",
				headers: {"Content-Type": "application/json"},
				body: JSON.stringify(group.members)
			};
			return fetch("api/groups/"+group.name, postReq);
		});

		Promise.all(pendingReqs)
			.then(() => this.getGroups())
			.catch(err => console.error("Members: createManyGroup", err));
	}

	deleteGroup = groupName => {
		console.info("RESTful: delete group "+groupName);
	
		var delReq = {
			method: "DELETE"
		};
		fetch("api/groups/"+groupName, delReq)
			.then(rsp => this.getGroups())
			.catch(err => console.error("Members: deleteGroup", err));
	}




	//reshuffling groups
	onMemberChange = (memberName, groupName) => {
		var groupMembers = new Set(this.state.members.get_group_members(groupName));
		if (groupMembers.has(memberName))
			groupMembers.delete(memberName);
		else
			groupMembers.add(memberName);

		this.createGroup(groupName, Array.from(groupMembers));
	}





	onDeleteGroup = groupName => {
		this.deleteGroup(groupName);
	}

	onInputNameChange = value => {
		console.debug("Members: onInputNameChange", value);
		this.setState({inputName: value});
	}

	//Changing the state of the group
	onStateGroupChange = (groupName, action) => {
		console.info("RESTful: state of group "+ groupName+"is  "+action);

		var postReq = {
			method: "POST",
			headers: {"Content-Type": "application/json"},
			body: JSON.stringify(groupName)
		};
		fetch("api/groups/"+groupName+"?action="+action, postReq)
			.then(rsp => this.getGroups())
			.catch(err => console.error("State of Group not changed", err));
	}


	onMemberStateChange = (plugname,action) => {

		console.log("action now"+action)
		if(action=="off"){
			this.setState({memberState: "on"});
		}
		else if (action=="on") {
			this.setState({memberState: "off"});
		}
		console.log("toggke me"+this.state.memberState);
		var postReq = {
			method: "GET",
			headers: {"Content-Type": "application/json"},
		};
		fetch("api/plugs/"+plugname+"?action="+this.state.memberState, postReq)
			.then(rsp => this.getGroups())
			.catch(err => console.error("State of Plug "+plugname+" not changed", err));
		
	}

	onInputMembersChange = value => {
		console.debug("Members: onInputMembersChange", value);
		this.setState({inputMembers: value});
	}

	onAddGroup = () => {
		var name = this.state.inputName;
		var members = this.state.inputMembers.split(',');
	
		this.createGroup(name, members);
	}
//On Click event ot add plug to all the groups 
	onAddMemberToAllGroups = memberName => {
		var groups = [];
		for (var groupName of this.state.members.get_group_names()) {
			var groupMembers = new Set(this.state.members.get_group_members(groupName));
			groupMembers.add(memberName);
			groups.push({name: groupName, members: Array.from(groupMembers)});
		}
		this.createManyGroups(groups);
	}
	//On Click event ot remove plug to all the groups 
	onRemoveMemberFromAllGroups = memberName => {
		var groups = [];
		for (var groupName of this.state.members.get_group_names()) {
			var groupMembers = new Set(this.state.members.get_group_members(groupName));
			groupMembers.delete(memberName);
			groups.push({name: groupName, members: Array.from(groupMembers)});
		}
		this.createManyGroups(groups);
	}
}

// export
window.Members = Members;