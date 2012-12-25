package base;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import model.Pair;

import org.joda.time.DateTime;

import road.Bus;
import road.model.Message;
import util.ListFactory;
import util.MapFactory;
import util.Tool;
import base.model.SensorInfo;

public class Agent {

	private final String sid;

	private final List<Pair<Long, Long>> timeList = ListFactory.newArrayList();

	private final Map<SensorInfo, Long> inSensors = MapFactory
			.newConcurrentHashMap();

	private Map<SensorInfo, Double> monitorRateMap = MapFactory
			.newConcurrentHashMap();

	private Map<String, Double> atrMap = MapFactory.newConcurrentHashMap();

	private AgentHandler agentHandler = null;

	public Agent(String sid) {
		this.sid = sid == null ? "" : sid;
	}

	public long getAgentTime() {
		long agentTime = 0;
		for (Pair<Long, Long> time : timeList) {
			agentTime += time.getB() - time.getA();
		}
		if (agentHandler != null) {
			agentTime += DateTime.now().getMillis()
					- agentHandler.getStartTime();
		}
		return agentTime;
	}

	public double getTotalRate() {
		double totalRate = 0;
		for (double rate : monitorRateMap.values()) {
			totalRate += rate;
		}
		return totalRate;
	}

	public void updateSensors(SensorInfo sensorInfo) {
		if (Tool.hasSameGate(sid, sensorInfo.getSid())) {
			inSensors.put(sensorInfo, DateTime.now().getMillis());
		}
	}

	public void updateMonitorRate(SensorInfo sensorInfo, double monitorRate) {
		monitorRateMap.put(sensorInfo, monitorRate);
	}

	public void updateAtrMap(String sid, double atr) {
		atrMap.put(sid, atr);
	}

	public long getStartTime() {
		long ret = -1;
		if (agentHandler != null) {
			return agentHandler.getStartTime();
		}
		return ret;
	}

	public void startWork() {
		if (agentHandler == null) {
			inSensors.clear();
			atrMap.clear();
			agentHandler = new AgentHandler();
			agentHandler.startWork();
			Bus.agentGetOn(sid);
		}
	}

	public void stopWork() {
		if (agentHandler != null) {
			agentHandler.stopWork();
			timeList.add(new Pair<Long, Long>(agentHandler.getStartTime(),
					DateTime.now().getMillis()));
			agentHandler = null;
			Bus.agentGetOff(sid);
		}
	}

	private class AgentHandler extends Thread {

		private long lastAliveCheckTime = DateTime.now().getMillis();

		private int neCount = 0;
		private boolean aaSending = false;

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
			while (working) {
				Bus.broadcast(new Message(sid, Message.Type.ALIVE));
				long now = DateTime.now().getMillis();
				if (now - lastAliveCheckTime > Setting.ALIVE_CHECK_INTERVAL) {
					aliveCheck();
					lastAliveCheckTime = now;
				}
				retireCheck();
				Tool.sleep(Setting.THREAD_SLEEP_INTERVAL);
			}
		}

		private void aliveCheck() {
			Iterator<SensorInfo> it = inSensors.keySet().iterator();
			while (it.hasNext()) {
				SensorInfo sensorInfo = it.next();
				long ackTime = inSensors.get(sensorInfo);
				if (DateTime.now().getMillis() - ackTime > Setting.ALIVE_CHECK_LOST) {
					it.remove();
				}
			}
		}

		private void retireCheck() {
			if (DateTime.now().getMillis() - startTime > Setting.AGENT_TENURE) {
				if (atrMap.size() < inSensors.size()) {
					if (neCount < 3) {
						Bus.broadcast(new Message(sid, Message.Type.NE));
						neCount++;
					} else {
						sendAA();
					}
				} else {
					sendAA();
				}
			}
		}

		private void sendAA() {
			if (!aaSending) {
				String minSid = sid;
				double minAtr = 1;
				for (String key : atrMap.keySet()) {
					if ((atrMap.get(key) < minAtr)
							|| (atrMap.get(key) == minAtr && Tool.ipCompare(
									key, minSid) < 0)) {
						minSid = key;
						minAtr = atrMap.get(key);
					}
				}
				Bus.send(new Message(sid, minSid, Message.Type.AA));
				aaSending = true;
			}
		}

	}

}
