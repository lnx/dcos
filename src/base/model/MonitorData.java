package base.model;

import model.Point;

public class MonitorData {

	private final String sid;

	private final Point location;

	private final double data;

	private final long time;

	public MonitorData(String sid, Point location, double data, long time) {
		this.sid = sid == null ? "" : sid;
		this.location = location;
		this.data = data;
		this.time = time;
	}

	public String getSid() {
		return sid;
	}

	public Point getLocation() {
		return location;
	}

	public double getData() {
		return data;
	}

	public long getTime() {
		return time;
	}

}
