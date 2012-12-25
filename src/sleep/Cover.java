package sleep;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import model.Point;
import model.Rect;
import util.MapFactory;
import base.Sensor;

public class Cover {

	public static final double R = 20.0;

	public static final double RF = 1.0;

	public static Set<Sensor> findCoverSet(Rect rect, Set<Sensor> sensors) {
		Set<Sensor> coverSet = new HashSet<Sensor>();
		Map<Integer, List<Sensor>> map = MapFactory.newHashMap();
		int n = (int) Math.ceil((rect.getUp() - rect.getDown()) / (RF * R));
		for (int i = 0; i < n; i++) {
			map.put(i, new LinkedList<Sensor>());
		}
		for (Sensor sensor : sensors) {
			Point p = sensor.getSensorInfo().getLocation();
			double dis = p.getY() - rect.getDown();
			if (dis == 0) {
				map.get(0).add(sensor);
			} else {
				map.get((int) Math.ceil(dis / (RF * R)) - 1).add(sensor);
			}
		}
		double right = rect.getRight();
		for (int i = 0; i < n; i++) {
			List<Sensor> sensorSet = map.get(i);
			double left = rect.getLeft();
			double up = rect.getDown() + (i + 1) * RF * R;
			double down = rect.getDown() + i * RF * R;
			up = up <= rect.getUp() ? up : rect.getUp();
			while (left <= right) {
				Sensor maxPointSensor = null;
				double max = left;
				Point leftUp = new Point(left, up);
				Point leftDown = new Point(left, down);
				for (Sensor sensor : sensorSet) {
					Point p = sensor.getSensorInfo().getLocation();
					if (isCover(p, leftUp) && isCover(p, leftDown)) {
						double xright1 = p.getX()
								+ Math.pow(
										(R * R - (up - p.getY())
												* (up - p.getY())), 0.5);
						double xright2 = p.getX()
								+ Math.pow((R * R - (down - p.getY())
										* (down - p.getY())), 0.5);
						double xright = xright1 > xright2 ? xright1 : xright2;
						if (xright > max) {
							max = xright;
							maxPointSensor = sensor;
						}
					}
				}
				if (maxPointSensor != null) {
					coverSet.add(maxPointSensor);
					left = max;
				} else {
					left++;
				}
			}
		}
		return coverSet;
	}

	private static boolean isCover(Point a, Point b) {
		return (a.getX() - b.getX()) * (a.getX() - b.getX())
				+ (a.getY() - b.getY()) * (a.getY() - b.getY()) < R * R;
	}

}
