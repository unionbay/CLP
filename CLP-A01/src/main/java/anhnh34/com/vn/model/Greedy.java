package anhnh34.com.vn.model;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

public class Greedy {

	final static Logger logger = Logger.getLogger(Greedy.class);
	

	public PartialSolution getPartialSolution() {
		return partialSolution;
	}

	public void setPartialSolution(PartialSolution partialSolution) {
		this.partialSolution = partialSolution;
	}

	public void setContainer(Container container) {
		this.container = container;
	}

	public String getAlgorithm() {
		return algorithm;
	}

	public void setAlgorithm(String algorithm) {
		this.algorithm = algorithm;
	}

	public Container getContainer() {
		return container;
	}

	public String getSelectedRotation() {
		return selectedRotation;
	}

	protected void setSelectedRotation(String rotation) {
		this.selectedRotation = rotation;
	}

	public List<Space> getAvaiableSpaces() {
		return avaiableSpaces;
	}

	public Batch getNotPlacedBoxes() {
		return notPlacedBoxes;
	}

	public void setNotPlacedBoxes(Batch notPlacedBoxes) {	
		this.notPlacedBoxes.getBoxes().clear();
		for(Box box : notPlacedBoxes.getBoxes()) {
			Box nBox = new Box(box);
			this.notPlacedBoxes.addBox(nBox);
		}
	}
	
	public void setNotPlacedBoxes(List<Box> notPlacedBoxes) {	
		this.notPlacedBoxes.getBoxes().clear();
		for(Box box : notPlacedBoxes) {
			Box nBox = new Box(box);
			this.notPlacedBoxes.addBox(nBox);
		}
	}

	public void setNSupportRatio(double nSupportRatio) {
		this.notSupportRatio = nSupportRatio;
	}

	public double getNSupportRatio() {
		return notSupportRatio;
	}

	public Batch getPlacedBoxes() {
		return placedBoxes;
	}

	public void setPlacedBoxes(Batch placedBoxes) {
		this.placedBoxes.getBoxes().clear();
		for(Box box : placedBoxes.getBoxes()) {
			Box nBox = new Box(box);
			this.placedBoxes.addBox(nBox);
		}
	}

	public void setAvaiableSpaces(List<Space> avaiableSpaces) {	
		this.getAvaiableSpaces().clear();
		for (Space space : avaiableSpaces) {
			Space nSpace = new Space(space.getMinimum(), space.getMaximum(), space.getRatioSupport(),
					space.getMaximumSupportX(), space.getMaximumSupportY());
			
			this.getAvaiableSpaces().add(nSpace);
		}
		
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
		logger.setLevel(Level.FATAL);
		this.candidates = new ArrayList<BoxCandidate>();
		this.avaiableSpaces = new ArrayList<Space>();
		this.placedBoxes = new Batch();
		this.notPlacedBoxes = new Batch();
		this.loadParameters();
	}

	public void loadParameters() {
		String nSupportParam = Utility.getInstance().getConfigValue("none_support_constraint");
		String fragilityParam = Utility.getInstance().getConfigValue("fragility_constraint");
		String lifoParam = Utility.getInstance().getConfigValue("lifo_constraint");
		String algorithmParam = Utility.getInstance().getConfigValue("algorithm");
		String selectedRotation = Utility.getInstance().getConfigValue("selected_rotation");

		this.setAlgorithm(algorithmParam);
		this.setSelectedRotation(selectedRotation);
		this.isOverhang = "0".equals(nSupportParam) ? false : true;
		this.isFragility = "0".equals(fragilityParam) ? false : true;
		this.isLifo = "0".equals(lifoParam) ? false : true;

		if (isOverhang) {
			this.setNSupportRatio(Double.valueOf(nSupportParam));
		}
	}

	private void createCandiates(Space space, Box box) {
		int k = 1;
		for (Box boxJ : this.getNotPlacedBoxes().getBoxes()) {
			if (boxJ.getBoxType() == box.getBoxType() && boxJ.getId() != box.getId()) {
				k++;
			}
		}
		BoxCandidate candidate = new BoxCandidate(box, space, k);
		candidate.initialize(this.getAlgorithm());
		candidates.add(candidate);
	}

