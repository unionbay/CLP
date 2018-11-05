package anhnh34.com.vn.model;

import java.util.List;

public class Customer {

	public Customer() {

	}

	public Customer(String customerID, Location currentLocation, List<Location> locationList) {
		super();
		this.customerID = customerID;
		this.currentLocation = currentLocation;
		this.locationList = locationList;
	}

//	public void loadLocations(List<Node> nodes) {
//		for (Node node : nodes) {
//			if(node.getId().equalsIgnoreCase(this.customerID)) {
//				continue;
//			}			
//			Location location = new Location(node.getId(),node.getxAxis(), node.getyAxis());			
//			double distance = Math.sqrt(Math.pow(location.getX() - currentLocation.getX(), 2)
//					+ Math.pow(location.getY() - currentLocation.getY(), 2));
//			location.setDistance(distance);
//		}
//		
//		//sort all of locations by distance asc.
//		this.locationList.sort(new LocationComparator());
//		
//	}

		
	public String getCustomerID() {
		return customerID;
	}

	public Location getCurrentLocation() {
		return currentLocation;
	}

	public List<Location> getLocationList() {
		return locationList;
	}

	public void setCustomerID(String customerID) {
		this.customerID = customerID;
	}

	public void setCurrentLocation(Location currentLocation) {
		this.currentLocation = currentLocation;
	}

	public void setLocationList(List<Location> locationList) {
		this.locationList = locationList;
	}	

	public int getDemand() {
		return demand;
	}

	public void setDemand(int demand) {
		this.demand = demand;
	}



	private String customerID;
	private Location currentLocation;
	private int demand;
	private List<Location> locationList;
}
