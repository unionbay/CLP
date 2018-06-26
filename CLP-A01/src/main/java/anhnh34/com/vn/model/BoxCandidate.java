package anhnh34.com.vn.model;

public class BoxCandidate {
	private Box box;
	private Space space;
	private int k;
	private double potenSpaceUtilization;
	private double lengthwiseProtrustion;
	private double boxVolume;

	public int getK() {
		return k;
	}

	public void setK(int k) {
		this.k = k;
	}
	
	public Box getBox() {
		return box;
	}

	public double getLengthwiseProtrustion() {
		return lengthwiseProtrustion;
	}

	public double getBoxVolume() {
		return boxVolume;
	}

	public double getPotenSpaceUtilization() {
		return potenSpaceUtilization;
	}

	public void setBox(Box box) {
		this.box = box;
	}

	public void setSpace(Space space) {
		this.space = space;
	}

	public BoxCandidate(Box box, Space space, int k) {
		this.k = k;
		this.box = box;
		this.space = space;
	}

	public void initialize() {

		// find minK
		int numOfAvaiableBox = (int) Math.round(space.getHeight() / box.getHeight());
		this.k = this.k < numOfAvaiableBox ? k : numOfAvaiableBox;

		// caculate pontetial Space Utilization
		this.potenSpaceUtilization = this.k * (this.box.getLength() * this.box.getWidth() * this.box.getHeight()
				/ (this.space.getLenght() * this.space.getWidth() * this.space.getHeight()));
		
		//caculate lengthwise protrution.
		this.lengthwiseProtrustion = this.space.getMinimum().getX() + this.box.getLength();
		
		//caculate volume of box.
		this.boxVolume = this.box.getLength() * this.box.getWidth() * this.box.getHeight();
	}

}
