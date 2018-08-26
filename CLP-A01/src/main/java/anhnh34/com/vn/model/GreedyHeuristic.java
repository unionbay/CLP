package anhnh34.com.vn.model;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

public class GreedyHeuristic {
	final static Logger logger = Logger.getLogger(GreedyHeuristic.class);

	public void run() throws Exception {		
		while (this.checkContinue()) {				
			this.initiliaze();
			boolean isStop = false;
			int cIndex = 0;			
			while (this.containerList.size() > 0) {
				if (isStop) {
					isStop = false;
					this.currLocation = this.getDeport();
					if (this.currLocation == null) {
						throw new Exception("Deport not found exception");
					}
					this.lIndex = 0;
				}

				// logger.info("Containers size: " + this.containerList.size());
				Container container = this.containerList.get(0);
				container.getCurrentSolution().getIdList().add(this.currLocation.getLocationID());
				container.getCurrentSolution().getLocationList().add(currLocation);
				this.resetCheckLocation();

				List<Location> closestLocationList = new ArrayList<Location>();
				boolean isFound = false;
				this.lIndex = 0;
				isStop = true;
				// Location location = null;

				while (true) {
					if (closestLocationList.size() == 0 || isFound) {
						closestLocationList = this.getClosetLocations();
					}

					if (containerList.size() == 1 && closestLocationList.size() == 0
							&& container.getCurrentSolution().getPlacedBoxes().getBoxes().size() == 0) {
//						logger.info("\n\n\n");
//						for (Location l : this.locationList) {
//							logger.info(String.format("%s - isVisited: %b - isChecked: %b",
//									new Object[] { l.getLocationID(), l.isVisited(), l.isChecked() }));
//						}
						this.getClosetLocations();
					}

					if (closestLocationList.size() == 0) {
						cIndex++;
						this.solutionList.add(container);
						// logger.info(String.format("Container %d is full: %s %.2f",
						// new Object[] { cIndex, container.getCurrentSolution().getIdList().toString(),
						// container.getCurrentCapacity() }));
						this.containerList.remove(container);
						// Thread.sleep(2000);
						break;
					}

					Random ranNum = new Random();
					int aRandomPos = ranNum.nextInt(closestLocationList.size());
					Location randomLocation = closestLocationList.get(aRandomPos);
					if (container.checkCapacity(randomLocation.getDemand()) == false) {
						isFound = false;
						for (int i = 0; i < closestLocationList.size(); i++) {
							Location location = closestLocationList.get(i);
							if (randomLocation.getX() == location.getX() && randomLocation.getY() == location.getY()) {
								closestLocationList.remove(i);
								break;
							}
						}
						this.setCheckLocation(randomLocation.getLocationID());
						continue;
					}

					isFound = checkLocationIsFeasible(container, randomLocation);

					if (isFound) {
							
						//logger.info("Found id: " + randomLocation.getLocationID());

						container.getCurrentSolution().getIdList().add(randomLocation.getLocationID());
						//container.getCurrentSolution().getLocationList().add(randomLocation);
						// remove location out of location list.
						

						this.updateLocation(randomLocation);

						// update new current location
						this.lIndex = 0;

						/*
						 * logger.info("Current container capacity: " + container.getCurrentCapacity() +
						 * " location capacity: " + randomLocation.getCapacity());
						 */
						// update container info.
						container.updateContainer(greedyInstance, this.currLocation);
						container.addCapacity(this.currLocation.getCapacity());

						// check container is full
						if (this.checkContainerFull(container)) {
							// logger.info(String.format("Container %d is full: %s %.2f",
							// new Object[] { cIndex, container.getCurrentSolution().getIdList().toString(),
							// container.getCurrentCapacity() }));
							// Thread.sleep(2000);
							cIndex++;
							this.solutionList.add(container);
							this.containerList.remove(container);
							break;
						}
					} else {
						//logger.info("checked location: " + randomLocation.getLocationID());
						randomLocation.setIsChecked(true);
						this.setCheckLocation(randomLocation.getLocationID());
						for (int i = 0; i < closestLocationList.size(); i++) {
							Location location = closestLocationList.get(i);
							if (randomLocation.getX() == location.getX() && randomLocation.getY() == location.getY()) {
								closestLocationList.remove(i);
								break;
							}
						}

					}
				}
			}

			this.showResult();
			roundNumber++;
		}
		
		this.calculateTimeConsume();
		this.writeToFile();
	}
	
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
	
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
	
	
	public Date getStartDate() {
		return startDate;
	}
	
	public Date getEndDate() {
		return endDate;
	}

