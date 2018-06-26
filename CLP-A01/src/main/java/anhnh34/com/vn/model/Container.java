package anhnh34.com.vn.model;

import java.util.ArrayList;
import java.util.List;

import org.omg.CORBA.OMGVMCID;

public class Container {
	private double length;
	private double width;
	private double height;
	private int capacity;
	private Dimension dimension;
	private List<Space> avaiableSpaces;

	public List<Space> getAvaiableSpaces() {
		return avaiableSpaces;
	}

	public void setLength(double length) {
		this.length = length;
	}

	public double getLength() {
		return length;
	}

	public int getCapacity() {
		return capacity;
	}

	public void setHeight(double height) {
		this.height = height;
	}

	public void setCapacity(int capacity) {
		this.capacity = capacity;
	}

	public void setAvaiableSpaces(List<Space> avaiableSpaces) {
		this.avaiableSpaces = avaiableSpaces;
	}

	public double getHeight() {
		return height;
	}

	public void setWidth(double width) {
		this.width = width;
	}

	public double getWidth() {
		return width;
	}
		

	public Container(double d, double w, double h) {
		this.length = d;
		this.width = w;
		this.height = h;
	}

	public void removeSubset() {

	}

	public void amalgamate() {

	}

	public void spaceSort() {

	}

	public Container() {
		this.avaiableSpaces = new ArrayList<Space>();	

	}

	public void loadingSpace() {
		Dimension minDimension = new Dimension(0, 0, 0);

		// init maximum dimension with corresponde coordinate X, Y, Z
		Dimension maxDimension = new Dimension(length, width, height);
		Space containerSpace = new Space(minDimension, maxDimension);
			
		if (this.getAvaiableSpaces().isEmpty()) {			
			this.getAvaiableSpaces().add(containerSpace);
		}
	}

	public void sortSpaces() {
		//check spaces's size.
		if (avaiableSpaces.isEmpty() || avaiableSpaces.size() == 1) return;
		 
		this.mergeSort((Space[])this.avaiableSpaces.toArray(new Space[0]), 0, this.avaiableSpaces.size());	
	}

	public Space[] mergeSort(Space[] spaceList, int start, int end) {
		Space[] returnSpace = new Space[spaceList.length];
		if (end - start > 1) {
			int mid = (end + start) / 2;
			mergeSort(spaceList, start, mid);
			mergeSort(spaceList, mid + 1, end);

			returnSpace = this.merge(spaceList, start, end, mid);
		}

		return returnSpace;
	}

	private Space[] merge(Space[] spaceList, int start, int end, int mid) {
		Space[] temp = new Space[spaceList.length];

		//copy both parts into the temp array
		for (int i = start; i < end; i++) {
			temp[i] = spaceList[i];
		}

		int i = start;
		int k = start;
		int j = mid + 1;

		//copy smallest values from either left or right side back 
		//to orginal array.
		while (i < mid && j < end) {
			if (spaceList[i].compare(spaceList[j]) <= 0) {
				spaceList[k] = temp[i];
				i++;
			} else {
				spaceList[k] = temp[j];
				j++;
			}

			k++;
		}

		while (i < mid) {
			spaceList[k] = temp[i];
			k++;
			i++;
		}
		// Since we are sorting in-place any leftover elements from the right
		// side
		// are already at the right position.
		return spaceList;
	}	
	
	
}
