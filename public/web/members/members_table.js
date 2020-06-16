const btnClassAdd = "btn btn-primary btn-block";
const btnClassDel = "btn btn-danger btn-block";
const btnon = "btn btn-warning btn-block";
const btnoff = "btn btn-default btn-block";

/**
 * This is a stateless view showing the table header.
 */
function Header(props) {
	var ths = [];
	for (var groupName of props.groupNames) {
		ths.push(
			<th key={groupName}>
				{/* <th>
					<th>
					<button className={btnon}>ON</button>
					</th>
					<th>
					<button className={btnoff}>OFF</button>
					</th>
					
				</th> */}
				<button className={btnClassAdd}>{groupName}</button>
			</th>
		);
	}

	return (
		<thead>
			<tr>
				<th rowSpan="2" width="10%">States</th>
				<th rowSpan="2" width="10%">Members</th>
				<th colSpan={props.groupNames.length}>Groups</th>
				<th rowSpan="2" width="10%">Remove from All Groups</th>
			</tr>
			<tr>
				{ths}
			</tr>
		</thead>
	);
}

/**
 * This is a stateless view showing one row.
 */
function Row(props) {
	var members = props.members;
	var tds = members.get_group_names().map(groupName => {
		var onChange = () => props.onMemberChange(props.memberName, groupName);
		var checked = members.is_member_in_group(props.memberName, groupName);
		return (<td key={groupName}>
			<input type="checkbox" onChange={onChange} checked={checked}/></td>);
	});
	var onDeleteMember = () => props.onRemoveMemberFromAllGroups(props.memberName);
	var onAddClick = () => props.onAddMemberToAllGroups(props.memberName);
	// var onAddClickState = () => props.onStateofMemberChange(props.memberName,"on");
	var onAddClickState = () => {
		props.onMemberStateChange(props.memberName,members.get_member_state(props.memberName));

	}
	var btnplugstate = (members.get_member_state(props.memberName) == "on") ? "btn btn-warning btn-block" : "btn btn btn-block";
	return (
		<tr>
			<td><button className={btnplugstate} onClick = {onAddClickState}>{members.get_member_state(props.memberName)}</button></td>
			<td><button className={btnClassAdd} onClick={onAddClick}>{props.memberName}</button></td>
			{tds}
			<td><button className={btnClassDel} onClick={onDeleteMember}>X</button></td>
		</tr>
	);
}

/**
 * This is a stateless view showing the row for delete groups.
 */
function DeleteGroupsRow(props) {
	var tds = props.groupNames.map(groupName => {
		var onClick = () => props.onDeleteGroup(groupName);
		return <td key={groupName}>
			<div>
			<button className={btnClassDel} onClick={onClick}>X</button>

			</div>
			<div>
			<button className="btn-primary " onClick={() => props.onStateGroupChange(groupName, "on")}>
					Switch On
				</button>
				<button className="btn-primary " onClick={() => props.onStateGroupChange(groupName, "off")}>
					Switch Off
				</button>	
			</div>
			{/* <td className= "text-align: center; vertical-align: top; ">
				
			</td> */}
			</td>;
	});

	return (
		<tr>
			<td></td>
			<td></td>

			{tds}
			<td></td>
		</tr>
	);
}

/**
 * This is a stateless view showing inputs for add/replace groups.
 */
function AddGroup(props) {
	var onChangeName = event => props.onInputNameChange(event.target.value);
	// console.log(event.target.value)
	var onChangeMembers = event => props.onInputMembersChange(event.target.value);

	return (
		<div>
			<label>Group Name</label>
			<input type="text" onChange={onChangeName} value={props.inputName}/>
			<label>Members</label>
			<input type="text" onChange={onChangeMembers} value={props.inputMembers}
				size="60" placeholder="e.g. a,b,c"/>
			<button className="btn btn-primary" onClick={props.onAddGroup}>
				Add/Replace</button>
		</div>
	);
}

/**
 * This is a stateless view showing the table body.
 */
function Body(props) {
	var rows = props.members.get_member_names().map(memberName =>
		<Row key={memberName}  memberName={memberName} members={props.members}
			onMemberChange={props.onMemberChange}
			onAddMemberToAllGroups={props.onAddMemberToAllGroups} 
			onMemberStateChange={props.onMemberStateChange}
			onStateGroupChange={props.onStateGroupChange}
			onRemoveMemberFromAllGroups={props.onRemoveMemberFromAllGroups}
			onStateofMemberChange={props.onStateofMemberChange} />);

	return (
		<tbody>
			{rows}
			<DeleteGroupsRow groupNames={props.members.get_group_names()}
				onDeleteGroup={props.onDeleteGroup} />
			<tr><td colSpan="7">
				<AddGroup inputName={props.inputName} inputMembers={props.inputMembers}
					onInputNameChange={props.onInputNameChange}
					onInputMembersChange={props.onInputMembersChange}
					onAddGroup={props.onAddGroup} />
			</td></tr>
		</tbody>
	);
}

/**
 * This is a stateless view showing the whole members table.
 */
function MembersTable(props) {
	//console.info("MembersTable()");
	if (props.members.get_group_names().length == 0)
		return (
			<div>
				<div>There are no groups.</div>
				<AddGroup inputName={props.inputName} inputMembers={props.inputMembers}
					onInputNameChange={props.onInputNameChange}
					onInputMembersChange={props.onInputMembersChange}
					onAddGroup={props.onAddGroup} />
			</div>);

	return (
		<table className="table table-striped table-bordered">
			<Header groupNames={props.members.get_group_names()} />
			<Body members={props.members}
				inputName={props.inputName} inputMembers={props.inputMembers}
				onStateofMemberChange = {props.onStateofMemberChange}
				onMemberChange={props.onMemberChange}
				onDeleteGroup={props.onDeleteGroup}
				onMemberStateChange={props.onMemberStateChange}
				onStateGroupChange={props.onStateGroupChange}
				onInputNameChange={props.onInputNameChange}
				onInputMembersChange={props.onInputMembersChange}
				onAddGroup={props.onAddGroup}
				onAddMemberToAllGroups={props.onAddMemberToAllGroups}
				onRemoveMemberFromAllGroups={props.onRemoveMemberFromAllGroups}
				onStateofMemberChange={props.onStateofMemberChange} />
		</table>);
}

//export
window.MembersTable = MembersTable;