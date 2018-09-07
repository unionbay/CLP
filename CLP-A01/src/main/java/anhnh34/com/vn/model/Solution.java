package anhnh34.com.vn.model;

import java.util.ArrayList;
import java.util.List;

public class Solution {
	private List<Container> containerList;
	private double wastedSpace;
	private int numOfAmalgamate;	
	private double totalCost;
	
		
	
	public List<Container> getContainerList() {
		return containerList;
	}
	
	public void setContainerList(List<Container> containerList) {
		this.containerList = containerList;
	}
	
	public double getWastedSpace() {
		return wastedSpace;
	}
	
	public void setWastedSpace(double wastedSpace) {
		this.wastedSpace = wastedSpace;
	}
	
	public int getNumOfAmalgamate() {
		return numOfAmalgamate;
	}
	
	public void setNumOfAmalgamate(int numOfAmalgamate) {
		this.numOfAmalgamate = numOfAmalgamate;
	}
	
	public Solution() {
		this.containerList = new ArrayList<Container>();		
	}
	
	public double getTotalCost() {
		return totalCost;
	}
	
	public void calculateTotalCost() {
		this.totalCost = 0;
		for(Container container : containerList) {
			PartialSolution partialSolution = container.getCurrentSolution();
			this.totalCost += partialSolution.getCost();
		}
	}
}
