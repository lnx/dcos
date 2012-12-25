package brain;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import model.Point;
import util.ListFactory;
import base.Monitor;
import base.Monitor.Status;
import base.Sensor;
import base.Setting;
import base.model.MonitorData;
import brain.model.AgentData;
import brain.model.MonitorPoint;
import brain.model.SensorData;

import com.google.gson.Gson;

public class DataCenter {

	private static double eventFlicker = 3;
	private static boolean flicker = true;

	private static int totalNumber = 0;
	private static int monitorNumber = 0;
	private static int sleepNumber = 0;
	private static int agentNumber = 0;

	public static long getTenure() {
		return Setting.AGENT_TENURE;
	}

	public static void updateNumbers() {
		int tn = 0, mn = 0, sn = 0, an = 0;
		for (Set<Sensor> sensorSet : CommandCenter.sensors) {
			tn += sensorSet.size();
			for (Sensor sensor : sensorSet) {
				if (sensor.getMonitor().getStatus() == Status.MONITOR) {
					mn++;
				}
				if (sensor.getMonitor().getStatus() == Status.SLEEP) {
					sn++;
				}
				if (sensor.getStatus() == Sensor.Status.AGENT) {
					an++;
				}
			}
		}
		totalNumber = tn;
		monitorNumber = mn;
		sleepNumber = sn;
		agentNumber = an;
	}

	public static int getTotalNumber() {
		return totalNumber;
	}

	public static int getMonitorNumber() {
		return monitorNumber;
	}

	public static int getSleepNumber() {
		return sleepNumber;
	}

	public static int getAgentNumber() {
		return agentNumber;
	}

	public static String getMonitorPoints() {
		eventFlicker += 1;
		eventFlicker = eventFlicker <= 6 ? eventFlicker : 3;
		flicker = !flicker;
		List<List<MonitorPoint>> monitorPoints = ListFactory.newArrayList();
		for (Set<Sensor> sensorSet : CommandCenter.sensors) {
			List<MonitorPoint> mps = ListFactory.newArrayList();
			for (Sensor sensor : sensorSet) {
				Point point = sensor.getSensorInfo().getLocation();
				double r = 1;
				double agentR = 2;
				double monitorR = 1.3;
				double sleepR = 0.8;
				if (sensor.getStatus() == Sensor.Status.AGENT) {
					r *= agentR;
				} else if (sensor.getMonitor().getStatus() == Status.MONITOR) {
					if (flicker) {
						r *= monitorR;
					}
				} else if (sensor.getMonitor().getStatus() == Status.SLEEP) {
					r *= sleepR;
				}
				mps.add(new MonitorPoint(point.getX(), point.getY(), r));
			}
			if (mps.size() > 0) {
				monitorPoints.add(mps);
			}
		}
		if (CommandCenter.event != null) {
			List<MonitorPoint> mps = ListFactory.newArrayList();
			mps.add(new MonitorPoint(CommandCenter.event.getLocation().getX(),
					CommandCenter.event.getLocation().getY(), eventFlicker));
			if (mps.size() > 0) {
				monitorPoints.add(mps);
			}
		}
		return new Gson().toJson(monitorPoints);
	}

	public static String getDataPoints() {
		List<List<Point>> Points = ListFactory.newArrayList();
		if (CommandCenter.event != null) {
			List<List<MonitorData>> monitorDatas = CommandCenter.event
					.getMonitorDatas();
			for (List<MonitorData> mdList : monitorDatas) {
				List<Point> dpList = ListFactory.newArrayList();
				int x = 0;
				for (MonitorData md : mdList) {
					dpList.add(new Point(x, md.getData()));
					x += 2;
				}
				Points.add(dpList);
			}
		}
		return new Gson().toJson(Points);
	}

	public static List<AgentData> getAgentDataList() {
		List<AgentData> ret = ListFactory.newArrayList();
		for (Set<Sensor> sensorSet : CommandCenter.sensors) {
			for (Sensor sensor : sensorSet) {
				if (sensor.getStatus() == Sensor.Status.AGENT) {
					AgentData ad = new AgentData();
					ad.setSid(sensor.getSensorInfo().getSid());
					ad.setLocation(sensor.getSensorInfo().getLocation());
					ad.setTotalRate(sensor.getAgent().getTotalRate());
					ret.add(ad);
				}
			}
		}
		Collections.sort(ret);
		return ret;
	}

	public static List<SensorData> getSensorDataList() {
		List<SensorData> sensorDataList = ListFactory.newArrayList();
		for (Set<Sensor> sensorSet : CommandCenter.sensors) {
			for (Sensor sensor : sensorSet) {
				SensorData sd = new SensorData();
				sd.setId(sensor.getSensorInfo().getSid());
				sd.setLocation(sensor.getSensorInfo().getLocation());
				sd.setStatus(sensor.getStatus());
				sd.setAgentTime(sensor.getAgent().getAgentTime());
				if (sensor.getStatus() == Sensor.Status.AGENT) {
					sd.setAgentStart(sensor.getAgent().getStartTime());
				}
				sd.setMonitorStatus(sensor.getMonitor().getStatus());
				sd.setMonitorTime(sensor.getMonitor().getMonitorTime());
				if (sensor.getMonitor().getStatus() == Monitor.Status.MONITOR) {
					sd.setMonitorStart(sensor.getMonitor().getStartTime());
				}
				sd.setMonitorRate(sensor.getMonitor().getRate());
				sensorDataList.add(sd);
			}
		}
		Collections.sort(sensorDataList);
		return sensorDataList;
	}

}
