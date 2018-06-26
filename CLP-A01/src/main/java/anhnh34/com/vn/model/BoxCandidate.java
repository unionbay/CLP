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
	
	public Space getSpace() {
		return space;
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

	public void initialize(String algorithmType) {
		switch (algorithmType) {
		case Greedy.ST_ALGORITHM:
			this.stType();
			break;
		case Greedy.VL_ALGORITHM:
			this.vlType();
			break;
		case Greedy.EL_ALGORITHM:
			this.elType();
			break;
		}
	}

	private void stType() {
		// find minK
		int numOfAvaiableBox = (int) Math.round(space.getHeight() / box.getHeight());
		this.k = this.k < numOfAvaiableBox ? k : numOfAvaiableBox;

		// caculate pontetial Space Utilization
		this.potenSpaceUtilization = this.k * (this.box.getLength() * this.box.getWidth() * this.box.getHeight()
				/ (this.space.getLength() * this.space.getWidth() * this.space.getHeight()));

		// caculate lengthwise protrution.
		this.lengthwiseProtrustion = this.space.getMinimum().getX() + this.box.getLength();

		// caculate volume of box.
		this.boxVolume = this.box.getLength() * this.box.getWidth() * this.box.getHeight();
	}

	private void vlType() {
			int feasibleBoxX = (int) Math.round(space.getLength()/box.getLength());
			int feasibleBoxY = (int) Math.round(space.getWidth()/box.getWidth());
			int feasibleBoxZ = (int) Math.round(space.getHeight()/box.getHeight());
			
			int numVolumeUsageNum = feasibleBoxX * feasibleBoxY * feasibleBoxZ;
			
			this.k = this.k < numVolumeUsageNum  ? this.k : numVolumeUsageNum;
			
			// caculate pontetial Space Utilization
			this.potenSpaceUtilization = this.k * (this.box.getLength() * this.box.getWidth() * this.box.getHeight()
					/ (this.space.getLength() * this.space.getWidth() * this.space.getHeight()));

			// caculate lengthwise protrution.
			this.lengthwiseProtrustion = this.space.getMinimum().getX() + this.box.getLength();

			// caculate volume of box.
			this.boxVolume = this.box.getLength() * this.box.getWidth() * this.box.getHeight();
	}

	private void elType() {

	}

}