	public BoxCandidate stAlgorithm() {
		if (roundNumber == 3) {
			System.out.println("Debug");
		}
		candidates = this.mainCriterion();

		if (candidates.size() == 1) {
			return candidates.get(0);
		}

		candidates = this.firstTieBreaker();
		if (candidates.size() == 1) {
			return candidates.get(0);
		}

		candidates = this.secondTieBreaker();
		if (candidates.size() == 1) {
			return candidates.get(0);
		}

		candidates = this.thirdTieBreaker();
		if (candidates.size() == 1) {
			return candidates.get(0);
		}

		for (BoxCandidate candidate : candidates) {
			if (this.getSelectedRotation()
					.compareToIgnoreCase(candidate.getBox().getSelectedRotation().getRotationCode()) == 0) {
				return candidate;
			}
		}

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

		if (candidates.size() == 1) {
			return candidates.get(0);
		}

		candidates = this.thirdTieBreaker();

		if (candidates.size() == 1) {
			return candidates.get(0);
		}

		for (BoxCandidate candidate : candidates) {
			logger.info("selected rotation: " + candidate.getBox().getSelectedRotation().getRotationCode());
			if (this.getSelectedRotation()
					.compareToIgnoreCase(candidate.getBox().getSelectedRotation().getRotationCode()) == 0) {
				return candidate;
			}
		}

		return candidates.get(0);
	}

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
				smallestLengthwise = c.getLengthwiseProtrustion();
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
				largestBoxVolume = c.getBoxVolume();
				helperList.clear();
				helperList.add(c);
			}
		}
		return helperList;
	}

	private List<BoxCandidate> thirdTieBreaker() {
		List<BoxCandidate> helperList = new ArrayList<BoxCandidate>();
		Double minSpaceY = candidates.get(0).getSpace().getMinimum().getY();

		for (BoxCandidate c : candidates) {
			if (c.getSpace().getMinimum().getY() == minSpaceY) {
				helperList.add(c);
			}

			if (minSpaceY > c.getSpace().getMinimum().getY()) {
				minSpaceY = c.getSpace().getMinimum().getY();
				helperList.clear();
				helperList.add(c);
			}
		}
		return helperList;
	}

	public void updateSpaces(FeasibleObject obj) {
		logger.info("---Start method update Spaces---");
		Box box = obj.getBox();
		Space space = obj.getSpace();

		Space frontSpace = generateFrontSpace(box, space);
		Space rightSpace = generateRightSpace(box, space);
		Space aboveSpace = generateAboveSpace(box, space);

		// remove old space
		this.getAvaiableSpaces().remove(space);

		// add all new generate spaces.
		this.addEmptySpace(frontSpace);
		this.addEmptySpace(rightSpace);
		this.addEmptySpace(aboveSpace);

		this.showSpaceInfo("front space:", frontSpace);
		this.showSpaceInfo("right space", rightSpace);
		this.showSpaceInfo("above space", aboveSpace);

		this.removeBoxOverlap(box); // check overlappinupdateSpacesg space.

		this.amalgamation(); // Amalgamation

		this.removeSmallSpaces(); // removal too small spaces

		logger.info("=====Finish update spaces======");
		logger.info("\n");

	}

	private void addEmptySpace(Space s) {
		if (s != null && s.isValid()) {
			this.getAvaiableSpaces().add(s);
		}
	}

	private void removeBoxOverlap(Box box) {
		logger.info("Start remove box with overlap");
		List<Space> helperList = new ArrayList<>();
		List<Space> removeableList = new ArrayList<Space>();

		for (int i = 0; i < this.getAvaiableSpaces().size(); i++) {
			Space selectedSpace = this.getAvaiableSpaces().get(i);
			if (checkOverlapping(box, selectedSpace)) {
				removeableList.add(selectedSpace);
				if (isOverhang && box.getMinimum().getZ() > 0) {
					helperList.addAll(this.overhangUpdateOverlapSpaces(box, selectedSpace));
				} else {
					helperList.addAll(this.updateOverlapSpaces(box, selectedSpace));
				}
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
				if (this.isOverhang && s.getMinimum().getZ() > 0) {
					amalgateSpace = this.overlapAmalgamate(s, t);
				} else {
					amalgateSpace = this.amalgamate(s, t);
				}

				if (amalgateSpace != null) {
					this.amalgamateCount++;
					helperList.add(amalgateSpace);
				}
			}
		}

		if (helperList != null) {
			for (Space amalagateSpace : helperList) {
				this.showSpaceInfo("", amalagateSpace);
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

			result = new Space(minUDimension, maxUDimension, this.getNSupportRatio(), maxUDimension.getX(),
					maxUDimension.getY());
			logger.info("Amalgamate folow x direction");
			this.showSpaceInfo("", s);
			this.showSpaceInfo("", t);
			this.showSpaceInfo("result: ", result);
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

			result = new Space(minYuDimension, maxYuDimension, this.getNSupportRatio(), maxYuDimension.getX(),
					maxYuDimension.getY());
			logger.info("Amalgamate folow y direction");
			this.showSpaceInfo("", s);
			this.showSpaceInfo("", t);
			this.showSpaceInfo("result: ", result);
			logger.info("\n");

		}
		return result;
	}

	private void removeSubsets() {
		logger.info("\n");
		logger.info("===Method: removeSubsets (Start)===");
		logger.info("number of space: " + this.getAvaiableSpaces().size());
		List<Space> helperList = new ArrayList<Space>();
		for (int i = 0; i < this.getAvaiableSpaces().size() - 1; i++) {
			Space s = this.getAvaiableSpaces().get(i);
			for (int j = i + 1; j < this.getAvaiableSpaces().size(); j++) {

				// check if space s is a subset of space t
				Space t = this.getAvaiableSpaces().get(j);

				if (checkSubsetSpace(s, t)) {
					showSpaceInfo("S's space: ", s);
					showSpaceInfo("t's space: ", t);
					helperList.add(s);
					continue;
				}

				// check if space t is a subset of space s
				if (checkSubsetSpace(t, s)) {
					showSpaceInfo("S's space: ", s);
					showSpaceInfo("t's space: ", t);
					helperList.add(t);
					continue;
				}
			}
		}
		this.avaiableSpaces.removeAll(helperList);
		logger.info("number of space: " + this.getAvaiableSpaces().size());
		logger.info("===Method: removeSubsets (End) ===");
	}

	private boolean checkSubsetSpace(Space s, Space t) {
		if ((s.getMinimum().compare(t.getMinimum()) == true) && (s.getMaximum().compare(t.getMaximum()) == true)
				&& (s.getMaximumSupportX() <= t.getMaximumSupportX())
				&& (s.getMaximumSupportY() <= t.getMaximumSupportY())) {
			return true;
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

		Space behindSpace = new Space(behindMinimumDimension, behindMaximumDimension, this.getNSupportRatio(),
				behindMaximumDimension.getX(), behindMaximumDimension.getY());

		// create front space.
		Dimension frontMinimumDimension = new Dimension(maxBoxDimension.getX(), minSpaceDimension.getY(),
				minSpaceDimension.getZ());

		Dimension frontMaximumDimension = maxSpaceDimension;
		Space frontSpace = new Space(frontMinimumDimension, frontMaximumDimension, this.getNSupportRatio(),
				frontMaximumDimension.getX(), frontMaximumDimension.getY());

		// create left space.
		Dimension leftMinimumDimension = minSpaceDimension;
		Dimension leftMaximumDimension = new Dimension(maxSpaceDimension.getX(), minBoxDimension.getY(),
				maxSpaceDimension.getZ());
		Space rightSpace = new Space(leftMinimumDimension, leftMaximumDimension, this.getNSupportRatio(),
				leftMaximumDimension.getX(), leftMaximumDimension.getY());

		// create right space.
		Dimension rightMinimumDimension = new Dimension(minSpaceDimension.getX(), maxBoxDimension.getY(),
				minSpaceDimension.getZ());
		Dimension rightMaximumDimension = maxSpaceDimension;
		Space leftSpace = new Space(rightMinimumDimension, rightMaximumDimension, this.getNSupportRatio(),
				rightMaximumDimension.getX(), rightMaximumDimension.getY());

		if (behindSpace.isValid()) {
			result.add(behindSpace);
			showCuboidInfo("behind space: ", behindSpace);
		}

		if (frontSpace.isValid()) {
			result.add(frontSpace);
			showCuboidInfo("frontSpace: ", frontSpace);
			// =showCuboidInfo(frontSpace);
		}

		if (leftSpace.isValid()) {
			showCuboidInfo("leftSpace: ", leftSpace);
			// showCuboidInfo(leftSpace);
			result.add(leftSpace);
		}

		if (rightSpace.isValid()) {
			logger.info("right space");
			this.showSpaceInfo("rightspace", rightSpace);
			result.add(rightSpace);
		}

		logger.info("===End Update Overlap Space===");
		logger.info("\n\n");
		return result;

	}

	private void removeSmallSpaces() {
		logger.info("Start method remove small spaces");
		double smallestDimension = this.getSmallestDimension();
		logger.info("Smallest dimension: " + smallestDimension);
		List<Space> removeAbleSpace = new ArrayList<Space>();

		for (int i = 0; i < this.getAvaiableSpaces().size(); i++) {
			Space s = this.getAvaiableSpaces().get(i);
			if ((s.getMaximum().getZ() - s.getMinimum().getZ()) < smallestDimension) {
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
							&& s.getMinimum().getZ() <= t.getMinimum().getZ()) {
						fExist = true;
					}

					// selected s space can be delete if exist's flag equal
					// false.
					if (fExist == false) {
						showSpaceInfo("s", s);
						removeAbleSpace.add(s);
					}
				}
			}
		}

		// removal of too small spaces
		this.getAvaiableSpaces().removeAll(removeAbleSpace);
		logger.info("End method remove small spaces");
	}

	private double getSmallestDimension() {
		double smallestDimension = 0;

//		if (this.containerLoading.getNotPlacedBox().getBoxes().size() > 0) {
//			smallestDimension = this.containerLoading.getNotPlacedBox().getBoxes().get(0).getSmallestDimension();
//		}
//
//		for (Box box : this.containerLoading.getNotPlacedBox().getBoxes()) {
//			smallestDimension = smallestDimension > box.getSmallestDimension() ? box.getSmallestDimension()
//					: smallestDimension;
//		}
		if (this.getNotPlacedBoxes().getBoxes().size() > 0) {
			smallestDimension = this.getNotPlacedBoxes().getBoxes().get(0).getSmallestDimension();
		}

		for (Box box : this.getNotPlacedBoxes().getBoxes()) {
			smallestDimension = smallestDimension > box.getSmallestDimension() ? box.getSmallestDimension()
					: smallestDimension;
		}
		
		return smallestDimension;
	}

	public FeasibleObject findBox_Test() {
		List<BoxCandidate> feasibleCandidates = new ArrayList<BoxCandidate>();

		for (Box box : this.getNotPlacedBoxes().getBoxes()) {
			for (Space space : this.getAvaiableSpaces()) {
				if (roundNumber == 2) {
					System.out.println("Round number: " + roundNumber);
				}

				List<String> rotations = this.findRotations(box, space);

				if (rotations.isEmpty()) {
					continue;
				}

				for (String rotation : rotations) {
					Box tBox = new Box(box);
					tBox.setPossibleRotations(rotations);
					tBox.setSelectedRotation(rotation);
					this.updateBoxPosition(tBox, space);

					if (isLifo && isMultiDropFeasiblePacking(tBox) == false) {
						continue;
					}

					BoxCandidate candidate = new BoxCandidate(tBox, space, 0);
					feasibleCandidates.add(candidate);
				}
			}
		}

		BoxCandidate bestCandidate = this.findBestSpaceBs(feasibleCandidates);
		if (bestCandidate == null) {
			return null;
		}

		return new FeasibleObject(bestCandidate.getBox(), bestCandidate.getBox().getSelectedRotation(),
				bestCandidate.getSpace());
	}

	public void findBox() {
		boolean isBoxFound = true;
		while (true) {
			List<BoxCandidate> feasibleCandidates = new ArrayList<BoxCandidate>();
			for (BoxType bt : this.getNotPlacedBoxes().getBoxTypes()) {
				if (bt.getBoxes().size() == 0) {
					continue;
				}

				for (Space s : this.getAvaiableSpaces()) {
					Box box = bt.getBoxes().get(0);
					List<String> rotations = this.findRotations(box, s);

					if (rotations.isEmpty()) {
						continue;
					}

					for (String rotation : rotations) {
						Box tBox = new Box(box);
						tBox.setPossibleRotations(rotations);
						tBox.setSelectedRotation(rotation);
						this.updateBoxPosition(tBox, s);
						if (isLifo && isMultiDropFeasiblePacking(tBox) == false) {
							continue;
						}

						BoxCandidate candidate = new BoxCandidate(tBox, s, 0);
						feasibleCandidates.add(candidate);
					}
				}

				if (feasibleCandidates.isEmpty()) {
					continue;
				}
			}

			BoxCandidate bestCandidate = this.findBestSpaceBs(feasibleCandidates);

			if (bestCandidate == null || this.avaiableSpaces.isEmpty()
					|| this.getNotPlacedBoxes().getBoxNumber() == 0) {
				isBoxFound = false;
				break;
			}

			if (isBoxFound == true) {

				// get Feasible Object
				Box selectedBox = bestCandidate.getBox();
				Space selectedSpace = bestCandidate.getSpace();
				String selectedRotation = bestCandidate.getBox().getSelectedRotation().getRotationCode();

				Dimension maximumDimension = getMaximumDimension(selectedRotation, selectedSpace.getMinimum(),
						selectedBox);
				bestCandidate.getBox().setMaximum(maximumDimension);

				logger.info("founded feasible Object Info: ");
				this.showSpaceInfo("Founded space", selectedSpace);
				this.showBoxInfo("Founded box", selectedBox);

				this.getNotPlacedBoxes().removeBox(selectedBox);
				this.getPlacedBoxes().getBoxes().add(selectedBox);
				this.reloadSequenceNumberTest(selectedBox);
			}
		}
	}

	public FeasibleObject greedyStAlgorithm() {
		this.clearCandidates();
		this.showSpaceList();
		this.showBoxList(this.getNotPlacedBoxes().getBoxes());

		for (Box box : this.getNotPlacedBoxes().getBoxes()) {
			for (Space space : this.getAvaiableSpaces()) {
				List<String> rotations = this.findRotations(box, space);

				if (this.roundNumber == 5) {
					System.out.println("Debug");
				}
				if (rotations.isEmpty()) {
					continue;
				}

				box.setPossibleRotations(rotations);
				String possibleRotation = "";
				this.showSpaceInfo("space info", space);
				for (String rotation : rotations) {
					possibleRotation = possibleRotation.concat(" ," + rotation);
					Box tempBox = new Box(box);
					tempBox.setSelectedRotation(rotation);
					this.updateBoxPosition(tempBox, space);

					if (isLifo && this.isMultiDropFeasiblePacking(tempBox) == false) {
						continue;
					}

					if (checkBoxBelowIsFragility(space)) {
						if (tempBox.isFragile() == false) {
							continue;
						}
					}

					this.createCandiates(space, tempBox);
				}
				this.showSpaceInfo("", space);
				logger.info("possible rotation: " + possibleRotation);
			}
		}

		BoxCandidate candidate = this.findBestFittedBox();
		if (candidate == null) {
			return null;
		}

		return new FeasibleObject(candidate.getBox(), candidate.getSpace());
	}

	public FeasibleObject greedyVlAlgorithm() {

		this.clearCandidates();
		this.showSpaceList();
		this.showBoxList(this.getNotPlacedBoxes().getBoxes());

		for (Box box : this.getNotPlacedBoxes().getBoxes()) {
			for (Space space : this.getAvaiableSpaces()) {
				List<String> rotations = this.findRotations(box, space);

				if (this.roundNumber == 5) {
					System.out.println("Debug");
				}
				if (rotations.isEmpty()) {
					continue;
				}

				box.setPossibleRotations(rotations);

				for (String rotation : rotations) {
					Box tempBox = new Box(box);
					tempBox.setSelectedRotation(rotation);
					this.updateBoxPosition(tempBox, space);

					if (isLifo && this.isMultiDropFeasiblePacking(tempBox) == false) {
						continue;
					}

					if (checkBoxBelowIsFragility(space)) {
						if (tempBox.isFragile() == false) {
							continue;
						}
					}

					this.createCandiates(space, tempBox);
				}
			}
		}

		BoxCandidate candidate = this.findBestFittedBox();
		if (candidate == null) {
			return null;
		}

		return new FeasibleObject(candidate.getBox(), candidate.getSpace());

	}

	public FeasibleObject greedySbAlgorithm() {
		this.avaiableSpaces.sort(new SpaceComparator());
		this.getNotPlacedBoxes().getBoxes().sort(new BoxComparator());

		this.showSpaceList();
		this.showBoxList(this.getNotPlacedBoxes().getBoxes());

		for (Space space : this.avaiableSpaces) {
			for (Box box : this.getNotPlacedBoxes().getBoxes()) {
				List<String> rotations = this.findRotations(box, space);

				if (rotations.isEmpty()) {
					continue;
				}
				box.setPossibleRotations(rotations);
				box.setSelectedRotation(rotations.get(0));
				Box tempBox = new Box(box);
				this.updateBoxPosition(tempBox, space);

				Rotation selectedRotation = null;
				for (Rotation r : tempBox.getPossibleRotations()) {
					if (this.getSelectedRotation().compareToIgnoreCase(r.getRotationCode()) == 0) {
						selectedRotation = r;
						tempBox.setSelectedRotation(r);
						break;
					}
				}

				if (selectedRotation == null) {
					tempBox.setSelectedRotation(tempBox.getPossibleRotations().get(0));
				}

				if (isLifo && isMultiDropFeasiblePacking(tempBox) == false) {
					continue;
				}

				if (checkBoxBelowIsFragility(space)) {
					if (tempBox.isFragile() == false) {
						continue;
					}
				}

				return new FeasibleObject(tempBox, space);
			}
		}
		return null;
	}

	public FeasibleObject greedyBsAlgorithm() {

		this.avaiableSpaces.sort(new SpaceComparator());
		this.getNotPlacedBoxes().getBoxes().sort(new BoxComparator());

		logger.info("Space list");
		this.showSpaceList();

		logger.info("\n");
		logger.info("Boxes list");
		for (Box box : this.getNotPlacedBoxes().getBoxes()) {
			this.showBoxInfo("box:", box);
		}

		// List<BoxCandidate> feasibleCandidates = new ArrayList<BoxCandidate>();
		for (Box box : this.getNotPlacedBoxes().getBoxes()) {
			for (Space space : this.getAvaiableSpaces()) {
				if (roundNumber == 5) {
					System.out.println("Round number: " + roundNumber);
				}

				List<String> rotations = this.findRotations(box, space);
				String rotationString = "";
				for (String rotation : rotations) {
					rotationString = rotationString.concat(", " + rotation);
				}

				logger.info(String.format("Box %f %f %f %s", new Object[] { box.getBiggestDimension(),
						box.getMiddleDimension(), box.getSmallestDimension(), rotationString }));
				logger.info(String.format("Space: %f %f %f",
						new Object[] { space.getLength(), space.getWidth(), space.getHeight() }));

				if (rotations.isEmpty()) {
					continue;
				}

				Box tBox = new Box(box);

				tBox.setPossibleRotations(rotations);
				Rotation selectedRotation = null;
				for (Rotation r : tBox.getPossibleRotations()) {
					if (this.getSelectedRotation().compareToIgnoreCase(r.getRotationCode()) == 0) {
						selectedRotation = r;
						tBox.setSelectedRotation(r);
						break;
					}
				}

				if (selectedRotation == null) {
					tBox.setSelectedRotation(tBox.getPossibleRotations().get(0));
				}

				// logger.info("selected rotation: " +
				// tBox.getSelectedRotation().getRotationCode());
				this.updateBoxPosition(tBox, space);

				if (isLifo && isMultiDropFeasiblePacking(tBox) == false) {
					continue;
				}

				if (checkBoxBelowIsFragility(space)) {
					if (tBox.isFragile() == false) {
						continue;
					}
				}

				return new FeasibleObject(tBox, tBox.getSelectedRotation(), space);
			}
		}
		return null;
	}

	public List<FeasibleObject> basicGreedyAlgorithm(Box selectedBox) {
		List<FeasibleObject> feasibleList = new ArrayList<FeasibleObject>();
		selectedBox = this.getNotPlacedBoxes().getBoxes().get(0);
		for (Space space : this.getAvaiableSpaces()) {
			List<String> rotations = this.findRotations(selectedBox, space);
			if (rotations.isEmpty()) {
				continue;
			}

			selectedBox.setPossibleRotations(rotations);
			for (String rotation : rotations) {

				Box tempBox = new Box(selectedBox);

				tempBox.setSelectedRotation(rotation);

				this.updateBoxPosition(tempBox, space);

				if (isLifo && this.isMultiDropFeasiblePacking(tempBox) == false) {
					continue;
				}

				if (checkBoxBelowIsFragility(space)) {
					if (tempBox.isFragile() == false) {
						continue;
					}
				}
				FeasibleObject feasibleObj = new FeasibleObject(tempBox, space);
				feasibleList.add(feasibleObj);
			}

		}
		return feasibleList;

	}

	private BoxCandidate findBestSpaceBs(List<BoxCandidate> candidates) {
		this.clearCandidates();
		for (BoxCandidate candidate : candidates) {
			this.createCandiates(candidate.getSpace(), candidate.getBox());
		}

		return this.findBestFittedBox();
	}

	private BoxCandidate findBestFittedBox() {
		if (candidates.size() == 0) {
			return null;
		}

		switch (this.getAlgorithm()) {
		case Constant.GREEDY_ST:
			return this.stAlgorithm();
		case Constant.GREEDY_VL:
			return this.vlAlgorithm();
		default:
			return null;
		}
	}

	private List<String> findRotations(Box box, Space space) {
		// loop all possible rotations.
		int[] rotations = box.getfRotation();
		List<String> fesRotations = new ArrayList<String>();
		// can be rotation by X.
		if (rotations[0] == 1) {

			// XYZ
			if (space.getLength() >= box.getBiggestDimension() && space.getWidth() >= box.getMiddleDimension()
					&& space.getHeight() >= box.getSmallestDimension()) {
				fesRotations.add(Rotation.XYZ);
			}

			// XZY
			if (space.getLength() >= box.getBiggestDimension() && space.getWidth() >= box.getSmallestDimension()
					&& space.getHeight() >= box.getMiddleDimension()) {
				fesRotations.add(Rotation.XZY);
			}
		}
		// can be rotation by Y.
		if (rotations[1] == 1) {
			// YXZ
			if (space.getWidth() >= box.getBiggestDimension() && space.getLength() >= box.getMiddleDimension()
					&& space.getHeight() >= box.getSmallestDimension()) {
				fesRotations.add(Rotation.YXZ);
			}

			// YZX
			if (space.getWidth() >= box.getBiggestDimension() && space.getLength() >= box.getSmallestDimension()
					&& space.getHeight() >= box.getMiddleDimension()) {
				fesRotations.add(Rotation.YZX);
			}
		}
		// can be rotation by Z.
		if (rotations[2] == 1) {
			// ZXY
			if (space.getHeight() >= box.getBiggestDimension() && space.getLength() >= box.getMiddleDimension()
					&& space.getWidth() >= box.getSmallestDimension()) {
				fesRotations.add(Rotation.ZXY);
			}
			// ZYX
			if (space.getHeight() >= box.getBiggestDimension() && space.getWidth() >= box.getMiddleDimension()
					&& space.getLength() >= box.getSmallestDimension()) {
				fesRotations.add(Rotation.ZYX);
			}
		}
		return fesRotations;
	}

	public void update(FeasibleObject feasObj) {
		// get Feasible Object
		Box selectedBox = feasObj.getBox();
		Space selectedSpace = feasObj.getSpace();
		String selectedRotation = feasObj.getBox().getSelectedRotation().getRotationCode();

		logger.info("selected rotation: " + feasObj.getBox().getSelectedRotation().getRotationCode());
		Dimension maximumDimension = getMaximumDimension(selectedRotation, selectedSpace.getMinimum(), selectedBox);
		feasObj.getBox().setMaximum(maximumDimension);

		logger.info("founded feasible Object Info: ");
		this.showSpaceInfo("Founded space", selectedSpace);
		this.showBoxInfo("Founded box", selectedBox);
		this.getNotPlacedBoxes().removeBox(selectedBox);
		this.getPlacedBoxes().getBoxes().add(selectedBox);
		this.reloadSequenceNumber(selectedBox);
	}

	private Dimension getMaximumDimension(String selectedRotation, Dimension minimum, Box selectedBox) {
		Dimension maximumDimension = null;
		switch (selectedRotation) {
		case Rotation.XYZ:
			maximumDimension = minimum.addDimension(new Dimension(selectedBox.getBiggestDimension(),
					selectedBox.getMiddleDimension(), selectedBox.getSmallestDimension()));

			selectedBox.setLength(selectedBox.getBiggestDimension());
			selectedBox.setWidth(selectedBox.getMiddleDimension());
			selectedBox.setHeight(selectedBox.getSmallestDimension());
			break;
		case Rotation.XZY:
			maximumDimension = minimum.addDimension(new Dimension(selectedBox.getBiggestDimension(),
					selectedBox.getSmallestDimension(), selectedBox.getMiddleDimension()));

			selectedBox.setLength(selectedBox.getBiggestDimension());
			selectedBox.setWidth(selectedBox.getSmallestDimension());
			selectedBox.setHeight(selectedBox.getMiddleDimension());
			break;
		case Rotation.YXZ:
			maximumDimension = minimum.addDimension(new Dimension(selectedBox.getMiddleDimension(),
					selectedBox.getBiggestDimension(), selectedBox.getSmallestDimension()));

			selectedBox.setWidth(selectedBox.getBiggestDimension());
			selectedBox.setLength(selectedBox.getMiddleDimension());
			selectedBox.setHeight(selectedBox.getSmallestDimension());

			break;
		case Rotation.YZX:
			maximumDimension = minimum.addDimension(new Dimension(selectedBox.getSmallestDimension(),
					selectedBox.getBiggestDimension(), selectedBox.getMiddleDimension()));

			selectedBox.setWidth(selectedBox.getBiggestDimension());
			selectedBox.setLength(selectedBox.getSmallestDimension());
			selectedBox.setHeight(selectedBox.getMiddleDimension());
			break;
		case Rotation.ZXY:
			maximumDimension = minimum.addDimension(new Dimension(selectedBox.getMiddleDimension(),
					selectedBox.getSmallestDimension(), selectedBox.getBiggestDimension()));

			selectedBox.setLength(selectedBox.getMiddleDimension());
			selectedBox.setWidth(selectedBox.getSmallestDimension());
			selectedBox.setHeight(selectedBox.getBiggestDimension());
			break;
		case Rotation.ZYX:
			maximumDimension = minimum.addDimension(new Dimension(selectedBox.getSmallestDimension(),
					selectedBox.getMiddleDimension(), selectedBox.getBiggestDimension()));

			selectedBox.setLength(selectedBox.getSmallestDimension());
			selectedBox.setWidth(selectedBox.getMiddleDimension());
			selectedBox.setHeight(selectedBox.getBiggestDimension());
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
			this.showBoxInfo("", box);
		}

		logger.info("List of not placed box: " + this.getNotPlacedBoxes().getBoxes().size());

		for (Box box : this.getNotPlacedBoxes().getBoxes()) {
			this.showBoxInfo("", box);
		}
		logger.info("\n");

		logger.info(String.format("Volume utilisation: %.2f",
				new Object[] { this.totalBoxVolumes / this.containerVolumes * 100 }));

		logger.info(String.format(String.format("Number of amalgamation: %d", this.amalgamateCount)));
		Utility.getInstance().writeResultToFile(this.placedBoxes.getBoxes()); // write result to file.

	}

	public void showCuboidInfo(String name, Cuboid c) {
		if (c.getMinimumPoint() == null && c.getMaximumPoint() == null) {
			logger.info(String.format("%s l: %.2f, w: %.2f, h: %.2f%, v: %.2f, mi: null  ma: null",
					new Object[] { name, c.getLength(), c.getWidth(), c.getHeigth(), c.getVolume() }));
			return;
		}

		if (c.getMinimumPoint() == null) {
			logger.info(String.format("%s l: %.2f, w: %.2f, h: %.2f, v: %.2f mi: null ma(%.2f, %.2f, %.2f)",
					new Object[] { name, c.getLength(), c.getWidth(), c.getHeigth(), c.getVolume(),
							c.getMaximumPoint().getX(), c.getMaximumPoint().getY(), c.getMaximumPoint().getZ() }));
			return;
		}

		if (c.getMaximumPoint() == null) {
			logger.info(String.format("%s l: %.2f, w: %.2f, h: %.2f, v: %2.f mi(%.2f, %.2f, %.2f) ma: null",
					new Object[] { name, c.getLength(), c.getWidth(), c.getHeigth(), c.getVolume(),
							c.getMinimumPoint().getX(), c.getMinimumPoint().getY(), c.getMinimumPoint().getZ() }));
			return;
		}

		logger.info(String.format("%s l: %.2f, w: %.2f, h: %.2f, v: %.2f  mi(%.2f, %.2f, %.2f) ma(%.2f, %.2f, %.2f)",
				new Object[] { name == null ? "" : name, c.getLength(), c.getWidth(), c.getHeigth(), c.getVolume(),
						c.getMinimumPoint().getX(), c.getMinimumPoint().getY(), c.getMinimumPoint().getZ(),
						c.getMaximumPoint().getX(), c.getMaximumPoint().getY(), c.getMaximumPoint().getZ() }));
	}

	public void showSpaceInfo(String name, Space s) {
		if (s == null) {
			return;
		}

		if (s.getMinimumPoint() == null && s.getMaximumPoint() == null) {
			logger.info(String.format("%s l: %.2f, w: %.2f, h: %.2f%, v: %.2f a:%2f  b:%2f",
					new Object[] { name, s.getLength(), s.getWidth(), s.getHeigth(), s.getVolume(),
							s.getMaximumSupportX(), s.getMaximumSupportY() }));
			return;
		}

		if (s.getMinimumPoint() == null) {
			logger.info(
					String.format("%s l: %.2f, w: %.2f, h: %.2f, v: %.2f a:%2f  b:%2f mi: null ma(%.2f, %.2f, %.2f)",
							new Object[] { name, s.getLength(), s.getWidth(), s.getHeigth(), s.getVolume(),
									s.getMaximumSupportX(), s.getMaximumSupportY(), s.getMaximumPoint().getX(),
									s.getMaximumPoint().getY(), s.getMaximumPoint().getZ() }));
			return;
		}

		if (s.getMaximumPoint() == null) {
			logger.info(
					String.format("%s l: %.2f, w: %.2f, h: %.2f, v: %2.f a:%2f  b:%2f mi(%.2f, %.2f, %.2f) ma: null",
							new Object[] { name, s.getLength(), s.getWidth(), s.getHeigth(), s.getVolume(),
									s.getMaximumSupportX(), s.getMaximumSupportY(), s.getMinimumPoint().getX(),
									s.getMinimumPoint().getY(), s.getMinimumPoint().getZ() }));
			return;
		}

		logger.info(String.format(
				"%s l: %.2f, w: %.2f, h: %.2f, v: %.2f a:%2f  b:%2f   mi(%.2f, %.2f, %.2f) ma(%.2f, %.2f, %.2f)",
				new Object[] { name == null ? "" : name, s.getLength(), s.getWidth(), s.getHeigth(), s.getVolume(),
						s.getMaximumSupportX(), s.getMaximumSupportY(), s.getMinimumPoint().getX(),
						s.getMinimumPoint().getY(), s.getMinimumPoint().getZ(), s.getMaximumPoint().getX(),
						s.getMaximumPoint().getY(), s.getMaximumPoint().getZ() }));

	}

	public void showBoxInfo(String name, Box box) {
		String stringFormat = "%s - id: %s, bt: %s,dimen: %f, suface: %f, l: %.2f, w: %.2f, h: %.2f, v: %.2f, sq: %d, fra: %b";

		if (box.getMinimumPoint() != null) {
			stringFormat = stringFormat.concat(" ,mi(%.2f, %.2f, %.2f)");
		}

		if (box.getMaximumPoint() != null) {
			stringFormat = stringFormat.concat(" ,ma(%.2f, %.2f, %.2f)");
		}

		logger.info(String.format(stringFormat,
				new Object[] { name, box.getCustomerId(), box.getBoxType(), box.getBiggestDimension(),
						box.getLargestSurface(), box.getLength(), box.getWidth(), box.getHeigth(), box.getVolume(),
						box.getSequenceNumber(), box.isFragile(),
						box.getMinimum() == null ? -1 : box.getMinimum().getX(),
						box.getMinimum() == null ? -1 : box.getMinimum().getY(),
						box.getMinimum() == null ? -1 : box.getMinimum().getZ(),
						box.getMaximum() == null ? -1 : box.getMaximum().getX(),
						box.getMaximum() == null ? -1 : box.getMaximum().getY(),
						box.getMaximum() == null ? -1 : box.getMaximum().getZ() }));
	}

	public void sortSpaces() {
		// check spaces's size.
		if (avaiableSpaces.isEmpty() || avaiableSpaces.size() == 1)
			return;
		Space[] avaiableSpaceList = this.avaiableSpaces.toArray(new Space[0]);
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

	public void showSpaceList() {
		for (Space s : this.getAvaiableSpaces()) {
			this.showSpaceInfo("", s);
		}
		logger.info("\n");
	}

	public void showBoxList(List<Box> boxlist) {
		for (Box b : boxlist) {
			this.showBoxInfo("", b);
		}
		logger.info("\n");
	}

	public void updateBoxPosition(Box selectedBox, Space space) {		
		Dimension maximumDimension = getMaximumDimension(selectedBox.getSelectedRotation().getRotationCode(),
				space.getMinimum(), selectedBox);
		if(space.getMinimum() == null) {
			this.showSpaceInfo("",space);
		}
		selectedBox.setMinimum(space.getMinimum());		
		selectedBox.setMaximum(maximumDimension);
	}

	// Generate above space
	private Space generateAboveSpace(Box box, Space space) {
		// check fragility
		if (isFragility == true && box.isFragile() == true) {
			return null;
		}

		Dimension maxBox = box.getMaximum();
		Dimension minSpace = space.getMinimum();
		Dimension maxSpace = space.getMaximum();

		double xMaxTemp = minSpace.getX() + box.getLength() * (1 + this.getNSupportRatio());
		if (roundNumber == 4) {
			logger.info(String.format("X max temp: %f, space max: %f", new Object[] { xMaxTemp, maxSpace.getX() }));
		}
		double xMax = maxSpace.getX() < xMaxTemp ? maxSpace.getX() : xMaxTemp;
		double yMaxTemp = minSpace.getY() + box.getWidth() * (1 + this.getNSupportRatio());
		double yAboveMaximum = maxSpace.getY() < yMaxTemp ? maxSpace.getY() : yMaxTemp;

		Dimension minimum = new Dimension(minSpace.getX(), minSpace.getY(), maxBox.getZ());
		Dimension maximum = new Dimension(xMax, yAboveMaximum, maxSpace.getZ());
		Space result = new Space(minimum, maximum);
		logger.info("maximum x: " + box.getMaximum().getX() + " max support box x: " + space.getMaximumSupportX());
		result.setMaximumSupportX(box.getMaximum().getX() < space.getMaximumSupportX() ? box.getMaximum().getX()
				: space.getMaximumSupportX());

		result.setMaximumSupportY(box.getMaximum().getY() < space.getMaximumSupportY() ? box.getMaximum().getY()
				: space.getMaximumSupportY());

		result.setRatioSupport(this.getNSupportRatio());

		return result;
	}

	private Space generateFrontSpace(Box box, Space space) {
		Dimension maxBox = box.getMaximum();
		Dimension minSpace = space.getMinimum();
		Dimension maxSpace = space.getMaximum();

		if (minSpace.getZ() == 0 || isOverhang == false) { // In case space is full support from below and no overhang
															// is
															// allow.
			// create new front space
			Dimension minimum = new Dimension(maxBox.getX(), minSpace.getY(), minSpace.getZ());
			Dimension maximum = maxSpace;

			return new Space(minimum, maximum, 0, maxSpace.getX(), maxSpace.getY());
		}

		double xFrontMaximum = (1 + getNSupportRatio()) * space.getMaximumSupportX()
				- maxBox.getX() * getNSupportRatio();
		Dimension minimum = new Dimension(maxBox.getX(), minSpace.getY(), minSpace.getZ());
		Dimension maximum = new Dimension(xFrontMaximum, maxSpace.getY(), maxSpace.getZ());
		return new Space(minimum, maximum, this.getNSupportRatio(), space.getMaximumSupportX(),
				space.getMaximumSupportY());

		// create new front space
		// Dimension minimum = new Dimension(maxBox.getX(), minSpace.getY(),
		// minSpace.getZ());
		// Dimension maximum = maxSpace;
		// return new Space(minimum, maximum);
	}

	private boolean checkBoxBelowIsFragility(Space space) {
		if (this.isFragility == false) {
			return false;
		}

		boolean checkFragility = false;

		for (Box box : this.placedBoxes.getBoxes()) {
			if (space.getMinimum().getZ() == box.getMaximum().getZ()
					&& box.getMaximum().getX() >= space.getMinimum().getX()
					&& space.getMaximum().getX() >= box.getMinimum().getX()
					&& box.getMaximum().getY() >= space.getMinimum().getY()
					&& space.getMaximum().getY() >= box.getMaximum().getY() && box.isFragile() == true) {

				checkFragility = true;
				break;

			}
		}

		return checkFragility;
	}

	private Space generateRightSpace(Box box, Space space) {
		Dimension minBox = box.getMinimum();
		Dimension maxBox = box.getMaximum();
		Dimension minSpace = space.getMinimum();
		Dimension maxSpace = space.getMaximum();

		if (minSpace.getZ() == 0) {
			Dimension minimum = new Dimension(minBox.getX(), maxBox.getY(), minBox.getZ());
			Dimension maximum = maxSpace;
			return new Space(minimum, maximum, 0, maxSpace.getX(), maxSpace.getY());
		}

		if (isOverhang) {
			Dimension minimum = new Dimension(minSpace.getX(), maxBox.getY(), minSpace.getZ());
			double yMaximum = (1 + this.getNSupportRatio()) * space.getMaximumSupportY()
					- maxBox.getY() * this.getNSupportRatio();
			Dimension maximum = new Dimension(maxSpace.getX(), yMaximum, maxSpace.getZ());
			return new Space(minimum, maximum, getNSupportRatio(), space.getMaximumSupportX(),
					space.getMaximumSupportY());
		}

		// create new right space
		Dimension minimum = new Dimension(minBox.getX(), maxBox.getY(), minBox.getZ());
		Dimension maximum = maxSpace;
		return new Space(minimum, maximum);

	}

	private List<Space> overhangUpdateOverlapSpaces(Box box, Space space) {

		logger.info("===updateOverlapSpaces===");

		if (this.getRoundNumber() == 3) {
			System.out.println("Debug");
		}
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

		behindSpace.setRatioSupport(getNSupportRatio());

		behindSpace.setMaximumSupportX(space.getMaximumSupportX() < minBoxDimension.getX() ? space.getMaximumSupportX()
				: minBoxDimension.getX());

		behindSpace.setMaximumSupportY(space.getMaximumSupportY());

		// create front space.
		Dimension frontMinimumDimension = new Dimension(maxBoxDimension.getX(), minSpaceDimension.getY(),
				minSpaceDimension.getZ());
		double tempFrontMaxX = space.getMaximumSupportX() * (1 + this.getNSupportRatio())
				- maxBoxDimension.getX() * this.getNSupportRatio();
		double frontMaximumX = tempFrontMaxX < maxSpaceDimension.getX() ? tempFrontMaxX : maxSpaceDimension.getX();
		Dimension frontMaximumDimension = new Dimension(frontMaximumX, maxSpaceDimension.getY(),
				maxSpaceDimension.getZ());
		Space frontSpace = new Space(frontMinimumDimension, frontMaximumDimension);
		frontSpace.setMaximumSupportX(space.getMaximumSupportX());
		frontSpace.setMaximumSupportY(space.getMaximumSupportY());

		// create left space.
		Dimension leftMinimumDimension = minSpaceDimension;
		Dimension leftMaximumDimension = new Dimension(maxSpaceDimension.getX(), minBoxDimension.getY(),
				maxSpaceDimension.getZ());
		Space leftSpace = new Space(leftMinimumDimension, leftMaximumDimension);
		leftSpace.setMaximumSupportX(space.getMaximumSupportX());
		leftSpace.setMaximumSupportY(space.getMaximumSupportY() < minBoxDimension.getY() ? space.getMaximumSupportY()
				: minBoxDimension.getY());

		// create right space.
		Dimension rightMinimumDimension = new Dimension(minSpaceDimension.getX(), maxBoxDimension.getY(),
				minSpaceDimension.getZ());
		double tempMaxRightDimension = space.getMaximumSupportY() * (1 + this.getNSupportRatio())
				- maxBoxDimension.getY() * this.getNSupportRatio();
		double maxRightDimension = tempMaxRightDimension < maxSpaceDimension.getY() ? tempMaxRightDimension
				: maxSpaceDimension.getY();
		Dimension rightMaximumDimension = new Dimension(maxSpaceDimension.getX(), maxRightDimension,
				maxSpaceDimension.getZ());
		Space rightSpace = new Space(rightMinimumDimension, rightMaximumDimension);
		rightSpace.setMaximumSupportX(space.getMaximumSupportX());
		rightSpace.setMaximumSupportY(space.getMaximumSupportY());

		// create below space
		Dimension belowMinimumDimension = minSpaceDimension;
		Dimension belowMaximumDimension = new Dimension(maxSpaceDimension.getX(), maxSpaceDimension.getY(),
				minBoxDimension.getZ());
		Space belowSpace = new Space(belowMinimumDimension, belowMaximumDimension);
		belowSpace.setMaximumSupportX(space.getMaximumSupportX());
		belowSpace.setMaximumSupportY(space.getMaximumSupportY());

		this.showBoxInfo("selected box", box);
		this.showSpaceInfo("selected space", space);
		this.showSpaceInfo("beind space", behindSpace);
		this.showSpaceInfo("front space", frontSpace);
		this.showSpaceInfo("", rightSpace);
		this.showSpaceInfo("", leftSpace);
		this.showSpaceInfo("below space", belowSpace);

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

	private Space overlapAmalgamate(Space s, Space t) {
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

			double maxX = s.getMaximum().getX() > t.getMaximum().getX() ? s.getMaximum().getX() : t.getMaximum().getX();
			double maxY = maxSupportY * (1 + this.getNSupportRatio()) - minY * this.getNSupportRatio();
			double maxZ = s.getMaximum().getZ() < t.getMaximum().getZ() ? s.getMaximum().getZ() : t.getMaximum().getZ();

			Dimension minimum = new Dimension(minX, minY, minZ);
			Dimension maximum = new Dimension(maxX, maxY, maxZ);
			space = new Space(minimum, maximum);
			space.setMaximumSupportX(maxSupportX);
			space.setMaximumSupportY(maxSupportY);
			logger.info("Amalgamate folow x direction");
			this.showSpaceInfo("", s);
			this.showSpaceInfo("", t);
			this.showSpaceInfo("", space);
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
			double maxY = s.getMaximum().getY() > t.getMaximum().getY() ? s.getMaximum().getY() : t.getMaximum().getY();
			double maxZ = s.getMaximum().getZ() < t.getMaximum().getZ() ? s.getMaximum().getZ() : t.getMaximum().getZ();

			Dimension minimum = new Dimension(minX, minY, minZ);
			Dimension maximum = new Dimension(maxX, maxY, maxZ);

			space = new Space(minimum, maximum);
			space.setMaximumSupportX(maxSupportX);
			space.setMaximumSupportY(maxSupportY);
			logger.info("Amalgamate folow y direction");
			this.showSpaceInfo("", s);
			this.showSpaceInfo("", t);
			this.showSpaceInfo("", space);
			return space;
		}
		return null;
	}

	private boolean checkPassageExist(Box box) {
		for (Box b : this.getPlacedBoxes().getBoxes()) {

			// double boxDistance = Math.sqrt(Math.pow(box.getMaximumPoint().getX(), 2) +
			// Math.pow(box.getMaximumPoint().getY(), 2));

			// double distance = Math.sqrt(Math.pow(b.getMinimum().getX(), 2) +
			// Math.pow(b.getMinimumPoint().getY(), 2));

			// if (boxDistance > distance) {
			// if (b.getMinimumPoint().getY() < selectedBox.getMaximumPoint().getY()
			// && selectedBox.getMinimumPoint().getY() < b.getMaximumPoint().getY()) {
			// if (selectedBox.getSequenceNumber() > b.getSequenceNumber()) {
			// return false;
			// }
			// }
			// }
			//
			// if (boxDistance < distance) {
			// if (b.getMinimumPoint().getY() < selectedBox.getMaximumPoint().getY()
			// && selectedBox.getMinimumPoint().getY() < b.getMaximumPoint().getY()) {
			// if (selectedBox.getSequenceNumber() < b.getSequenceNumber()) {
			// return false;
			// }
			// }
			// }
			// logger.info(String.format("sequence %d : %d", new Object[]
			// {box.getSequenceNumber(), b.getSequenceNumber()}));

			if (box.getMinimum().getY() < b.getMaximum().getY() && b.getMinimum().getY() < box.getMaximum().getY()
					&& (box.getMinimum().getZ() < b.getMaximum().getZ())) {

				if (box.getMaximum().getX() <= b.getMinimum().getX()) {
					if (box.getSequenceNumber() < b.getSequenceNumber()) {
						return false;
					}
				}

				if (box.getMinimum().getX() >= b.getMaximum().getX()) {
					if (box.getSequenceNumber() > b.getSequenceNumber()) {
						return false;
					}
				}

			}
			// if ((box.getMaximum().getX() <= b.getMinimum().getX())
			// && (box.getSequenceNumber() > b.getSequenceNumber())) {
			// return false;
			// }else {

			// }

		}
		return true;
	}

	private boolean checkNoneOverlappingPlane(Box box) {		
		for (Box b : this.getPlacedBoxes().getBoxes()) {
			this.showBoxInfo("Box", box);
			this.showBoxInfo("", b);
			if (box.getMinimum().getX() < b.getMaximum().getX() && b.getMinimum().getX() < box.getMaximum().getX()
					&& box.getMinimum().getY() < b.getMaximum().getY()
					&& b.getMinimum().getY() < box.getMinimum().getY()) {

				if (box.getSequenceNumber() > b.getSequenceNumber()) {
					return false;
				}
			}
		}
		return true;
	}

	private boolean isMultiDropFeasiblePacking(Box selectedBox) {
		if (roundNumber == 6) {
			System.out.println("Testing");
		}
		if (checkNoneOverlappingPlane(selectedBox) == false) {
			return false;
		}

		if (checkPassageExist(selectedBox) == false) {
			return false;
		}

		return true;

	}

	/* Clear all previous candidates */
	private void clearCandidates() {
		this.candidates.clear();
	}

	public int getRoundNumber() {
		return roundNumber;
	}

	public void setRoundNumber(int roundNumber) {
		this.roundNumber = roundNumber;
	}

	private void reloadSequenceNumber(Box box) {
		if (!isLifo) {
			return;
		}

		if (box.getSequenceNumber() > 0) {
			return;
		}

		box.setSequenceNumber(1);
		for (Box b : this.getNotPlacedBoxes().getBoxes()) {
			if (box.getCustomerId().compareTo(b.getCustomerId()) == 0) {
				b.setSequenceNumber(1);
			}
		}

		// update all sequence number
		for (Box b : this.getPlacedBoxes().getBoxes()) {
			if (b.getCustomerId() == box.getCustomerId()) {
				continue;
			}

			b.setSequenceNumber(b.getSequenceNumber() + 1);
			for (Box b1 : this.getNotPlacedBoxes().getBoxes()) {
				if (b.getCustomerId() == b1.getCustomerId()) {
					b1.setSequenceNumber(b.getSequenceNumber());
				}
			}
		}
	}

	private void reloadSequenceNumberTest(Box box) {
		if (!isLifo) {
			return;
		}

		if (box.getSequenceNumber() > 0) {
			return;
		}

		box.setSequenceNumber(1);

		for (BoxType boxType : this.notPlacedBoxes.getBoxTypes()) {
			for (Box b : boxType.getBoxes()) {
				if (box.getCustomerId().compareTo(b.getCustomerId()) == 0) {
					b.setSequenceNumber(1);
				}
			}
		}

		// for (Box b : this.getNotPlacedBoxes().getBoxes()) {
		// if (box.getCustomerId().compareTo(b.getCustomerId()) == 0) {
		// b.setSequenceNumber(1);
		// }
		// }

		// update all sequence number
		for (Box b : this.getPlacedBoxes().getBoxes()) {
			if (b.getCustomerId() == box.getCustomerId()) {
				continue;
			}

			b.setSequenceNumber(b.getSequenceNumber() + 1);

			for (BoxType bt : this.getNotPlacedBoxes().getBoxTypes()) {
				for (Box b1 : bt.getBoxes()) {
					if (b.getCustomerId() == b1.getCustomerId()) {

						b1.setSequenceNumber(b.getSequenceNumber());
					}
				}
			}
		}
	}

	private double totalBoxVolumes = 0;
	private double containerVolumes;
	private List<BoxCandidate> candidates;
	private List<Space> avaiableSpaces;
	private Batch placedBoxes;
	private Batch notPlacedBoxes;
	private ContainerLoading containerLoading;
	private PartialSolution partialSolution;
	private String algorithm;
	private Container container;
	private double notSupportRatio;
	private int amalgamateCount = 0;
	private boolean isFragility;
	private boolean isLifo;
	private boolean isOverhang;
	private String selectedRotation;
	private int roundNumber = 0;

	public static final String SB_ALGORITHM = "SB";
	public static final String BS_ALGORITHM = "BS";
	public static final String ST_VL_ALGORITHM = "ST_VL";
	public static final String ST_ALGORITHM = "ST";
	public static final String VL_ALGORITHM = "AL";
	public static final String EL_ALGORITHM = "EL";
	public static final double NOT_SUPPORT_RATIO = 0.25;
}
