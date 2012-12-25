package base.model;

import model.Point;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import util.Tool;

public class SensorInfo {

	private final String sid;

	private final Point location;

	public SensorInfo(String sid, Point location) {
		this.sid = sid == null ? "" : sid;
		this.location = location;
	}

	public String getSid() {
		return sid;
	}

	public Point getLocation() {
		return location;
	}

	public boolean isCorrect() {
		return Tool.checkIp(sid) && !sid.matches("^.*\\.0$")
				&& location != null;
	}

	public int hashCode() {
		return sid.hashCode() + location.hashCode();
	}

	public boolean equals(Object obj) {
		boolean ret = false;
		if (obj != null && obj instanceof SensorInfo) {
			SensorInfo sensorInfo = (SensorInfo) obj;
			if (sid.equals(sensorInfo.getSid())) {
				ret = true;
			}
		}
		return ret;
	}

	public String toString() {
		return ToStringBuilder.reflectionToString(this,
				ToStringStyle.SHORT_PREFIX_STYLE);
	}

}
