package base;

import java.util.List;
import java.util.Random;

import model.Pair;

import org.joda.time.DateTime;

import util.ListFactory;
import util.Tool;
import base.model.MonitorData;
import base.model.SensorInfo;
import brain.CommandCenter;

public class Monitor {

	private static final Random random = new Random();

	private final List<Pair<Long, Long>> timeList = ListFactory.newArrayList();

	private final SensorInfo sensorInfo;
	private MonitorHandler monitorHandler = null;

	private Status status = Status.SLEEP;
	private double rate = 1;

	public Monitor(SensorInfo sensorInfo) {
		this.sensorInfo = sensorInfo;
	}

	public long getMonitorTime() {
		long monitorTime = 0;
		for (Pair<Long, Long> time : timeList) {
			monitorTime += time.getB() - time.getA();
		}
		if (monitorHandler != null) {
			monitorTime += DateTime.now().getMillis()
					- monitorHandler.getStartTime();
		}
		return monitorTime;
	}

	public Status getStatus() {
		return status;
	}

	public double getRate() {
		double ret = 0;
		if (status == Status.MONITOR) {
			ret = rate;
		}
		return ret;
	}

	public void setRate(long rate) {
		if (rate > 0 && rate <= 1000) {
			this.rate = rate;
		}
	}

	public long getStartTime() {
		long ret = -1;
		if (monitorHandler != null) {
			return monitorHandler.getStartTime();
		}
		return ret;
	}

	public void startWork() {
		if (monitorHandler == null) {
			monitorHandler = new MonitorHandler();
			monitorHandler.startWork();
			status = Status.MONITOR;
		}
	}

	public void stopWork() {
		if (monitorHandler != null) {
			monitorHandler.stopWork();
			timeList.add(new Pair<Long, Long>(monitorHandler.getStartTime(),
					DateTime.now().getMillis()));
			monitorHandler = null;
			status = Status.SLEEP;
		}
	}

	private class MonitorHandler extends Thread {

		private long startTime = -1;
		private boolean life = true;
		private boolean working = false;

		public long getStartTime() {
			return startTime;
		}

		public void startWork() {
			if (life && !working) {
				startTime = DateTime.now().getMillis();
				working = true;
				start();
			}
		}

		public void stopWork() {
			life = false;
			working = false;
		}

		public void run() {
			int x = (int) (180 * random.nextDouble());
			while (working) {
				if (x >= 180) {
					x = 0;
				}
				x += 10;
				double from = 50;
				double to = 3;
				double data = from + to * random.nextDouble()
						+ Math.sin(x / 180.0 * Math.PI) * 35;
				CommandCenter.addMonitorData(new MonitorData(sensorInfo
						.getSid(), sensorInfo.getLocation(), data, DateTime
						.now().getMillis()));
				Tool.sleep((long) (1000.0 / rate));
			}
		}
	}

	public enum Status {
		MONITOR, SLEEP;
	}

}
