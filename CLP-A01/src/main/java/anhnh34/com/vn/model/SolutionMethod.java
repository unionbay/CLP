package anhnh34.com.vn.model;

import java.io.File;
import java.util.List;
import java.util.ArrayList;
import java.io.IOException;
import org.apache.log4j.Logger;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class SolutionMethod {
	final static Logger logger = Logger.getLogger(SolutionMethod.class);
	public ContainerLoading conLoading;
	private Greedy greedyInstance;

	public Greedy getGreedyInstance() {
		return greedyInstance;
	}

	public void setGreedyInstance(Greedy greedyInstance) {
		this.greedyInstance = greedyInstance;
	}

	public SolutionMethod() {
	}

	public void setConLoading(ContainerLoading conLoading) {
		this.conLoading = conLoading;
	}

	public ContainerLoading getConLoading() {
		return conLoading;
	}

	public void run() {
		int roundNumber = 1;
		for (Box box : greedyInstance.getNotPlacedBoxes().getBoxes()) {
				greedyInstance.showBoxInfo("", box);
		}
		while (greedyInstance.getNotPlacedBoxes().getBoxes().size() > 0) {
			logger.info(String.format("-------Start round: %d %n", new Object[] { roundNumber }));
			if(roundNumber == 5) {
				System.out.println("Start debug here");
			}

			// for (Box box : greedyInstance.getNotPlacedBoxes().getBoxes()) {
			// this.getConLoading().showCuboidInfo("", box);
			// }

			// FeasibleObject feaObject = greedyInstance.findFeasibleObject();
			FeasibleObject feaObject = greedyInstance.findObjectBs();
			if (feaObject == null) {
				break;
			}
			greedyInstance.update(feaObject);
			greedyInstance.updateSpaces(feaObject);
			roundNumber++;
		}
		greedyInstance.showResult();
	}

	public void GREEDY() {
		int roundNumber = 1;

		while (conLoading.getNotPlacedBox().getNumberOfBox() != 0) {
			logger.info(String.format("-------Start round: %d %n", new Object[] { roundNumber }));
			FeasibleObject feaObject = this.findFeasibleObject();
			if (feaObject != null) {
				conLoading.update(feaObject); // update list of not placed box.
				conLoading.updateSpaces(feaObject); // update list of not placed.
			} else {
				// no box in the current subset of Batch not placed box fit in
				// the left empty space
				break;
			}
			roundNumber++;
		}
		this.showResult();
	}

	private FeasibleObject bSAlgorithm() {
		return null;
	}

	// find the box to the choose space.
	private FeasibleObject findFeasibleObject() {
		logger.info("---Start FindFeasibleObject---");

		// re-sort all current avaiable spaces acending...
		this.getConLoading().getContainer().sortSpaces();
		logger.info("\n");
		logger.info("List of spaces ");
		this.getConLoading().showSpaceInfo();

		for (Space space : conLoading.getAllSpaces()) {
			// init feasible box list
			List<Box> feasibleListBox = new ArrayList<Box>();

			for (Box box : conLoading.getNotPlacedBox().getBoxes()) {
				String selectedRotation = this.checkBoxIsFeasible(box, space);
				if (selectedRotation.isEmpty()) { // if box is not fit in space
					continue;
				}
				// put box into feasible list and then choose the best one.
				box.setSelectedRotation(selectedRotation);
				box.setSize(selectedRotation);
				this.getConLoading().updateBoxPosition(box, space, selectedRotation);
				// if (isMultiDropFeasiblePacking(box)) {
				// feasibleListBox.add(box);
				// }
				feasibleListBox.add(box);
			}

			if (feasibleListBox.isEmpty()) {
				continue;
			}

			// find the best one box.
			BoxCandidate candidate = this.findBestFittedBox(space, feasibleListBox);
			return new FeasibleObject(candidate.getBox(), candidate.getBox().getSelectedRotation(), space);
		}

		return null;
	}

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

	private BoxCandidate findBestFittedBox(Space space, List<Box> feasibleBoxList) {
		switch (greedyInstance.getSelectedAlgorithm()) {
		case Greedy.ST_ALGORITHM:
			return greedyInstance.stAlgorithm();
		case Greedy.VL_ALGORITHM:
			return greedyInstance.vlAlgorithm();
		case Greedy.EL_ALGORITHM:
			return greedyInstance.vlAlgorithm();
		default:
			return null;
		}

		// List<Box> npBoxList = this.getConLoading().getNotPlacedBox().getBoxes();
		// List<BoxCandidate> candidates = new ArrayList<BoxCandidate>();
		// //
		// // listBoxes = findBiggestSequenceNumber(listBoxes);
		//
		// for (Box box : listBoxes) {
		// // count number of b of selected box type left yet to be placed.
		// int k = 1;
		// for (Box boxJ : npBoxList) {
		// if (box.getBoxType() == boxJ.getBoxType()) {
		// k = k + 1;
		// }
		// }
		//
		// BoxCandidate candidate = new BoxCandidate(box, space, k);
		// candidate.initialize();
		// candidates.add(candidate);
		// }
		//
		// // Add subsequence number testing.
		//
		// // Main cretiation.
		// candidates = this.mainCriterion(candidates);
		// logger.info("After main cretiation: " + candidates.size());
		// if (candidates.size() == 1) {
		// return candidates.get(0).getBox();
		// }
		//
		// // First tie breaker
		// candidates = firstTieBreaker(candidates);
		// if (candidates.size() == 1) {
		// return candidates.get(0).getBox();
		// }
		//
		// // Second tie breaker
		// candidates = secondTieBreaker(candidates);
		// if (candidates.size() >= 1) {
		// return candidates.get(0).getBox();
		// }
		//
		// return candidates.get(0).getBox();

	}

	private List<Box> findBiggestSequenceNumber(List<Box> boxes) {
		int number = boxes.get(0).getSequenceNumber();
		List<Box> result = new ArrayList<>();
		for (Box box : boxes) {
			if (number < box.getSequenceNumber()) {
				number = box.getSequenceNumber();
			}
		}

		for (Box box : boxes) {
			if (box.getSequenceNumber() == number) {
				result.add(box);
			}
		}

		return result;
	}

	private List<BoxCandidate> mainCriterion(List<BoxCandidate> candidates) {
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

	private List<BoxCandidate> firstTieBreaker(List<BoxCandidate> candidates) {
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

	private List<BoxCandidate> secondTieBreaker(List<BoxCandidate> candidates) {
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

	private void showResult() {
		logger.info("\n\n\n");
		logger.info("===== Final Resutl=====");

		// logger.info(String.format("Number of avaiable spaces: %s",
		// this.getConLoading().getAllSpaces().size()));
		int numberOfItem = this.getConLoading().getProblem().getNumOfItem();
		int numberOfPlacedBoxes = this.getConLoading().getPlacedBox().getNumberOfBox();

		logger.info(String.format("Number of item: %d, number of placed box: %d",
				new Object[] { numberOfItem, numberOfPlacedBoxes }));

		for (Box box : this.getConLoading().getPlacedBox().getBoxes()) {
			this.getConLoading().showCuboidInfo("", box);
		}

		logger.info("List of not placed box");
		for (Box npBox : this.getConLoading().getNotPlacedBox().getBoxes()) {
			this.getConLoading().showCuboidInfo("", npBox);
		}

		this.writeResultToFile();
	}

	private void writeResultToFile() {
		List<Box> placedBox = this.getConLoading().getPlacedBox().getBoxes();
		Boxes[] outBoxList = new Boxes[placedBox.size()];
		Boxes[] sortedBoxList = new Boxes[placedBox.size()];
		List<Node> nodeList = new ArrayList<Node>();
		List<Node> sortedNodeList = new ArrayList<Node>();

		for (int i = 0; i < placedBox.size(); i++) {
			Box selectedBox = placedBox.get(i);
			Boxes oBox = new Boxes(selectedBox.getMinimum(), selectedBox.getLength(), selectedBox.getWidth(),
					selectedBox.getHeight(), selectedBox.getSequenceNumber());
			outBoxList[i] = oBox;
			sortedBoxList[i] = oBox;
		}

		Utility.getInstance().sortBox(sortedBoxList);

		for (int i = 0; i < outBoxList.length; i++) {
			Node node = new Node(i, outBoxList[i]);
			nodeList.add(node);
		}

		for (int i = 0; i < sortedBoxList.length; i++) {
			Node node = new Node(i, sortedBoxList[i]);
			sortedNodeList.add(node);
		}

		Nodes nodes = new Nodes(nodeList);
		Nodes sortedNodes = new Nodes(sortedNodeList);

		ObjectMapper mapper = new ObjectMapper();

		// Object to Json in file
		try {
			String outputPath = Utility.getInstance().getConfigValue("output_path");
			String jsonResultPath = outputPath + "result.json";
			String sortResultPath = outputPath + "sort_result.json";
			mapper.writeValue(new File(jsonResultPath), nodes);
			mapper.writeValue(new File(sortResultPath), sortedNodes);
		} catch (JsonGenerationException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private boolean isMultiDropFeasiblePacking(Box selectedBox) {
		if (checkPassageExist(selectedBox) == false) {
			return false;
		}

		if (checkNoneOverlappingPlane(selectedBox) == false) {
			return false;
		}

		return true;

	}

	private boolean checkPassageExist(Box selectedBox) {
		for (Box b : getConLoading().getPlacedBox().getBoxes()) {

			double boxDistance = Math.sqrt(Math.pow(selectedBox.getMaximumPoint().getX(), 2)
					+ Math.pow(selectedBox.getMaximumPoint().getY(), 2));

			double distance = Math.sqrt(Math.pow(b.getMinimum().getX(), 2) + Math.pow(b.getMinimumPoint().getY(), 2));

			if (boxDistance > distance) {
				if (b.getMinimumPoint().getY() < selectedBox.getMaximumPoint().getY()
						&& selectedBox.getMinimumPoint().getY() < b.getMaximumPoint().getY()) {
					if (selectedBox.getSequenceNumber() > b.getSequenceNumber()) {
						return false;
					}
				}
			}

			if (boxDistance < distance) {
				if (b.getMinimumPoint().getY() < selectedBox.getMaximumPoint().getY()
						&& selectedBox.getMinimumPoint().getY() < b.getMaximumPoint().getY()) {
					if (selectedBox.getSequenceNumber() < b.getSequenceNumber()) {
						return false;
					}
				}
			}
		}
		return true;
	}

	private boolean checkNoneOverlappingPlane(Box selectedBox) {
		for (Box b : this.getConLoading().getPlacedBox().getBoxes()) {
			if (selectedBox.getMinimum().getX() < b.getMaximum().getX()
					&& b.getMinimum().getX() < selectedBox.getMinimum().getX()
					&& selectedBox.getMaximum().getY() < b.getMaximum().getY()
					&& b.getMinimum().getY() < selectedBox.getMaximum().getY()
					&& selectedBox.getMaximum().getZ() <= b.getMinimum().getZ()) {

				if (selectedBox.getSequenceNumber() < b.getSequenceNumber()) {
					return false;
				}
			}
		}
		return true;
	}

}
