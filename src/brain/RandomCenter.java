package brain;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import model.Point;
import model.Rect;
import util.ListFactory;
import util.MapFactory;
import util.SetFactory;
import base.Sensor;

public class RandomCenter {

	private static final double X_MAX = 100.0;

	private static final double Y_MAX = 100.0;

	private static final Random random = new Random();

	private static final Set<Point> pointSet = SetFactory.newHashSet();

	private static final double minDist = 3;

	public static Map<String, Set<Sensor>> randomSensors(Integer... parameters) {
		List<Integer> plist = ListFactory.newArrayList();
		for (int param : parameters) {
			plist.add(param);
		}
		return randomSensors(plist);
	}

	public static Map<String, Set<Sensor>> randomSensors(
			List<Integer> parameters) {
		Map<String, Set<Sensor>> sensorMap = MapFactory.newHashMap();
		for (int i = 0, size = parameters.size(); i < size; i++) {
			String gate = "192.168." + (i + 1) + ".0";
			Set<Sensor> sensorSet = SetFactory.newHashSet();
			for (int j = 1; j <= parameters.get(i); j++) {
				Point point = new Point(X_MAX * random.nextDouble(), Y_MAX
						* random.nextDouble());
				Sensor sensor = new Sensor("192.168." + (i + 1) + "." + j,
						point);
				sensorSet.add(sensor);
			}
			sensorMap.put(gate, sensorSet);
		}
		return sensorMap;
	}

	public static Map<String, Set<Sensor>> randomSensors() {
		Map<String, Set<Sensor>> sensorMap = MapFactory.newHashMap();
		pointSet.clear();
		double split = 20;
		int count = 3;
		sensorMap.put("192.168.1.0",
				randomSensorSet("192.168.1.", split, count, 60, 0, 0, 60));
		sensorMap.put("192.168.2.0",
				randomSensorSet("192.168.2.", split, count, 60, 0, 60, 100));
		sensorMap.put("192.168.3.0",
				randomSensorSet("192.168.3.", split, count, 100, 60, 40, 100));
		sensorMap.put("192.168.4.0",
				randomSensorSet("192.168.4.", split, count, 100, 60, 0, 40));
		return sensorMap;
	}

	private static Set<Sensor> randomSensorSet(String gate, double split,
			int count, double up, double down, double left, double right) {
		Set<Sensor> sensorSet = SetFactory.newHashSet();
		int index = 1;
		for (double x = left; x < right; x += split) {
			for (double y = down; y < up; y += split) {
				double rectUp = y + split;
				rectUp = rectUp <= up ? rectUp : up;
				double rectRight = x + split;
				rectRight = rectRight <= right ? rectRight : right;
				for (int i = 0; i < count; i++) {
					Point point = randomPoint(new Rect(rectUp, y, x, rectRight));
					while (isTooClose(point)) {
						point = randomPoint(new Rect(rectUp, y, x, rectRight));
					}
					pointSet.add(point);
					sensorSet.add(new Sensor(gate + index++, point));
				}
			}
		}
		return sensorSet;
	}

	private static Point randomPoint(Rect rect) {
		Point point = null;
		if (rect != null) {
			point = new Point(rect.getLeft()
					+ (rect.getRight() - rect.getLeft()) * random.nextDouble(),
					rect.getDown() + (rect.getUp() - rect.getDown())
							* random.nextDouble());
		}
		return point;
	}

	private static boolean isTooClose(Point point) {
		boolean ret = false;
		if (point != null) {
			for (Point p : pointSet) {
				if (point.dist(p) < minDist) {
					ret = true;
					break;
				}
			}
		}
		return ret;
	}

}
