package ece448.iot_sim;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// import ch.qos.logback.core.pattern.color.BoldRedCompositeConverter;
import ece448.iot_sim.http_server.RequestHandler;

public class HTTPCommands implements RequestHandler {

	// Use a map so we can search plugs by name.
	private final TreeMap<String, PlugSim> plugs = new TreeMap<>();

	public HTTPCommands(final List<PlugSim> plugs) {
		for (final PlugSim plug : plugs) {
			this.plugs.put(plug.getName(), plug);
		}
	}

	@Override
	public String handleGet(final String path, final Map<String, String> params) {
		// list all: /
		// do switch: /plugName?action=on|off|toggle
		// just report: /plugName

		logger.info("HTTPCmd {}: {}", path, params);

		if (path.equals("/"))
		{
			return listPlugs();
		}

		final PlugSim plug = plugs.get(path.substring(1));
		if (plug == null)
			return null; // no such plug

		final String action = params.get("action");
		if (action == null)
			// params.ge
			return report(plug);
		
		// P2: add your code here, modify the next line if necessary


		switch (action){
		case "on":
			plug.updateState(true); //calling method turn switch on 
			break;
		case "off":
			plug.updateState(false); //calling method turn switch off 
			break;
		case "toggle":
			plug.toggle(); // toggle a swtich
			break;
		default: 
			return "<html><body style='background-color:powderblue;'></body></html>";		
		}
		return report(plug); //update plug report
	}

	protected String listPlugs() {
		final StringBuilder sb = new StringBuilder();

		sb.append("<html><body>");
		for (final String plugName : plugs.keySet()) {
			sb.append(String.format("<p><a href='/%s'>%s</a></p>", plugName, plugName));
		}
		sb.append("</body></html>");

		return sb.toString();
	}

	protected String report(final PlugSim plug) {
		final String name = plug.getName();
		return String.format("<html><body>"
			+"<p>Plug %s is %s.</p>"
			+"<p>Power reading is %.3f.</p>"
			+"<p><a href='/%s?action=on'>Switch On</a></p>"
			+"<p><a href='/%s?action=off'>Switch Off</a></p>"
			+"<p><a href='/%s?action=toggle'>Toggle</a></p>"
			+"</body></html>",
			name,
			plug.isOn()? "on": "off",
			plug.getPower(), name, name, name);
	}

	private static final Logger logger = LoggerFactory.getLogger(HTTPCommands.class);
}
