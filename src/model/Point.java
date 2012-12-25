package model;

import java.text.DecimalFormat;

public class Point {

	private static final DecimalFormat decimalFormat = new DecimalFormat("0.00");

	private final double x;

	private final double y;

	public Point(double x, double y) {
		this.x = x;
		this.y = y;
	}

	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}

	public double dist(Point point) {
		double ret = -1.0;
		if (point != null) {
			ret = Math.sqrt((point.getX() - x) * (point.getX() - x)
					+ (point.getY() - y) * (point.getY() - y));
		}
		return ret;
	}

	public int hashCode() {
		return (int) (x + y);
	}

	public boolean equals(Object obj) {
		boolean ret = false;
		if (obj != null && obj instanceof Point) {
			Point point = (Point) obj;
			if (x == point.getX() && y == point.getY()) {
				ret = true;
			}
		}
		return ret;
	}

	public String toString() {
		return "(" + decimalFormat.format(x) + ", " + decimalFormat.format(y)
				+ ")";
	}

}
