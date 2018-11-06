package anhnh34.com.vn.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

import org.apache.log4j.Logger;

public class TreeSearch {
	
	final static Logger logger = Logger.getLogger(TreeSearch.class);

	public TreeSearch() {		
	}

	private void initiliaze() {
		this.iteration = 1;
		this.currentPartialSolutions = new ArrayList<PartialSolution>();
		this.newSolutions = new ArrayList<PartialSolution>();
		this.bestSolutions = new ArrayList<PartialSolution>();
		this.setBreadth(Integer.valueOf(Utility.getInstance().getConfigValue("breadth")));
		this.initRoot(container);
	}

	public void initRoot(ContainerLoading container) {
		PartialSolution root = new PartialSolution();
		root.setAvaiableSpaces(container.getAllSpaces());
		root.setNotPlacedBoxes(container.getNotPlacedBox());
		root.setSubPartialSolutions(new ArrayList<PartialSolution>());
		currentPartialSolutions.add(root);
	}

	public List<PartialSolution> getCurrentPartialSolutions() {
		return currentPartialSolutions;
	}

	public void setContainer(ContainerLoading container) {
		this.container = container;
		this.initiliaze();
	}

	public void setCurrentPartialSolutions(List<PartialSolution> currentPartialSolutions) {
		this.currentPartialSolutions = currentPartialSolutions;
	}

	public int getBreadth() {
		return breadth;
	}

	public void setBreadth(int breadth) {
		this.breadth = breadth;
	}

	public void run() {
		while (true) {						
			for (PartialSolution solution : currentPartialSolutions) {				
				for (Box box : solution.getNotPlacedBoxes().getBoxes()) {
					greedyInstance.setAvaiableSpaces(solution.getAvaiableSpaces());
					greedyInstance.setNotPlacedBoxes(solution.getNotPlacedBoxes());
					greedyInstance.setPlacedBoxes(solution.getPlacedBoxes());
					List<FeasibleObject> feasibleObjectList = greedyInstance.basicGreedyAlgorithm(box);					

					for (FeasibleObject feasObj : feasibleObjectList) {
						PartialSolution newSolution = this.createNewPartialSolution(solution, feasObj);
						this.mesuareSolution(newSolution);								
						solution.getSubPartialSolutions().add(newSolution);
						newSolutions.add(newSolution);
					}

				}
			}
						
			//delete identical solutions
			this.deleteIdenticalSolution();						
			//keep best lamda partial solutions.
			this.chooseLamdaSolutions();
			
			if(currentPartialSolutions == null) {
				break;
			}
			
			if(bestSolutions.size() == 3) {
				break;
			}		
			
			iteration = iteration + 1;
			logger.info("iteration number: " + iteration);
			logger.info("current partial solution size: " + currentPartialSolutions.size());
			
		}
	}

	private PartialSolution createNewPartialSolution(PartialSolution solution, FeasibleObject feasibleObj) {
		PartialSolution partialSolution = new PartialSolution(solution.getAvaiableSpaces(),
				solution.getNotPlacedBoxes().getBoxes(), solution.getPlacedBoxes().getBoxes(),new ArrayList<String>(), new ArrayList<Location>());

		// add box space
		partialSolution.getNotPlacedBoxes().removeBox(feasibleObj.getBox());
		partialSolution.getNotPlacedBoxes().addBox(feasibleObj.getBox());
		greedyInstance.setNotPlacedBoxes(partialSolution.getNotPlacedBoxes());
		greedyInstance.setPlacedBoxes(partialSolution.getPlacedBoxes());
		greedyInstance.updateSpaces(feasibleObj);
		
		partialSolution.setAvaiableSpaces(greedyInstance.getAvaiableSpaces());
		return partialSolution;
	}
	
	private void deleteIdenticalSolution() {
		List<PartialSolution> removeSolutions = new ArrayList<PartialSolution>();		
		for(int i = 0; i < newSolutions.size() -1 ; i ++) {
			PartialSolution firstSolution = newSolutions.get(i);
			for(int j = i+1; j < newSolutions.size(); j++) {
				PartialSolution secondSolution = newSolutions.get(j);
				if(checkIdenticalSolution(firstSolution, secondSolution)) {
					removeSolutions.add(firstSolution);
				}
			}
		}
		
		newSolutions.removeAll(removeSolutions);
	}
	
	private void chooseLamdaSolutions() {
		List<PartialSolution> bestSolutions = new ArrayList<PartialSolution>();
		int i = 0;
		newSolutions.sort(new Comparator<PartialSolution>() {

			@Override
			public int compare(PartialSolution fPartial, PartialSolution sPartial) {
				if(fPartial.getVolumeUtitlisation() > sPartial.getVolumeUtitlisation()) {
					return -1;
				}
				if(fPartial.getVolumeUtitlisation() < sPartial.getVolumeUtitlisation()) {
					return 1;
				}
				return 0;
			}
			
		});
		
		
		
		for(PartialSolution p : newSolutions) {
			bestSolutions.add(p);
			i = i  + 1;
			
			if(i == this.getBreadth()) {
				break;
			}
		}		
		currentPartialSolutions = bestSolutions;
	}
	
