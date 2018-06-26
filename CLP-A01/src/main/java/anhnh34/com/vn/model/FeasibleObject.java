package anhnh34.com.vn.model;

public class FeasibleObject {
	private Box box;
	private Rotation rotation;
	private Space space;
	private String selectedRotation;
	
	
	public String getSelectedRotation() {
		return selectedRotation;
	}
	
	public void setSelectedRotation(String selectedRotation) {
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
	public FeasibleObject(Box box,String  selectedRotation, Space space) {
		super();
		this.box = box;
		this.selectedRotation = selectedRotation;
		this.space = space;
	}		
}
