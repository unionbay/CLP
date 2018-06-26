package anhnh34.com.vn.model;

import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;

public class SolutionMethod {
	ContainerLoading conLoading;
	final static Logger logger = Logger.getLogger(SolutionMethod.class);

	public SolutionMethod() {
	}

	public void setConLoading(ContainerLoading conLoading) {
		this.conLoading = conLoading;
	}

	public ContainerLoading getConLoading() {
		return conLoading;
	}

	public void GREEDY() {
		int roundNumber = 1;

		while (conLoading.getNotPlacedBox().getNumberOfBox() != 0) {
			logger.info(String.format("-------Start round: %d %n", new Object[] { roundNumber }));
			FeasibleObject feaObject = this.findFeasibleObject();
			if (feaObject != null) {
				conLoading.update(feaObject);
				conLoading.updateSpaces(feaObject);
			} else {
				break;
			}

			roundNumber++;
		}

		this.showResult();
	}

	// find the box to the choose space.
	private FeasibleObject findFeasibleObject() {
		logger.info("---Start FindFeasibleObject---");

		// re-sort all current avaiable spaces.
		this.getConLoading().getContainer().sortSpaces();
		logger.info("\n");
		logger.info("List of spaces ");
		this.getConLoading().showSpaceInfo();

		for (Space space : conLoading.getAllSpaces()) {
			// init feasible box list
			List<Box> feasibleListBox = new ArrayList<Box>();

			for (Box box : conLoading.getNotPlacedBox().getBoxes()) {

				// if box is not fit in space
				String selectedRotation = this.checkBoxIsFeasible(box, space);
				if (selectedRotation.isEmpty()) {
					continue;
				}

				// put box into feasible list and then choose the best one.
				box.setSelectedRotation(selectedRotation);
				box.setSize(selectedRotation);
				feasibleListBox.add(box);
			}

			if (feasibleListBox.isEmpty()) {
				continue;
			}

			// find the best one box.
			Box fittedBox = this.findBestFittedBox(space, feasibleListBox);
			return new FeasibleObject(fittedBox, fittedBox.getSelectedRotation(), space);
		}

		return null;
	}

	private String checkBoxIsFeasible(Box selectedBox, Space selectedSpace) {
		// loop all possible rotations.
		int[] rotations = selectedBox.getfRotation();

		// can be rotation by X.
		if (rotations[0] == 1) {
			// XYZ
			if (selectedSpace.getLenght() >= selectedBox.getBiggestDimension()
					&& selectedSpace.getWidth() >= selectedBox.getMiddleDimension()
					&& selectedSpace.getHeight() >= selectedBox.getSmallestDimension()) {
				return Rotation.XYZ;
			}

			// XZY
			if (selectedSpace.getLenght() >= selectedBox.getBiggestDimension()
					&& selectedSpace.getWidth() >= selectedBox.getSmallestDimension()
					&& selectedSpace.getHeight() >= selectedBox.getMiddleDimension()) {
				return Rotation.XZY;
			}
		}

		if (rotations[1] == 1) {
			// YXZ
			if (selectedSpace.getWidth() >= selectedBox.getBiggestDimension()
					&& selectedSpace.getLenght() >= selectedBox.getMiddleDimension()
					&& selectedSpace.getHeight() >= selectedBox.getSmallestDimension()) {
				return Rotation.YXZ;
			}

			// YZX
			if (selectedSpace.getWidth() >= selectedBox.getBiggestDimension()
					&& selectedSpace.getLenght() >= selectedBox.getSmallestDimension()
					&& selectedSpace.getHeight() >= selectedBox.getMiddleDimension()) {
				return Rotation.YZX;
			}
		}

		if (rotations[2] == 1) {
			// ZXY
			if (selectedSpace.getHeight() >= selectedBox.getBiggestDimension()
					&& selectedSpace.getLenght() >= selectedBox.getMiddleDimension()
					&& selectedSpace.getWidth() >= selectedBox.getSmallestDimension()) {
				return Rotation.ZXY;
			}
			// ZYX
			if (selectedSpace.getHeight() >= selectedBox.getBiggestDimension()
					&& selectedSpace.getWidth() >= selectedBox.getMiddleDimension()
					&& selectedSpace.getLenght() >= selectedBox.getSmallestDimension()) {
				return Rotation.ZYX;
			}
		}
		return "";
	}