	private boolean checkContinue() {
		if (this.bestSolutionList.size() <= 1000) {
			return true;
		}

		// for(Solution solution : this.bestSolutionList) {
		// if(solution.getTotalCost() <= 400) {
		// return false;d
		// }
		// }		
		return false;
	}

	private void setCheckLocation(String id) {
		for (Location l : this.locationList) {
			if (l.getLocationID().equalsIgnoreCase(id)) {
				int index = this.locationList.indexOf(l);
				this.locationList.get(index).setIsChecked(true);
			}
			for (Location sl : l.getLocationList()) {
				if (id.equalsIgnoreCase(sl.getLocationID())) {
					int index = l.getLocationList().indexOf(sl);
					l.getLocationList().get(index).setIsChecked(true);
				}
			}
		}
	}

	private void resetCheckLocation() {
		for (int i = 0; i < this.locationList.size(); i++) {
			Location l = this.locationList.get(i);
			if (l.isVisited() == false) {
				l.setIsChecked(false);
			}
			for (int j = 0; j < this.locationList.size(); j++) {
				Location lj = this.locationList.get(j);
				for (Location slj : lj.getLocationList()) {
					if (l.isVisited() == false) {
						// logger.info(l.getLocationID() + " " + slj.getLocationID());
						if (slj.getLocationID().equalsIgnoreCase(l.getLocationID())) {
							slj.setIsChecked(false);
						}
					}
				}
			}
		}
		// for(Location l : this.locationList) {
		//
		// for(Location sl : l.getLocationList()) {
		//
		// if(l.isVisited() == false) {
		// logger.info(l.getLocationID() + " " + sl.getLocationID());
		// if(sl.getLocationID().equalsIgnoreCase(l.getLocationID())) {
		// sl.setIsChecked(false);
		// }
		// }
		// }
		// }
	}
	
	private void calculateTimeConsume() {
		this.endDate = new Date();
		logger.info("Start date: " + startDate.getTime() + " end date: " + endDate.getTime());
		long runningTime = this.getDateDiff(startDate, endDate,TimeUnit.SECONDS);
		logger.info("Running time: " + runningTime);
	}

	private void writeToFile() throws IOException {
		PrintWriter writer = null;
		String folderPath = Utility.getInstance().getConfigValue(Constant.OUTPUT_PATH);
		try {		
			bestSolutionList.sort(new SolutionComparator());				
			for (Solution solution : bestSolutionList) {
				int sIndex = bestSolutionList.indexOf(solution);
				if (sIndex == 10) {
					break;
				}
					
				logger.info(String.format("Best Total cost: %.4f", solution.getTotalCost()));
				
				String filePath = "";
				String fileName = String.format("Solution_%d", bestSolutionList.indexOf(solution));
				filePath = folderPath + fileName;
				FileWriter fileWrite = new FileWriter(filePath);
				writer = new PrintWriter(fileWrite);

				// write header.
				for (Container container : solution.getContainerList()) {
					int index = solution.getContainerList().indexOf(container);
					PartialSolution currentSolution = container.getCurrentSolution();
					writer.println(String.format("Route: %d, number of customers %d: %s", new Object[] { index + 1,
							currentSolution.getIdList().size(), currentSolution.getLocationIds() }));
				}

				writer.println(String.format("Total cost of solution : %f", new Object[] { solution.getTotalCost() }));

				// write content/
				for (Container container : solution.getContainerList()) {
					int index = solution.getContainerList().indexOf(container);
					PartialSolution currentSolution = container.getCurrentSolution();
					writer.write(String.format("route: %d, customer: %d",
							new Object[] { index + 1, currentSolution.getIdList().size() }));
					writer.println();
					writer.println();
					writer.println();
					for (String id : currentSolution.getIdList()) {
						List<Box> boxList = new ArrayList<Box>();
						for (Box box : currentSolution.getPlacedBoxes().getBoxes()) {
							if (id.equalsIgnoreCase(box.getCustomerId())) {
								boxList.add(box);
							}
						}

						for (Box b : boxList) {
							writer.println(String.format(
									"CustomerNo: %2s   (l, w, h): %2.0f %2.0f %2.0f 	(x,y,z): %2.0f %2.0f %2.0f		(x1, y1, z1): %2.0f %2.0f %2.0f",
									new Object[] { b.getCustomerId(), b.getLength(), b.getWidth(), b.getHeight(),
											b.getMinimum().getX(), b.getMinimum().getY(), b.getMinimum().getZ(),
											b.getMaximum().getX(), b.getMaximum().getY(), b.getMaximum().getZ() }));
						}

						writer.println();
					}

					int solutionIndex = this.bestSolutionList.indexOf(solution);
					String jsonFilePath = "";
					jsonFilePath = folderPath + "" + solutionIndex + "" + index;
					Utility.getInstance().writeJsonFile(currentSolution.getPlacedBoxes().getBoxes(), jsonFilePath);

				}
				writer.flush();
				sIndex++;
			}

		} finally {
			if (writer != null) {
				writer.close();
			}
		}
	}

