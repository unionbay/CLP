package anhnh34.com.vn.model;

import java.util.ArrayList;
import java.util.List;

public class Batch {
	private List<Box> boxes;
	private List<BoxType> boxTypes;
	private int boxNumber;
	
	
	public int getBoxNumber() {
		return boxNumber;
	}
	
	public void setBoxNumber(int boxNumber) {
		this.boxNumber = boxNumber;
	}
	
	public void initialize() {
		this.boxes = new ArrayList<Box>();
		this.boxTypes = new ArrayList<BoxType>();
	}

	public List<BoxType> getBoxTypes() {
		return boxTypes;
	}
	
	public void setBoxTypes(List<BoxType> boxTypes) {
		for(BoxType bt : boxTypes) {
			this.addBoxType(bt);
		}
		
	}
	
	public void addBoxType(BoxType boxType) {
		this.getBoxTypes().add(new BoxType());
	}

	public List<Box> getBoxes() {
		return boxes;
	}

	public void setBoxes(List<Box> boxes) {
		this.boxes.clear();
		for(Box box : boxes) {			
			this.addBox(box);
		}		
	}

	public Batch(List<Box> boxes) {
		this.initialize();
		this.setBoxes(boxes);		
	}

	public void placeBox() throws Exception {
		throw new Exception("Not implemented yet");
	}

	public int getNumberOfBox() {
		return boxes == null ? 0 : boxes.size();
	}

//	public void sorted(int sortType) {
//		switch (sortType) {
//		case VaribleConstant.DIMENSION:
//			sortByDimension();
//			break;
//		case VaribleConstant.VOLUME:
//			sortByVolume();
//			break;
//		}
//	}
//
//	private void sortByDimension() {
//		
//	}
//
//	private void sortByVolume() {
//
//	}
//	
//	private void sortByBoxType() {
//		
//	}
	
	

	/**
	 * default contructor
	 */
	public Batch() {
		this.initialize();
	}
	
	public Batch(Batch bObj) {
		this.initialize();
		setBoxNumber(bObj.getBoxNumber());
		setBoxes(bObj.getBoxes());
		setBoxTypes(bObj.getBoxTypes());
	}

	public void removeBox(Box box) {
		Box removeBox = null;
		for(Box b :  this.boxes) {
			if(box.getId() == b.getId()) {
				removeBox = b;
			}
		}
		
		if(removeBox != null) {
			this.boxes.remove(removeBox);
		}
	}
	
	public void addBox(Box box) { 		
		this.getBoxes().add(new Box(box));
	}
}
