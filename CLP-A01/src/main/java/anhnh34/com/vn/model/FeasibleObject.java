package anhnh34.com.vn.model;

public class FeasibleObject {
	private Box box;
	private Rotation rotation;
	private Space space;
	private Rotation selectedRotation;
	
	
	public Rotation getSelectedRotation() {
		return selectedRotation;
	}
	
	public void setSelectedRotation(Rotation selectedRotation) {
		this.selectedRotation = selectedRotation;
	}
	
	public Box getBox() {
		return box;
	}
	
	public void setBox(Box box) {
		this.box = box;
	}
	
	public Rotation getRotation() {
		return rotation;
	}
	
	public void setRotation(Rotation rotation) {
		this.rotation = rotation;
	}
	
	public Space getSpace() {
		return space;
	}
	
	public void setSpace(Space space) {
		this.space = space;
	}

	/**
	 * @param box
	 * @param rotation
	 * @param space
	 */
	public FeasibleObject(Box box,Rotation  selectedRotation, Space space) {
		super();
		this.box = box;
		this.selectedRotation = selectedRotation;
		this.space = space;
	}		
	
	public FeasibleObject(Box box, Space space) {
		super();
		this.setBox(box);		
		this.setSpace(space);
		this.setSelectedRotation(box.getSelectedRotation());
	}
}
