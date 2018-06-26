package anhnh34.com.vn.model;

public class Space extends Cuboid {
	private double[] sizeList; 

	public double[] getSizeList() {
		return sizeList;
	}
	
	public double getWidth() {
		return super.getWidth();
	}

	public double getLength() {
		return super.getLength();
	}

	public double getHeight() {
		return super.getHeigth();
	}

	public void setWidth(double width) {
		super.setWidth(width);
	}

	public void setLenght(double lenght) {
		super.setLength(lenght);
	}

	public void setHeight(double height) {
		super.setHeight(height);
	}

	public void setMinimum(Dimension minimum) {
		super.setMinimumPoint(minimum);
	}

	public void setMaximum(Dimension maximum) {
		super.setMaximumPoint(maximum);
	}

	public Dimension getMinimum() {
		return super.getMinimumPoint();
	}

	public Dimension getMaximum() {
		return super.getMaximumPoint();
	}

	/**
	 * @param minimum
	 * @param maximum
	 */
	public Space(Dimension minimum, Dimension maximum) {
		super();
		this.setMaximum(maximum);
		this.setMinimum(minimum);
		this.initialize();
	}
	
	/**
	 * @param ratioSupport
	 * @param maximumSupportX
	 * @param maximumSupportY
	 */
	public Space(Dimension min, Dimension max, double ratioSupport, double maximumSupportX, double maximumSupportY) {
		super();
		this.ratioSupport = ratioSupport;
		this.setMinimum(min);
		this.setMaximum(max);
		this.maximumSupportX = maximumSupportX;
		this.maximumSupportY = maximumSupportY;
		this.initialize();
	}

	public void initialize() {	
		// calculate length width heigh.	
		super.setLength(super.getMaximumPoint().getX() - super.getMinimumPoint().getX());
		super.setWidth(super.getMaximumPoint().getY() - super.getMinimumPoint().getY());
		super.setHeight(super.getMaximumPoint().getZ() - super.getMinimumPoint().getZ());				
		super.init();
	}

	/**
	 * 
	 */
	public Space() {
		super();
		this.initialize();
	}

	public int compare(Space s) {
//		if (this.getMinimum().getZ() < s.getMinimum().getZ()) {
//			return -1;
//		}
//
//		if (this.getMinimum().getZ() > s.getMinimum().getZ()) {
//			return 1;
//		}

		// if two spaces have same z, going to check distance from root to
		// minimum point
		 double distance = Math.sqrt(Math.pow(this.getMinimum().getX(), 2) +
		 Math.pow(this.getMinimum().getY(), 2));
		 double sDistance = Math.sqrt(Math.pow(s.getMinimum().getX(), 2) +
		 Math.pow(s.getMinimum().getY(), 2));
		 if(distance > sDistance) {
			 return 1;
		 }
		//
		 if(distance < sDistance) {
			 return -1;
		 }

		// if two spaces have same x,y,z. we going to check their volume
		double volume = this.getLength() * this.getWidth() * this.getHeight();
		double sVolume = s.getLength() * s.getWidth() * s.getHeight();

		if (volume < sVolume) {
			return -1;
		}

		if (volume > sVolume) {
			return 1;
		}

		return 0;
	}

	public boolean checkFeasibleBox(Box box) {
		return false;
	}

	public boolean isValid() {
		if (this.getMinimum().getX() < 0 || this.getMinimum().getY() < 0 || this.getMinimum().getZ() < 0) {
			return false;
		}

		if (this.getMaximum().getX() <= this.getMinimum().getX() || this.getMaximum().getY() <= this.getMinimum().getY()
				|| this.getMaximum().getZ() <= this.getMinimum().getZ()) {
			return false;
		}
		
		return true;
	}

	public double getMaximumSupportX() {
		return maximumSupportX;
	}

	public void setMaximumSupportX(double maximumSupportX) {
		this.maximumSupportX = maximumSupportX;
	}

	public double getMaximumSupportY() {
		return maximumSupportY;
	}

	public void setMaximumSupportY(double maximumSupportY) {
		this.maximumSupportY = maximumSupportY;
	}
	
	public double getRatioSupport() {
		return ratioSupport;
	}
	
	public void setRatioSupport(double ratioSupport) {
		this.ratioSupport = ratioSupport;
	}

	private double ratioSupport;
	private double maximumSupportX;
	private double maximumSupportY;	
}
