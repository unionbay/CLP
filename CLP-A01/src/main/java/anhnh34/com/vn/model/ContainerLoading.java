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
	private Batch placedBox;
	private Batch notPlacedBox;
	private Container container;
	private Solution solution;
	private Problem problem;

	final static Logger logger = Logger.getLogger(ContainerLoading.class);

	/**
	 * 
	 */
	public ContainerLoading() {
		init();
	}

	private void init() {
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

		// read node list information
		this.readNodeInfo(fileArray);

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
				// problem.setNumOfItem(Integer.parseInt(line.substring(0,
				// line.indexOf('-')).trim()));
			}

			// get container info
			if (index == 6) {
				problem.setContainerInfo(line);
				this.loadContainerInfo(line);
			}

			if (line.contains("Node - number of items")) {
				problem.setItemsList(this.loadItems(index, fileArray));

				// sort all boxes base on their volume
				this.getNotPlacedBox().getBoxes().sort(new Comparator<Box>() {

					@Override
					public int compare(Box arg0, Box arg1) {
						double arg0Volume = arg0.getLength() * arg0.getWidth() * arg0.getHeight();
						double arg1Volume = arg1.getLength() * arg1.getWidth() * arg1.getHeight();
						
						//order by sequence number.
//						if (arg0.getSequenceNumber() > arg1.getSequenceNumber()) {
//							return -1;
//						}
//
//						if (arg0.getSequenceNumber() < arg1.getSequenceNumber()) {
//							return 1;
//						}
						
					
						
						//order by  largest surface.
//						if(arg1.getLargestSurface() >= arg0.getLargestSurface()) {
//							return 1;							
//						}
//						
//						if(arg1.getLargestSurface() < arg0.getLargestSurface()) {
//							return -1;
//						}
													
						if (arg1Volume > arg0Volume) {
							return 1;
						} else if (arg1Volume < arg0Volume) {
							return -1;
						} 
						
						//order by largest dimension.
						if(arg1.getBiggestDimension() > arg0.getBiggestDimension()) {
							return 1;							
						}
					
						if(arg1.getBiggestDimension() < arg0.getBiggestDimension()) {
							return -1;
						}
						
						return 0;
					}
				});
				this.loadBoxType();
			}
			index++;
		}
	}

	private List<String> loadItems(int index, List<String> itemList) {
		// get all items in this class

		List<String> subItemList = itemList.subList(index + 1, itemList.size());
		for (String item : subItemList) {
			item = item.trim();
		
			//check if item not  actived, skip read current line.
			if( ! item.contains("x")) {
				continue;
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

		problem.setNumOfItem(problem.getNumOfItem() + numbItem);

		int index = 1;
		String nodeId = itemArray[0];
		int sequenceNumber = getSequenceNumber(nodeId);
		for (int i = 0; i < numbItem; i++) {
			double heigh = Long.parseLong(itemArray[++index]);
			double width = Integer.parseInt(itemArray[++index]);
			double length = Integer.parseInt(itemArray[++index]);
			int fragility = Integer.parseInt(itemArray[++index]);
			boolean isFragility = fragility == 1 ? true : false;

			// Create new box and then add to not placed box
			Box box = new Box(length, width, heigh);
			box.setFragile(isFragility);
			box.setSequenceNumber(sequenceNumber);
			notPlacedBox.getBoxes().add(box);
		}
	}

	public List<Space> getAllSpaces() {
		return this.container.getAvaiableSpaces();
	}

	private void addEmptySpace(Space s) {
		if (s.isValid()) {
			this.container.getAvaiableSpaces().add(s);
		}
	}

	private void loadContainerInfo(String line) {
		int index = 0;
		String[] conInfoString = line.trim().split("\\s+");
		container.setCapacity(Integer.parseInt(conInfoString[index]));
		container.setHeight(Double.parseDouble(conInfoString[++index]));
		container.setWidth(Double.parseDouble(conInfoString[++index]));
		container.setLength(Double.parseDouble(conInfoString[++index]));		
		
		// setup default space;
		container.loadingSpace();
	}

	public void update(FeasibleObject feasObj) {

		// get Feasible Object
		Box selectedBox = feasObj.getBox();
		Space selectedSpace = feasObj.getSpace();
		String selectedRotation = feasObj.getSelectedRotation();

		Dimension maximumDimension = getMaximumDimension(selectedRotation, selectedSpace.getMinimum(), selectedBox);
		// selectedBox.setMinimum(selectedSpace.getMinimum());
		// selectedBox.setMaximum(maximumDimension);

		feasObj.getBox().setMaximum(maximumDimension);

		logger.info("founded feasible Object Info: ");
		this.showCuboidInfo("Founded space", selectedSpace);
		this.showCuboidInfo("Founded box", selectedBox);
		notPlacedBox.getBoxes().remove(selectedBox);
		placedBox.getBoxes().add(selectedBox);
	}

	public void updateBoxPosition(Box selectedBox, Space space, String rotation) {
		Dimension maximumDimension = getMaximumDimension(rotation, space.getMinimum(), selectedBox);
		selectedBox.setMinimum(space.getMinimum());
		selectedBox.setMaximum(maximumDimension);
	}

	public void updateSpaces(FeasibleObject obj) {
		logger.info("---Start method update Spaces---");

		Box selectedBox = obj.getBox();
		Space selectedSpace = obj.getSpace();

		Dimension boxMinimum = selectedBox.getMinimum();
		Dimension boxMaximum = selectedBox.getMaximum();

		Dimension spaceMinimum = selectedSpace.getMinimum();
		Dimension spaceMaximum = selectedSpace.getMaximum();

		// create new front space
		Dimension frontSpaceMinimum = new Dimension(selectedBox.getMaximum().getX(), selectedSpace.getMinimum().getY(),
				selectedSpace.getMinimum().getZ());

		Dimension frontSpaceMaximum = selectedSpace.getMaximum();
		Space frontSpace = new Space(frontSpaceMinimum, frontSpaceMaximum);

		// create new right space
		Dimension rightSpaceMinimum = new Dimension(boxMinimum.getX(), boxMaximum.getY(), boxMinimum.getZ());
		Dimension rightSpaceMaximum = selectedSpace.getMaximum();
		Space rightSpace = new Space(rightSpaceMinimum, rightSpaceMaximum);

		// create above space.
		Dimension aboveSpaceMinimum = new Dimension(spaceMinimum.getX(), spaceMinimum.getY(), boxMaximum.getZ());
		Dimension aboveSpaceMaximum = new Dimension(boxMaximum.getX(), boxMaximum.getY(), spaceMaximum.getZ());
		Space aboveSpace = new Space(aboveSpaceMinimum, aboveSpaceMaximum);

		// remove old space
		this.getAllSpaces().remove(selectedSpace);

		// add all new generate spaces.
		this.addEmptySpace(frontSpace);
		this.addEmptySpace(rightSpace);
		this.addEmptySpace(aboveSpace);

		// this.showSpaceInfo();

		// remove box overlapping.
		this.removeBoxWithOverlap(selectedBox);

		// Amalgamation
		this.amalgamation();

		// removal too small spaces
		this.removeSmallSpaces();

		logger.info("=====Finish update spaces======");
		logger.info("\n");
		// this.showSpaceInfo();
	}

	private void amalgamation() {
		List<Space> helperList = new ArrayList<>();
		for (int i = 0; i < this.getAllSpaces().size() - 1; i++) {
			Space s = this.getAllSpaces().get(i);
			for (int j = i + 1; j < this.getAllSpaces().size(); j++) {
				Space t = this.getAllSpaces().get(j);
				Space amalgateSpace = this.amalgamate(s, t);
				if (amalgateSpace != null) {
					helperList.add(amalgateSpace);
				}
			}
		}

		if (helperList != null) {
			for (Space amalagateSpace : helperList) {
				this.addEmptySpace(amalagateSpace);
			}
		}

		this.removeSubsets();
	}

	private Dimension getMaximumDimension(String selectedRotation, Dimension minimum, Box selectedBox) {
		Dimension maximumDimension = null;
		switch (selectedRotation) {
		case Rotation.XYZ:
			maximumDimension = minimum.addDimension(new Dimension(selectedBox.getBiggestDimension(),
					selectedBox.getMiddleDimension(), selectedBox.getSmallestDimension()));
			break;
		case Rotation.XZY:
			maximumDimension = minimum.addDimension(new Dimension(selectedBox.getBiggestDimension(),
					selectedBox.getSmallestDimension(), selectedBox.getMiddleDimension()));
			break;
		case Rotation.YXZ:
			maximumDimension = minimum.addDimension(new Dimension(selectedBox.getMiddleDimension(),
					selectedBox.getBiggestDimension(), selectedBox.getSmallestDimension()));
			break;
		case Rotation.YZX:
			maximumDimension = minimum.addDimension(new Dimension(selectedBox.getSmallestDimension(),
					selectedBox.getBiggestDimension(), selectedBox.getMiddleDimension()));
			break;
		case Rotation.ZXY:
			maximumDimension = minimum.addDimension(new Dimension(selectedBox.getMiddleDimension(),
					selectedBox.getSmallestDimension(), selectedBox.getBiggestDimension()));
			break;
		case Rotation.ZYX:
			maximumDimension = minimum.addDimension(new Dimension(selectedBox.getSmallestDimension(),
					selectedBox.getMiddleDimension(), selectedBox.getBiggestDimension()));
			break;

		}
		return maximumDimension;
	}

	private boolean checkOverlapping(Cuboid p, Cuboid q) {
		if (p.getMinimumPoint().getX() < q.getMaximumPoint().getX()
				&& q.getMinimumPoint().getX() < p.getMaximumPoint().getX()
				&& p.getMinimumPoint().getY() < q.getMaximumPoint().getY()
				&& q.getMinimumPoint().getY() < p.getMaximumPoint().getY()
				&& p.getMinimumPoint().getZ() < q.getMaximumPoint().getZ()
				&& q.getMinimumPoint().getZ() < p.getMaximumPoint().getZ()) {
			return true;
		}
		return false;
	}

	private void removeBoxWithOverlap(Box box) {
		logger.info("Start remove box with overlap");
		List<Space> helperList = new ArrayList<>();
		List<Space> removeableList = new ArrayList<Space>();

		for (int i = 0; i < this.getAllSpaces().size(); i++) {
			Space selectedSpace = this.getAllSpaces().get(i);
			if (checkOverlapping(box, selectedSpace)) {
				this.showCuboidInfo(selectedSpace);
				removeableList.add(selectedSpace);
				helperList.addAll(this.updateOverlapSpaces(box, selectedSpace));

			}
		}

		// remove invaid space.
		for (Space space : removeableList) {
			this.getAllSpaces().remove(space);
		}

		// add new spaces to current space.
		if (helperList != null && !helperList.isEmpty()) {
			this.getAllSpaces().addAll(helperList);
			this.removeSubsets();
		}

		logger.info("Stop remove box with overlap");
	}

	private List<Space> updateOverlapSpaces(Box box, Space space) {
		logger.info("===updateOverlapSpaces===");
		List<Space> result = new ArrayList<>();
		Dimension minBoxDimension = box.getMinimum();
		Dimension maxBoxDimension = box.getMaximum();

		Dimension minSpaceDimension = space.getMinimum();
		Dimension maxSpaceDimension = space.getMaximum();

		// create behind space.
		Dimension behindMinimumDimension = minSpaceDimension;
		Dimension behindMaximumDimension = new Dimension(minBoxDimension.getX(), maxSpaceDimension.getY(),
				maxSpaceDimension.getZ());
		Space behindSpace = new Space(behindMinimumDimension, behindMaximumDimension);

		// create front space.
		Dimension frontMinimumDimension = new Dimension(maxBoxDimension.getX(), minSpaceDimension.getY(),
				minSpaceDimension.getZ());

		Dimension frontMaximumDimension = maxSpaceDimension;
		Space frontSpace = new Space(frontMinimumDimension, frontMaximumDimension);

		// create left space.
		Dimension leftMinimumDimension = minSpaceDimension;
		Dimension leftMaximumDimension = new Dimension(maxSpaceDimension.getX(), minBoxDimension.getY(),
				maxSpaceDimension.getZ());
		Space rightSpace = new Space(leftMinimumDimension, leftMaximumDimension);

		// create right space.
		Dimension rightMinimumDimension = new Dimension(minSpaceDimension.getX(), maxBoxDimension.getY(),
				minSpaceDimension.getZ());
		Dimension rightMaximumDimension = maxSpaceDimension;
		Space leftSpace = new Space(rightMinimumDimension, rightMaximumDimension);

		if (behindSpace.isValid()) {
			result.add(behindSpace);
			logger.info("behind space");
			showCuboidInfo(behindSpace);
		}

		if (frontSpace.isValid()) {
			result.add(frontSpace);
			logger.info("front space");
			showCuboidInfo(frontSpace);
		}

		if (leftSpace.isValid()) {
			logger.info("left space");
			showCuboidInfo(leftSpace);
			result.add(leftSpace);
		}

		if (rightSpace.isValid()) {
			logger.info("right space");
			result.add(rightSpace);
		}

		logger.info("===End Update Overlap Space===");
		logger.info("\n\n");
		return result;

	}

	private void addSpace(Space space) {
		if (space.isValid()) {
			this.getAllSpaces().add(space);
		}
	}

	private void removeSubsets() {
		logger.info("\n");
		logger.info("===Method: removeSubsets (Start)===");

		List<Space> helperList = new ArrayList<>(this.getAllSpaces());
		for (int i = 0; i < helperList.size() - 1; i++) {
			Space s = helperList.get(i);
			for (int j = i + 1; j < helperList.size(); j++) {

				// check if space s is a subset of space t
				Space t = helperList.get(j);
				if (checkSubsetSpace(s, t)) {
					this.showCuboidInfo("Space S info", s);
					this.showCuboidInfo("Space T info", t);
					helperList.remove(i);
					break;
				}

				// check if space t is a subset of space s
				if (checkSubsetSpace(t, s)) {
					this.showCuboidInfo("Space T info ", t);
					this.showCuboidInfo("Space S info", s);
					helperList.remove(j);
				}
			}
		}
		this.container.setAvaiableSpaces(helperList);
		logger.info("===Method: removeSubsets (End) ===");
	}

	private boolean checkSubsetSpace(Space s, Space t) {
		if ((s.getMinimum().compare(t.getMinimum()) == 1 || s.getMinimum().compare(t.getMinimum()) == 0)
				&& (t.getMaximum().compare(s.getMaximum()) == 1 || t.getMaximum().compare(s.getMaximum()) == 0)) {
			return true;
		}
		return false;
	}

	private Space amalgamate(Space s, Space t) {

		if ((s.getMinimum().getZ() != t.getMinimum().getZ())) {
			return null;
		}

		Space result = null;

		// Amalgamate in x direction
		// check overlap by x
		if (t.getMinimum().getY() < s.getMaximum().getY() && s.getMinimum().getY() < t.getMaximum().getY()
				&& (s.getMinimum().getX() == t.getMaximum().getX() || t.getMinimum().getX() == s.getMaximum().getX())) {
			/* create X's amalgamate space. */

			// Caculate min dimension
			double minAmalgamateX = s.getMinimum().getX() < t.getMinimum().getX() ? s.getMinimum().getX()
					: t.getMinimum().getX();

			double minAmalgamateY = s.getMinimum().getY() > t.getMinimum().getY() ? s.getMinimum().getY()
					: t.getMinimum().getY();

			double minAmalgamateZ = s.getMinimum().getZ();

			Dimension minUDimension = new Dimension(minAmalgamateX, minAmalgamateY, minAmalgamateZ);

			// Caculate max dimension
			double maxAmalgamateX = s.getMaximum().getX() > t.getMaximum().getX() ? s.getMaximum().getX()
					: t.getMaximum().getX();

			double maxAmalgamateY = s.getMaximum().getY() < t.getMaximum().getY() ? s.getMaximum().getY()
					: t.getMaximum().getY();

			double maxAmalgamateZ = s.getMaximum().getZ();

			Dimension maxUDimension = new Dimension(maxAmalgamateX, maxAmalgamateY, maxAmalgamateZ);

			result = new Space(minUDimension, maxUDimension);
			// this.getAllSpaces().add(xAmalgamateSpace);
			logger.info("Amalgamate folow x direction");
			showCuboidInfo(s);
			showCuboidInfo(t);
			this.showCuboidInfo(result);
			logger.info("\n");
		}

		// Amalgamate in y direction
		if (s.getMinimum().getX() < t.getMaximum().getX() && t.getMinimum().getX() < s.getMaximum().getX()
				&& (s.getMinimum().getY() == t.getMaximum().getY() || s.getMaximum().getY() == t.getMinimum().getY())) {

			// Create Y's amalgamate space.

			// Caculate min dimension
			double minYuX = s.getMinimum().getX() > t.getMinimum().getX() ? s.getMinimum().getX()
					: t.getMinimum().getX();

			double minYuY = s.getMinimum().getY() < t.getMinimum().getY() ? s.getMinimum().getY()
					: t.getMinimum().getY();

			double minYuZ = s.getMinimum().getZ();

			Dimension minYuDimension = new Dimension(minYuX, minYuY, minYuZ);

			double maxYuX = s.getMaximum().getX() < t.getMaximum().getX() ? s.getMaximum().getX()
					: t.getMaximum().getX();

			double maxYuY = s.getMaximum().getY() > t.getMaximum().getY() ? s.getMaximum().getY()
					: t.getMaximum().getY();

			double maxYuZ = s.getMaximum().getZ();

			Dimension maxYuDimension = new Dimension(maxYuX, maxYuY, maxYuZ);

			result = new Space(minYuDimension, maxYuDimension);
			this.getAllSpaces().add(result);
			logger.info("Amalgamate folow y direction");
			this.showCuboidInfo(s);
			this.showCuboidInfo(t);
			this.showCuboidInfo(result);
			logger.info("\n");

		}

		return result;

	}

	private void removeSmallSpaces() {
		// logger.info("Start method remove small spaces");
		double smallestDimension = this.getSmallestDimension();
		List<Space> removeAbleSpace = new ArrayList<Space>();

		for (int i = 0; i < this.getAllSpaces().size(); i++) {
			Space s = this.getAllSpaces().get(i);
			if (s.getHeight() < smallestDimension) {
				removeAbleSpace.add(s);
				continue;
			}

			if (s.getWidth() < smallestDimension || s.getLength() < smallestDimension) {
				// check if exist space t.
				boolean fExist = false;
				for (int j = 0; j < this.getAllSpaces().size(); j++) {
					if (i == j)
						continue;
					Space t = this.getAllSpaces().get(j);
					if (s.getMinimum().getX() <= t.getMaximum().getX() && t.getMinimum().getX() <= t.getMaximum().getX()
							&& s.getMinimum().getY() <= t.getMaximum().getY()
							&& t.getMinimum().getY() <= s.getMaximum().getY()
							&& s.getMinimum().getZ() >= t.getMinimum().getZ()) {
						fExist = true;
					}

					// selected s space can be delete if exist's flag equal
					// false.
					if (fExist == false) {
						removeAbleSpace.add(s);
					}
				}
			}
		}

		// removal of too small spaces
		this.getAllSpaces().removeAll(removeAbleSpace);
		// logger.info("End method remove small spaces");
	}

	private double getSmallestDimension() {
		double smallestDimension = 0;

		if (this.getNotPlacedBox().getBoxes().size() > 0) {
			smallestDimension = this.getNotPlacedBox().getBoxes().get(0).getSmallestDimension();
		}

		for (Box box : this.getNotPlacedBox().getBoxes()) {
			smallestDimension = smallestDimension > box.getSmallestDimension() ? box.getSmallestDimension()
					: smallestDimension;
		}

		return smallestDimension;
	}

	private void loadBoxType() {
		List<Box> boxList = this.getNotPlacedBox().getBoxes();

		for (int i = 0; i < boxList.size() - 1; i++) {
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

	public void showSpaceInfo() {
		for (Space s : this.getAllSpaces()) {
			this.showCuboidInfo("", s);
		}
	}

	private void showSpaceInfo(List<Space> spacesList) {
		for (Space s : spacesList) {
			logger.info(String.format("size: %f %f %f || coordinate: %f %f %f, %f %f %f",
					new Object[] { s.getLength(), s.getWidth(), s.getHeight(), s.getMinimum().getX(),
							s.getMinimum().getY(), s.getMinimum().getZ(), s.getMaximum().getX(), s.getMaximum().getY(),
							s.getMaximum().getZ() }));
		}
	}

	private void showCuboidInfo(Cuboid c) {
		logger.info(String.format("%f %f %f, %f %f %f",
				new Object[] { c.getMinimumPoint().getX(), c.getMinimumPoint().getY(), c.getMinimumPoint().getZ(),
						c.getMaximumPoint().getX(), c.getMaximumPoint().getY(), c.getMaximumPoint().getZ() }));
	}

	public void showCuboidInfo(String name, Cuboid c) {
		if (c.getMinimumPoint() == null && c.getMaximumPoint() == null) {
			logger.info(String.format("%s length: %.2f, width: %.2f, height: %.2f min: null  max: null",
					new Object[] { name, c.getLength(), c.getWidth(), c.getHeigth() }));
			return;
		}

		if (c.getMinimumPoint() == null) {
			logger.info(String.format("%s length: %.2f, width: %.2f, height: %.2f min: null max(%.2f, %.2f, %.2f)",
					new Object[] { name, c.getLength(), c.getWidth(), c.getHeigth(), c.getMaximumPoint().getX(),
							c.getMaximumPoint().getY(), c.getMaximumPoint().getZ() }));
			return;
		}

		if (c.getMaximumPoint() == null) {
			logger.info(String.format("%s length: %.2f, width: %.2f, height: %.2f min(%.2f, %.2f, %.2f) max: null",
					new Object[] { name, c.getLength(), c.getWidth(), c.getHeigth(), c.getMinimumPoint().getX(),
							c.getMinimumPoint().getY(), c.getMinimumPoint().getZ() }));
			return;
		}

		logger.info(
				String.format("%s length: %.2f, width: %.2f, height: %.2f min(%.2f, %.2f, %.2f) max(%.2f, %.2f, %.2f)",
						new Object[] { name == null ? "" : name, c.getLength(), c.getWidth(), c.getHeigth(),
								c.getMinimumPoint().getX(), c.getMinimumPoint().getY(), c.getMinimumPoint().getZ(),
								c.getMaximumPoint().getX(), c.getMaximumPoint().getY(), c.getMaximumPoint().getZ() }));
	}

	private void readNodeInfo(List<String> fileContent) {
		int startIndex = 9;

		while (startIndex < fileContent.size()) {
			String lineData = fileContent.get(startIndex);
			if (lineData.contains("Node - number of items")) {
				break;
			}
			lineData = lineData.trim();
			String[] lineItems = splitUpLine(lineData);
			Node node = new Node(lineItems[0], lineItems[1], lineItems[2], lineItems[3]);
			this.getContainer().addNode(node);
			startIndex++;
		}
	}

	private String[] splitUpLine(String data) {
		String[] dataArrays = data.split("\\s+");
		return dataArrays;
	}

	private int getSequenceNumber(String nodeId) {
		for (Node node : this.getContainer().getNodeList()) {
			if (node.getId().compareToIgnoreCase(nodeId.trim()) == 0) {
				return node.getDemand();
			}
		}
		return 0;
	}

}