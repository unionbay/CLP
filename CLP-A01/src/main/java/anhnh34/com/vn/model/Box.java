package anhnh34.com.vn.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;

public class Box extends Cuboid{
	final static Logger logger = Logger.getLogger(Box.class);
	private int id;
	private String customerId;
	private double biggestDimension;
	private double middleDimension;
	private double smallestDimension;
	private float mass;
	private boolean fragile;
	private int priority;
	private int sequenceNumber;
	private Rotation selectedRotation;
	private List<Rotation> pRotations;
	private int[] fRotation;
	private double[] dimension;
	private String boxType;
	private int numberOfItem;
	private double supportParamA;
	private double supportParamB;
	private double largestSurface;
	private double volume;
	

	public int getNumberOfItem() {
		return numberOfItem;
	}

	public void setNumberOfItem(int numberOfItem) {
		this.numberOfItem = numberOfItem;
	}

	public List<Rotation> getPossibleRotations() {
		return pRotations;
	}

	public String getCustomerId() {
		return customerId;
	}

	public void setCustomerId(String id) {
		this.customerId = id;
	}

	public void setPossibleRotations(List<String> rotations) {
		if (rotations == null) {
			return;
		}

		for (String rotationString : rotations) {
			Rotation r = this.calculateSize(rotationString, this);
			if (r != null) {
				this.pRotations.add(r);
			}
		}
	}

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

	private Rotation calculateSize(String selectedRotation, Box box) {
		Rotation rotation = new Rotation();
		switch (selectedRotation) {
		case Rotation.XYZ:
			rotation.setRotationCode(Rotation.XYZ);
//			rotation.setLength(this.getBiggestDimension());
//			rotation.setWidth(this.getMiddleDimension());
//			rotation.setHeight(this.getSmallestDimension());
			rotation.setHeight(this.getLength());
			rotation.setLength(this.getWidth());
			rotation.setWidth(this.getHeight());

			break;
		case Rotation.XZY:
			rotation.setRotationCode(Rotation.XZY);
			rotation.setHeight(this.getLength());
			rotation.setLength(this.getHeight());
			rotation.setWidth(this.getWidth());
//			rotation.setLength(this.getBiggestDimension());
//			rotation.setWidth(this.getSmallestDimension());
//			rotation.setHeight(this.getMiddleDimension());
			break;
		case Rotation.YXZ:
			rotation.setRotationCode(Rotation.YXZ);
			rotation.setHeight(this.getWidth());
			rotation.setLength(this.getLength());
			rotation.setWidth(this.getHeight());
//			rotation.setWidth(this.getBiggestDimension());
//			rotation.setLength(this.getMiddleDimension());
//			rotation.setHeight(this.getSmallestDimension());
			break;
		case Rotation.YZX:
			rotation.setRotationCode(Rotation.YZX);
			rotation.setHeight(this.getWidth());
			rotation.setLength(this.getHeight());
			rotation.setWidth(this.getLength());
//			rotation.setWidth(this.getBiggestDimension());
//			rotation.setHeight(this.getMiddleDimension());
//			rotation.setLength(this.getSmallestDimension());
			break;
		case Rotation.ZXY:
			rotation.setRotationCode(Rotation.ZXY);
			//Start-UPRO
			rotation.setHeight(this.getHeight());
			rotation.setLength(this.getLength());
			rotation.setWidth(this.getWidth());
			//End-UPRO
			
			//rotation.setHeight(this.getBiggestDimension());
			//rotation.setLength(this.getMiddleDimension());
			//rotation.setWidth(this.getSmallestDimension());
			break;
		case Rotation.ZYX:
			rotation.setRotationCode(Rotation.ZYX);
			
			//Start-UPRO
			rotation.setHeight(this.getHeight());
			rotation.setLength(this.getWidth());
			rotation.setWidth(this.getLength());
			//End-UPRO
			
			//rotation.setHeight(this.getBiggestDimension());
			//rotation.setWidth(this.getMiddleDimension());
			//rotation.setLength(this.getSmallestDimension());
			break;
		default:
			return null;
		}

		return rotation;

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

	public Rotation getSelectedRotation() {
		return selectedRotation;
	}
	

	public void setSelectedRotation(Rotation rotation) {
		//UpRo
		this.selectedRotation = new Rotation(rotation);
		//End UpRo
		
//		this.selectedRotation = rotation;
//		this.selectedRotation.setLength(rotation.getLength());
//		this.selectedRotation.setWidth(rotation.getWidth());
//		this.selectedRotation.setHeight(rotation.getHeight());
		//this.setLength(rotation.getLength());
		//this.setWidth(rotation.getWidth());
		//this.setHeight(rotation.getHeight());
	}

	public void setSelectedRotation(String rCode) {
		if (rCode == null || rCode.isEmpty()) {
			this.setSelectedRotation(new Rotation());
		}
		Rotation rotation = findRotation(rCode);
		this.selectedRotation = new Rotation(rotation);
	}

	private Rotation findRotation(String rCode) {
		for (Rotation r : this.pRotations) {
			if (rCode.compareToIgnoreCase(r.getRotationCode()) == 0) {
				return r;
			}
		}
		return null;
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

	public Box(int id, double l, double w, double h) {
		super();
		this.setId(id);
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
		this.id = obj.getId();
		this.customerId = obj.getCustomerId();
		this.volume = obj.getVolume();
		this.biggestDimension = obj.getBiggestDimension();
		this.middleDimension = obj.getMiddleDimension();
		this.smallestDimension = obj.getSmallestDimension();
		this.mass = obj.getMass();
		this.fragile = obj.isFragile();
		this.priority = obj.getPriority();
		this.sequenceNumber = obj.getSequenceNumber();
		this.fRotation = obj.getfRotation();
		this.dimension = obj.getDimension();						
		this.boxType = obj.getBoxType();
		this.supportParamA = obj.getSupportParamA();
		this.supportParamB = obj.getSupportParamB();
		this.largestSurface = obj.getLargestSurface();
	
		
		
		this.setLength(obj.getLength());
		this.setWidth(obj.getWidth());
		this.setHeight(obj.getHeight());
		
		if(obj.getSelectedRotation() != null) {
			this.selectedRotation = new Rotation(obj.getSelectedRotation());
		}else {
			this.selectedRotation = new Rotation();
		}
		
		if(obj.getMinimum() != null) {
			this.setMinimum(obj.getMinimum());
		}else {
			this.setMinimum(new Dimension());
		}
		
		if(obj.getMaximum() != null) {
			this.setMaximum(obj.getMaximum());
		}else {
			this.setMaximum(new Dimension());
		}
		
		if(obj.getPossibleRotations() != null) {
			List<Rotation> rotations = new ArrayList<Rotation>();
			for(Rotation r : obj.getPossibleRotations()) {
				Rotation nRotation = new Rotation(r);
				rotations.add(nRotation);
			}
			this.pRotations = rotations;			
		}else {
			this.pRotations = new ArrayList<Rotation>();
		}
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
		//Start Rotation
		this.fRotation = new int[] { 1, 1, 1 };
		//End Rotation
		
		//this.fRotation = new int[] { 1, 1, 1 };
		this.pRotations = new ArrayList<Rotation>();
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

	private void calculateLargestSurface() {
		double surface01 = this.getLength() * this.getWidth();
		double surface02 = this.getLength() * this.getHeight();
		double surface03 = this.getWidth() * this.getHeight();
		double[] surfaces = new double[] { surface01, surface02, surface03 };
		Arrays.sort(surfaces);
		this.setLargestSurface(surfaces[2]);
	}

	@Override
	public String toString() {
		return super.toString();
	}

}
