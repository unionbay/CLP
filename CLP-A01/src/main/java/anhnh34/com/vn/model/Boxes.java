package anhnh34.com.vn.model;

//Json Boxes
public class Boxes {
	private Dimension root;
	private double length;
	private double width;
	private double height;
	private int sequenceNumber;
	private String colorCode;

	public String getColorCode() {
		return colorCode;
	}

	public void setColorCode(String colorCode) {
		this.colorCode = colorCode;
	}

	public int getSequenceNumber() {
		return sequenceNumber;
	}

	public void setSequenceNumber(int sequenceNumber) {
		this.sequenceNumber = sequenceNumber;
	}

	public Dimension getRoot() {
		return root;
	}

	public void setRoot(Dimension root) {
		this.root = root;
	}

	public double getLength() {
		return length;
	}

	public void setLength(double length) {
		this.length = length;
	}

	public double getHeight() {
		return height;
	}

	public void setHeight(double height) {
		this.height = height;
	}

	public void setWidth(double width) {
		this.width = width;
	}

	public double getWidth() {
		return width;
	}

	public Boxes() {

	}

	public Boxes(Dimension root, double length, double width, double height, int sequenceNumber) {
		this.setRoot(root);
		this.setLength(length);
		this.setWidth(width);
		this.setHeight(height);
		this.setSequenceNumber(sequenceNumber);		
	}
}
