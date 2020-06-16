/**
 * The App class is a controller holding the global state.
 * It creates all children controllers in render().
 */
class IoTHubApp extends React.Component {

	constructor(props) {
		super(props);
		this.state = {
			plugSelected: null
		};
	}

	updatePlugSelected(plug) {
		this.setState({ plugSelected: plug });
	}

	render() {
		return (
			<div className="container">
				<div className="row">
					<h3>Welcome to IoT Hub from ECE448/ECE528@IIT!</h3>
					<hr className="col-sm-12" />
				</div>
				<div className="row">
					<div className="col-sm-2">
						<Plugs
							updatePlugSelected={plug => this.updatePlugSelected(plug)}
							plugSelected={this.state.plugSelected} />
					</div>
					<div className="col-sm-10">
						<PlugDetails
							plugSelected={this.state.plugSelected} />
						
					</div>
				</div>
				{/* <div className="row">
					<p>

					<button href="./web/members.html">Manage Groups</button>

					</p>
				</div> */}
			</div>);
	}
}

window.IoTHubApp = IoTHubApp;
