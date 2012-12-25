package model;

public class Rect {

	private final double up;

	private final double down;

	private final double left;

	private final double right;

	public Rect(double up, double down, double left, double right) {
		this.up = up;
		this.down = down;
		this.left = left;
		this.right = right;
	}

	public Rect(Point a, Point b) {
		if (a.getY() >= b.getY()) {
			up = a.getY();
			down = b.getY();
		} else {
			up = b.getY();
			down = a.getY();
		}
		if (a.getX() <= b.getX()) {
			left = a.getX();
			right = b.getX();
		} else {
			left = b.getX();
			right = a.getX();
		}
	}

	public double getUp() {
		return up;
	}

	public double getDown() {
		return down;
	}

	public double getLeft() {
		return left;
	}

	public double getRight() {
		return right;
	}

	public Point getLeftDown() {
		return new Point(left, down);
	}

	public Point getRightUp() {
		return new Point(right, up);
	}

	public String toString() {
		return "[up=" + up + ", down=" + down + ", left=" + left + ", right="
				+ right + "]";
	}
	
}
