package anhnh34.com.vn.model;

import java.util.Arrays;

public class Box extends Cuboid {
	private int id;
	private double biggestDimension;
	private double middleDimension;
	private double smallestDimension;
	private float mass;
	private boolean fragile;
	private int priority;
	private int sequenceNumber;
	private String selectedRotation;
	private int[] fRotation;
	private double[] dimension;
	private String boxType;
	private double supportParamA;
	private double supportParamB;
	private double largestSurface;
	private double volume;
	
	public double getVolume() {
		return volume;
	}
	
	public void setVolume(double volume) {
		this.volume = volume;
	};

	public double getLargestSurface() {
		return largestSurface;
	}

	public void setLargestSurface(double largestSurface) {
		this.largestSurface = largestSurface;
	}

	public double getSupportParamA() {
		return supportParamA;
	}

	public void setSupportParamA(double supportParamA) {
		this.supportParamA = supportParamA;
	}

	public double getSupportParamB() {
		return supportParamB;
	}

	public void setSupportParamB(double supportParamB) {
		this.supportParamB = supportParamB;
	}

	public String getBoxType() {
		return boxType;
	}

	public void setBoxType(String boxType) {
		this.boxType = boxType;
	}

	public void setMinimum(Dimension minimum) {
		super.setMinimumPoint(minimum);
	}

	public Dimension getMinimum() {
		return super.getMinimumPoint();
	}

	public void setMaximum(Dimension maximum) {
		super.setMaximumPoint(maximum);
	}

	public Dimension getMaximum() {
		return super.getMaximumPoint();
	};

	@Override
	public void setLength(double length) {
		super.setLength(length);
	}

	public void setSize(String selectedRotation) {
		switch (selectedRotation) {
		case Rotation.XYZ:
			this.setLength(this.getBiggestDimension());
			this.setWidth(this.getMiddleDimension());
			this.setHeight(this.getSmallestDimension());
			break;

		case Rotation.XZY:
			this.setLength(this.getBiggestDimension());
			this.setWidth(this.getSmallestDimension());
			this.setHeight(this.getMiddleDimension());
			break;
		case Rotation.YXZ:
			this.setWidth(this.getBiggestDimension());
			this.setLength(this.getMiddleDimension());
			this.setHeight(this.getSmallestDimension());
			break;
		case Rotation.YZX:
			this.setWidth(this.getBiggestDimension());
			this.setHeight(this.getMiddleDimension());
			this.setLength(this.getSmallestDimension());
			break;
		case Rotation.ZXY:
			this.setHeight(this.getBiggestDimension());
			this.setLength(this.getMiddleDimension());
			this.setWidth(this.getSmallestDimension());
			break;
		case Rotation.ZYX:
			this.setHeight(this.getBiggestDimension());
			this.setWidth(this.getMiddleDimension());
			this.setLength(this.getSmallestDimension());
			break;
		}
	}

	public double getBiggestDimension() {
		return biggestDimension;
	}

	public double getMiddleDimension() {
		return middleDimension;
	}

	public double getSmallestDimension() {
		return smallestDimension;
	}

	public String getSelectedRotation() {
		return selectedRotation;
	}

	public void setSelectedRotation(String selectedRotation) {
		this.selectedRotation = selectedRotation;
	}

	public int getSequenceNumber() {
		return sequenceNumber;
	}

	public void setSequenceNumber(int sequenceNumber) {
		this.sequenceNumber = sequenceNumber;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}

	public int getPriority() {
		return priority;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}

	@Override
	public double getLength() {
		return super.getLength();
	}

	@Override
	public void setWidth(double width) {
		super.setWidth(width);
	}

	@Override
	public double getWidth() {
		return super.getWidth();
	}

	public double getHeight() {
		return super.getHeigth();
	}

	@Override
	public void setHeight(double height) {
		super.setHeight(height);
	}

	public float getMass() {
		return mass;
	}

	public boolean isFragile() {
		return fragile;
	}

	public void setMass(float mass) {
		this.mass = mass;
	}

	public void setFragile(boolean fragile) {
		this.fragile = fragile;
	}

	public Box(double l, double w, double h) {
		super();
		super.setLength(l);
		super.setWidth(w);
		super.setHeight(h);
		this.initializationModel();
	}

	public Box() {
		this.initializationModel();
	}		
	
	public double[] getDimension() {
		return dimension;
	}

	public Box(Box obj) {
		this. id = obj.getId();
		this.biggestDimension = obj.getBiggestDimension();
		this.middleDimension = obj.getMiddleDimension();		
		this.smallestDimension = obj.getSmallestDimension();
		this.mass = obj.getMass();
		this.fragile = obj.isFragile();
		this.priority = obj.getPriority();
		this.sequenceNumber = obj.getSequenceNumber();
		this.selectedRotation = obj.getSelectedRotation();		
		this.fRotation = obj.getfRotation();
		this.dimension = obj.getDimension();
		this.boxType = obj.getBoxType();
		this.supportParamA = obj.getSupportParamA();
		this.supportParamB = obj.getSupportParamB();		
		this.largestSurface = obj.getLargestSurface();
		this.volume  = obj.getVolume();		
	}
	public int[] getfRotation() {
		return fRotation;
	}

	@Override
	public boolean equals(Object obj) {
		Box objBox = (Box) obj;
		if (this.getHeight() == objBox.getHeight() && this.getWidth() == objBox.getWidth()
				&& this.getLength() == objBox.getLength() && this.isFragile() == objBox.isFragile())
			return true;
		return false;
	}

	private void initializationModel() {
		this.fRotation = new int[] { 1, 1, 1 };
		this.caculateVolume();
		this.loadingDimension();
		this.calculateLargestSurface();		
	}
	
	private void caculateVolume() {
			this.volume = this.getWidth() * this.getHeight() * this.getLength();
	}

	private void loadingDimension() {
		dimension = new double[] { super.getLength(), super.getWidth(), super.getHeigth() };
		Arrays.sort(dimension);
		this.smallestDimension = dimension[0];
		this.middleDimension = dimension[1];
		this.biggestDimension = dimension[2];		
	}

	private void  calculateLargestSurface() {
		double surface01 = this.getLength() * this.getWidth();
		double surface02 = this.getLength() * this.getHeight();
		double surface03 = this.getWidth() * this.getHeight();
		double[] surfaces = new double[] {surface01, surface02, surface03};
		Arrays.sort(surfaces);
		this.setLargestSurface(surfaces[2]);
	}

	@Override
	public String toString() {
		return super.toString();
	}

}
