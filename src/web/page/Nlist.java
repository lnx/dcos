package web.page;

import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import util.MapFactory;
import web.TemplateReader;
import brain.DataCenter;

@Path("/nlist")
public class Nlist {

	@GET
	@Produces("text/html")
	public String running() {
		Map<String, Object> root = MapFactory.newHashMap();
		return TemplateReader.getInstance().readTemplate("nlist.html", root);
	}

	@GET
	@Path("/nodes")
	@Produces("text/plain")
	public String result() {
		Map<String, Object> root = MapFactory.newHashMap();
		root.put("sensorDataList", DataCenter.getSensorDataList());
		return TemplateReader.getInstance().readTemplate("nlist.nodes.html",
				root);
	}

}