	private boolean checkIdenticalSolution(PartialSolution firstSolution, PartialSolution secondSolution) {
		if(firstSolution.getPlacedBoxes().getBoxes().size() != secondSolution.getPlacedBoxes().getBoxes().size()) {
			return false;
		}
		
		for(int i = 0; i < firstSolution.getPlacedBoxes().getBoxes().size(); i++) {
			Box fBox = firstSolution.getPlacedBoxes().getBoxes().get(i);
			Box sBox = secondSolution.getPlacedBoxes().getBoxes().get(i);
			
			if(fBox.getBoxType() != sBox.getBoxType()) {
				return false;
			}
			
				
			
			if(!fBox.getSelectedRotation().getRotationCode().equalsIgnoreCase(sBox.getSelectedRotation().getRotationCode())) {
				return false;
			}
			
			Dimension fMin = fBox.getMinimum();
			Dimension sMin = sBox.getMinimum();
			
			if(fMin.getX() != sMin.getX() || fMin.getY()  != sMin.getY() || fMin.getZ() != sMin.getZ()) {
				return false;
			}
		}
		
		return true;
	}
	
	

	private void mesuareSolution(PartialSolution partialSolution) {
		int roundNumber = 1;
		while (greedyInstance.getNotPlacedBoxes().getBoxes().size() > 0) {

			// logger.info(String.format("-------Start round: %d ----------", new Object[] {
			// roundNumber }));

			greedyInstance.setRoundNumber(roundNumber);

			String algorithm = Utility.getInstance().getConfigValue("algorithm");
			FeasibleObject feasibleItem;

			switch (algorithm) {
			case Constant.GREEDY_SB:
				feasibleItem = greedyInstance.greedySbAlgorithm(new BoxComparator(new Random()), new SpaceComparator(new Random()));
				break;
			case Constant.GREEDY_BS:
				feasibleItem = greedyInstance.greedyBsAlgorithm(new BoxComparator(new Random()), new SpaceComparator(new Random()));
				break;
			case Constant.GREEDY_ST:
				feasibleItem = greedyInstance.greedyStAlgorithm();
				break;
			case Constant.GREEDY_VL:
				feasibleItem = greedyInstance.greedyVlAlgorithm();
				break;
			default:
				feasibleItem = greedyInstance.greedyBsAlgorithm(new BoxComparator(new Random()), new SpaceComparator(new Random()));

			}

			if (feasibleItem == null) {
				break;
			}

			greedyInstance.update(feasibleItem);
			greedyInstance.updateSpaces(feasibleItem);
			Object[] objects = greedyInstance.getNotPlacedBoxes().getBoxes().toArray();

			Box[] boxList = Arrays.copyOf(objects, objects.length, Box[].class);
			Utility.getInstance().reOrderBox(boxList);
			greedyInstance.getNotPlacedBoxes().getBoxes().clear();
			greedyInstance.getNotPlacedBoxes().getBoxes().addAll(Arrays.asList(boxList));
			roundNumber++;
		}

		double totalBoxVolumes = 0;
		double containerVolume =  this.container.getContainer().getLength() * this.container.getContainer().getWidth() * this.container.getContainer().getHeight();
		
		for (Box box : greedyInstance.getPlacedBoxes().getBoxes()) {
			double boxVolume = box.getLength() * box.getWidth() * box.getHeight();
			totalBoxVolumes += boxVolume;
		}
		
		

		if (greedyInstance.getNotPlacedBoxes().getBoxes().size() == 0) {
			PartialSolution bestSolution = new PartialSolution(greedyInstance.getAvaiableSpaces(),
					greedyInstance.getNotPlacedBoxes().getBoxes(), greedyInstance.getPlacedBoxes().getBoxes(),new ArrayList<String>(),new ArrayList<Location>());
			bestSolution.setFeasible(true);
			bestSolution.setVolumeUtitlisation((totalBoxVolumes/containerVolume) * 100);
			bestSolutions.add(bestSolution);			
		}
		
		partialSolution.setVolumeUtitlisation((totalBoxVolumes/containerVolume) * 100);								
	}
	
	public void setGreedyInstance(Greedy greedyInstance) {
		this.greedyInstance = greedyInstance;
	}
		

	private int breadth;
	private int iteration;
	private Greedy greedyInstance;
	private PartialSolution root;
	private List<PartialSolution> currentPartialSolutions;
	private List<PartialSolution> newSolutions;
	private List<PartialSolution> bestSolutions;
	private ContainerLoading container;

}

