package anhnh34.com.vn.model;

import java.util.ArrayList;
import java.util.List;

public class Problem {
	private String name;
	private String zClass;
	private int numOfCustomer;
	private int numOfVehicle;
	private int numOfItem;
	private String containerInfo;
	private List<Container> containerList;
	private List<Location> locationList;
	private Batch placedBox;
	private Batch notPlacedBox;
	
	List<String> itemsList;

	public String getName() {
		return name;
	}

	public String getzClass() {
		return zClass;
	}

	public int getNumOfCustomer() {
		return numOfCustomer;
	}

	public int getNumOfVehicle() {
		return numOfVehicle;
	}

	public int getNumOfItem() {
		return numOfItem;
	}

	public String getContainerInfo() {
		return containerInfo;
	}

	public List<String> getItemsList() {
		return itemsList;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setzClass(String zClass) {
		this.zClass = zClass;
	}

	public void setNumOfCustomer(int numOfCustomer) {
		this.numOfCustomer = numOfCustomer;
	}

	public void setNumOfVehicle(int numOfVehicle) {
		this.numOfVehicle = numOfVehicle;
	}

	public void setNumOfItem(int numOfItem) {
		this.numOfItem = numOfItem;
	}

	public void setContainerInfo(String containerInfo) {
		this.containerInfo = containerInfo;
	}

	public void setItemsList(List<String> itemsList) {
		this.itemsList = itemsList;
	}
	
	public void addContainer(Container con) {
		Container cloneContainer = new Container(con);
		this.containerList.add(cloneContainer);
	}
	
	public Batch getNotPlacedBox() {
		return notPlacedBox;
	}
	
	
	public List<Location> getLocationList() {
		return locationList;
	}
	
	public List<Container> getContainerList() {
		return containerList;
	}
	
	
	
	
	public void setContainerList(List<Container> containerList) {
		this.containerList.clear();
		for(Container con :containerList) {
			this.addContainer(con);
		}
	}
	
	public void addLocation(Location location) {
		Location cloneLocation = new  Location(location);
		this.locationList.add(cloneLocation);
	}

	/**
	 * @param name
	 * @param zClass
	 * @param numOfCustomer
	 * @param numOfVehicle
	 * @param numOfItem
	 * @param containerInfo
	 * @param itemsList
	 */
	public Problem(String name, String zClass, int numOfCustomer, int numOfVehicle, int numOfItem, String containerInfo,
			List<String> itemsList) {
		super();
		this.name = name;
		this.zClass = zClass;
		this.numOfCustomer = numOfCustomer;
		this.numOfVehicle = numOfVehicle;
		this.numOfItem = numOfItem;
		this.containerInfo = containerInfo;
		this.itemsList = itemsList;
	}

	/**
	 * 
	 */
	public Problem() {
		super();
		this.containerList = new ArrayList<>();
		this.placedBox = new Batch();
		this.notPlacedBox = new Batch();
		this.locationList = new ArrayList<>();	
		this.containerList = new ArrayList<>();								
	}

}
