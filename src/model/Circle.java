package model;

public class Circle {

	private final Point o;

	private final double r;

	public Circle(Point o, double r) {
		this.o = o;
		this.r = r;
	}

	public Point getO() {
		return o;
	}

	public double getR() {
		return r;
	}

	public boolean isInCircle(Point point) {
		boolean ret = false;
		if (point != null) {
			ret = r * r >= (point.getX() - o.getX())
					* (point.getX() - o.getX()) + (point.getY() - o.getY())
					* (point.getY() - o.getY());
		}
		return ret;
	}

	public String toString() {
		return "[o=" + o + ", r=" + r + "]";
	}
	
}