	private void showResult() throws InterruptedException {
		int numberOfItems = 0;
		int i = 1;
		for (Container container : solutionList) {
			PartialSolution currentSolution = container.getCurrentSolution();
			int containerBoxSize = currentSolution.getPlacedBoxes().getBoxes().size();
			numberOfItems = numberOfItems + containerBoxSize;			
			currentSolution.calculateCost();

//			System.out.println(String.format("%d: number of items: %d - %s - cost: %.4f",
//					new Object[] { i, currentSolution.getPlacedBoxes().getBoxes().size(),
//							currentSolution.getIdList().toString(), currentSolution.getCost() }));
			i++;
		}

		Solution solution = new Solution();
		solution.setContainerList(solutionList);
		solution.calculateTotalCost();
//		logger.info("Round number: " + roundNumber);
		
		this.currentItemNumber = numberOfItems;
		
		if (numberOfItems == this.getContainerLoading().getProblem().getNumOfItem()) {
//			logger.info(String.format("Total number of placed boxes: %d - total cost: %.3f",
//					new Object[] { numberOfItems, solution.getTotalCost() }));
			
			bestSolutionList.add(solution);
			//logger.info("Number of solution: " + bestSolutionList.size());
			//Thread.sleep(1000);
		}
	}

	public GreedyHeuristic() throws IOException {		
		this.bestSolutionList = new ArrayList<Solution>();
		this.initiliaze();
	}

	public GreedyHeuristic(ContainerLoading clObj) throws IOException {
		this.setContainerLoading(clObj);
		this.bestSolutionList = new ArrayList<Solution>();
		this.initiliaze();
	}

	public ContainerLoading getContainerLoading() {
		return containerLoading;
	}

	private boolean checkContainerFull(Container container) {
		for (Location location : this.locationList) {
			if (location.isVisited()) {
				continue;
			}

			if (container.getCapacity() >= container.getCurrentCapacity() + location.getCapacity()) {
				return false;
			}

		}
		return true;
	}

	public void setContainerLoading(ContainerLoading containerLoading) {
		this.containerLoading = containerLoading;
	}

	public Greedy getGreedyInstance() {
		return greedyInstance;
	}

	public List<PartialSolution> getSolutions() {
		return solutions;
	}

	public void setGreedyInstance(Greedy greedyInstance) {
		this.greedyInstance = greedyInstance;
	}

	public void setSolutions(List<PartialSolution> solutions) {
		this.solutions = solutions;
	}

	public void setContainerList(List<Container> containers) {
		this.containerList = containers;
	}

	private void initiliaze() throws IOException {
		this.loadAlgorithms();
		this.setContainerLoading(new ContainerLoading());
		this.containerLoading.loadingData();
		this.setGreedyInstance(new Greedy());
		this.greedyInstance.loadParameters();
		this.greedyInstance.setConLoading(this.getContainerLoading());
		this.setContainerList(this.getContainerLoading().getContainerList());		
		this.solutions = new ArrayList<PartialSolution>();
		this.locationList = new ArrayList<Location>(this.getContainerLoading().getLocationList());
		this.solutionList = new ArrayList<Container>();

		// load deport.
		Node node = this.getContainerLoading().getDeport();
		this.currLocation = this.getDeport();		
		this.lIndex = 0;

	}

	private Location getDeport() {
		for (Location l : this.locationList) {
			if ("0".equalsIgnoreCase(l.getLocationID())) {
				l.setVisited(true);
				l.setIsChecked(true);
				return l;
			}
		}
		return null;
	}

	public Location reloadDeport(List<Location> loacationList) {
		Location deport = new Location();
		double distance = 0;
		for (Location location : locationList) {
			if (location.isVisited()) {
				continue;
			}

			double lDistance = Math.sqrt(Math.pow(location.getX(), 2) + Math.pow(location.getY(), 2));
			if (distance == 0) {
				distance = lDistance;
				continue;
			}

			if (lDistance < distance) {
				deport = location;
			}
		}
		return deport;
	}

	private void updateLocation(Location lc) {
		Location nextLocation = null;
		for (Location location : this.locationList) {
			if (location.getX() == lc.getX() && location.getY() == lc.getY()) {
				nextLocation = location;
				break;
			}
		}
		nextLocation.setVisited(true);
		nextLocation.setIsChecked(true);
		
		this.currLocation = nextLocation; 

		for (Location pLocation : this.locationList) {
			for (Location subLocation : pLocation.getLocationList()) {
				if (subLocation.getLocationID().equalsIgnoreCase(nextLocation.getLocationID())) {
					subLocation.setVisited(true);
					subLocation.setIsChecked(true);
				}
			}
		}
	}

