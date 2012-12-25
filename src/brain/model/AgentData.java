package brain.model;

import util.Tool;
import model.Point;

public class AgentData implements Comparable<AgentData> {

	private String sid;

	private Point location;

	private double totalRate;

	public String getSid() {
		return sid;
	}

	public void setSid(String sid) {
		this.sid = sid;
	}

	public Point getLocation() {
		return location;
	}

	public void setLocation(Point location) {
		this.location = location;
	}

	public double getTotalRate() {
		return totalRate;
	}

	public void setTotalRate(double totalRate) {
		this.totalRate = totalRate;
	}

	public int compareTo(AgentData agentData) {
		int ret = 0;
		if (agentData != null) {
			ret = Tool.ipCompare(sid, agentData.getSid());
		}
		return ret;
	}

}
