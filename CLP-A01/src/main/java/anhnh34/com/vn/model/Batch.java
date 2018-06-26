package anhnh34.com.vn.model;

import java.util.ArrayList;
import java.util.List;

public class Batch {
	private List<Box> boxes;

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

	public void sorted(int sortType) {
		switch (sortType) {
		case VaribleConstant.DIMENSION:
			sortByDimension();
			break;
		case VaribleConstant.VOLUME:
			sortByVolume();
			break;
		}
	}

	private void sortByDimension() {

	}

	private void sortByVolume() {

	}

	/**
	 * default contructor
	 */
	public Batch() {
		this.boxes = new ArrayList<Box>();
	}

}
