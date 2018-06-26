package anhnh34.com.vn.model;

public class Dimension {
	private double x;
	private double y;
	private double z;

	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}

	public double getZ() {
		return z;
	}

	public void setX(double x) {
		this.x = x;
	}

	public void setY(double y) {
		this.y = y;
	}

	public void setZ(double z) {
		this.z = z;
	}

	public Dimension(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public Dimension() {

	}

	public boolean isEqual(Dimension d) {
		return false;
	}

	public Dimension addDimension(Dimension d) {
		return new Dimension(this.getX() + d.getX(), this.getY() + d.getY(), this.getZ() + d.getZ());
	}

	public Dimension subtractDimension(Dimension d) {
		return null;
	}

	public int compare(Dimension d) {		
		if(this.x >= d.x && this.y >= d.y && this.z >= d.z) {
			return 1;
		}					
		return -1;	
	}

}