	private Box findBestFittedBox(Space space, List<Box> listBoxes) {
		
		if (listBoxes.size() == 1) {
			return listBoxes.get(0);
		}

		List<Box> npBoxList = this.getConLoading().getNotPlacedBox().getBoxes();
		List<BoxCandidate> candidates = new ArrayList<BoxCandidate>();

		for (Box box : listBoxes) {
			// count number of b of selected box type left yet to be placed.
			int k = 1;
			for (Box boxJ : npBoxList) {
				if (box.getBoxType() == boxJ.getBoxType()) {
					k = k + 1;
				}
			}
			BoxCandidate candidate = new BoxCandidate(box, space, k);
			candidate.initialize();
			candidates.add(candidate);
		}

		// Main cretiation.
		candidates = this.mainCriterion(candidates);

		if (candidates.size() == 1) {
			return candidates.get(0).getBox();
		}

		// First tie breaker
		candidates = firstTieBreaker(candidates);
		if (candidates.size() == 1) {
			return candidates.get(0).getBox();
		}

		// Second tie breaker
		candidates = secondTieBreaker(candidates);
		if (candidates.size() >= 1) {
			return candidates.get(0).getBox();
		}

		return null;

	}

	private List<BoxCandidate> mainCriterion(List<BoxCandidate> candidates) {
		List<BoxCandidate> helperList = new ArrayList<BoxCandidate>();
		double largestPotentialSpace = 0;

		for (BoxCandidate can : candidates) {
			if (largestPotentialSpace == can.getPotenSpaceUtilization()) {
				helperList.add(can);
			}

			if (largestPotentialSpace < can.getPotenSpaceUtilization()) {
				largestPotentialSpace = can.getPotenSpaceUtilization();
				helperList.clear();
				helperList.add(can);
			}
		}

		return helperList;
	}

	private List<BoxCandidate> firstTieBreaker(List<BoxCandidate> candidates) {
		List<BoxCandidate> helperList = new ArrayList<BoxCandidate>();
		double smallestLengthwise = 0;
		for (BoxCandidate c : candidates) {
			if (smallestLengthwise > c.getLengthwiseProtrustion()) {
				helperList.clear();
				helperList.add(c);
			}

			if (smallestLengthwise == c.getLengthwiseProtrustion()) {
				helperList.add(c);
			}
		}

		return helperList;
	}

	private List<BoxCandidate> secondTieBreaker(List<BoxCandidate> candidates) {
		List<BoxCandidate> helperList = new ArrayList<BoxCandidate>();
		double largestBoxVolume = 0;
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

	private void showResult() {
		logger.info("\n\n\n");
		logger.info("===== Final Resutl=====");
		
		logger.info(String.format("Number of avaiable spaces: %s", this.getConLoading().getAllSpaces().size()));
		
		logger.info(String.format("Number of placed box: %d",
				new Object[] { this.getConLoading().getPlacedBox().getNumberOfBox() }));
		
		logger.info(String.format("Number of not placed box: %d",
				new Object[] { this.getConLoading().getNotPlacedBox().getNumberOfBox() }));

		logger.info(String.format("Number of avaiable space: %d",
				new Object[] { this.getConLoading().getAllSpaces().size() }));

		logger.info("List of placed box");

		
		for (Box b : this.getConLoading().getPlacedBox().getBoxes()) {
//			logger.info(String.format("length: %f, width: %f, height: %f",
//					new Object[] { b.getLength(), b.getWidth(), b.getHeight() }));
//			logger.info(String.format("minimum( %f, %f, %f) maximum(%f, %f, %f)",
//					new Object[] { b.getMinimum().getX(), b.getMinimum().getY(), b.getMinimum().getZ(),
//							b.getMaximum().getX(), b.getMaximum().getY(), b.getMaximum().getZ() }));
			this.getConLoading().showCuboidInfo("Placed box", b);
		}

		logger.info("\n\n");

		logger.info("List of not placed Box ");
		for (Box b : this.getConLoading().getNotPlacedBox().getBoxes()) {
			this.getConLoading().showCuboidInfo("Not Placed Box", b);
		}
		
		logger.info("\n\n");
		logger.info("All avaiable spaces");

		for (Space s : this.getConLoading().getAllSpaces()) {
			this.getConLoading().showCuboidInfo("Avaiable Space", s);
		}

	}

	private void showCuboidInfo(Cuboid c) {
		logger.info(String.format("%f %f %f, %f %f %f",
				new Object[] { c.getMinimumPoint().getX(), c.getMinimumPoint().getY(), c.getMinimumPoint().getZ(),
						c.getMaximumPoint().getX(), c.getMaximumPoint().getY(), c.getMaximumPoint().getZ() }));
	}

}
