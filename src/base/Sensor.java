package base;

import java.util.List;

import model.Point;

import org.joda.time.DateTime;

import road.Bus;
import road.model.Message;
import util.ListFactory;
import util.Log;
import util.Tool;
import base.model.SensorInfo;

public class Sensor extends Thread {

	private final SensorInfo sensorInfo;

	private List<Message> messageList = ListFactory.newCopyOnWriteArrayList();

	private String agentSid = "";
	private Status status = Status.INIT;

	private long lastAliveCheck = -1;
	private int wiaCount = 0;
	private int clCount = 0;

	private final Agent agent;
	private final Monitor monitor;

	private long startTime = -1;
	private boolean life = true;
	private boolean working = false;

	public Sensor(String sid, Point location) {
		this.sensorInfo = new SensorInfo(sid, location);
		this.agent = new Agent(sensorInfo.getSid());
		this.monitor = new Monitor(sensorInfo);
		this.setName(sensorInfo.getSid());
	}

	public SensorInfo getSensorInfo() {
		return sensorInfo;
	}

	public void addMessage(Message message) {
		if (message != null) {
			messageList.add(message);
		}
	}

	public String getAgentSid() {
		return agentSid;
	}

	public Status getStatus() {
		return status;
	}

	public Agent getAgent() {
		return agent;
	}

	public Monitor getMonitor() {
		return monitor;
	}

	public long getStartTime() {
		return startTime;
	}

	public void startWork() {
		if (sensorInfo.isCorrect()) {
			if (life && !working) {
				startTime = DateTime.now().getMillis() - 1;
				working = true;
				start();
			}
		} else {
			Log.error("can not start " + sensorInfo);
		}
	}

	public void stopWork() {
		life = false;
		working = false;
		agent.stopWork();
		monitor.stopWork();
		messageList.clear();
	}

	public void run() {
		while (working) {
			if (messageList.size() > 0) {
				processMessage();
			}
			if (status == Status.INIT) {
				register();
			} else {
				wiaCount = 0;
			}
			if (status == Status.NODE
					&& DateTime.now().getMillis() - lastAliveCheck > Setting.ALIVE_CHECK_LOST) {
				selfRecomend();
			} else {
				clCount = 0;
			}
			Tool.sleep(Setting.THREAD_SLEEP_INTERVAL);
		}
	}

	public int hashCode() {
		return sensorInfo.hashCode();
	}

	public boolean equals(Object obj) {
		boolean ret = false;
		if (obj != null && obj instanceof Sensor) {
			Sensor sensor = (Sensor) obj;
			if (sensor.getSensorInfo().equals(getSensorInfo())) {
				ret = true;
			}
		}
		return ret;
	}

	private void processMessage() {
		while (messageList.size() > 0) {
			Message message = messageList.remove(0);
			switch (message.getType()) {
			case WIA:
				processWia(message);
				break;
			case WIA_ACK:
				processWiaAck(message);
				break;
			case CL:
				processCl(message);
				break;
			case CLD:
				processCld(message);
				break;
			case NE:
				processNe(message);
				break;
			case NE_ACK:
				processNeAck(message);
				break;
			case AA:
				processAa(message);
				break;
			case AA_ACK:
				processAaAck(message);
				break;
			case NA:
				processNa(message);
				break;
			case NA_ACK:
				processNaAck(message);
				break;
			case ALIVE:
				processAlive(message);
				break;
			case ALIVE_ACK:
				processAliveAck(message);
				break;
			case AGENT_CONFLICT:
				processAgentConflict(message);
				break;
			default:
				break;
			}
		}
	}

	private void processWia(Message message) {
		if (!sensorInfo.getSid().equals(message.getSource())) {
			if (status == Status.AGENT) {
				Bus.send(new Message(sensorInfo.getSid(), message.getSource(),
						Message.Type.WIA_ACK));
				agent.updateSensors((SensorInfo) message.getContent());
			}
		}
	}

	private void processWiaAck(Message message) {
		agentSid = message.getSource();
		status = Status.NODE;
		lastAliveCheck = DateTime.now().getMillis();
	}

