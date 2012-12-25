package web.page;

import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import util.MapFactory;
import web.TemplateReader;
import brain.CommandCenter;
import brain.DataCenter;
import brain.model.Event;

@Path("/monitor")
public class Monitor {

	@GET
	@Produces("text/html")
	public String monitor() {
		Map<String, Object> root = MapFactory.newHashMap();
		return TemplateReader.getInstance().readTemplate("monitor.html", root);
	}

	@GET
	@Path("/points")
	@Produces("text/plain")
	public String getDataPoints() {
		return DataCenter.getDataPoints();
	}

	@GET
	@Path("/statistics")
	@Produces("text/plain")
	public String getStatistics() {
		Map<String, Object> root = MapFactory.newHashMap();
		String startTime = "-";
		String duration = "-";
		String location = "-";
		int dataCount = 0;
		if (CommandCenter.event != null) {
			Event event = CommandCenter.event;
			startTime = event.getStartTime();
			duration = event.getDuration();
			location = event.getLocation().toString();
			dataCount = event.getDataCount();
		}
		root.put("startTime", startTime);
		root.put("duration", duration);
		root.put("location", location);
		root.put("dataCount", dataCount);
		return TemplateReader.getInstance().readTemplate(
				"monitor.statistics.html", root);
	}

}
