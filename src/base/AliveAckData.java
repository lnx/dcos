package base;

import base.model.SensorInfo;

public class AliveAckData {

	private final SensorInfo sensorInfo;

	private final double rate;

	public AliveAckData(SensorInfo sensorInfo, double rate) {
		this.sensorInfo = sensorInfo;
		this.rate = rate;
	}

	public SensorInfo getSensorInfo() {
		return sensorInfo;
	}

	public double getRate() {
		return rate;
	}

}
