package anhnh34.com.vn.model;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

public class Solution {
	private List<Container> containerList;
	private double wastedSpace;
	private int numOfAmalgamate;
	private double totalCost;
	
	final static Logger logger = Logger.getLogger(Solution.class);

	public List<Container> getContainerList() {
		return containerList;
	}

	public void setContainerList(List<Container> containerList) {
		this.containerList.clear();
		for (Container con : containerList) {
			Container container = new Container(con);
			this.containerList.add(container);
		}
		this.calculateCost();
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
	
	public void setContainer(Container con) {
		this.containerList.add(new Container(con));
	}

	public void calculateTotalCost() {
		this.totalCost = 0;
		for (Container container : containerList) {
			PartialSolution partialSolution = container.getCurrentSolution();
			this.totalCost += partialSolution.getCost();
		}
	}

	public Solution(Solution s) {
		this.containerList = new ArrayList<Container>();
		this.setContainerList(s.getContainerList());		
		this.setNumOfAmalgamate(s.getNumOfAmalgamate());
		this.setWastedSpace(s.getWastedSpace());
		this.calculateCost();
	}
	
	private void calculateCost() {
		for(Container container : this.containerList) {
			container.getCurrentSolution().calculateCost();
		}
		this.calculateTotalCost();
	}
	
	public void showResult() {
		for(Container con : this.containerList) {
			PartialSolution lastSolution = con.getCurrentSolution();
			logger.info(String.format("%s %.4f", new Object[] {lastSolution.getIdList().toString(), lastSolution.getCost()}));
		}
		logger.info(String.format("Total cost: %.4f", new Object[] {this.getTotalCost()}));
	}
}
