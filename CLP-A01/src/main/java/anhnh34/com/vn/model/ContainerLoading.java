package anhnh34.com.vn.model;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;

public class ContainerLoading {

	final static Logger logger = Logger.getLogger(ContainerLoading.class);

	public static Logger getLogger() {
		return logger;
	}

	public void setProblem(Problem problem) {
		this.problem = problem;
	}

	/**
	 * 
	 */
	public ContainerLoading() {
		init();
	}

	private void init() {
		this.locationList = new ArrayList<>();
		this.containerList = new ArrayList<>();
		this.notPlacedBox = new Batch();
		this.placedBox = new Batch();
		this.container = new Container();
		this.solution = new Solution();
		this.problem = new Problem();
	}

	public Container getContainer() {
		return container;
	}

	public Solution getSolution() {
		return solution;
	}

	public void setSolution(Solution solution) {
		this.solution = solution;
	}

	public Batch getPlacedBox() {
		return placedBox;
	}

	public void setPlacedBox(Batch placedBox) {
		this.placedBox = placedBox;
	}

	public Batch getNotPlacedBox() {
		return notPlacedBox;
	}

	public void setNotPlacedBox(Batch notPlacedBox) {
		this.notPlacedBox = notPlacedBox;
	}

	public void setContainer(Container container) {
		this.container = container;

	}

	public void insertBoxInFoundSpace() {

	}

	public Problem getProblem() {
		return problem;
	}

	// Find available space.
	public Space getAvaiableSpace() {
		return this.container.getAvaiableSpaces() == null ? null : this.container.getAvaiableSpaces().get(0);
	}

	public void loadingData() throws IOException {

		Properties prop = new Properties();

		prop.load(this.getClass().getClassLoader().getResourceAsStream("config.properties"));

		String dataPath = prop.getProperty("data_path");

		// load properties file
		Path filePath = FileSystems.getDefault().getPath(dataPath);

		if (filePath == null) {
			System.out.println("Couldn't find the specific path");
			return;
		}

		List<String> fileArray = Files.readAllLines(filePath);

		int index = 0;
		for (String line : fileArray) {
			if (line == null || line.isEmpty()) {
				index++;
				continue;
			}

			line = line.trim();

			// Get instance name.
			if (index == 0) {
				if (line.contains("Instance")) {
					String name = line.substring(line.indexOf(""), line.length());
					problem.setName(name);
				}
			}

			// Get number of customer.
			if (index == 2) {
				if (line.contains("number of customers")) {
					String numCus = line.substring(0, line.indexOf('-')).trim();
					int numberOfCustomer = Integer.parseInt(numCus);
					problem.setNumOfCustomer(numberOfCustomer);
				}
			}

			// get number of vehicles
			if (index == 3) {
				problem.setNumOfVehicle(Integer.parseInt(line.substring(0, line.indexOf('-')).trim()));
			}

			 // get number of items
			 if (index == 4) {
			  problem.setNumOfItem(Integer.parseInt(line.substring(0,
			  line.indexOf('-')).trim()));
			 }

			if (line.contains("Node - number of items")) {
				problem.setItemsList(this.loadItems(index, fileArray));
			}
			index++;
		}

		// Sort boxes
		this.getNotPlacedBox().getBoxes().sort(new BoxComparator());

		// setup box type.
		this.loadBoxType();

		// calculate number of items of a customer.
		this.calculateNumberOfItem();

		// load containers
		this.loadContainers(fileArray);

		// read node list information
		this.readNodeInfo(fileArray);
		
		//read all location info
		this.loadLocations();
	}

	private void loadContainers(List<String> fileArray) {
		// get container info
		String line = fileArray.get(6);
		problem.setContainerInfo(line);
		this.loadContainerInfo(line);
	}

	private void calculateNumberOfItem() {

		List<Box> boxList = new ArrayList<Box>();
		for (int i = 0; i < this.getNotPlacedBox().getBoxes().size() - 1; i++) {
			int numOfItem = 1;
			Box box = this.getNotPlacedBox().getBoxes().get(i);
			if (box.getNumberOfItem() > 1) {
				continue;
			}

			boxList.add(box);

			for (int j = i + 1; j < this.getNotPlacedBox().getBoxes().size(); j++) {
				Box boxJ = this.getNotPlacedBox().getBoxes().get(j);
				if (box.getCustomerId() == boxJ.getCustomerId()) {
					numOfItem++;
					boxList.add(boxJ);
				}
			}

			for (Box r : boxList) {
				r.setNumberOfItem(numOfItem);
			}
		}
	}

	private List<String> loadItems(int index, List<String> itemList) {
		// get all items in this class

		List<String> subItemList = itemList.subList(index + 1, itemList.size());
		for (String item : subItemList) {
			item = item.trim();

			// check if item not actived, skip read current line.
			if (!item.contains("x")) {
				// continue;
			}

			String[] itemArray = item.split("\\s+");
			this.createItem(itemArray);
		}

		return subItemList;
	}

