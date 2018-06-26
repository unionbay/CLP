package anhnh34.com.vn.model;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

public class Greedy {
	final static Logger logger = Logger.getLogger(Greedy.class);
	private List<BoxCandidate> candidates;
	private Batch placedBoxes;
	private Batch notPlacedBoxes;
	private ContainerLoading containerLoading;
	private String selectedAlgorithm;
	private List<Space> avaiableSpaces;
	private double defaultRatioSupport;
	private Container container;

	public static final String ST_ALGORITHM = "ST";
	public static final String VL_ALGORITHM = "AL";
	public static final String EL_ALGORITHM = "EL";
	public static final double NOT_SUPPORT_RATIO= 0.25;

	public void setContainer(Container container) {
		this.container = container;
	}

	public Container getContainer() {
		return container;
	}

	public List<Space> getAvaiableSpaces() {
		return avaiableSpaces;
	}

	public Batch getNotPlacedBoxes() {
		return notPlacedBoxes;
	}

	public void setNotPlacedBoxes(Batch notPlacedBoxes) {
		this.notPlacedBoxes = notPlacedBoxes;
	}

	public void setDefaultRatioSupport(double defaultRatioSupport) {
		this.defaultRatioSupport = defaultRatioSupport;
	}

	public void setNSupportRatio(double nSupportRatio) {
		this.defaultRatioSupport = nSupportRatio;
	}

	public double getNSupportRatio() {
		return defaultRatioSupport;
	}

	public Batch getPlacedBoxes() {
		return placedBoxes;
	}

	public void setPlacedBoxes(Batch placedBoxes) {
		this.placedBoxes = placedBoxes;
	}

	public void setAvaiableSpaces(List<Space> avaiableSpaces) {
		this.avaiableSpaces = avaiableSpaces;
	}

	public double getSupportRate() {
		return defaultRatioSupport;
	}

	public void setSupportRate(double sr) {
		this.defaultRatioSupport = sr;
	}

	public String getSelectedAlgorithm() {
		return selectedAlgorithm;
	}

	public void setSelectedAlgorithm(String selectedAlgorithm) {
		this.selectedAlgorithm = selectedAlgorithm;
	}

	public ContainerLoading getConLoading() {
		return containerLoading;
	}

	public void setConLoading(ContainerLoading con) {
		this.containerLoading = con;
		this.container = con.getContainer();
		this.notPlacedBoxes = con.getNotPlacedBox();
		this.avaiableSpaces = con.getAllSpaces();
	};

	public Greedy() {
		this.candidates = new ArrayList<BoxCandidate>();
		this.placedBoxes = new Batch();
		this.notPlacedBoxes = new Batch();
	}

	// private void packing() {
	//
	// }

	private void createCandiates(Space space, Box box) {
		int k = 1;
		for (Box boxJ : this.getNotPlacedBoxes().getBoxes()) {
			if (boxJ.getBoxType() == box.getBoxType()) {
				k++;
			}
		}
		BoxCandidate candidate = new BoxCandidate(box, space, k);
		candidate.initialize(this.selectedAlgorithm);
		candidates.add(candidate);
	}

	private void createCandiateBoxList(Space space, List<Box> feasibleBoxes) {
		for (Box box : feasibleBoxes) {
			int k = 1; // count number of b of selected box type left yet to be placed.
			for (Box boxJ : this.getNotPlacedBoxes().getBoxes()) {
				if (box.getBoxType() == boxJ.getBoxType()) {
					k = k + 1;
				}
			}
			BoxCandidate candidate = new BoxCandidate(box, space, k);
			candidate.initialize(this.selectedAlgorithm);
			candidates.add(candidate);
		}
	}

	public BoxCandidate stAlgorithm() {
		candidates = this.mainCriterion();

		if (candidates.size() == 1) {
			return candidates.get(0);
		}

		candidates = this.firstTieBreaker();
		if (candidates.size() == 1) {
			return candidates.get(0);
		}

		candidates = this.secondTieBreaker();
		return candidates.get(0);
	}

	public BoxCandidate vlAlgorithm() {
		candidates = this.mainCriterion();

		if (candidates.size() == 1) {
			return candidates.get(0);
		}

		candidates = this.firstTieBreaker();
		if (candidates.size() == 1) {
			return candidates.get(0);
		}

		candidates = this.secondTieBreaker();
		return candidates.get(0);
	}

	// private Box elAlgorithm() {
	// return null;
	// }

	private List<BoxCandidate> mainCriterion() {
		List<BoxCandidate> helperList = new ArrayList<BoxCandidate>();
		double largestPotentialSpace = candidates.get(0).getPotenSpaceUtilization();

		for (BoxCandidate can : candidates) {
			if (largestPotentialSpace == can.getPotenSpaceUtilization()) {
				helperList.add(can);
				continue;
			}

			if (largestPotentialSpace < can.getPotenSpaceUtilization()) {
				largestPotentialSpace = can.getPotenSpaceUtilization();
				helperList.clear();
				helperList.add(can);
			}
		}
		return helperList;
	}

	private List<BoxCandidate> firstTieBreaker() {
		List<BoxCandidate> helperList = new ArrayList<BoxCandidate>();
		double smallestLengthwise = candidates.get(0).getLengthwiseProtrustion();
		for (BoxCandidate c : candidates) {
			if (smallestLengthwise == c.getLengthwiseProtrustion()) {
				helperList.add(c);
				continue;
			}
			if (smallestLengthwise > c.getLengthwiseProtrustion()) {
				helperList.clear();
				helperList.add(c);
			}
		}

		return helperList;
	}

	private List<BoxCandidate> secondTieBreaker() {
		List<BoxCandidate> helperList = new ArrayList<BoxCandidate>();
		double largestBoxVolume = candidates.get(0).getBoxVolume();

		for (BoxCandidate c : candidates) {
			if (c.getBoxVolume() == largestBoxVolume) {
				helperList.add(c);
			}

			if (largestBoxVolume < c.getBoxVolume()) {
				helperList.clear();
				helperList.add(c);
			}
		}
		return helperList;
	}

