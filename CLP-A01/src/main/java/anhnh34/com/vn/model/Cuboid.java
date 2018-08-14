package anhnh34.com.vn.model;

public class Cuboid {

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
	}

	public Dimension getMaximumPoint() {
		return maximumPoint;
	}

	public Dimension getMinimumPoint() {
		return minimumPoint;		
	}

	public void setMaximumPoint(Dimension maximum) {
		Dimension max = new Dimension(maximum.getX(), maximum.getY(), maximum.getZ());
		this.maximumPoint = max;
	}

	public void setMinimumPoint(Dimension minimumPoint) {
		Dimension minimum = new Dimension(minimumPoint.getX(), minimumPoint.getY(), minimumPoint.getZ());
		this.minimumPoint = minimum;
	}

	public Cuboid(Dimension min, Dimension max) {
		Dimension cMin = new Dimension(min.getX(), min.getY(), min.getZ());
		Dimension cMax = new Dimension(max.getX(), max.getY(), max.getZ());
		this.minimumPoint = cMin;
		this.maximumPoint = cMax;
	}
	
	public double getVolume() {
		return volume;
	}
	
	private void setVolume(double volume) {
		this.volume = volume;
	}
	
	public void init() {
		double volume = this.getLength() * this.getWidth() * this.getHeigth();
		this.setVolume(volume);
	}
	
	private double width;
	private double length;
	private double height;
	private double volume;
	private Dimension minimumPoint;
	private Dimension maximumPoint;
}
