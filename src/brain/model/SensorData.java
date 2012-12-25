package brain.model;

import java.text.DecimalFormat;

import model.Point;

import org.joda.time.DateTime;

import util.Tool;

import base.Monitor;
import base.Sensor;
import base.Sensor.Status;

public class SensorData implements Comparable<SensorData> {

	private static final DecimalFormat df = new DecimalFormat("0.00");

	private String id;

	private Point location;

	private Sensor.Status status;

	private String agentTime;

	private String agentStart = "-";

	private Monitor.Status monitorStatus;

	private String monitorTime;

	private String monitorStart = "-";

	private double monitorRate;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Point getLocation() {
		return location;
	}

	public void setLocation(Point location) {
		this.location = location;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public String getAgentTime() {
		return agentTime;
	}

	public void setAgentTime(long agentTime) {
		this.agentTime = df.format(agentTime / 60000.0) + " min";
	}

	public String getAgentStart() {
		return agentStart;
	}

	public void setAgentStart(long agentStart) {
		this.agentStart = new DateTime(agentStart)
				.toString("yyyy-MM-dd HH:mm:ss");
	}

	public Monitor.Status getMonitorStatus() {
		return monitorStatus;
	}

	public void setMonitorStatus(Monitor.Status monitorStatus) {
		this.monitorStatus = monitorStatus;
	}

	public String getMonitorTime() {
		return monitorTime;
	}

	public void setMonitorTime(long monitorTime) {
		this.monitorTime = df.format(monitorTime / 60000.0) + " min";
	}

	public String getMonitorStart() {
		return monitorStart;
	}

	public void setMonitorStart(long monitorStart) {
		this.monitorStart = new DateTime(monitorStart)
				.toString("yyyy-MM-dd HH:mm:ss");
	}

	public double getMonitorRate() {
		return monitorRate;
	}

	public void setMonitorRate(double monitorRate) {
		this.monitorRate = monitorRate;
	}

	public int compareTo(SensorData sensorData) {
		int ret = 0;
		if (sensorData != null) {
			ret = Tool.ipCompare(id, sensorData.getId());
		}
		return ret;
	}

}
