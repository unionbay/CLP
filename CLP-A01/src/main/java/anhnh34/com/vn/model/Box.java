package anhnh34.com.vn.model;

import java.util.Arrays;

public class Box extends Cuboid{
	private int id;		
	private double biggestDimension;
	private double middleDimension;
	private double smallestDimension;
	private float mass;
	private boolean fragile;	
	private int priority;
	private int frequenceNum;
	private String selectedRotation;
	private int[] fRotation;	
	private double[] dimension;	
	private String boxType;
	
	
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
	
	public void setLength(double length) {
		super.setLength(length);
	}
	
	
	
	public void setSize(String selectedRotation){
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
	
	public int getFrequenceNum() {
		return frequenceNum;
	}

	public void setFrequenceNum(int frequenceNum) {
		this.frequenceNum = frequenceNum;
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


	public double getLength() {
		return super.getLength();
	}

	public void setWidth(double width) {
		super.setWidth(width);
	}

	public double getWidth() {
		return super.getWidth();
	}

	public double getHeight() {
		return super.getHeigth();
	}
		
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
	
	public Box(){
		this.initializationModel();
	}
	
	public int[] getfRotation() {
		return fRotation;
	}

	@Override
	public boolean equals(Object obj) {
		Box objBox = (Box)obj;
		if(this.getHeight() == objBox.getHeight() &&  
				this.getWidth() == objBox.getWidth()&& 
				this.getLength() == objBox.getLength() && 
				this.isFragile() == objBox.isFragile())
			return true;		
		return false;		
	}
	
	
	private void initializationModel(){
		this.fRotation = new int[]{1,1,1};
		this.loadingDimension();
	}
	
	
	private void loadingDimension(){
		dimension = new double[]{super.getLength(), super.getWidth(), super.getHeigth()};
		Arrays.sort(dimension);
		this.smallestDimension = dimension[0];
		this.middleDimension = dimension[1];
		this.biggestDimension = dimension[2];
	} 
	
	
	
	
}
