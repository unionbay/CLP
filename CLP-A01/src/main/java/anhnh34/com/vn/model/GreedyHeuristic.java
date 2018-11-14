package anhnh34.com.vn.model;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
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

				//logger.info("Containers size: " + this.containerList.size());
				//Thread.sleep(1000);
				
				Container container = new Container(this.containerList.get(0));
				container.getCurrentSolution().getIdList().add(this.currLocation.getLocationID());
				container.getCurrentSolution().getLocationList().add(currLocation);
				container.getSolutionList().add(container.getCurrentSolution());
				// .getSolutionList().add(currentSolution);
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

//					if (closestLocationList.size() == 0) {
//						 logger.info("\n\n\n");
//						 for (Location l : this.locationList) {
//						 logger.info(String.format("%s - isVisited: %b - isChecked: %b",
//								 new Object[] { l.getLocationID(), l.isVisited(), l.isChecked() }));
//						 }
////						 this.getClosetLocations();
//					}

					if (closestLocationList.size() == 0) {
						cIndex++;
						this.solutionList.add(container);
//						 logger.info(String.format("Container %d is full: %s %.2f",
//						 new Object[] { cIndex, container.getCurrentSolution().getIdList().toString(),
//						 container.getCurrentCapacity()}));
						this.boxComp.reloadOrderString();
						this.spaceComp.reloadOrderString();
						this.containerList.remove(0);
						 //Thread.sleep(2000);
						break;
					}

					int aRandomPos = this.getRandomNumber(0, closestLocationList.size() - 1);
					Location randomLocation = closestLocationList.get(aRandomPos);
					//logger.info("Location checked: " + randomLocation.getLocationID());
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

					// this.boxComp.reloadOrderString();
					// this.spaceComp.reloadOrderString();
					isFound = checkLocationIsFeasible(container.getCurrentSolution(), randomLocation);

					if (isFound) {

						//logger.info("Found id: " + randomLocation.getLocationID());

						// container.getCurrentSolution().getIdList().add(randomLocation.getLocationID());
						// container.getCurrentSolution().getLocationList().add(randomLocation);
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
						//container.addCapacity(this.currLocation.getCapacity());
						// check container is full
						if (this.checkContainerFull(container)) {
//							 //logger.info(String.format("Container %d is full: %s %.2f",
//							 new Object[] { cIndex, container.getCurrentSolution().getIdList().toString(),
//							 container.getCurrentCapacity() }));
							 //Thread.sleep(2000);
							cIndex++;
							this.solutionList.add(container);
							this.containerList.remove(0);						
							break;
						}
					} else {
//						if(container.getCurrentSolution().getIdList().size() < 5) {
							Container con = this.checkLocationIsFeasible_01(container.getCurrentSolution(), randomLocation);							
							if(con != null) {
								isFound = true;
								container = con;							
								this.updateLocation(randomLocation);							
								// update new current location
								this.lIndex = 0;							
								if (this.checkContainerFull(container)) {
//									 logger.info(String.format("Container %d is full: %s %.2f",
//									 new Object[] { cIndex, container.getCurrentSolution().getIdList().toString(),
//									 container.getCurrentCapacity() }));									 
									cIndex++;
									this.solutionList.add(container);
									this.containerList.remove(0);
									this.boxComp.reloadOrderString();
									this.spaceComp.reloadOrderString();
									break;
								}
								continue;
							}
//						}
					
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
			logger.info("Round number: " + roundNumber);
			this.showResult();
			if (this.containerLoading.getProblem().getNumOfItem() == this.calNumberOfPlacedBox(solutionList)) {

				Solution solution = new Solution();
				solution.setContainerList(solutionList);
				solution.calculateTotalCost();
				this.newSolution = solution;
				this.writeToFile(this.newSolution, "InitSolution");
				logger.info("Greedy solution: " + roundNumber);
				this.newSolution.showResult();
				logger.info("\n");

			}

			roundNumber++;
		}

		logger.info("Algorithm is stop");
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

	private boolean checkContinue() throws Exception {
		// if(this.calculateRunningTime() > this.runningTime) {
		// return false;
		// }
		if (this.newSolution != null) {
			if (this.routingSearch()) {
				this.newSolution.showResult();
				return false;
			}
		}

		// if (this.bestSolutionList.size() == 1) {
		// this.showResult();
		// Solution solution = new Solution();
		// solution.setContainerList(this.bestSolutionList.get(0).getContainerList());
		// solution.calculateTotalCost();
		//
		// if (bestSolution == null) {
		// this.bestSolution = solution;
		// }
		//
		// if (this.bestSolution.getTotalCost() >
		// this.bestSolutionList.get(0).getTotalCost()) {
		// this.bestSolution = solution;
		// this.writeToFile();
		// }
		//
		// if (this.exchangeCustomer()) {
		// if (this.bestRoutingTotalCost > this.routingTotalCost) {
		// this.bestRoutingTotalCost = this.routingTotalCost;
		// this.writeToFile(testSolutionList);
		// }
		// /*if (this.routingTotalCost <= this.bestSolution.getTotalCost()) {
		// return false;
		// } */
		// }
		//
		// this.bestSolutionList.clear();
		// }

		// if (this.roundNumber < 100) {
		// return true;
		// }

		// for(Solution solution : this.bestSolutionList) {
		// if(solution.getTotalCost() <= 400) {
		// return false;d
		// }
		// }
		this.newSolution = null;
		return true;
	}

	private boolean checkRouting() {
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
	}

	private void calculateTimeConsume() {
		this.endDate = new Date();
		logger.info("Start date: " + startDate.getTime() + " end date: " + endDate.getTime());
		long runningTime = this.getDateDiff(startDate, endDate, TimeUnit.SECONDS);
		logger.info("Running time: " + runningTime);
	}

	private long calculateRunningTime() {
		this.endDate = new Date();
		// logger.info("Start date: " + startDate.getTime() + " end date: " +
		// endDate.getTime());
		return this.getDateDiff(startDate, endDate, TimeUnit.SECONDS);
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

				// logger.info(String.format("Best Total cost: %.4f", solution.getTotalCost()));

				String filePath = "";
				String fileName = String.format("%s_%d", new Object[] { this.getContainerLoading().getInstanceName(),
						bestSolutionList.indexOf(solution) });
				filePath = folderPath + fileName;
				FileWriter fileWrite = new FileWriter(filePath);
				writer = new PrintWriter(fileWrite);

				// write header.
				for (Container container : solution.getContainerList()) {
					int index = solution.getContainerList().indexOf(container);
					PartialSolution currentSolution = container.getCurrentSolution();
					writer.println(String.format("Route: %d,cost: %.3f, number of customers %d: %s",
							new Object[] { index + 1, currentSolution.getCost(), currentSolution.getIdList().size(),
									currentSolution.getLocationIds() }));
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
					jsonFilePath = folderPath + containerLoading.getInstanceName() + "_" + solutionIndex + "_" + index;
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
			// logger.info(currentSolution.getIdList().toString());
			/*
			 * System.out.println(String.format("%d: number of items: %d - %s - cost: %.4f",
			 * new Object[] { i, currentSolution.getPlacedBoxes().getBoxes().size(),
			 * currentSolution.getIdList().toString(), currentSolution.getCost() }));
			 */
			i++;
		}
		// logger.info("\n");

		Solution solution = new Solution();
		solution.setContainerList(solutionList);
		solution.calculateTotalCost();

		// UP-RO
		 solution.showResult();		
		// writeToFile(solution, "Testing_"+ roundNumber);
		// logger.info("Num of placed boxs: " + numberOfItems);
		// logger.info("Number of boxs: " +
		// this.containerLoading.getProblem().getNumOfItem());
		//Thread.sleep(500);
		// //writeToFile(solution, "RoundNumber-"+ roundNumber);
		//logger.info("Num of placed box: " + numberOfItems);
		logger.info("\n");
		// Thread.sleep(1000);
		// End UP-RO

		this.currentItemNumber = numberOfItems;

		if (numberOfItems == this.getContainerLoading().getProblem().getNumOfItem()) {
			// logger.info(String.format("Total number of placed boxes: %d - total cost:
			// %.3f",
			// new Object[] { numberOfItems, solution.getTotalCost() }));

			bestSolutionList.add(solution);

			/*
			 * logger.info(String.format("round number: %d number of solution:  %.3f", new
			 * Object[] { this.roundNumber, solution.getTotalCost() }));
			 */
			// Thread.sleep(1000);
		}

		// this.roundNumber++;
	}

	private int calNumberOfPlacedBox(List<Container> containers) {
		int totalNumber = 0;
		for (Container con : containers) {
			totalNumber = totalNumber + con.getCurrentSolution().getPlacedBoxes().getNumberOfBox();
		}
		return totalNumber;
	}

	private double showResult(List<PartialSolution> solutions) {
		int numberOfItems = 0;
		double totalCost = 0;
		for (PartialSolution currentSolution : solutions) {

			int containerBoxSize = currentSolution.getPlacedBoxes().getBoxes().size();
			numberOfItems = numberOfItems + containerBoxSize;
			currentSolution.calculateCost();
			totalCost += currentSolution.getCost();

			/*
			 * System.out.println(String.format("%d: number of items: %d - %s - cost: %.4f",
			 * new Object[] { solutions.indexOf(currentSolution),
			 * currentSolution.getPlacedBoxes().getBoxes().size(),
			 * currentSolution.getIdList().toString(), currentSolution.getCost() }));
			 */
		}
		return totalCost;
	}

	private void writeToFile(Solution solution, String name) {
		PrintWriter writer = null;
		String folderPath = Utility.getInstance().getConfigValue(Constant.OUTPUT_PATH);
		try {

			List<PartialSolution> solutions = new ArrayList<>();
			for (Container con : solution.getContainerList()) {
				solutions.add(con.getCurrentSolution());
			}
			String filePath = "";
			String fileName = String.format("%s_%s",
					new Object[] { this.getContainerLoading().getInstanceName(), name });
			filePath = folderPath + fileName;
			FileWriter fileWrite = new FileWriter(filePath);
			writer = new PrintWriter(fileWrite);
			double totalCost = 0;
			// write header.

			for (PartialSolution s : solutions) {
				int index = solutions.indexOf(s);
				totalCost += s.getCost();
				writer.println(String.format("Route: %d, cost: %.3f, number of customers %d: %s",
						new Object[] { index + 1, s.getCost(), s.getIdList().size(), s.getLocationIds() }));
			}

			writer.println(String.format("Total cost of solution : %f", new Object[] { totalCost }));
			// write content/
			for (PartialSolution s : solutions) {
				int index = solutions.indexOf(s);
				writer.write(
						String.format("route: %d, customer: %d", new Object[] { index + 1, s.getIdList().size() }));
				writer.println();
				writer.println();
				writer.println();
				for (String id : s.getIdList()) {
					List<Box> boxList = new ArrayList<Box>();
					for (Box box : s.getPlacedBoxes().getBoxes()) {
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

				String jsonFilePath = "";
				jsonFilePath = folderPath + fileName + index;
				Utility.getInstance().writeJsonFile(s.getPlacedBoxes().getBoxes(), jsonFilePath);
			}
			writer.flush();
			// sIndex++;

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public GreedyHeuristic() throws IOException {
		this.bestSolutionList = new ArrayList<Solution>();
		this.notPlacedLocations = new HashMap<>();
		this.containerList = new ArrayList<Container>();
		//this.ran = new Random(this.ranLongId);
		this.ran = new Random();
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
		this.containerList.clear();
		for (Container con : containers) {
			Container nCon = new Container(con);
			this.containerList.add(nCon);
		}

	}

	private void initiliaze() throws IOException {
		this.loadAlgorithms();
		this.setContainerLoading(new ContainerLoading());
		this.containerLoading.loadingData();
		this.setGreedyInstance(new Greedy());
		this.greedyInstance.loadParameters();
		this.greedyInstance.setRan(this.ran);
		this.greedyInstance.setConLoading(this.getContainerLoading());
		this.setContainerList(this.getContainerLoading().getContainerList());
		this.solutions = new ArrayList<PartialSolution>();
		this.locationList = new ArrayList<Location>(this.getContainerLoading().getLocationList());
		this.solutionList = new ArrayList<Container>();
		this.testSolutionList = new ArrayList<PartialSolution>();
		this.runningTime = Long.valueOf(Utility.getInstance().getConfigValue("running_time"));
		this.constRoundNumber = Integer.valueOf(Utility.getInstance().getConfigValue("round_number"));
		this.boxComp = new BoxComparator(this.ran);
		this.spaceComp = new SpaceComparator(this.ran);

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

		// lc.setLocationList(this.locationList);

	}

	private List<Location> getClosetLocations() {
		List<Location> closestLocations = new ArrayList<Location>();
		// logger.info("\n\n\n");
		// for (Location l : this.locationList) {
		//
		// logger.info(String.format("%s - isVisited: %b - isChecked: %b",
		// new Object[] { l.getLocationID(), l.isVisited(), l.isChecked() }));
		//
		// }
		String idList = "random id list: ";
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
						
			idList = idList.concat(" " + this.currLocation.getLocationList().get(lIndex).getLocationID() + " : " + this.currLocation.getLocationList().get(lIndex).getDistance());
			closestLocations.add(this.currLocation.getLocationList().get(lIndex));
			this.lIndex++;
			i++;

		}
		//logger.info(idList);
		return closestLocations;
	}

	private List<Location> getClosetLocations(Location currLocation) {
		List<Location> closestLocations = new ArrayList<Location>();
		int i = 0;
		lIndex = 0;
		while (i < 3) {
			if (lIndex >= currLocation.getLocationList().size()) {
				lIndex = 0;
				break;
			}

			Location location = currLocation.getLocationList().get(lIndex);

			if (location.isVisited() || location.isChecked()) {
				lIndex++;
				continue;
			}

			closestLocations.add(currLocation.getLocationList().get(lIndex));
			this.lIndex++;
			i++;

		}
		return closestLocations;

	}

	private boolean checkLocationIsFeasible(PartialSolution solution, Location lc) {
		boolean foundSolution = false;
		int subRound = 0;
		while (subRound < 1) {
			greedyInstance.setAlgorithm(getRandomGreedyAlgorithm());
			greedyInstance.setAvaiableSpaces(solution.getAvaiableSpaces());
			greedyInstance.setPlacedBoxes(solution.getPlacedBoxes());
			greedyInstance.setNotPlacedBoxes(lc.getBoxes());

			//this.boxComp.reloadOrderString();
			//this.spaceComp.reloadOrderString();

			while (greedyInstance.getNotPlacedBoxes().getBoxes().size() > 0) {
				FeasibleObject feasibleItem;

				switch (greedyInstance.getAlgorithm()) {
				case Constant.GREEDY_SB:
					feasibleItem = greedyInstance.greedySbAlgorithm(this.boxComp, this.spaceComp);
					break;
				case Constant.GREEDY_BS:
					feasibleItem = greedyInstance.greedyBsAlgorithm(this.boxComp, this.spaceComp);
					break;
				case Constant.GREEDY_ST:
					feasibleItem = greedyInstance.greedyStAlgorithm();
					break;
				case Constant.GREEDY_VL:
					feasibleItem = greedyInstance.greedyVlAlgorithm();
					break;
				default:
					feasibleItem = greedyInstance.greedyBsAlgorithm(this.boxComp, this.spaceComp);
				}

				if (feasibleItem != null) {
					greedyInstance.update(feasibleItem);
					greedyInstance.updateSpaces(feasibleItem);
					Object[] objects = greedyInstance.getNotPlacedBoxes().getBoxes().toArray();
					Box[] boxList = Arrays.copyOf(objects, objects.length, Box[].class);
					Utility.getInstance().reOrderBox(boxList);
					greedyInstance.getNotPlacedBoxes().getBoxes().clear();
					greedyInstance.getNotPlacedBoxes().getBoxes().addAll(Arrays.asList(boxList));
				} else {
					subRound++;
					break;
				}
			}

			if (greedyInstance.getNotPlacedBoxes().getBoxes().size() == 0) {
				// Reload real not placed box.
				List<Box> notPlacedBoxes = new ArrayList<Box>();

				for (Box box : solution.getNotPlacedBoxes().getBoxes()) {
					if (box.getCustomerId().equalsIgnoreCase(lc.getLocationID())) {
						continue;
					}
					Box nBox = new Box(box);
					notPlacedBoxes.add(nBox);
				}

				greedyInstance.getNotPlacedBoxes().setBoxes(notPlacedBoxes);
				//greedyInstance.showResult();
				foundSolution = true;
				break;
			}

		}
		return foundSolution;
	
	}

	private Container checkLocationIsFeasible_01(PartialSolution currSolution, Location lc) throws InterruptedException {		
		boolean foundedPattern = false;
		Container con = null;
		int numberOfCheck = 0;
		int limit_round_number = 0;
		List<String> newLcIdList = new ArrayList<>();
		List<Location> locationList = new ArrayList<>();
		List<String> lcIdList = currSolution.getIdList();	
		List<String> testLocationIdList = new ArrayList<>();
		
		//logger.info("Current id list: "  + currSolution.getIdList().toString());
		
		switch (currSolution.getLocationIds().length()) {
		case 2:
			limit_round_number = 5;
			break;
		case 3:
			limit_round_number = 16;
			break;
		case 4:
			limit_round_number = 32;
			break;
		default:
			limit_round_number = 64;		
			break;
		}
		for(String id : lcIdList) {
			String nId = id;
			testLocationIdList.add(nId);			
		}
		
		String newId = lc.getLocationID();
		testLocationIdList.add(newId);
		
		
		
		

		for (String id : testLocationIdList) {
			for (Location location : this.getLocations()) {
				if (location.getLocationID().equalsIgnoreCase(id)) {
					Location tLocation = new Location(location);
					locationList.add(tLocation);
				}
			}
		}
		
	
		
		while (true) {			
			//logger.info("Number of check: " + numberOfCheck);
			//logger.info("Number of check: " + numberOfCheck);
			if(numberOfCheck >= limit_round_number) {
				
				break;
			}			
			this.boxComp.reloadOrderString();
			this.spaceComp.reloadOrderString();
			
			con = new Container(this.getContainerLoading().getProblem().getContainerList().get(0));
			List<Location> testLocationList = new ArrayList<>();
			
			for(Location l : locationList) {
				Location testLocation = new Location(l);
				testLocation.setIsChecked(false);
				testLocation.setVisited(false);
				testLocationList.add(testLocation);
			}
			
			newLcIdList.clear();
			String idList = "";
			
			//if(testLocationIdList.size() == 2) {
				Location deport = testLocationList.get(0);
				if(checkLocationIsFeasible(con.getCurrentSolution(), deport)) {
					newLcIdList.add(deport.getLocationID());
					idList = idList.concat(" " + deport.getLocationID());
					deport.setVisited(true);
					deport.setIsChecked(true);
					con.updateContainer(this.greedyInstance, deport);
					testLocationList.remove(0);
				}
				
				
				
				while(testLocationList.size() > 0) {								
									
					int index = this.getRandomNumber(0, testLocationList.size() -1);
					Location testLocation = testLocationList.get(index);
									
					
					if(testLocation.isChecked()) {
						continue;
					}
					
					if(checkLocationIsFeasible(con.getCurrentSolution(), testLocation)) {
						if(testLocationIdList.size() == newLcIdList.size()) {
							foundedPattern = true;
							break;
						}
						
						newLcIdList.add(testLocation.getLocationID());
						idList = idList.concat(" " + testLocation.getLocationID());
						testLocation.setVisited(true);
						testLocation.setIsChecked(true);
						con.updateContainer(this.greedyInstance, testLocation);
					}				
						testLocationList.remove(index);					
																				
				}			
				
				if(testLocationIdList.size() == newLcIdList.size()) {
					foundedPattern = true;
					//logger.info(String.format("old: %s new: %s, capacity: %f", new Object[] {lcIdList.toString(), con.getCurrentSolution().getLocationIds().toString(), con.getCurrentCapacity()}));
					break;
				}
			//}else {
				for (Location l : testLocationList) {
					if (checkLocationIsFeasible(con.getCurrentSolution(), l)) {
						newLcIdList.add(l.getLocationID());
						idList = idList.concat(" " + l.getLocationID());
						l.setVisited(true);
						l.setIsChecked(true);
						con.updateContainer(this.greedyInstance, l);
					} else {
						foundedPattern = false;
						break;
					}
				}
				// logger.info("Id list: " + idList);
				// Thread.sleep(1000);

				foundedPattern = checkLocationIsVisited(testLocationIdList, newLcIdList);

				if (foundedPattern == true) {
					// logger.info(String.format("old: %s new: %s, capacity: %f", new Object[]
					// {lcIdList.toString(), con.getCurrentSolution().getLocationIds().toString(),
					// con.getCurrentCapacity()}));
					break;
				}
			//}
											
			
			numberOfCheck++;
		}		
		
		
		if(foundedPattern) {
			return con;
		}
				
		return null;

	}

	private boolean checkLocationIsVisited(List<String> currentLocations, List<String> newLocations) {
		if (currentLocations.size() != newLocations.size()) {
			return false;
		}

		for (int i = 0; i < newLocations.size(); i++) {
			String nId  = newLocations.get(i);
			String id = currentLocations.get(i);

			if (nId.equalsIgnoreCase(id) == false) {
				return false;
			}
		}
		return true;
	}

	private String getRandomGreedyAlgorithm() {
		Random ranNum = new Random();
		// int aRandomPos = ranNum.nextInt(this.algorithms.size());
		int aRandomPos = this.ran.nextInt(this.algorithms.size());
		return this.algorithms.get(aRandomPos);
	}

	private void loadAlgorithms() {
		this.algorithms = new ArrayList<String>();
		// this.algorithms.add(Greedy.BS_ALGORITHM);
		// this.algorithms.add(Greedy.SB_ALGORITHM);
		// this.algorithms.add(Greedy.ST_ALGORITHM);
		// this.algorithms.add(Greedy.VL_ALGORITHM);

		// UPRO
		this.algorithms.add(Constant.GREEDY_SB);
		this.algorithms.add(Constant.GREEDY_BS);
		this.algorithms.add(Constant.GREEDY_ST);
		this.algorithms.add(Constant.GREEDY_VL);
		// END-UPRO

		// this.algorithms.add(Constant.GREEDY_SB);
		// this.algorithms.add(Constant.GREEDY_BS);
		// this.algorithms.add(Constant.GREEDY_ST);
		// this.algorithms.add(Constant.GREEDY_VL);
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
		return timeUnit.convert(diffInMillies, TimeUnit.MILLISECONDS);
	}

	public Solution getBestSolution() {
		return bestSolution;
	}

	public void setBestSolution(Solution bestSolution) {
		this.bestSolution = bestSolution;
	}

	private boolean routingSearch() throws InterruptedException {
		logger.info("\n");
		logger.info("Initial solution");
		// this.newSolution.showResult();
		Solution partialSolution = new Solution(this.newSolution);
		int roundNumber = 0;
		boolean isFindNewSolution = false;

		while (roundNumber <= this.constRoundNumber) {
			if (isFindNewSolution == true) {
				partialSolution = new Solution(this.newSolution);
				isFindNewSolution = false;
			}
			// List<Location> notPlacedLocations = new ArrayList<Location>();
			List<Container> containers = new ArrayList<Container>();
			List<Location> closestLocations = new ArrayList<Location>();
			List<String> randomLocations = new ArrayList<>();

			while (true) {									
				roundNumber++;
				if(roundNumber % 400 == 0 ) {
					logger.info("round number:" + roundNumber);
				}				
				containers.clear();			
				this.initLocationContext();				
				randomLocations = this.getRandomLocations(partialSolution, containers);
				
				if (randomLocations.size() == 0 || randomLocations.size() == 1) {
					continue;
				}

				/*
				 * for(Location lc : this.locationList) { logger.info(String.format("%s %b %b",
				 * new Object[] {lc.getLocationID(), lc.isChecked(), lc.isVisited()}));
				 * logger.info("Sub location"); for(Location subLocation : lc.getLocationList())
				 * { logger.info(String.format("%s %b %b", new Object[]
				 * {subLocation.getLocationID(), subLocation.isChecked(),
				 * subLocation.isVisited()})); } }
				 */

				while (checkAllLocationIsVisited() == false) {
					if (this.checkContainersIsFull(containers)) {
//						 logger.info("\n"); 
//						 //logger.info("All container is full");
//						 Solution tempSolution = new Solution();
//						 tempSolution.setContainerList(containers);
//						 tempSolution.showResult();						 
//						 logger.info("\n");
						// Thread.sleep(500);
						break;
					}

					int index = this.getRandomNumber(0, containers.size() - 1);
					Container con = containers.get(index);

					// for(Container c : containers) {
					// if(c.getCurrentSolution().getIdList().size() == 1) {
					// con = c;
					// break;
					// }
					// }

					if (con.isFull()) {
						continue;
					}

					this.resetCheckLocation();

					boolean isLocationInserted = false;

					while (true) {

						Location tempLocation = con.getCurrentSolution().getLocationList().get(con.getCurrentSolution().getLocationList().size() - 1);
						Location currentLocation = this.getLocationById(tempLocation.getLocationID());
					
						if (isLocationInserted == true || closestLocations.isEmpty()) {
							closestLocations = this.getClosetLocations(currentLocation);
						}

						if (closestLocations.isEmpty()) {							
							con.setFull(true);
							break;
						}

						int lIndex = this.getRandomNumber(0, closestLocations.size() - 1);
						Location location = closestLocations.get(lIndex);

						if (con.checkCapacity(location.getDemand()) == false) {
							for (int i = 0; i < closestLocations.size(); i++) {
								//logger.info("id list: " + con.getCurrentSolution().getIdList().toString() + "Capacity: " + (con.getCurrentCapacity() + location.getDemand()) + "id: " + location.getLocationID());
								Location lc = closestLocations.get(i);
								if (location.getX() == lc.getX() && location.getY() == lc.getY()) {
									closestLocations.remove(i);
									break;
								}
							}
							this.setCheckLocation(location.getLocationID());
							continue;
						}

						/*
						 * if(!checkDemand(con, location)){ closestLocations.remove(location);
						 * this.setCheckLocation(location.getLocationID()); continue; }
						 */

						// this.boxComp.reloadOrderString();
						// this.spaceComp.reloadOrderString();

						if (this.checkLocationIsFeasible(con.getCurrentSolution(), location) == false) {							
							Container newContainer = this.checkLocationIsFeasible_01(con.getCurrentSolution(), location);
							if(newContainer != null) {										
								containers.set(index, newContainer);
								con = containers.get(index);
								isLocationInserted = true;
								this.updateLocation(location);																							
								//con.updateContainer(greedyInstance, location);
								//con.addCapacity(this.currLocation.getCapacity());
								closestLocations.remove(lIndex);			
								continue;
							}
							
							//when not found any new packing pattern.							
							isLocationInserted = false;
							location.setIsChecked(true);
							this.setCheckLocation(location.getLocationID());
							closestLocations.remove(lIndex);
							continue;
																			
						}

						isLocationInserted = true;
						this.updateLocation(location);
						con.updateContainer(greedyInstance, location);
						//con.addCapacity(this.currLocation.getCapacity());
						closestLocations.remove(lIndex);

					}
				}

				if (this.checkAllLocationIsVisited() == true) {
					// logger.info("\n");
				    //logger.info("Round number: " + roundNumber);
					//this.newSolution.showResult();

					// logger.info(randomLocations.toString());

					Solution newSolution = new Solution();
					newSolution.setContainerList(containers);
					// newSolution.showResult();
					// this.writeToFile(newSolution, "Round_number_" + roundNumber);
					// System.out.println("round number: " + roundNumber);

					if (this.bestSolution == null) {
						this.bestSolution = newSolution;
					} else {
						if (this.bestSolution.getTotalCost() > newSolution.getTotalCost()) {
							this.bestSolution = newSolution;
							this.writeToFile(this.bestSolution, "Routing");
						}
					}

					// logger.info("Best solution code: " + this.bestSolution.getTotalCost());

					if (newSolution.getTotalCost() < this.newSolution.getTotalCost()) {
						// Thread.sleep(3000);
						// boolean isLegal = true;
						// for(Container con : containers) {
						// if(con.getCurrentSolution().getPlacedBoxes().getBoxes().size() == 1) {
						// logger.info("error of round number: " + roundNumber );
						// //isLegal = false;
						// //break;
						// }
						// }
						//
						// if(isLegal == false) {
						// continue;
						// }

						logger.info("Find a better solution");
						logger.info("Round number: " + roundNumber);
						this.newSolution = newSolution;
						newSolution.showResult();
						this.writeToFile(newSolution, "Round_number_" + roundNumber);
						isFindNewSolution = true;
						roundNumber = 0;
					}

					break;
				}

				/*
				 * if(isFindNewSolution == true) { break; }
				 */

				if (roundNumber > this.constRoundNumber) {
					// logger.info("Stop: " + roundNumber);
					// roundNumber = 0;
					isFindNewSolution = true;
					break;
				}
			}

			if (roundNumber > this.constRoundNumber) {
				logger.info("Stop: " + roundNumber);
				isFindNewSolution = true;
				break;
			}
		}

		return isFindNewSolution;
	}

	private boolean checkDemand(Container container, Location lc) {
		if (container.getCapacity() < container.getCurrentCapacity() + lc.getDemand()) {
			return false;
		}
		return true;
	}

	private boolean checkContainersIsFull(List<Container> containers) {
		for (Container con : containers) {
			if (con.isFull() == false) {
				return false;
			}
		}
		return true;
	}
	/*
	 * private Location getLocationById(String id) { for(Location lc :
	 * this.locationList) { if(lc.getLocationID().equalsIgnoreCase(id)) { return lc;
	 * } } return null; }
	 */

	private void initLocationContext() {
		for (Location lc : this.locationList) {
			lc.setIsChecked(true);
			lc.setVisited(true);
			for (Location subLocation : lc.getLocationList()) {
				subLocation.setIsChecked(true);
				subLocation.setVisited(true);
			}
		}
	}

	private boolean checkAllLocationIsVisited() {
		for (Location l : this.locationList) {
			if (l.isVisited() == false) {
				return false;
			}
		}
		return true;
	}

	private List<String> getRandomLocations(Solution partialSolution, List<Container> containers) {
		// List<Location> locationList = new ArrayList<Location>();
		List<String> randomLocations = new ArrayList<>();
		for (Container con : partialSolution.getContainerList()) {
			int lastIndex = 0;
			if (con.getCurrentSolution().getIdList().size() > 0) {
				lastIndex = con.getCurrentSolution().getIdList().size() - 1;
			}

			Container preCon = new Container(con);
			preCon.setFull(false);
			int ranIndex = 0;
			if(lastIndex > 0) {
				ranIndex = this.getRandomNumber(1, lastIndex);
			}
			
			//int ranIndex = this.getRandomNumber(0, lastIndex);
			// logger.info("size: " + con.getCurrentSolution().getIdList().size() + " index:
			// " + lastIndex);
			if (ranIndex == lastIndex) {
				containers.add(preCon);
				continue;
			}

			PartialSolution preSolution = con.getSolutionList().get(ranIndex);
			preCon.setAvaiableSpaces(preSolution.getAvaiableSpaces());
			preCon.setCurrentSolution(preSolution);
			preCon.setCurrentCapacity(preSolution.getCurrCapacity());
			List<PartialSolution> preSolutionList = new ArrayList<PartialSolution>();

			for (PartialSolution pSolution : preCon.getSolutionList().subList(0, ranIndex + 1)) {
				preSolutionList.add(pSolution);
			}

			preCon.setSolutionList(preSolutionList);
			containers.add(preCon);
			// logger.info("last index: " + lastIndex);
			// logger.info("pre solution: "+
			// preCon.getCurrentSolution().getIdList().toString());

			for (Location lc : con.getCurrentSolution().getLocationList().subList(ranIndex + 1, lastIndex + 1)) {
				randomLocations.add(lc.getLocationID());
				for (Location location : this.locationList) {
					if (lc.getLocationID().equalsIgnoreCase(location.getLocationID())) {
						location.setVisited(false);
						location.setIsChecked(false);
						break;
					}

				}

				for (Location pLocation : this.locationList) {
					for (Location subLocation : pLocation.getLocationList()) {
						if (subLocation.getLocationID().equalsIgnoreCase(lc.getLocationID())) {
							subLocation.setVisited(false);
							subLocation.setIsChecked(false);
						}
					}
				}

				/*
				 * takeoutId = takeoutId +" "+lc.getLocationID()+" "; Location rLocation = new
				 * Location(lc);
				 * 
				 * rLocation.setIsChecked(false); rLocation.setVisited(false);
				 * locationList.add(rLocation);
				 */
			}

			// logger.info("take out ids: "+ takeoutId);
			// logger.info("solution: " +
			// con.getCurrentSolution().getLocationIds().toString());
		}

		for (Location lc : locationList) {
			boolean isVisited = true;

			for (String ranLcId : randomLocations) {
				if (lc.getLocationID().compareToIgnoreCase(ranLcId) == 0) {
					isVisited = false;
				}
			}

			lc.setIsChecked(isVisited);
			lc.setVisited(isVisited);

			for (Location subLc : lc.getLocationList()) {
				if (lc.getLocationID().compareToIgnoreCase(subLc.getLocationID()) == 0) {
					subLc.setVisited(isVisited);
					subLc.setVisited(isVisited);
				}
			}
		}

		 //logger.info("Random locations: " + randomLocations.toString());

		return randomLocations;
	}

	private int getRandomNumber(int low, int height) {
		// Random ran = new Random(ranLongId);
		return this.ran.nextInt((height - low) + 1) + low;
	}

	public boolean exchangeCustomer() throws Exception {
		Random randomGenerator = new Random();
		Solution solution = bestSolutionList.get(0);
		List<Container> containerList = solution.getContainerList();
		boolean isFeasible = false;
		lPosition = new ArrayList<>(Arrays.asList("0 1", "1 0"));
		// try remove number of customers
		for (Container container : containerList) {
			if (container.getCurrentSolution().getLocationList().size() == 2) {
				lPosition = new ArrayList<>(Arrays.asList("0"));
				break;
			}
		}

		// int takeOutNumber = 2;

		/*
		 * if (takeOutNumber == 2) {
		 * 
		 * } else {
		 * 
		 * }
		 */

		for (Container container : containerList) {
			// PartialSolution lastSolution = container.getCurrentSolution();

			// testSolution.setTakeOutLocation(takeOutLocationList);

			testSolutionList.add(this.initRoutingData(container, lPosition.size()));
		}

		// for(PartialSolution ts : testSolutionList) {
		// logger.info(ts.getIdList().toString());
		// List<Location> takeOutLocationList = ts.getTakeOutLocation();
		// String takeoutIdList = "";
		// for(Location lc : takeOutLocationList) {
		// takeoutIdList = takeoutIdList.concat(lc.getLocationID() + " ");
		// }
		//
		// logger.info("takeout ids: " + takeoutIdList);
		// logger.info("\n");
		// }

		int roundNumber = 0;
		while (!isFeasible) {
			int index = randomGenerator.nextInt(testSolutionList.size());
			PartialSolution lSolution = testSolutionList.get(index);

			if (lSolution.isExChange() || lSolution.isTested()) {
				continue;
			}

			List<Location> locationList = lSolution.getTakeOutLocation();
			for (PartialSolution iSolution : testSolutionList) {
				if (index == testSolutionList.indexOf(iSolution) || iSolution.isInserted()) {
					continue;
				}
				if (this.checkExchangeLocationIsPossible(locationList, iSolution)) {
					lSolution.setExChange(true);
					iSolution.setInserted(true);
					// logger.info(String.format("Test: %d %d result: %b", new Object[]
					// {testSolutionList.indexOf(lSolution),
					// testSolutionList.indexOf(iSolution),iSolution.isInserted()}));
					break;
				}

				// logger.info(String.format("Test: %d %d result: %b", new Object[]
				// {testSolutionList.indexOf(lSolution),
				// testSolutionList.indexOf(iSolution),iSolution.isInserted()}));
			}
			// logger.info("solution: " + index + " is tested");
			lSolution.setTested(true);

			/*
			 * if (lSolution.isExChange() == false) { boolean isRealNotFit = false; for
			 * (PartialSolution pSolution : testSolutionList) {
			 * //logger.info(String.format(" %d %d is Exchange: %b isInseted: %b", new
			 * Object[] { index, // testSolutionList.indexOf(pSolution),
			 * pSolution.isExChange(), pSolution.isInserted() })); if (index ==
			 * testSolutionList.indexOf(pSolution)) { continue; }
			 * 
			 * if (pSolution.isInserted() == false) { isRealNotFit = true; break; } }
			 * 
			 * if (isRealNotFit) { isFeasible = false; } else { isFeasible = true;
			 * testSolutionList.remove(index);
			 * containerList.get(index).getCurrentSolution().setTested(true);
			 * testSolutionList.add(index,containerList.get(index).getCurrentSolution());
			 * 
			 * } }
			 */

			if (!checkAllLocationsIsTested(testSolutionList)) {
				continue;
			}

			isFeasible = true;
			for (int i = 0; i < testSolutionList.size(); i++) {
				PartialSolution iSolution = testSolutionList.get(i);
				if (iSolution.isExChange() == false && iSolution.isInserted() == false) {
					testSolutionList.remove(i);
					containerList.get(i).getCurrentSolution().setTested(true);
					containerList.get(i).getCurrentSolution().setExChange(true);
					containerList.get(i).getCurrentSolution().setInserted(true);
					testSolutionList.add(i, containerList.get(i).getCurrentSolution());
					continue;
				}

				if (iSolution.isExChange() != iSolution.isInserted()) {
					isFeasible = false;
				}

			}

			if (!isFeasible) {
				// testSolutionList.clear();
				// for (Container container : containerList) {
				// // PartialSolution lastSolution = container.getCurrentSolution();
				//
				// // testSolution.setTakeOutLocation(takeOutLocationList);
				// testSolutionList.add(this.initRoutingData(container, takeOutNumber));
				// }
				// logger.info("Routing round number: " + roundNumber);
				roundNumber++;

				if (roundNumber == 10) {
					break;
				}

				this.initTestRoutingData(containerList, lPosition.size());
				continue;
			}

			isFeasible = true;
			logger.info("Found new solution");
			double totalCost = 0;
			// for (PartialSolution nSolution : testSolutionList) {
			// nSolution.calculateCost();
			// totalCost = totalCost + nSolution.getCost();
			// //logger.info(nSolution.getIdList().toString() + nSolution.isTested());
			// }

			// Prepared data for exchange customers.
			logger.info("\n Current Solution: ");
			for (Container container : containerList) {
				logger.info(container.getCurrentSolution().getIdList().toString());
			}

			// totalCost = this.showResult(testSolutionList);
			logger.info(String.format("nTotalCost: %f oTotalCost: %f bestTotalCost: %f %f", new Object[] { totalCost,
					bestSolutionList.get(0).getTotalCost(), bestSolution.getTotalCost(), this.bestRoutingTotalCost }));
			this.routingTotalCost = totalCost;

			/*
			 * if(totalCost < bestSolutionList.get(0).getTotalCost()) {
			 * testSolutionList.clear(); for (Container container : containerList) { //
			 * PartialSolution lastSolution = container.getCurrentSolution();
			 * 
			 * // testSolution.setTakeOutLocation(takeOutLocationList);
			 * testSolutionList.add(this.initRoutingData(container, takeOutNumber)); }
			 * isFeasible = false; }else { this.showResult(testSolutionList);
			 * this.writeToFile(testSolutionList); }
			 */

			// boolean isFoundNewSolution = true;
			// for (PartialSolution rSolution : testSolutionList) {
			// if (rSolution.isInserted() == false) {
			// isFoundNewSolution = false;
			// }
			// }

			// if (!isFoundNewSolution) {
			//
			// } else {
			// isFeasible = true;
			// }

			/*
			 * for(PartialSolution test : testSolutionList) { if(test.isExChange()) {
			 * continue; }
			 * 
			 * List<Location> locationList = test.getTakeOutLocation();
			 * List<PartialSolution> testAbleSolution = new ArrayList<PartialSolution>();
			 * 
			 * for(PartialSolution s : testSolutionList) { if(s.isInserted() ||
			 * test.getLocationIds().equalsIgnoreCase(s.getLocationIds())) { continue; }
			 * 
			 * testAbleSolution.add(s); }
			 * 
			 * for(PartialSolution testSolution : testAbleSolution) {
			 * if(this.checkExchangeLocationIsPossible(locationList, testSolution)) {
			 * test.setExChange(true); testSolution.setInserted(true); } } }
			 */

		}
		return isFeasible;
	}

	private void initTestRoutingData(List<Container> containerList, int takeOutNumber) {
		testSolutionList.clear();
		for (Container container : containerList) {
			// PartialSolution lastSolution = container.getCurrentSolution();
			// testSolution.setTakeOutLocation(takeOutLocationList);

			testSolutionList.add(this.initRoutingData(container, takeOutNumber));
		}
	}

	private boolean checkAllLocationsIsTested(List<PartialSolution> solutions) {
		for (PartialSolution solution : solutions) {
			if (!solution.isTested()) {
				return false;
			}
		}
		return true;
	}

	private boolean checkExchangeLocationIsPossible(List<Location> testLocations, PartialSolution solution) {
		boolean isExchangeSucess = true;

		List<PartialSolution> tempSolutions = new ArrayList<PartialSolution>();
		PartialSolution bestSolution = null;
		PartialSolution tempSolution = this.cloneSolution(solution);
		if (checkLocationCapacity(tempSolution, testLocations) == false) {
			isExchangeSucess = false;
			return isExchangeSucess;
		}

		for (String p : lPosition) {
			String[] testOrder = p.split(" ");
			tempSolution = this.cloneSolution(solution);
			boolean isFit = true;
			for (int i = 0; i < testOrder.length; i++) {
				int index = Integer.valueOf(i);
				Location location = testLocations.get(index);

				if (!checkLocationIsFeasible(tempSolution, location)) {
					isFit = false;
					break;
				}

				tempSolution.getIdList().add(location.getLocationID());
				tempSolution.getLocationList().add(location);

				tempSolution.setAvaiableSpaces(this.greedyInstance.getAvaiableSpaces());
				tempSolution.setPlacedBoxes(greedyInstance.getPlacedBoxes());
				tempSolution.setNotPlacedBoxes(greedyInstance.getNotPlacedBoxes());
				tempSolution.setCurrCapacity(tempSolution.getCurrCapacity() + location.getCapacity());
				tempSolution.calculateCost();
			}

			if (isFit) {
				tempSolutions.add(tempSolution);
			}
		}

		for (PartialSolution s : tempSolutions) {
			if (bestSolution == null) {
				bestSolution = s;
				continue;
			}

			if (bestSolution.getCost() > s.getCost()) {
				bestSolution = s;
			}
		}

		if (bestSolution == null) {
			isExchangeSucess = false;
		}

		/*
		 * for (Location location : testLocations) { if
		 * (checkLocationIsFeasible(tempSolution, location)) {
		 * tempSolution.getIdList().add(location.getLocationID());
		 * tempSolution.getLocationList().add(location);
		 * 
		 * tempSolution.setAvaiableSpaces(this.greedyInstance.getAvaiableSpaces());
		 * tempSolution.setPlacedBoxes(greedyInstance.getPlacedBoxes());
		 * tempSolution.setNotPlacedBoxes(greedyInstance.getNotPlacedBoxes());
		 * tempSolution.setCurrCapacity(tempSolution.getCurrCapacity() +
		 * location.getCapacity()); } else { isExchangeSucess = false; break; } }
		 */

		if (isExchangeSucess) {
			solution.setIdList(bestSolution.getIdList());
			solution.setLocationList(bestSolution.getLocationList());
			solution.setAvaiableSpaces(bestSolution.getAvaiableSpaces());
			solution.setPlacedBoxes(bestSolution.getPlacedBoxes());
			solution.setNotPlacedBoxes(bestSolution.getNotPlacedBoxes());
			solution.setCurrCapacity(bestSolution.getCurrCapacity());
			solution.setCapacity(bestSolution.getCapacity());
		}

		return isExchangeSucess;
	}

	private boolean checkLocationCapacity(PartialSolution solution, List<Location> locationList) {
		double sumCapacity = 0;
		for (Location lc : locationList) {
			sumCapacity += lc.getCapacity();
		}

		if (solution.getCapacity() < solution.getCurrCapacity() + sumCapacity) {
			return false;
		}
		return true;
	}

	private List<Location> getLocations() {
		List<Location> locationList = new ArrayList<Location>();
		for (Location lc : this.getContainerLoading().getLocationList()) {
			Location nLocation = new Location(lc);
			locationList.add(nLocation);
		}
		return locationList;
	}

	private List<Location> getTestLocations(List<Location> lastLocations, int takeOutNumber) {
		List<Location> testLocationList = new ArrayList<Location>();

		int startIndex = locationList.size() - takeOutNumber;
		while (startIndex < locationList.size()) {
			testLocationList.add(locationList.get(startIndex));
			startIndex++;
		}

		return testLocationList;
	}

	private PartialSolution cloneSolution(PartialSolution solution) {
		PartialSolution cloneSolution = new PartialSolution(solution.getAvaiableSpaces(), solution.getPlacedBoxes(),
				solution.getNotPlacedBoxes(), solution.getIdList(), solution.getLocationList());
		cloneSolution.setCapacity(solution.getCapacity());
		cloneSolution.setCurrCapacity(solution.getCurrCapacity());

		return cloneSolution;
	}

	private PartialSolution initRoutingData(Container container, int takeoutNumber) {
		PartialSolution lastSolution = container.getCurrentSolution();
		// logger.info(lastSolution.getIdList().toString());
		int lastIndex = container.getSolutionList().size();
		PartialSolution preSolution;
		if (lastIndex == 0) {
			preSolution = lastSolution;
		} else {
			preSolution = container.getSolutionList().get(lastIndex - takeoutNumber);
		}

		// PartialSolution preSolution = container.getSolutionList().get(lastIndex -
		// takeoutNumber);
		PartialSolution cloneSolution = this.cloneSolution(preSolution);

		if (lastIndex == 0) {
			cloneSolution.setExChange(true);
			cloneSolution.setTested(true);
		} else {
			List<Location> takeoutLocationList = new ArrayList<Location>();
			int startIndex = lastSolution.getLocationList().size() - takeoutNumber;
			while (startIndex < (lastSolution.getLocationList().size())) {
				takeoutLocationList.add(lastSolution.getLocationList().get(startIndex));
				startIndex++;
			}
			cloneSolution.setTakeOutLocation(takeoutLocationList);
		}

		return cloneSolution;
	}

	private Location getLocationById(String id) {
		for (Location lc : this.locationList) {
			if (lc.getLocationID().equalsIgnoreCase(id)) {
				return lc;
			}
		}
		return null;
	}

	public long getRunningTime() {
		return runningTime;
	}

	public HashMap<String, List<Location>> notPlacedLocations;
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
	private List<String> lPosition;
	private Location currLocation;
	private double routingTotalCost;
	private double bestRoutingTotalCost = 100000;
	private int roundNumber;
	private int constRoundNumber;
	private long runningTime;
	private List<Solution> bestSolutionList;
	private Solution currentSolution;
	private Solution newSolution;
	private BoxComparator boxComp;
	private SpaceComparator spaceComp;
	private List<PartialSolution> testSolutionList;
	private Solution bestSolution;
	private Date startDate;
	private Date endDate;
	private Random ran;
	private final long ranLongId = 123456;
}
