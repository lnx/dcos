package brain;

import java.util.List;
import java.util.Map;
import java.util.Set;

import model.Point;
import model.Rect;

import org.apache.commons.lang3.StringUtils;

import road.Bus;
import sleep.Cover;
import util.ListFactory;
import util.SetFactory;
import util.Tool;
import base.Monitor;
import base.Sensor;
import base.Setting;
import base.model.MonitorData;
import brain.model.Event;
import brain.model.Execution;

public class CommandCenter {

	public static final Rect monitorRect = new Rect(100, 0, 0, 100);

	public static final List<Set<Sensor>> sensors = ListFactory
			.newCopyOnWriteArrayList();

	public static Event event = null;

	public static void addMonitorData(MonitorData monitorData) {
		if (monitorData != null && event != null) {
			Point point = event.getLocation();
			if (point.dist(monitorData.getLocation()) < Cover.R) {
				event.addData(monitorData);
			}
		}
	}

	public static Execution execute(String rawCommand) {
		Execution execution = new Execution(true, rawCommand);
		if (!StringUtils.isBlank(rawCommand)) {
			CommandType commandType = CommandType.NONE;
			String[] parts = rawCommand.toUpperCase().split("[ \t\n]+");
			for (CommandType ct : CommandType.values()) {
				if (ct.toString().equals(parts[0])) {
					commandType = ct;
					break;
				}
			}
			if (commandType != CommandType.NONE) {
				List<String> parameters = ListFactory.newArrayList();
				for (int i = 1; i < parts.length; i++) {
					parameters.add(parts[i]);
				}
				switch (commandType) {
				case CLEAR:
					processClear(parameters, execution);
					break;
				case INIT:
					processInit(parameters, execution);
					break;
				case AGENT:
					processAgent(parameters, execution);
					break;
				case TENURE:
					processTenure(parameters, execution);
					break;
				case START:
					processStart(parameters, execution);
					break;
				case STOP:
					processStop(parameters, execution);
					break;
				case SLEEP:
					processSleep(parameters, execution);
					break;
				case EVENT:
					processEvent(parameters, execution);
					break;
				case KILL:
					processKill(parameters, execution);
					break;
				default:
					break;
				}
			} else {
				execution.update(false, "no such command: " + rawCommand);
			}
		} else {
			execution.update(false, "command cannot be blank");
		}
		return execution;
	}

	private static void processClear(List<String> parameters,
			Execution execution) {
		for (Set<Sensor> sensorSet : sensors) {
			for (Sensor sensor : sensorSet) {
				sensor.stopWork();
			}
		}
		sensors.clear();
		Setting.resetTenure();
		event = null;
		Bus.clear();
	}

	private static void processInit(List<String> parameters, Execution execution) {
		List<Integer> pList = ListFactory.newArrayList();
		boolean paramCheck = true;
		for (String p : parameters) {
			if (Tool.isInteger(p)) {
				pList.add(Integer.parseInt(p));
			} else {
				paramCheck = false;
				break;
			}
		}
		if (paramCheck) {
			if (pList.size() == 1 && pList.get(0) == -1) {
				Map<String, Set<Sensor>> sensorMap = RandomCenter
						.randomSensors();
				for (Set<Sensor> sensorSet : sensorMap.values()) {
					sensors.add(sensorSet);
				}
			} else {
				Map<String, Set<Sensor>> sensorMap = RandomCenter
						.randomSensors(pList);
				for (Set<Sensor> sensorSet : sensorMap.values()) {
					sensors.add(sensorSet);
				}
			}
			for (Set<Sensor> sensorSet : sensors) {
				for (Sensor sensor : sensorSet) {
					Bus.register(sensor);
				}
			}
		} else {
			execution.update(false, "init parameter must be numbers");
		}
	}

	private static void processAgent(List<String> parameters,
			Execution execution) {
		for (Set<Sensor> sensorSet : sensors) {
			for (Sensor sensor : sensorSet) {
				sensor.startWork();
			}
		}
	}

	private static void processTenure(List<String> parameters,
			Execution execution) {
		if (parameters.size() >= 1 && Tool.isLong(parameters.get(0))) {
			long tenure = Long.parseLong(parameters.get(0));
			if (tenure >= 1000) {
				Setting.updateTenure(tenure);
			} else {
				execution.update(false,
						"tenure parameter must bigger than 1000");
			}
		} else {
			execution.update(false, "tenure parameter must be numbers");
		}

	}

	private static void processStart(List<String> parameters,
			Execution execution) {
		for (Set<Sensor> sensorSet : sensors) {
			for (Sensor sensor : sensorSet) {
				sensor.getMonitor().startWork();
			}
		}
	}

	private static void processStop(List<String> parameters, Execution execution) {
		for (Set<Sensor> sensorSet : sensors) {
			for (Sensor sensor : sensorSet) {
				sensor.getMonitor().stopWork();
			}
		}
	}

	private static void processSleep(List<String> parameters,
			Execution execution) {
		Set<Sensor> sensorSet = SetFactory.newHashSet();
		for (Set<Sensor> set : sensors) {
			sensorSet.addAll(set);
		}
		Set<Sensor> coverSet = Cover.findCoverSet(monitorRect, sensorSet);
		sensorSet.removeAll(coverSet);
		for (Sensor s : coverSet) {
			s.getMonitor().startWork();
		}
		for (Sensor s : sensorSet) {
			s.getMonitor().stopWork();
		}
	}

	private static void processEvent(List<String> parameters,
			Execution execution) {
		if (parameters.size() == 1
				&& parameters.get(0).toUpperCase().equals("CLEAR")) {
			event = null;
			processSleep(null, null);
		} else if (parameters.size() >= 2) {
			if (Tool.isDouble(parameters.get(0))
					&& Tool.isDouble(parameters.get(1))) {
				Point location = new Point(
						Double.parseDouble(parameters.get(0)),
						Double.parseDouble(parameters.get(1)));
				event = new Event(location);
				boolean eventFind = false;
				for (Set<Sensor> sensorSet : sensors) {
					for (Sensor sensor : sensorSet) {
						if (sensor.getMonitor().getStatus() == Monitor.Status.MONITOR
								&& location.dist(sensor.getSensorInfo()
										.getLocation()) <= Cover.R) {
							eventFind = true;
							break;
						}
					}
					if (eventFind) {
						break;
					}
				}
				if (eventFind) {
					for (Set<Sensor> sensorSet : sensors) {
						for (Sensor sensor : sensorSet) {
							if (location.dist(sensor.getSensorInfo()
									.getLocation()) <= Cover.R) {
								sensor.getMonitor().startWork();
							}
						}
					}
				}
			} else {
				execution.update(false,
						"event position must be double format number");
			}
		} else {
			execution.update(false, "event parameter format error");
		}
	}

	private static void processKill(List<String> parameters, Execution execution) {
		if (parameters.size() >= 1) {
			for (String sid : parameters) {
				if (Tool.checkIp(sid)) {
					for (Set<Sensor> sensorSet : sensors) {
						Sensor find = null;
						for (Sensor sensor : sensorSet) {
							if (sensor.getSensorInfo().getSid().equals(sid)) {
								find = sensor;
								break;
							}
						}
						if (find != null) {
							find.stopWork();
							sensorSet.remove(find);
							Bus.leave(find);
							break;
						}
					}
					if (event != null) {
						event.removeSid(sid);
					}
				}
			}
		}
	}

	private enum CommandType {

		NONE,

		CLEAR,

		INIT,

		AGENT, TENURE,

		START, STOP,

		SLEEP,

		EVENT,

		KILL;

	}

}
