package anhnh34.com.vn.model;

import java.util.ArrayList;
import java.util.List;

public class BoxType {	
	private int id;
	private List<Box> boxes;	
	private List<Rotation> rotations;
	public List<Box> getBoxes() {
		return boxes;
	}
	
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public List<Rotation> getRotations() {
		return rotations;
	}
	public void setBoxes(List<Box> boxes) {
		
		this.boxes = boxes;
	}
	public void setRotations(List<Rotation> rotations) {
		this.rotations = rotations;
	}
	
	public BoxType() {
		this.initiliaze();
	}	
	
	private void initiliaze() {
		this.boxes = new ArrayList<Box>();
		this.rotations = new ArrayList<Rotation>();
	}
	
	public BoxType(BoxType boxType) {
		this.initiliaze();
		
		for(Box b : boxType.getBoxes()) {
			Box nBox = new Box(b);
			this.boxes.add(nBox);
		}
		
		for(Rotation r : boxType.getRotations()) {
			Rotation nRotation = new Rotation(r);
			this.rotations.add(nRotation);			
		}		
	}
	
}