	private List<Location> getClosetLocations() {
		List<Location> closestLocations = new ArrayList<Location>();
//		logger.info("\n\n\n");
//		for (Location l : this.locationList) {
//			
//			logger.info(String.format("%s - isVisited: %b - isChecked: %b",
//					new Object[] { l.getLocationID(), l.isVisited(), l.isChecked() }));
//			
//		}
		int i = 0;
		while (i < 3) {
			if (lIndex >= this.currLocation.getLocationList().size()) {
				lIndex = 0;
				break;
			}

			Location location = this.currLocation.getLocationList().get(lIndex);

			if (location.isVisited() || location.isChecked()) {
				lIndex++;
				continue;
			}

			closestLocations.add(this.currLocation.getLocationList().get(lIndex));
			this.lIndex++;
			i++;

		}
		return closestLocations;
	}

	private boolean checkLocationIsFeasible(Container container, Location lc) {
		greedyInstance.setAlgorithm(getRandomGreedyAlgorithm());
		greedyInstance.setAvaiableSpaces(container.getCurrentSolution().getAvaiableSpaces());
		greedyInstance.setPlacedBoxes(container.getCurrentSolution().getPlacedBoxes());
		greedyInstance.setNotPlacedBoxes(lc.getBoxes());

		while (greedyInstance.getNotPlacedBoxes().getBoxes().size() > 0) {

			FeasibleObject feasibleItem;

			switch (greedyInstance.getAlgorithm()) {
			case Constant.GREEDY_SB:
				feasibleItem = greedyInstance.greedySbAlgorithm();
				break;
			case Constant.GREEDY_BS:
				feasibleItem = greedyInstance.greedyBsAlgorithm();
				break;
			case Constant.GREEDY_ST:
				feasibleItem = greedyInstance.greedyStAlgorithm();
				break;
			case Constant.GREEDY_VL:
				feasibleItem = greedyInstance.greedyVlAlgorithm();
				break;
			default:
				feasibleItem = greedyInstance.greedyBsAlgorithm();
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
		}

		if (greedyInstance.getNotPlacedBoxes().getBoxes().size() == 0) {
			// Reload real not placed box.
			List<Box> notPlacedBoxes = new ArrayList<Box>();

			for (Box box : container.getCurrentSolution().getNotPlacedBoxes().getBoxes()) {
				if (box.getCustomerId().equalsIgnoreCase(lc.getLocationID())) {
					continue;
				}
				Box nBox = new Box(box);
				notPlacedBoxes.add(nBox);
			}

			greedyInstance.getNotPlacedBoxes().setBoxes(notPlacedBoxes);

			// logger.info(String.format("id: %s, space: %d, placed boxes: %d, not placed
			// boxes: %d",
			// new Object[] { lc.getLocationID(), greedyInstance.getAvaiableSpaces().size(),
			// greedyInstance.getPlacedBoxes().getBoxes().size(),
			// greedyInstance.getNotPlacedBoxes().getBoxes().size() }));

			greedyInstance.showResult();
			return true;
		}

		return false;
	}

	private String getRandomGreedyAlgorithm() {
		Random ranNum = new Random();
		int aRandomPos = ranNum.nextInt(this.algorithms.size());
		return this.algorithms.get(aRandomPos);
	}

	private void loadAlgorithms() {
		this.algorithms = new ArrayList<String>();
		this.algorithms.add(Greedy.BS_ALGORITHM);
		this.algorithms.add(Greedy.SB_ALGORITHM);
		this.algorithms.add(Greedy.ST_ALGORITHM);
		this.algorithms.add(Greedy.VL_ALGORITHM);
	}

	private Location getLocation(double x, double y) {
		for (Location location : this.locationList) {
			if (location.getX() == x && location.getY() == y) {
				return location;
			}
		}
		return null;
	}
	
	private long getDateDiff(Date date1, Date date2, TimeUnit timeUnit) {
	    long diffInMillies = date2.getTime() - date1.getTime();
	    return timeUnit.convert(diffInMillies,TimeUnit.MILLISECONDS);
	}

	public int itemNumber = 32;
	private int currentItemNumber = 0;
	private List<Container> solutionList;
	private ContainerLoading containerLoading;
	private Greedy greedyInstance;
	private int lIndex;
	private List<PartialSolution> solutions;
	private List<Location> locationList;
	private List<Container> containerList;
	private List<String> algorithms;
	private Node deport;
	private Location currLocation;
	private int roundNumber;
	private List<Solution> bestSolutionList;
	private Date startDate;
	private Date endDate;
}