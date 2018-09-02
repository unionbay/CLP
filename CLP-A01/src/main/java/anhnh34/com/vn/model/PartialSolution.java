package anhnh34.com.vn.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PartialSolution {

	public PartialSolution() {
		this.initiliaze();

	}

	public PartialSolution(List<Space> spaces, List<Box> npBoxes, List<Box> pBoxes, List<String> IDList,
			List<Location> locationList) {
		this.initiliaze();
		Batch batchNotPlacedBox = new Batch(npBoxes);
		Batch batchPlacedBox = new Batch(pBoxes);
		this.setNotPlacedBoxes(batchNotPlacedBox);
		this.setPlacedBoxes(batchPlacedBox);
		this.setAvaiableSpaces(spaces);
		this.setIdList(IDList);
	}

	public PartialSolution(List<Space> spaces, Batch pBoxes, Batch npBoxes, List<String> locationIdList,
			List<Location> locationList) {
		this.initiliaze();
		this.setPlacedBoxes(new Batch(pBoxes));
		this.setNotPlacedBoxes(new Batch(npBoxes));
		this.setAvaiableSpaces(spaces);
		this.setIdList(locationIdList);
		this.setLocationList(locationList);
	}

	private void initiliaze() {
		this.idList = new ArrayList<String>();
		this.locationList = new ArrayList<Location>();
		this.avaiableSpaces = new ArrayList<Space>();
		this.notPlacedBoxes = new Batch();
		this.placedBoxes = new Batch();
	}

	public List<Space> getAvaiableSpaces() {
		return avaiableSpaces;
	}

	public void setAvaiableSpaces(List<Space> avaiableSpaces) {
		this.avaiableSpaces.clear();
		for (Space space : avaiableSpaces) {
			Space newSpace = new Space(space.getMinimum(), space.getMaximum());
			newSpace.setRatioSupport(space.getRatioSupport());
			newSpace.setMaximumSupportX(space.getMaximumSupportX());
			newSpace.setMaximumSupportY(space.getMaximumSupportY());
			this.avaiableSpaces.add(newSpace);
		}
	}

	public String getLocationIds() {
		String str = "";
		for (String id : this.idList) {
			str = str.concat(id + " ");
		}
		return str;
	}

	public void calculateCost() {
		 int index = 0;
		
		while (index < this.locationList.size() - 1) {
			Location startLocation = this.locationList.get(index);
			Location endLocation = this.locationList.get(++index);
			double cost = Math.sqrt(Math.pow(endLocation.getX() - startLocation.getX(), 2)
					+ Math.pow(endLocation.getY() - startLocation.getY(), 2));
			this.totalCost = this.totalCost + cost;
		}

//		int index = this.locationList.size() - 1;
//
//		while (index > 0) {
//			Location startLocation = this.locationList.get(index);
//			Location endLocation = this.locationList.get(--index);
//			double cost = Math.sqrt(Math.pow(endLocation.getX() - startLocation.getX(), 2)
//					+ Math.pow(endLocation.getY() - startLocation.getY(), 2));
//			this.totalCost = this.totalCost + cost;
//		}
//		
		Location lastLocation = this.locationList.get(0);
		Location firstLocation = this.locationList.get(this.locationList.size() -1);
		Double cost = Math.sqrt(Math.pow(lastLocation.getX() - firstLocation.getX(), 2)
				+ Math.pow(lastLocation.getY() - firstLocation.getY(), 2));
		
		this.totalCost=  this.totalCost + cost;		
		this.totalCost = this.round(this.totalCost, 4); 

	}

	public Batch getPlacedBoxes() {
		return placedBoxes;
	}

	public Batch getNotPlacedBoxes() {
		return notPlacedBoxes;
	}

	public void setPlacedBoxes(Batch placedBoxes) {
		this.placedBoxes = placedBoxes;
	}

	public void setNotPlacedBoxes(Batch notPlacedBoxes) {
		this.notPlacedBoxes = notPlacedBoxes;
	}

	public void setSubPartialSolutions(List<PartialSolution> subPartialSolutions) {
		this.subPartialSolutions = subPartialSolutions;
	}

	public List<PartialSolution> getSubPartialSolutions() {
		return subPartialSolutions;
	}

	public void addPartialSolution(PartialSolution solution) {
		this.getSubPartialSolutions().add(solution);
	}

	public boolean isFeasible() {
		return isFeasible;
	}

	public void setFeasible(boolean isFeasible) {
		this.isFeasible = isFeasible;
	}

	public double getSolutionTime() {
		return solutionTime;
	}

	public void setSolutionTime(double solutionTime) {
		this.solutionTime = solutionTime;
	}

	public double getVolumeUtitlisation() {
		return volumeUtitlisation;
	}

	public void setVolumeUtitlisation(double volumeUtitlisation) {
		this.volumeUtitlisation = volumeUtitlisation;
	}

	public String getCurrLocationID() {
		return currLocationID;
	}

	public void setCurrLocationID(String currLocationID) {
		this.currLocationID = currLocationID;
	}

	public List<String> getIdList() {
		return idList;
	}

	public void setIdList(List<String> ids) {
		this.idList.clear();
		for (String id : ids) {
			String nID = id;
			this.idList.add(nID);
		}
	}

	public double getCost() {
		return totalCost;
	}

	public void setCost(double cost) {
		this.totalCost = cost;
	}

	public void addCost(double cost) {
		this.totalCost += cost;
	}

	public List<Location> getLocationList() {
		return locationList;
	}

	public void setLocationList(List<Location> lcList) {
		this.locationList.clear();
		for (Location lc : lcList) {
			Location nLc = new Location(lc.getLocationID(), lc.getX(), lc.getY(), lc.getDemand());
			this.locationList.add(nLc);			
		}
				

	}

	public void addLocation(Location location) {
		Location newLocation = new Location(location.getLocationID(), location.getX(), location.getY(),
				location.getDemand());
		this.locationList.add(newLocation);
	}
	
	
	private double round(double value, int places) {
	    if (places < 0) throw new IllegalArgumentException();

	    BigDecimal bd = new BigDecimal(value);
	    bd = bd.setScale(places, RoundingMode.HALF_UP);
	    return bd.doubleValue();
	}
	
	public void setTakeOutLocation(List<Location> takeOutLocation) {
		this.takeOutLocation.clear();
		
		for(Location lc : takeOutLocation) {
			Location location = new Location(lc.getLocationID(), lc.getX(), lc.getY(),
					lc.getDemand());
			takeOutLocation.add(location);
		}		
	}
	
	public void addTakeoutLocation(Location lc) {
		Location location = new Location(lc.getLocationID(), lc.getX(), lc.getY(),
				lc.getDemand());
		takeOutLocation.add(location);
	}
	
	public List<Location> getTakeOutLocation() {
		return takeOutLocation;
	}

	private Batch placedBoxes;
	private Batch notPlacedBoxes;
	private double solutionTime;
	private double volumeUtitlisation;
	private List<Location> locationList;
	private String currLocationID;
	private boolean isFeasible;
	private List<Space> avaiableSpaces;
	private List<PartialSolution> subPartialSolutions;
	private double totalCost;
	private List<String> idList;
	private List<Location> takeOutLocation;

}
