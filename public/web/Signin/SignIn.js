class SignIn extends React.Component {

	// constructor(props) {
	// 	super(props);
	// }

	render() {
		return (
            <div>
                <div  className="container">
                    <h1>Welcome</h1>
                    <form className="form">
                        <input type="text" placeholder = "Username"></input>
                        <input type="password" placeholder = "Password"></input>
                        <button type="submit" id="login-button"> Login</button>
                    </form>

                </div>
                {/* <ul class="bg-bubbles">
                    <li></li>
                    <li></li>
                    <li></li>
                    <li></li>
                    <li></li>
                    <li></li>

                </ul> */}
            </div>
        )
	}
}

// window. = Members;
window.SignIn = SignIn;