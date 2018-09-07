package anhnh34.com.vn.model;

import java.util.ArrayList;
import java.util.List;

public class Location {
	
	public Location() {
		this.initialize();
	}
	
	public Location(String id, double x, double y, int demand,List<Box>boxes) {		
		this.initialize();
		this.setX(x);
		this.setY(y);	
		this.setLocationID(id);
		this.setDemand(demand);		
		this.setBoxes(boxes);
		
	}
	
	private void initialize() {
		this.boxes = new ArrayList<Box>();
		this.locationList = new ArrayList<Location>();
		this.setVisited(false);
	}
		
	
	public List<Location> getLocationList() {
		return locationList;
	}
	
	public boolean isVisited() {
		return isVisited;
	}	

	public void setVisited(boolean isVisited) {
		this.isVisited = isVisited;
	}

	public double getX() {
		return x;
	}
	public double getY() {
		return y;
	}
	public void setX(double x) {
		this.x = x;
	}
	public void setY(double y) {
		this.y = y;
	}		
	
	
	public double getCapacity() {
		return getDemand();
	}
	
	public double getDistance() {
		return distance;
	}

	public void setDistance(double distance) {
		this.distance = distance;
	}	 

	public void setLocationID(String locationID) {
		this.locationID = locationID;
	}
	
	
	public String getLocationID() {
		return locationID;
	}
	
	
	public List<Box> getBoxes() {
		return boxes;
	}
	
	public void setBoxes(List<Box> boxes) {
		List<Box> boxList = new ArrayList<Box>();
		for(Box box : boxes) {
			Box nBox = new Box(box);
			boxList.add(nBox);
		}
		this.boxes = boxList;
	}
	
	public void setDemand(int demand) {
		this.demand = demand;
	}
	
	public int getDemand() {
		return demand;
	}
	
	public void addBox(Box box) {
		this.getBoxes().add(box);
	}
	
	public boolean isChecked() {
		return isChecked;
	}
	
	public void setIsChecked(boolean check) {
		this.isChecked = check;
	}
	
	public void loadLocations(List<Location> nodeList) {		
		for(Location location : nodeList) {
			if(location.getLocationID().equalsIgnoreCase(locationID) || "0".equalsIgnoreCase(location.getLocationID())) {
				continue;
			}
						
			Location neightborLocation = new Location(location.getLocationID(),location.getX(), location.getY(), location.getDemand(),location.getBoxes());
			double distance = Math.sqrt(Math.pow(getX() - neightborLocation.getX(), 2) + Math.pow(getY() - neightborLocation.getY(),2));
			neightborLocation.setDistance(distance);
			neightborLocation.setBoxes(location.getBoxes());
			
			this.locationList.add(neightborLocation);
		}
		
		//sort location ascending by distance.
		this.locationList.sort(new LocationComparator());
	}
	

	private double x;
	private double y;
	private double distance;
	private String locationID;
	private boolean isVisited;
	private boolean isChecked;
	private int demand;
	private List<Location> locationList;
	private List<Box> boxes;
	
}
