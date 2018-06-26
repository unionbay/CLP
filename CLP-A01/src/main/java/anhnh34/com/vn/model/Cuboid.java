package anhnh34.com.vn.model;

public class Cuboid {
	private Dimension minimumPoint;
	private Dimension maximumPoint;

	private double length;
	private double width;
	private double height;

	public double getLength() {
		return length;
	}

	public void setLength(double length) {
		this.length = length;
	}

	public double getWidth() {
		return width;
	}

	public void setWidth(double width) {
		this.width = width;
	}

	public double getHeigth() {
		return height;
	}

	public void setHeight(double height) {
		this.height = height;
	}

	public Cuboid() {
		// TODO Auto-generated constructor stub
	}

	public Dimension getMaximumPoint() {
		return maximumPoint;
	}

	public Dimension getMinimumPoint() {
		return minimumPoint;		
	}

	public void setMaximumPoint(Dimension maximumPoint) {
		this.maximumPoint = maximumPoint;
	}

	public void setMinimumPoint(Dimension minimumPoint) {
		this.minimumPoint = minimumPoint;
	}

	public Cuboid(Dimension min, Dimension max) {
		this.minimumPoint = min;
		this.maximumPoint = max;
	}
}
