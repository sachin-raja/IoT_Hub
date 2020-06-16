/**
 * This is a stateless view showing one plug.
 */
function PlugRow(props) {
	var btnClass = (props.plug.state == "on") ? "btn-block btn-warning" : "btn-block btn-default";

	return (
		<p><button className={btnClass}
			onClick={() => props.selectPlug(props.plug)}>
			{props.plug.name}
		</button></p>
	);
}

/**
 * This is a stateless view listing all plugs.
 */
window.PlugsView = function (props) {
	if (props.plugs.length == 0)
		return (<div>There are no plugs.</div>);

	var rows = props.plugs.map(function (plug) {
		return (
			<PlugRow key={plug.name}
				plug={plug}
				plugSelected={props.plugSelected}
				selectPlug={props.selectPlug} />);
	});

	return (
		<div>
			{rows}
		</div>);
}
