package anhnh34.com.vn.model;

//Json Boxes
public class Boxes {
	private Dimension root;
	private String customerId;
	private Dimension maximum;
	private double volume;
	private double length;
	private double width;
	private double height;
	private int sequenceNumber;
	private String colorCode;
	
	public double getVolume() {
		return volume;
	}
	
	public void setVolume(double volume) {
		this.volume = volume;
	}
	
	public String getCustomerId() {
		return customerId;
	}
	
	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}
	
	public void setMaximum(Dimension maximum) {
		this.maximum = maximum;
	}
	
	public Dimension getMaximum() {
		return maximum;
	}
	

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

	public Boxes(Dimension root,Dimension max, double length, double width, double height,double volume, int sequenceNumber, String customerId) {		
		this.setRoot(root);
		this.setMaximum(max);
		this.setLength(length);
		this.setWidth(width);
		this.setHeight(height);
		this.setVolume(volume);
		this.setCustomerId(customerId);
		this.setSequenceNumber(sequenceNumber);		
	}
}
