function plugAction(name, action) {
	var url = "../api/plugs/" + name + "?action=" + action;
	console.info("PlugDetails: request " + url);
	fetch(url);
}

/**
 * This is a stateless view showing details of one plug.
 */
window.PlugDetails = function (props) {
	var plug = props.plugSelected;
	if (plug == null)
		return (<div>Please select a plug from the left.</div>);

	return (
		<div>
			<p>Plug : {plug.name}</p>
			<p>State : {plug.state}</p>
			<p>Power : {plug.power}</p>
			<button className="btn-primary" onClick={() => plugAction(plug.name, "on")}>
				Switch On
			</button>
			<button className="btn-primary" onClick={() => plugAction(plug.name, "off")}>
				Switch Off
			</button>
		</div>);
}
