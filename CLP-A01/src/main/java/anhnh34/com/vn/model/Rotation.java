package anhnh34.com.vn.model;

import java.awt.CheckboxMenuItem;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Rotation {

	public static final String XYZ = "xyz";
	public static final String XZY = "xzy";
	public static final String YXZ = "yxz";
	public static final String YZX = "yzx";
	public static final String ZXY = "zxy";
	public static final String ZYX = "zyz";

	private double length;
	private double width;
	private double height;
	private double biggestDimension;
	private double middleDimension;
	private double smallestDimension;

	private HashMap<String, Dimension> rotationAble;

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
	
	private void loadingRotation(){
		
	}
	
	private void initRotation() {
		//this.checkDimension();		
		
	}

	private void checkDimension() {
		List<Double> dimensionList = new ArrayList<Double>();
		dimensionList.add(length);
		dimensionList.add(width);
		dimensionList.add(height);

		for (int i = 0; i < dimensionList.size(); i++) {
			if (dimensionList.get(i) < dimensionList.get(i + 1)) {
				Double temp = dimensionList.get(i);
				dimensionList.set(i + 1, dimensionList.get(i));
				dimensionList.set(i, temp);
			}
		}	
		biggestDimension = dimensionList.get(0);
		middleDimension = dimensionList.get(1);
		smallestDimension = dimensionList.get(2);			
	}

	
}
