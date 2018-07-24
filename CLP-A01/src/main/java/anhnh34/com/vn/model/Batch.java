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

	public List<BoxType> getBoxTypes() {
		return boxTypes;
	}
	
	public void setBoxTypes(List<BoxType> boxTypes) {
		this.boxTypes = boxTypes;
	}

	public List<Box> getBoxes() {
		return boxes;
	}

	public void setBoxes(List<Box> boxes) {
		this.boxes = boxes;
	}

	public Batch(List<Box> boxes) {
		this.boxes = boxes;
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
		this.boxes = new ArrayList<Box>();
		this.boxTypes = new ArrayList<BoxType>();
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
		
	}
}
