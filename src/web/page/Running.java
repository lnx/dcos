package web.page;

import java.util.Map;

import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import brain.CommandCenter;
import brain.DataCenter;
import brain.model.Execution;

import util.MapFactory;
import web.TemplateReader;

@Path("/running")
public class Running {

	private static Execution execution = new Execution(true,
			"Waiting for commands...");

	@GET
	@Produces("text/html")
	public String running() {
		Map<String, Object> root = MapFactory.newHashMap();
		return TemplateReader.getInstance().readTemplate("running.html", root);
	}

	@GET
	@Path("/command")
	@Produces("text/plain")
	public String result() {
		Map<String, Object> root = MapFactory.newHashMap();
		root.put("execution", execution);
		return TemplateReader.getInstance().readTemplate(
				"running.command.html", root);
	}

	@POST
	@Path("/command")
	public void submit(@FormParam("rawCommand") String rawCommand) {
		execution = CommandCenter.execute(rawCommand);
	}

	@GET
	@Path("/points")
	@Produces("text/plain")
	public String getPoints() {
		return DataCenter.getMonitorPoints();
	}

	@GET
	@Path("/statistics")
	@Produces("text/plain")
	public String getStatistics() {
		Map<String, Object> root = MapFactory.newHashMap();
		DataCenter.updateNumbers();
		root.put("totalNumber", DataCenter.getTotalNumber());
		root.put("monitorNumber", DataCenter.getMonitorNumber());
		root.put("sleepNumber", DataCenter.getSleepNumber());
		root.put("agentNumber", DataCenter.getAgentNumber());
		root.put("agentDataList", DataCenter.getAgentDataList());
		return TemplateReader.getInstance().readTemplate(
				"running.statistics.html", root);
	}

}
