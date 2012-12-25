package road;

import java.util.Map;
import java.util.Set;

import road.model.Message;
import util.Log;
import util.MapFactory;
import util.SetFactory;
import util.Tool;
import base.Sensor;

public class Bus {

	private static final Map<String, Sensor> smap = MapFactory.newHashMap();
	private static final Map<String, Set<Sensor>> bmap = MapFactory
			.newHashMap();

	private static final Set<String> aset = SetFactory.newCopyOnWriteArraySet();

	public static void clear() {
		smap.clear();
		bmap.clear();
		aset.clear();
	}

	public static void register(Sensor sensor) {
		if (sensor != null) {
			smap.put(sensor.getSensorInfo().getSid(), sensor);
			String gate = Tool.getGate(sensor.getSensorInfo().getSid());
			if (!bmap.containsKey(gate)) {
				Set<Sensor> sensorSet = SetFactory.newHashSet();
				bmap.put(gate, sensorSet);
			}
			bmap.get(gate).add(sensor);
			Log.info("register " + sensor.getSensorInfo());
		}
	}

	public static void leave(Sensor sensor) {
		if (sensor != null) {
			smap.remove(sensor.getSensorInfo().getSid());
			Set<Sensor> sensorSet = bmap.get(Tool.getGate(sensor
					.getSensorInfo().getSid()));
			sensorSet.remove(sensor);
		}
	}

	public static void send(Message message) {
		if (message != null) {
			Sensor sensor = smap.get(message.getDest());
			if (sensor != null) {
				sensor.addMessage(message);
			}
		}
	}

	public static void broadcast(Message message) {
		if (message != null) {
			Set<Sensor> sensorSet = bmap.get(message.getDest());
			if (sensorSet != null) {
				for (Sensor sensor : sensorSet) {
					if (sensor != null) {
						sensor.addMessage(message);
					}
				}
			}
		}
	}

	public static void agentGetOn(String sid) {
		if (sid != null) {
			aset.add(sid);
		}
	}

	public static void agentGetOff(String sid) {
		if (sid != null) {
			aset.remove(sid);
		}
	}

}