	private void processCl(Message message) {
		if (!sensorInfo.getSid().equals(message.getSource())) {
			double selfAtr = getAgentTimeRate();
			double msgAtr = (double) message.getContent();
			if (status == Status.NODE) {
				if ((selfAtr < msgAtr)
						|| (selfAtr == msgAtr && Tool.ipCompare(
								sensorInfo.getSid(), message.getSource()) < 0)) {
					Bus.send(new Message(sensorInfo.getSid(), message
							.getSource(), Message.Type.CLD));
					lastAliveCheck = -1;
				} else {
					lastAliveCheck = DateTime.now().getMillis();
				}
			} else if (status == Status.AGENT) {
				Bus.send(new Message(sensorInfo.getSid(), message.getSource(),
						Message.Type.CLD));
			}
		}
	}

	private void processCld(Message message) {
		lastAliveCheck = DateTime.now().getMillis();
		if (status == Status.AGENT) {
			agent.stopWork();
			status = Status.NODE;
		}
	}

	private void processNe(Message message) {
		Bus.send(new Message(sensorInfo.getSid(), message.getSource(),
				Message.Type.NE_ACK).setContent(getAgentTimeRate()));
	}

	private void processNeAck(Message message) {
		agent.updateAtrMap(message.getSource(), (double) message.getContent());
	}

	private void processAa(Message message) {
		if (sensorInfo.getSid().equals(message.getSource())) {
			agent.stopWork();
		} else {
			Bus.send(new Message(sensorInfo.getSid(), message.getSource(),
					Message.Type.AA_ACK));
		}
		Bus.broadcast(new Message(sensorInfo.getSid(), Message.Type.NA));
		agent.startWork();
		status = Status.AGENT;
	}

	private void processAaAck(Message message) {
		agent.stopWork();
		status = Status.NODE;
	}

	private void processNa(Message message) {
		Bus.send(new Message(sensorInfo.getSid(), message.getSource(),
				Message.Type.NA_ACK).setContent(sensorInfo));
		agentSid = message.getSource();
	}

	private void processNaAck(Message message) {
		agent.updateSensors((SensorInfo) message.getContent());
	}

	private void processAlive(Message message) {
		if (status == Status.AGENT
				&& !sensorInfo.getSid().equals(message.getSource())) {
			Bus.send(new Message(sensorInfo.getSid(), message.getSource(),
					Message.Type.AGENT_CONFLICT).setContent(agent
					.getStartTime()));
		}
		Bus.send(new Message(sensorInfo.getSid(), message.getSource(),
				Message.Type.ALIVE_ACK).setContent(new AliveAckData(sensorInfo,
				monitor.getRate())));
		lastAliveCheck = DateTime.now().getMillis();
	}

	private void processAliveAck(Message message) {
		AliveAckData aad = (AliveAckData) message.getContent();
		agent.updateSensors(aad.getSensorInfo());
		agent.updateMonitorRate(aad.getSensorInfo(), aad.getRate());
	}

	private void processAgentConflict(Message message) {
		long agentStartTime = (long) message.getContent();
		if ((agent.getStartTime() > agentStartTime)
				|| (agent.getStartTime() == agentStartTime && Tool.ipCompare(
						sensorInfo.getSid(), message.getSource()) > 0)) {
			agent.stopWork();
			status = Status.NODE;
		}
	}

	private void register() {
		if (wiaCount < Setting.MESSAGE_RETRY_COUNT) {
			Bus.broadcast(new Message(sensorInfo.getSid(), Message.Type.WIA)
					.setContent(sensorInfo));
			wiaCount++;
			Tool.sleep(Setting.WAITING_ACK_INTERVAL);
		} else {
			status = Status.NODE;
		}
	}

	private void selfRecomend() {
		if (clCount < Setting.MESSAGE_RETRY_COUNT) {
			Bus.broadcast(new Message(sensorInfo.getSid(), Message.Type.CL)
					.setContent(getAgentTimeRate()));
			clCount++;
			Tool.sleep(Setting.WAITING_ACK_INTERVAL);
		} else {
			Bus.broadcast(new Message(sensorInfo.getSid(), Message.Type.NA));
			agent.startWork();
			status = Status.AGENT;
		}
	}

	private double getAgentTimeRate() {
		return (double) agent.getAgentTime()
				/ (DateTime.now().getMillis() - startTime);
	}
	
	public enum Status {
		INIT, NODE, AGENT;
	}

}