	private void createItem(String[] itemArray) {
		int numbItem = Integer.parseInt(itemArray[1]);
		if (numbItem == 0)
			return;

		//problem.setNumOfItem(problem.getNumOfItem() + numbItem);

		int index = 1;
		String customerId = itemArray[0];

		for (int i = 0; i < numbItem; i++) {
			double heigh = Long.parseLong(itemArray[++index]);
			double width = Integer.parseInt(itemArray[++index]);
			double length = Integer.parseInt(itemArray[++index]);
			int fragility = Integer.parseInt(itemArray[++index]);
			boolean isFragility = fragility == 1 ? true : false;

			// Create new box and then add to not placed box
			Box box = new Box(notPlacedBox.getBoxes().size() + 1, length, width, heigh);
			box.setFragile(isFragility);
			box.setCustomerId(customerId);
			notPlacedBox.getBoxes().add(box);
		}
	}

	public List<Space> getAllSpaces() {
		return this.container.getAvaiableSpaces();
	}

	private void loadContainerInfo(String line) {
		int index = 0;
		String[] conInfoString = line.trim().split("\\s+");
		int capacity = Integer.parseInt(conInfoString[index]);
		double height = Double.parseDouble(conInfoString[++index]);
		double width = Double.parseDouble(conInfoString[++index]);
		double length = Double.parseDouble(conInfoString[++index]);
		container.setCapacity(capacity);
		container.setHeight(height);
		container.setWidth(width);
		container.setLength(length);
		container.initiliaze(this);
		// container.loadingSpace();

		for (int i = 0; i < problem.getNumOfVehicle(); i++) {
			Container con = new Container();

			con.setCapacity(capacity);
			con.setHeight(height);
			con.setWidth(width);
			con.setLength(length);
			con.initiliaze(this);
			// con.loadingSpace(); // setup default space;
			this.containerList.add(con);
		}
	}

	private void loadBoxType() {
		List<Box> boxList = this.getNotPlacedBox().getBoxes();

		for (int i = 0; i <= boxList.size() - 1; i++) {
			Box boxI = boxList.get(i);

			if (boxI.getBoxType() == null || boxI.getBoxType().isEmpty()) {
				boxI.setBoxType(String.valueOf(i));
				for (int j = i + 1; j < boxList.size(); j++) {
					Box boxJ = boxList.get(j);

					if (boxI.equals(boxJ)) {
						if (boxJ.getBoxType() == null || boxJ.getBoxType().isEmpty()) {
							boxJ.setBoxType(boxI.getBoxType());
						}
					}
				}
			}
		}
	}

	private void readNodeInfo(List<String> fileContent) {
		int startIndex = 8;

		while (startIndex < fileContent.size()) {
			String lineData = fileContent.get(startIndex);
			if (lineData.contains("Node - number of items")) {
				break;
			}

			lineData = lineData.trim();
			String[] lineItems = splitUpLine(lineData);
			Node node = new Node(lineItems[0], lineItems[1], lineItems[2], lineItems[3]);

			if ("0".equalsIgnoreCase(lineItems[0])) {
				this.setDeport(node);
			}
			this.getContainer().addNode(node);

			startIndex++;
		}
	}

	private String[] splitUpLine(String data) {
		String[] dataArrays = data.split("\\s+");
		return dataArrays;
	}

	// private int getSequenceNumber(String nodeId) {
	// for (Node node : this.getContainer().getNodeList()) {
	// if (node.getId().compareToIgnoreCase(nodeId.trim()) == 0) {
	// return node.getDemand();
	// }
	// }
	// return 0;
	// }

	private void loadLocations() {
		for (Node node : this.getContainer().getNodeList()) {
			Location location = new Location();
			location.setLocationID(node.getId());
			location.setX(node.getxAxis());
			location.setY(node.getyAxis());
			location.setDemand(node.getDemand());						
			this.loadBoxByLocation(location);
			this.getLocationList().add(location);
		}
		
		for(Location location : this.getLocationList()) {
			location.loadLocations(this.getLocationList());
		}
	}

	private void loadBoxByLocation(Location location) {
		for (Box box : this.getNotPlacedBox().getBoxes()) {
			if (box.getCustomerId().equalsIgnoreCase(location.getLocationID())) {
				location.addBox(box);
			}
		}
	}

	public List<Container> getContainerList() {
		return containerList;
	}

	public void setContainerList(List<Container> containerList) {
		this.containerList = containerList;
	}

	public void setDeport(Node deport) {
		this.deport = deport;
	}

	public Node getDeport() {
		return deport;
	}

	public List<Location> getLocationList() {
		return locationList;
	}

	public void setLocationList(List<Location> locationList) {
		this.locationList = locationList;
	}

	private Batch placedBox;
	private Batch notPlacedBox;
	private Container container;
	private Solution solution;
	private Problem problem;
	private Node deport;

	private List<Container> containerList;
	private List<Location> locationList;

}