	public void updateSpaces(FeasibleObject obj) {
		logger.info("---Start method update Spaces---");
		Box selectedBox = obj.getBox();

		if (this.getNSupportRatio() == 0) {
			this.createSpaceWithNoneOverhang(obj);
			this.removeBoxWithOverlap(selectedBox); // remove overlap with other empty spaces.
		} else {
			this.createSpaceWithOverhang(obj);
			this.removeBoxOverlappingWithOverhang(selectedBox); // remove overlap with other empty spaces.

		}

		this.amalgamation(); // Amalgamation
		this.removeSmallSpaces(); // removal too small spaces

		logger.info("=====Finish update spaces======");
		logger.info("\n");
		this.showSpaceInfo();
	}

	private void addEmptySpace(Space s) {
		if (s.isValid()) {
			this.getAvaiableSpaces().add(s);
		}
	}

	private void removeBoxWithOverlap(Box box) {
		logger.info("Start remove box with overlap");
		List<Space> helperList = new ArrayList<>();
		List<Space> removeableList = new ArrayList<Space>();

		for (int i = 0; i < this.getAvaiableSpaces().size(); i++) {
			Space selectedSpace = this.getAvaiableSpaces().get(i);
			if (checkOverlapping(box, selectedSpace)) {
				// this.showCuboidInfo(selectedSpace);
				removeableList.add(selectedSpace);
				helperList.addAll(this.updateOverlapSpaces(box, selectedSpace));

			}
		}

		// remove invaid space.
		for (Space space : removeableList) {
			this.getAvaiableSpaces().remove(space);
		}

		// add new spaces to current space.
		if (helperList != null && !helperList.isEmpty()) {
			this.getAvaiableSpaces().addAll(helperList);
			this.removeSubsets();
		}

		logger.info("Stop remove box with overlap");
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

	private void amalgamation() {
		List<Space> helperList = new ArrayList<>();
		for (int i = 0; i < this.getAvaiableSpaces().size() - 1; i++) {
			Space s = this.getAvaiableSpaces().get(i);
			for (int j = i + 1; j < this.getAvaiableSpaces().size(); j++) {
				Space t = this.getAvaiableSpaces().get(j);
				Space amalgateSpace = null;
				if (this.getNSupportRatio() == 0) {
					amalgateSpace = this.amalgamate(s, t);
				} else {
					// incase overhang is allow.
					amalgateSpace = this.overlapAmalgate(s, t);
				}

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
			// showCuboidInfo(s);
			// showCuboidInfo(t);
			// this.showCuboidInfo(result);
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
			this.getAvaiableSpaces().add(result);
			logger.info("Amalgamate folow y direction");
			// this.showCuboidInfo(s);
			// this.showCuboidInfo(t);
			// this.showCuboidInfo(result);
			logger.info("\n");

		}
		return result;
	}

	private void removeSubsets() {
		logger.info("\n");
		logger.info("===Method: removeSubsets (Start)===");

		List<Space> helperList = new ArrayList<>(this.getAvaiableSpaces());
		for (int i = 0; i < helperList.size() - 1; i++) {
			Space s = helperList.get(i);
			for (int j = i + 1; j < helperList.size(); j++) {

				// check if space s is a subset of space t
				Space t = helperList.get(j);
				if (checkSubsetSpace(s, t)) {
					// this.showCuboidInfo("Space S info", s);
					// this.showCuboidInfo("Space T info", t);
					helperList.remove(i);
					break;
				}

				// check if space t is a subset of space s
				if (checkSubsetSpace(t, s)) {
					// this.showCuboidInfo("Space T info ", t);
					// this.showCuboidInfo("Space S info", s);
					helperList.remove(j);
				}
			}
		}
		this.setAvaiableSpaces(helperList);
		logger.info("===Method: removeSubsets (End) ===");
	}

	private boolean checkSubsetSpace(Space s, Space t) {
		if ((s.getMinimum().compare(t.getMinimum()) == 1 || s.getMinimum().compare(t.getMinimum()) == 0)
				&& (t.getMaximum().compare(s.getMaximum()) == 1 || t.getMaximum().compare(s.getMaximum()) == 0) && 
				s.getMaximumSupportX() >= t.getMaximumSupportX() && s.getMaximumSupportY() >= t.getMaximumSupportY()) {
			return true;
//			if (this.defaultRatioSupport == 0 || s.getMinimum().getZ() == 0) {
//				return true;
//			}			
//			if (s.getMaximumSupportX() <= t.getMaximumSupportX() && s.getMaximumSupportY() <= t.getMaximumSupportY() && s.getMinimum().getZ() > 0) {
//				return true;
//			}
		}
		return false;
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
		showCuboidInfo("behind space: ", behindSpace);
		}

		if (frontSpace.isValid()) {
			result.add(frontSpace);			
			showCuboidInfo("frontSpace: ", frontSpace);
			// showCuboidInfo(frontSpace);
		}

		if (leftSpace.isValid()) {			
			showCuboidInfo("leftSpace: ", leftSpace);
			// showCuboidInfo(leftSpace);
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

	private void removeSmallSpaces() {
		// logger.info("Start method remove small spaces");
		double smallestDimension = this.getSmallestDimension();
		List<Space> removeAbleSpace = new ArrayList<Space>();

		for (int i = 0; i < this.getAvaiableSpaces().size(); i++) {
			Space s = this.getAvaiableSpaces().get(i);
			if (s.getHeight() < smallestDimension) {
				removeAbleSpace.add(s);
				continue;
			}

			if (s.getWidth() < smallestDimension || s.getLength() < smallestDimension) {
				// check if exist space t.
				boolean fExist = false;
				for (int j = 0; j < this.getAvaiableSpaces().size(); j++) {
					if (i == j)
						continue;
					Space t = this.getAvaiableSpaces().get(j);
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
		this.getAvaiableSpaces().removeAll(removeAbleSpace);
		// logger.info("End method remove small spaces");
	}

	private double getSmallestDimension() {
		double smallestDimension = 0;

		if (this.containerLoading.getNotPlacedBox().getBoxes().size() > 0) {
			smallestDimension = this.containerLoading.getNotPlacedBox().getBoxes().get(0).getSmallestDimension();
		}

		for (Box box : this.containerLoading.getNotPlacedBox().getBoxes()) {
			smallestDimension = smallestDimension > box.getSmallestDimension() ? box.getSmallestDimension()
					: smallestDimension;
		}
		return smallestDimension;
	}

	public FeasibleObject finBoxStVl() {
		return null;
	}

	public FeasibleObject findObjectBs() {
		
		this.sortSpaces();
		List<BoxCandidate> feasibleCandidates = new ArrayList<BoxCandidate>();
		Box temp =  null;
		for (Box box : this.getNotPlacedBoxes().getBoxes()) {			
			for (Space space : this.getAvaiableSpaces()) {
				String rotation = this.checkBoxIsFeasible(box, space);
				if (rotation.isEmpty()) {
					continue;
				}
				temp = new Box(box);
				temp.setSelectedRotation(rotation);
				temp.setSize(rotation);

				this.updateBoxPosition(temp, space, rotation);
				BoxCandidate candidate = new BoxCandidate(temp, space, 0);		
				feasibleCandidates.add(candidate);
			}

			if (feasibleCandidates.isEmpty()) {
				continue;
			}

			
			BoxCandidate bestCan = this.findBestSpaceBs(feasibleCandidates);
			box.setSelectedRotation(bestCan.getBox().getSelectedRotation());
			box.setSize(bestCan.getBox().getSelectedRotation());
			updateBoxPosition(box, bestCan.getSpace(), box.getSelectedRotation());
			return new FeasibleObject(box, bestCan.getBox().getSelectedRotation(), bestCan.getSpace());
		}
		return null;
	}

	private BoxCandidate findBestSpaceBs(List<BoxCandidate> candidates) {
		this.clearCandidates();
		for (BoxCandidate candidate : candidates) {
			this.createCandiates(candidate.getSpace(), candidate.getBox());
		}
		return this.findBestFittedBox();
	}

	public FeasibleObject findFeasibleObject() {
		logger.info("---Start FindFeasibleObject---");

		// re-sort all current avaiable spaces acending...
		this.sortSpaces();
		logger.info("\n");
		logger.info("List of spaces ");
		this.showSpaceInfo();

		for (Space space : this.getAvaiableSpaces()) {
			// init feasible box list
			List<Box> feasibleListBox = new ArrayList<Box>();

			for (Box box : this.getNotPlacedBoxes().getBoxes()) {
				String selectedRotation = this.checkBoxIsFeasible(box, space);
				if (selectedRotation.isEmpty()) { // if box is not fit in space
					continue;
				}

				// put box into feasible list and then choose the best one.setSize
				box.setSelectedRotation(selectedRotation);
				box.setSize(selectedRotation);
				this.updateBoxPosition(box, space, selectedRotation);
				// if (isMultiDropFeasiblePacking(box)) {
				// feasibleListBox.add(box);
				// }
				feasibleListBox.add(box);
			}

			if (feasibleListBox.isEmpty()) {
				continue;
			}

			logger.info("Avaiable boxes");
			for (Box b : feasibleListBox) {
				this.showCuboidInfo("", b);
			}
			// find the best one box.
			BoxCandidate candidate = this.findBestFittedBox();
			// Box fittedBox = feasibleListBox.get(0);
			logger.info("The best fit box");
			this.showCuboidInfo("", candidate.getBox());
			return new FeasibleObject(candidate.getBox(), candidate.getBox().getSelectedRotation(), space);
		}

		return null;
	}

	private BoxCandidate findBestFittedBox() {
		if (candidates.size() == 1) {
			return candidates.get(0);
		}

		switch (this.getSelectedAlgorithm()) {
		case Greedy.ST_ALGORITHM:
			return this.stAlgorithm();
		case Greedy.VL_ALGORITHM:
			return this.vlAlgorithm();
		case Greedy.EL_ALGORITHM:
			return this.vlAlgorithm();
		default:
			return null;
		}
	}

//	private Box findBestFittedBox(Space space, List<Box> feasibleBoxList) {
//		if (feasibleBoxList.size() == 1) {
//			return feasibleBoxList.get(0);
//		}
//		candidates.clear();
//		switch (this.getSelectedAlgorithm()) {
//		case Greedy.ST_ALGORITHM:
//			return this.stAlgorithm(space, feasibleBoxList);
//		case Greedy.VL_ALGORITHM:
//			return this.vlAlgorithm(space, feasibleBoxList);
//		case Greedy.EL_ALGORITHM:
//			return this.vlAlgorithm(space, feasibleBoxList);
//		default:
//			return null;
//		}
//	}

	private String checkBoxIsFeasible(Box selectedBox, Space selectedSpace) {
		// loop all possible rotations.
		int[] rotations = selectedBox.getfRotation();

		// can be rotation by X.
		if (rotations[0] == 1) {
			// XZY
			if (selectedSpace.getLength() >= selectedBox.getBiggestDimension()
					&& selectedSpace.getWidth() >= selectedBox.getSmallestDimension()
					&& selectedSpace.getHeight() >= selectedBox.getMiddleDimension()) {
				return Rotation.XZY;
			}

			// XYZ
			if (selectedSpace.getLength() >= selectedBox.getBiggestDimension()
					&& selectedSpace.getWidth() >= selectedBox.getMiddleDimension()
					&& selectedSpace.getHeight() >= selectedBox.getSmallestDimension()) {
				return Rotation.XYZ;
			}
		}

		// can be rotation by Y.
		if (rotations[1] == 1) {
			// YXZ
			if (selectedSpace.getWidth() >= selectedBox.getBiggestDimension()
					&& selectedSpace.getLength() >= selectedBox.getMiddleDimension()
					&& selectedSpace.getHeight() >= selectedBox.getSmallestDimension()) {
				return Rotation.YXZ;
			}

			// YZX
			if (selectedSpace.getWidth() >= selectedBox.getBiggestDimension()
					&& selectedSpace.getLength() >= selectedBox.getSmallestDimension()
					&& selectedSpace.getHeight() >= selectedBox.getMiddleDimension()) {
				return Rotation.YZX;
			}
		}

		// can be rotation by Z.
		if (rotations[2] == 1) {
			// ZXY
			if (selectedSpace.getHeight() >= selectedBox.getBiggestDimension()
					&& selectedSpace.getLength() >= selectedBox.getMiddleDimension()
					&& selectedSpace.getWidth() >= selectedBox.getSmallestDimension()) {
				return Rotation.ZXY;
			}
			// ZYX
			if (selectedSpace.getHeight() >= selectedBox.getBiggestDimension()
					&& selectedSpace.getWidth() >= selectedBox.getMiddleDimension()
					&& selectedSpace.getLength() >= selectedBox.getSmallestDimension()) {
				return Rotation.ZYX;
			}
		}
		return "";
	}

	private List<String> findRotations(Box box, Space space) {
		// loop all possible rotations.
		int[] rotations = box.getfRotation();
		List<String> fesRotations = new ArrayList<String>();

		// can be rotation by X.
		if (rotations[0] == 1) {
			// XZY
			if (space.getLength() >= box.getBiggestDimension() && space.getWidth() >= box.getSmallestDimension()
					&& space.getHeight() >= box.getMiddleDimension()) {
				fesRotations.add(Rotation.XZY);
			}

			// XYZ
			if (space.getLength() >= box.getBiggestDimension() && space.getWidth() >= box.getMiddleDimension()
					&& space.getHeight() >= box.getSmallestDimension()) {
				fesRotations.add(Rotation.XYZ);
				// return Rotation.XYZ;
			}
		}
		// can be rotation by Y.
		if (rotations[1] == 1) {
			// YXZ
			if (space.getWidth() >= box.getBiggestDimension() && space.getLength() >= box.getMiddleDimension()
					&& space.getHeight() >= box.getSmallestDimension()) {
				// return Rotation.YXZ;
				fesRotations.add(Rotation.YXZ);
			}

			// YZX
			if (space.getWidth() >= box.getBiggestDimension() && space.getLength() >= box.getSmallestDimension()
					&& space.getHeight() >= box.getMiddleDimension()) {
				// return Rotation.YZX;
				fesRotations.add(Rotation.YZX);
			}
		}
		// can be rotation by Z.
		if (rotations[2] == 1) {
			// ZXY
			if (space.getHeight() >= box.getBiggestDimension() && space.getLength() >= box.getMiddleDimension()
					&& space.getWidth() >= box.getSmallestDimension()) {
				fesRotations.add(Rotation.ZXY);
				// return Rotation.ZXY;
			}
			// ZYX
			if (space.getHeight() >= box.getBiggestDimension() && space.getWidth() >= box.getMiddleDimension()
					&& space.getLength() >= box.getSmallestDimension()) {
				fesRotations.add(Rotation.ZYX);
				// return Rotation.ZYX;
			}
		}
		return fesRotations;
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
		this.showSpaceInfo("Founded space", selectedSpace);
		this.showCuboidInfo("Founded box", selectedBox);
		this.getNotPlacedBoxes().getBoxes().remove(selectedBox);
		this.getPlacedBoxes().getBoxes().add(selectedBox);
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

	public void showResult() {
		logger.info("\n\n\n");
		logger.info("===== Final Resutl=====");
		int numberOfItem = this.containerLoading.getProblem().getNumOfItem();
		int numberOfPlacedBoxes = this.getPlacedBoxes().getBoxes().size();
		List<Box> placedBoxes = this.getPlacedBoxes().getBoxes();

		logger.info(String.format("Number of item: %d, number of placed box: %d",
				new Object[] { numberOfItem, numberOfPlacedBoxes }));

		this.containerVolumes = this.getContainer().getLength() * this.getContainer().getWidth()
				* this.getContainer().getHeight();
		for (Box box : placedBoxes) {
			double boxVolume = box.getLength() * box.getWidth() * box.getHeight();
			this.totalBoxVolumes = this.totalBoxVolumes + boxVolume;
			this.showCuboidInfo("", box);
		}

		logger.info("List of not placed box: " + this.getNotPlacedBoxes().getBoxes().size());
		for (Box box : this.getNotPlacedBoxes().getBoxes()) {
			this.getConLoading().showCuboidInfo("", box);
		}
		logger.info(String.format("Volume utilisation: %.2f",
				new Object[] { this.totalBoxVolumes / this.containerVolumes * 100 }));
		Utility.getInstance().writeResultToFile(this.placedBoxes.getBoxes()); // write result to file.
	}

	// private void showCuboidInfo(Cuboid c) {
	// logger.info(String.format("%f %f %f, %f %f %f",
	// new Object[] { c.getMinimumPoint().getX(), c.getMinimumPoint().getY(),
	// c.getMinimumPoint().getZ(),
	// c.getMaximumPoint().getX(), c.getMaximumPoint().getY(),
	// c.getMaximumPoint().getZ() }));
	// }

	public void showCuboidInfo(String name, Cuboid c) {
		if (c.getMinimumPoint() == null && c.getMaximumPoint() == null) {
			logger.info(String.format("%s length: %.2f, width: %.2f, height: %.2f%, volume: %.2f, min: null  max: null",
					new Object[] { name, c.getLength(), c.getWidth(), c.getHeigth(), c.getVolume() }));
			return;
		}

		if (c.getMinimumPoint() == null) {
			logger.info(String.format("%s length: %.2f, width: %.2f, height: %.2f, volume: %.2f min: null max(%.2f, %.2f, %.2f)",
					new Object[] { name, c.getLength(), c.getWidth(), c.getHeigth(), c.getVolume(), c.getMaximumPoint().getX(),
							c.getMaximumPoint().getY(), c.getMaximumPoint().getZ() }));
			return;
		}

		if (c.getMaximumPoint() == null) {
			logger.info(String.format("%s length: %.2f, width: %.2f, height: %.2f, volume: %2.f min(%.2f, %.2f, %.2f) max: null",
					new Object[] { name, c.getLength(), c.getWidth(), c.getHeigth(), c.getVolume(),  c.getMinimumPoint().getX(),
							c.getMinimumPoint().getY(), c.getMinimumPoint().getZ() }));
			return;
		}

		logger.info(
				String.format("%s length: %.2f, width: %.2f, height: %.2f, volume: %.2f  min(%.2f, %.2f, %.2f) max(%.2f, %.2f, %.2f)",
						new Object[] { name == null ? "" : name, c.getLength(), c.getWidth(), c.getHeigth(),c.getVolume(),
								c.getMinimumPoint().getX(), c.getMinimumPoint().getY(), c.getMinimumPoint().getZ(),
								c.getMaximumPoint().getX(), c.getMaximumPoint().getY(), c.getMaximumPoint().getZ() }));
	}
	
	public void showSpaceInfo(String name, Space s) {

		if (s.getMinimumPoint() == null && s.getMaximumPoint() == null) {
			logger.info(String.format("%s length: %.2f, width: %.2f, height: %.2f%, volume: %.2f a:%2f  b:%2f",
					new Object[] { name, s.getLength(), s.getWidth(), s.getHeigth(), s.getVolume(), s.getMaximumSupportX(), s.getMaximumSupportY() }));
			return;
		}

		if (s.getMinimumPoint() == null) {
			logger.info(String.format("%s length: %.2f, width: %.2f, height: %.2f, volume: %.2f a:%2f  b:%2f min: null max(%.2f, %.2f, %.2f)",
					new Object[] { name, s.getLength(), s.getWidth(), s.getHeigth(), s.getVolume(), s.getMaximumSupportX(), s.getMaximumSupportY(), s.getMaximumPoint().getX(),
							s.getMaximumPoint().getY(), s.getMaximumPoint().getZ() }));
			return;
		}

		if (s.getMaximumPoint() == null) {
			logger.info(String.format("%s length: %.2f, width: %.2f, height: %.2f, volume: %2.f a:%2f  b:%2f min(%.2f, %.2f, %.2f) max: null",
					new Object[] { name, s.getLength(), s.getWidth(), s.getHeigth(), s.getVolume(), s.getMaximumSupportX(), s.getMaximumSupportY(), s.getMinimumPoint().getX(),
							s.getMinimumPoint().getY(), s.getMinimumPoint().getZ() }));
			return;
		}

		logger.info(
				String.format("%s length: %.2f, width: %.2f, height: %.2f, volume: %.2f a:%2f  b:%2f   min(%.2f, %.2f, %.2f) max(%.2f, %.2f, %.2f)",
						new Object[] { name == null ? "" : name, s.getLength(), s.getWidth(), s.getHeigth(),s.getVolume(), s.getMaximumSupportX(), s.getMaximumSupportY(),
								s.getMinimumPoint().getX(), s.getMinimumPoint().getY(), s.getMinimumPoint().getZ(),
								s.getMaximumPoint().getX(), s.getMaximumPoint().getY(), s.getMaximumPoint().getZ() }));
	
	}
	
	public void showBoxInfo(String name, Box box) {
		if (box.getMinimumPoint() == null && box.getMaximumPoint() == null) {
			logger.info(String.format("%s length: %.2f, width: %.2f, height: %.2f, volume: %.2f, min: null  max: null",
					new Object[] { name, box.getLength(), box.getWidth(), box.getHeigth(), box.getVolume() }));
			return;
		}

		if (box.getMinimumPoint() == null) {
			logger.info(String.format("%s length: %.2f, width: %.2f, height: %.2f min: null max(%.2f, %.2f, %.2f)",
					new Object[] { name, box.getLength(), box.getWidth(), box.getHeigth(), box.getMaximumPoint().getX(),
							box.getMaximumPoint().getY(), box.getMaximumPoint().getZ() }));
			return;
		}

		if (box.getMaximumPoint() == null) {
			logger.info(String.format("%s length: %.2f, width: %.2f, height: %.2f min(%.2f, %.2f, %.2f) max: null",
					new Object[] { name, box.getLength(), box.getWidth(), box.getHeigth(), box.getMinimumPoint().getX(),
							box.getMinimumPoint().getY(), box.getMinimumPoint().getZ() }));
			return;
		}

		logger.info(
				String.format("%s length: %.2f, width: %.2f, height: %.2f min(%.2f, %.2f, %.2f) max(%.2f, %.2f, %.2f)",
						new Object[] { name == null ? "" : name, box.getLength(), box.getWidth(), box.getHeigth(),
								box.getMinimumPoint().getX(), box.getMinimumPoint().getY(), box.getMinimumPoint().getZ(),
								box.getMaximumPoint().getX(), box.getMaximumPoint().getY(), box.getMaximumPoint().getZ() }));
	}

	public void sortSpaces() {
		// check spaces's size.
		if (avaiableSpaces.isEmpty() || avaiableSpaces.size() == 1)
			return;
		Space[] avaiableSpaceList = (Space[]) (Space[]) this.avaiableSpaces.toArray(new Space[0]);
		this.mergeSort(avaiableSpaceList, 0, this.avaiableSpaces.size() - 1);
		this.avaiableSpaces.clear();
		for (int i = 0; i < avaiableSpaceList.length; i++) {
			this.avaiableSpaces.add(avaiableSpaceList[i]);
		}

	}

	public void mergeSort(Space[] spaceList, int start, int end) {
		if (end - start > 0) {
			int mid = start + (end - start) / 2;
			mergeSort(spaceList, start, mid);
			mergeSort(spaceList, mid + 1, end);

			// returnSpace = this.merge(spaceList, start, end, mid);
			this.merge(spaceList, start, end, mid);
		}

		// return returnSpace;
	}

	private Space[] merge(Space[] spaceList, int start, int end, int mid) {
		Space[] temp = new Space[spaceList.length];

		// copy both parts into the temp array
		for (int i = start; i <= end; i++) {
			temp[i] = spaceList[i];
		}

		int i = start;
		int k = start;
		int j = mid + 1;

		// copy smallest values from either left or right side back
		// to orginal array.
		while (i <= mid && j <= end) {
			if (spaceList[i].compare(spaceList[j]) <= 0) {
				spaceList[k] = temp[i];
				i++;
			} else {
				spaceList[k] = temp[j];
				j++;
			}

			k++;
		}

		while (i <= mid) {
			spaceList[k] = temp[i];
			k++;
			i++;
		}
		// Since we are sorting in-place any leftover elements from the right side
		// are already at the right position.
		return spaceList;
	}

	public void showSpaceInfo() {
		for (Space s : this.getAvaiableSpaces()) {
			this.showSpaceInfo("", s);
		}
	}

	public void updateBoxPosition(Box selectedBox, Space space, String rotation) {
		Dimension maximumDimension = getMaximumDimension(rotation, space.getMinimum(), selectedBox);
		selectedBox.setMinimum(space.getMinimum());
		selectedBox.setMaximum(maximumDimension);
	}

	private void createSpaceWithNoneOverhang(FeasibleObject obj) {

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
		this.getAvaiableSpaces().remove(selectedSpace);

		// add all new generate spaces.
		this.addEmptySpace(frontSpace);
		this.addEmptySpace(rightSpace);
		this.addEmptySpace(aboveSpace);
	}

	private void createSpaceWithOverhang(FeasibleObject feasibleObj) {
		Box selectedBox = feasibleObj.getBox();
		Space selectedSpace = feasibleObj.getSpace();

		Space frontSpace = generateFrontSpace(selectedBox, selectedSpace);
		Space rightSpace = generateRightSpace(selectedBox, selectedSpace);
		Space aboveSpace = generateAboveSpace(selectedBox, selectedSpace);

		// remove old space
		this.getAvaiableSpaces().remove(selectedSpace);
		this.showSpaceInfo("front space:", frontSpace);
		this.showCuboidInfo("right space", rightSpace);
		this.showSpaceInfo("above space", aboveSpace);
		// add all new generate spaces.
		this.addEmptySpace(frontSpace);
		this.addEmptySpace(rightSpace);
		this.addEmptySpace(aboveSpace);
	}

	// Generate above space
	private Space generateAboveSpace(Box box, Space space) {
		// Dimension minBox = box.getMinimum();
		Dimension maxBox = box.getMaximum();
		Dimension minSpace = space.getMinimum();
		Dimension maxSpace = space.getMaximum();

		double xMaxTemp = minSpace.getX() + box.getLength() * (1 + this.getNSupportRatio());
		double xMax = maxSpace.getX() < xMaxTemp ? maxSpace.getX() : xMaxTemp;
		double yMaxTemp = minSpace.getY() + box.getWidth() * (1 + this.getNSupportRatio());
		double yAboveMaximum = maxSpace.getY() < yMaxTemp ? maxSpace.getY() : yMaxTemp;

		Dimension minimum = new Dimension(minSpace.getX(), minSpace.getY(), maxBox.getZ());
		Dimension maximum = new Dimension(xMax, yAboveMaximum, maxSpace.getZ());
		Space result = new Space(minimum, maximum);
		result.setMaximumSupportX(
				box.getLength() < space.getMaximumSupportX() ? box.getLength() : space.getMaximumSupportX());
		result.setMaximumSupportY(
				box.getWidth() < space.getMaximumSupportY() ? box.getWidth() : space.getMaximumSupportY());
		result.setRatioSupport(this.getNSupportRatio());
		return result;

	}

	private Space generateFrontSpace(Box box, Space space) {
		Dimension maxBox = box.getMaximum();
		Dimension minSpace = space.getMinimum();
		Dimension maxSpace = space.getMaximum();

		if (minSpace.getZ() == 0) { // Incase space is full support from below and no overhang is allow.
			// create new front space
			Dimension minimum = new Dimension(maxBox.getX(), minSpace.getY(), minSpace.getZ());
			Dimension maximum = maxSpace;
			Space result = new Space(minimum, maximum, 0, maxSpace.getX(), maxSpace.getY());
			return result;
		} else {
			double xFrontMaximum = (1 + getNSupportRatio()) * space.getMaximumSupportX()
					- maxBox.getX() * getNSupportRatio();

			Dimension minimum = new Dimension(maxBox.getX(), minSpace.getY(), minSpace.getZ());
			Dimension maximum = new Dimension(xFrontMaximum, maxSpace.getY(), maxSpace.getZ());
			Space result = new Space(minimum, maximum, this.getNSupportRatio(), space.getMaximumSupportX(),
					space.getMaximumSupportY());
			return result;
		}
	}

	private Space generateRightSpace(Box box, Space space) {
		Dimension minBox = box.getMinimum();
		Dimension maxBox = box.getMaximum();
		Dimension minSpace = space.getMinimum();
		Dimension maxSpace = space.getMaximum();

		if (minSpace.getZ() == 0) {
			Dimension rightSpaceMinimum = new Dimension(minBox.getX(), maxBox.getY(), minBox.getZ());
			Dimension rightSpaceMaximum = maxSpace;
			Space rightSpace = new Space(rightSpaceMinimum, rightSpaceMaximum, 0, maxSpace.getX(), maxSpace.getY());			
			rightSpace.setRatioSupport(0);
			return rightSpace;
		}

		Dimension rightSpaceMinimum = new Dimension(minSpace.getX(), maxBox.getY(), minSpace.getZ());
		double maximumY = (1 + this.getNSupportRatio()) * space.getMaximumSupportY()
				- maxBox.getY() * this.getNSupportRatio();
		Dimension rightSpaceMaximum = new Dimension(maxSpace.getX(), maximumY, maxSpace.getZ());

		Space rSpace = new Space(rightSpaceMinimum, rightSpaceMaximum, getNSupportRatio(), space.getMaximumSupportX(),
				space.getMaximumSupportY());
		return rSpace;
	}

	private Space initOverhangSpace(Space s, Box box) {

		Dimension min = s.getMinimum();
		Dimension max = s.getMaximum();
		double rs = this.defaultRatioSupport;
		Space space = new Space();

		/* Not calculate with space */
		if (min.getZ() == 0) {
			space.setRatioSupport(0);
			space.setMinimum(min);
			space.setMaximum(max);
			space.setMaximumSupportX(max.getX());
			space.setMaximumSupportY(max.getY());
			return space;
		}

		// set max and min supported part incase minZ >0.
		space.setMaximumSupportX(min.getX() + box.getLength());
		space.setMaximumSupportY(min.getY() + box.getWidth());

		// re-calculate supported rate.
		double noneSupportX = box.getLength() * rs;
		double noneSupportY = box.getWidth() * rs;

		double maxX = (min.getX() + s.getLength() + noneSupportX) <= max.getX()
				? min.getX() + s.getLength() + noneSupportX
				: max.getX();
		double maxY = (min.getY() + s.getWidth() + noneSupportY) <= max.getY()
				? min.getY() + s.getWidth() + noneSupportY
				: max.getY();

		// recalculate none support x and y parts.
		noneSupportX = maxX - s.getMaximumSupportX();
		noneSupportY = maxY - s.getMaximumSupportY();

		double newXRate = noneSupportX / s.getLength();
		double newYRate = noneSupportY / s.getWidth();
		rs = newXRate <= newYRate ? newXRate : newYRate;

		// recalculate xMax in case choose newYRate
		if (rs != newXRate) {
			noneSupportX = box.getLength() * rs;
			maxX = min.getX() + s.getLength() + noneSupportX;
		} else {
			noneSupportY = box.getLength() * rs;
			maxY = min.getY() + s.getWidth() + noneSupportY;
		}

		Dimension newMax = new Dimension(maxX, maxY, max.getZ());
		space.setMinimum(min);
		space.setMaximum(newMax);
		space.setRatioSupport(rs);
		return space;
	}

	private Space generateOverhangSpace(Space s) {
		s.setMaximumSupportX(s.getMaximum().getX());
		s.setMaximumSupportY(s.getMaximum().getY());
		double extensionX = s.getLength() * this.defaultRatioSupport;
		double extensionY = s.getWidth() * this.defaultRatioSupport;

		if ((s.getMaximum().getX() + extensionX <= getContainer().getLength())
				&& (s.getMaximum().getY() + extensionY) <= getContainer().getWidth()) {
			s.getMaximum().setX(s.getMaximum().getX() + extensionX);
			s.getMaximum().setY(s.getMaximum().getY() + extensionY);
			return s;
		}

		double tempMaximumX = s.getMaximum().getX() + extensionX > getContainer().getLength()
				? getContainer().getLength()
				: s.getMaximum().getX() + extensionX;
		double tempMaximumY = s.getMaximum().getY() + extensionY > getContainer().getWidth() ? getContainer().getWidth()
				: s.getMaximum().getY() + extensionY;
		double ratioSupportX = extensionX / (tempMaximumX - s.getMinimum().getX());
		double ratioSupportY = extensionY / (tempMaximumY - s.getMinimum().getY());

		if (ratioSupportX <= ratioSupportY) {
			s.setRatioSupport(ratioSupportX);
			s.getMaximum().setX(s.getMaximum().getX() + s.getLength() * ratioSupportX);
			s.getMaximum().setY(s.getMaximum().getY() + s.getWidth() * ratioSupportX);
			return s;
		}

		s.setRatioSupport(ratioSupportY);
		s.getMaximum().setX(s.getMaximum().getX() + s.getLength() * ratioSupportY);
		s.getMaximum().setY(s.getMaximum().getY() + s.getWidth() * ratioSupportY);
		return s;
	}

	private void removeBoxOverlappingWithOverhang(Box box) {

		logger.info("Start remove box with overlap");
		List<Space> helperList = new ArrayList<>();
		List<Space> removeableList = new ArrayList<Space>();

		for (int i = 0; i < this.getAvaiableSpaces().size(); i++) {
			Space selectedSpace = this.getAvaiableSpaces().get(i);
			if (checkOverlapping(box, selectedSpace)) {
				// this.showCuboidInfo(selectedSpace);
				removeableList.add(selectedSpace);
				helperList.addAll(this.overhangUpdateOverlapSpaces(box, selectedSpace));
			}
		}

		// remove invaid space.
		for (Space space : removeableList) {
			this.getAvaiableSpaces().remove(space);
		}

		// add new spaces to current space.
		if (helperList != null && !helperList.isEmpty()) {
			this.getAvaiableSpaces().addAll(helperList);
			this.removeSubsets();
		}
		logger.info("Stop remove box with overlap");
	}

	private List<Space> overhangUpdateOverlapSpaces(Box box, Space space) {

		logger.info("===updateOverlapSpaces===");
		List<Space> result = new ArrayList<>();
		Dimension minBoxDimension = box.getMinimum();
		Dimension maxBoxDimension = box.getMaximum();

		Dimension minSpaceDimension = space.getMinimum();
		Dimension maxSpaceDimension = space.getMaximum();

		// create behind space.
		Dimension behindMinimumDimension = minSpaceDimension;
		Dimension behindMaximumDimension = new Dimension(minBoxDimension.getX(), maxSpaceDimension.getY(), maxSpaceDimension.getZ());
		Space behindSpace = new Space(behindMinimumDimension, behindMaximumDimension);		
		behindSpace.setRatioSupport(getNSupportRatio());
		behindSpace.setMaximumSupportX(space.getMaximumSupportX() < minBoxDimension.getX() ? space.getMaximumSupportX() : minBoxDimension.getX());
		behindSpace.setMaximumSupportY(space.getMaximumSupportY());

		// create front space.
		Dimension frontMinimumDimension = new Dimension(maxBoxDimension.getX(), minSpaceDimension.getY(), minSpaceDimension.getZ());
		double tempFrontMaxX = space.getMaximumSupportX() * (1 + this.getNSupportRatio()) - maxBoxDimension.getX() * this.getNSupportRatio();
		double frontMaximumX = tempFrontMaxX < maxSpaceDimension.getX() ? tempFrontMaxX : maxSpaceDimension.getX();		
		Dimension frontMaximumDimension = new Dimension(frontMaximumX, maxSpaceDimension.getY(), maxSpaceDimension.getZ());
		Space frontSpace = new Space(frontMinimumDimension, frontMaximumDimension);
		frontSpace.setMaximumSupportX(space.getMaximumSupportX());
		frontSpace.setMaximumSupportY(space.getMaximumSupportY());

		// create left space.
		Dimension leftMinimumDimension = minSpaceDimension;
		Dimension leftMaximumDimension = new Dimension(maxSpaceDimension.getX(), minBoxDimension.getY(), maxSpaceDimension.getZ());
		Space leftSpace = new Space(leftMinimumDimension, leftMaximumDimension);
		leftSpace.setMaximumSupportX(space.getMaximumSupportX());
		leftSpace.setMaximumSupportY(space.getMaximumSupportY() < minBoxDimension.getY() ? space.getMaximumSupportY() : minBoxDimension.getY());

		// create right space.
		Dimension rightMinimumDimension = new Dimension(minSpaceDimension.getX(), maxBoxDimension.getY(), minSpaceDimension.getZ());
		double tempMaxRightDimension = space.getMaximumSupportY() * (1 + this.getNSupportRatio()) - maxBoxDimension.getY() * this.getNSupportRatio();
		double maxRightDimension = tempMaxRightDimension < maxSpaceDimension.getY() ? tempMaxRightDimension : maxSpaceDimension.getY();
		Dimension rightMaximumDimension = new Dimension(maxSpaceDimension.getX(), maxRightDimension,maxSpaceDimension.getZ());
		Space rightSpace = new Space(rightMinimumDimension, rightMaximumDimension);
		rightSpace.setMaximumSupportX(space.getMaximumSupportX());
		rightSpace.setMaximumSupportY(space.getMaximumSupportY());

		// create below space
		Dimension belowMinimumDimension = minSpaceDimension;
		Dimension belowMaximumDimension = new Dimension(maxSpaceDimension.getX(), maxSpaceDimension.getY(), minBoxDimension.getZ());
		Space belowSpace = new Space(belowMinimumDimension, belowMaximumDimension);
		belowSpace.setMaximumSupportX(space.getMaximumSupportX());
		belowSpace.setMaximumSupportY(space.getMaximumSupportY());
		
		this.showCuboidInfo("selected box", box);
		this.showCuboidInfo("selected space", space);
		this.showCuboidInfo("", behindSpace);
		this.showCuboidInfo("", frontSpace);
		this.showCuboidInfo("", rightSpace);
		this.showCuboidInfo("", leftSpace);
		this.showCuboidInfo("", belowSpace);
		
		if (behindSpace.isValid()) {
			result.add(behindSpace);
		}

		if (frontSpace.isValid()) {
			result.add(frontSpace);
		}

		if (rightSpace.isValid()) {
			result.add(rightSpace);
		}

		if (leftSpace.isValid()) {
			result.add(leftSpace);
		}

		if (belowSpace.isValid()) {
			result.add(belowSpace);
		}
		logger.info("===End Update Overlap Space===");
		logger.info("\n\n");
		return result;
	}

	private Space overlapAmalgate(Space s, Space t) {
		if ((s.getMinimum().getZ() != t.getMinimum().getZ())) {
			return null;
		}
		Space space = null;
		// Amalgamate in x direction
		// check overlap by x
		if (s.getMinimum().getY() < t.getMaximumSupportY() && t.getMinimum().getY() < s.getMaximumSupportY()
				&& (s.getMaximumSupportX() == t.getMinimum().getX()
						|| s.getMinimum().getX() == t.getMaximumSupportX())) {

			double maxSupportX = s.getMaximumSupportX() > t.getMaximumSupportX() ? s.getMaximumSupportX()
					: t.getMaximumSupportX();
			double maxSupportY = s.getMaximumSupportY() < t.getMaximumSupportY() ? s.getMaximumSupportY()
					: t.getMaximumSupportY();
			double minX = s.getMinimum().getX() < t.getMinimum().getX() ? s.getMinimum().getX() : t.getMinimum().getX();
			double minY = s.getMinimum().getY() > t.getMinimum().getY() ? s.getMinimum().getY() : t.getMinimum().getY();
			double minZ = s.getMinimum().getZ();

			double maxX = s.getMaximum().getX() > t.getMaximum().getX() ? s.getMaximum().getX() : s.getMaximum().getY();
			double maxY = maxSupportY * (1 + this.getNSupportRatio()) - minY * this.getNSupportRatio();
			double maxZ = s.getMaximum().getZ() < t.getMaximum().getZ() ? s.getMaximum().getZ() : t.getMaximum().getZ();

			Dimension minimum = new Dimension(minX, minY, minZ);
			Dimension maximum = new Dimension(maxX, maxY, maxZ);
			space = new Space(minimum, maximum);
			space.setMaximumSupportX(maxSupportX);
			space.setMaximumSupportY(maxSupportY);
			return space;
		}

		// Amalgamate in y, check overlap in Y's direction
		if (s.getMinimum().getX() < t.getMaximumSupportX() && t.getMinimum().getX() < s.getMaximumSupportX()
				&& (s.getMaximumSupportY() == t.getMinimum().getY()
						|| s.getMinimum().getY() == t.getMaximumSupportY())) {

			double maxSupportX = s.getMaximumSupportX() < t.getMaximumSupportX() ? s.getMaximumSupportX()
					: t.getMaximumSupportX();
			double maxSupportY = s.getMaximumSupportY() > t.getMaximumSupportY() ? s.getMaximumSupportY()
					: t.getMaximumSupportY();

			double minX = s.getMinimum().getX() > t.getMinimum().getX() ? s.getMinimum().getX() : t.getMinimum().getX();
			double minY = s.getMinimum().getY() < t.getMinimum().getY() ? s.getMinimum().getY() : t.getMinimum().getY();
			double minZ = s.getMinimum().getZ();

			double maxX = maxSupportX * (1 + s.getRatioSupport()) - minX * s.getRatioSupport();
			double maxY = s.getMaximum().getY() > t.getMaximum().getY() ? s.getMaximum().getX() : t.getMaximum().getY();
			double maxZ = s.getMaximum().getZ() < t.getMaximum().getZ() ? s.getMaximum().getZ() : t.getMaximum().getZ();

			Dimension minimum = new Dimension(minX, minY, minZ);
			Dimension maximum = new Dimension(maxX, maxY, maxZ);

			space = new Space(minimum, maximum);
			space.setMaximumSupportX(maxSupportX);
			space.setMaximumSupportY(maxSupportY);
			return space;
		}
		return null;
	}

	private void overhangRemoveSubset() {

		logger.info("\n");
		logger.info("===Method: removeSubsets (Start)===");

		List<Space> helperList = new ArrayList<>(this.getAvaiableSpaces());
		for (int i = 0; i < helperList.size() - 1; i++) {
			Space s = helperList.get(i);
			for (int j = i + 1; j < helperList.size(); j++) {

				// check if space s is a subset of space t
				Space t = helperList.get(j);
				if (checkSubsetSpace(s, t)) {
					// this.showCuboidInfo("Space S info", s);
					// this.showCuboidInfo("Space T info", t);
					helperList.remove(i);
					break;
				}

				// check if space t is a subset of space s
				if (checkSubsetSpace(t, s)) {
					// this.showCuboidInfo("Space T info ", t);
					// this.showCuboidInfo("Space S info", s);
					helperList.remove(j);
				}
			}
		}
		this.setAvaiableSpaces(helperList);
		logger.info("===Method: removeSubsets (End) ===");

	}
	
	/*Clear all previous candidates*/
	private void clearCandidates() {	
		this.candidates.clear();
	}

	private double totalBoxVolumes = 0;
	private double containerVolumes;
}
