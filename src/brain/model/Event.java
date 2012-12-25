package brain.model;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import model.Point;

import org.joda.time.DateTime;

import util.ListFactory;
import util.MapFactory;
import base.model.MonitorData;

public class Event {

	private static final DecimalFormat df = new DecimalFormat("0.00");

	private static final int maxNumber = 100;

	private final long startTime = DateTime.now().getMillis();

	private final Point location;

	private final Map<String, List<MonitorData>> monitorDataMap = MapFactory
			.newConcurrentHashMap();

	private int dataCount = 0;

	public Event(Point location) {
		this.location = location;
	}

	public String getStartTime() {
		return new DateTime(startTime).toString("yyyy-MM-dd HH:mm:ss");
	}

	public Point getLocation() {
		return location;
	}

	public String getDuration() {
		return df.format((DateTime.now().getMillis() - startTime) / 60000.0)
				+ " min";
	}

	public int getDataCount() {
		return dataCount;
	}

	public void addData(MonitorData monitorData) {
		if (monitorData != null) {
			if (!monitorDataMap.containsKey(monitorData.getSid())) {
				monitorDataMap.put(monitorData.getSid(),
						new CopyOnWriteArrayList<MonitorData>());
			}
			List<MonitorData> mdList = monitorDataMap.get(monitorData.getSid());
			mdList.add(monitorData);
			dataCount++;
			while (mdList.size() > maxNumber) {
				mdList.remove(0);
			}
		}
	}

	public void removeSid(String sid) {
		if (sid != null) {
			monitorDataMap.remove(sid);
		}
	}

	public List<List<MonitorData>> getMonitorDatas() {
		int pointNumber = 30;
		List<List<MonitorData>> monitorDatas = ListFactory.newArrayList();
		for (String key : monitorDataMap.keySet()) {
			List<MonitorData> mdList = monitorDataMap.get(key);
			List<MonitorData> retList = ListFactory.newArrayList();
			for (int i = mdList.size() - 1; i >= 0
					&& retList.size() < pointNumber; i--) {
				retList.add(mdList.get(i));
			}
			for (int i = 0; i < pointNumber - retList.size(); i++) {
				retList.add(new MonitorData(key, location, 0.0, DateTime.now()
						.getMillis()));
			}
			monitorDatas.add(retList);
		}
		return monitorDatas;
	}

}
