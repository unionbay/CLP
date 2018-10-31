package anhnh34.com.vn.model;

import java.util.HashMap;

public class Rotation {
	

	private double length;
	private double width;
	private double height;
	private String rotationCode;
	public static final String XYZ = "xyz";
	public static final String XZY = "xzy";
	public static final String YXZ = "yxz";
	public static final String YZX = "yzx";
	public static final String ZXY = "zxy";
	public static final String ZYX = "zyx";
	
	public Rotation(Rotation r) {
		this.length = r.getLength();
		this.width = r.getWidth();
		this.height = r.getHeight();
		this.rotationCode = r.getRotationCode();
	}

	private HashMap<String, Dimension> rotationAble;		

	public String getRotationCode() {
		return rotationCode;
	}
	
	public void setRotationCode(String rotationCode) {
		this.rotationCode = rotationCode;
	}
	
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

	public double getHeight() {
		return height;
	}

	public void setHeight(double height) {
		this.height = height;
	}

	public HashMap<String, Dimension> getRotationAble() {
		return rotationAble;
	}

	public void setRotationAble(HashMap<String, Dimension> rotationAble) {
		this.rotationAble = rotationAble;
	}

	public Rotation(double length, double width, double height) {
		this.length = length;
		this.width = width;
		this.height = height;		
	}
	
	public Rotation() {		
	}


	
//	private void loadingRotation(){
//		
//	}
//	
//	private void initRotation() {
//		//this.checkDimension();		
//		
//	}
//
//	private void checkDimension() {
//		List<Double> dimensionList = new ArrayList<Double>();
//		dimensionList.add(length);
//		dimensionList.add(width);
//		dimensionList.add(height);
//
//		for (int i = 0; i < dimensionList.size(); i++) {
//			if (dimensionList.get(i) < dimensionList.get(i + 1)) {
//				Double temp = dimensionList.get(i);
//				dimensionList.set(i + 1, dimensionList.get(i));
//				dimensionList.set(i, temp);
//			}
//		}	
//		biggestDimension = dimensionList.get(0);
//		middleDimension = dimensionList.get(1);
//		smallestDimension = dimensionList.get(2);			
//	}

	
}
