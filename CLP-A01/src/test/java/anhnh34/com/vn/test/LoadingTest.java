package anhnh34.com.vn.test;

import org.apache.log4j.Logger;
import org.junit.Test;

import anhnh34.com.vn.model.Box;
import anhnh34.com.vn.model.BoxComparator;
import anhnh34.com.vn.model.Constant;
import anhnh34.com.vn.model.Container;
import anhnh34.com.vn.model.ContainerLoading;
import anhnh34.com.vn.model.FeasibleObject;
import anhnh34.com.vn.model.Greedy;
import anhnh34.com.vn.model.Location;
import anhnh34.com.vn.model.PartialSolution;
import anhnh34.com.vn.model.Solution;
import anhnh34.com.vn.model.SpaceComparator;
import anhnh34.com.vn.model.Utility;


import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class LoadingTest {
	final static Logger logger = Logger.getLogger(LoadingTest.class);
	private int roundNumber;
	private BoxComparator boxComp;
	private SpaceComparator spaceComp;
	private Random ran;
//
//	@Test
//	public void LoadingUnitTest() throws IOException {
////		ContainerLoading containerLoading = new ContainerLoading();
////
////		containerLoading.loadingData();
////		
////		assertEquals(containerLoading.getProblem().getNumOfItem(),
////				containerLoading.getNotPlacedBox().getBoxes().size());
//	}
//	@Test
//	public void calculation() {
//		PartialSolution partialSolution = new PartialSolution();
//		List<Location> locationList =new  ArrayList<Location>();
//		locationList.add(new Location("0",30,40,0,new ArrayList<Box>()));
//		locationList.add(new Location("13",5,25,0, new ArrayList<Box>()));
//		locationList.add(new Location("14",12,42,0, new ArrayList<Box>()));
//		partialSolution.setLocationList(locationList);
//		partialSolution.calculateCost();
//		System.out.println(partialSolution.getCost());
//		assertEquals(65.6503, partialSolution.getCost(),0.00002);						
//	}
	
	@Test
	public void checkLoadingFesiable() {		
		try {			
			this.ran = new Random(1234567);
			//this.ran = new Random();
			Utility.getInstance().setRan(this.ran);
			this.roundNumber = 1;			
			String result_solution = "";
			String pattern_solution = "1 3 8 7 6";
			String[] testLocation = new String[] {"1", "3", "8", "7", "6"};			
			ContainerLoading containerLoading = new ContainerLoading();
			containerLoading.loadingData();
			Greedy greedyAlgorithm = new Greedy();
			greedyAlgorithm.setConLoading(containerLoading);
			List<Container> containers = containerLoading.getProblem().getContainerList();			
			List<String> foundedSolutionList = new ArrayList<String>();
			List<PartialSolution> solutionList = new ArrayList<PartialSolution>();
			List<PartialSolution> patternSolutionList = new ArrayList<PartialSolution>();
			
			while(patternSolutionList.size() <= 10) {		
			//logger.info("Round number " + roundNumber);
			//while(!pattern_solution.equalsIgnoreCase(result_solution.trim())) {				
				result_solution  = "";				
				
//				if(roundNumber == 829) {
//					Greedy.getLogger().setLevel(Level.ALL);
//				}
//				
//				if(roundNumber == 832) {
//					Greedy.getLogger().setLevel(Level.FATAL);
//				}
				//load location for testing				
				Container container = new Container(containers.get(0));				
				List<Location> locationList = new ArrayList<>();
				for(int i = 0; i < testLocation.length; i++) {
					String id = testLocation[i];
					for(Location lc : containerLoading.getProblem().getLocationList()) {
						if(lc.getLocationID().equalsIgnoreCase(id)) {
							Location nLocation = new Location(lc);
							locationList.add(nLocation);
						}
					}
				}
				
				this.boxComp = new BoxComparator();
				this.spaceComp = new SpaceComparator();
				
				while(true) {
					if(checkAllLocationIsChecked(locationList)) {
						break;
					}
					
					//lookup for new location
					int index  = getRandomNumber(0, locationList.size()-1);
					Location randomLocation = locationList.get(index);
					
					if(randomLocation.isChecked() == true) {
						continue;
					}					
					if(checkLocationIsFeasible(container.getCurrentSolution(), randomLocation, greedyAlgorithm)) {
						this.updateLocation(randomLocation, locationList);
						
						
						// update container info.						
						container.updateContainer(greedyAlgorithm, randomLocation);
						container.addCapacity(randomLocation.getCapacity());		
						result_solution = result_solution.concat(" "+randomLocation.getLocationID());
						if (this.checkContainerFull(container, locationList)) {
							// logger.info(String.format("Container %d is full: %s %.2f",
							// new Object[] { cIndex, container.getCurrentSolution().getIdList().toString(),
							// container.getCurrentCapacity() }));
							// Thread.sleep(2000);																				
							break;
						}						
						
												
					}else {						
						randomLocation.setIsChecked(true);
						this.setCheckLocation(randomLocation.getLocationID(), locationList);						
					}		
					
				}
				
				//logger.info("Result_solution: " + result_solution + " round number: " + roundNumber +"		" +  container.getVolumeUsage() + "%");
				
				
				
//				if(result_solution.trim().split(" ").length >= 3) {
//					boolean isFounded = false;
//					
//					for(String solutionStr :foundedSolutionList) {
//						if(solutionStr.equalsIgnoreCase(result_solution)) {
//							isFounded = true;
//							break;
//						}											
//					}	
//					
//					if(isFounded == false) {
//						foundedSolutionList.add(result_solution);
//						Solution solution = new Solution(); 
//						List<Container> containerList = new ArrayList<>();
//						containerList.add(container);
//						solution.setContainerList(containerList);						
//						Thread.sleep(1000);
//						logger.info("Result_solution: " + result_solution + " round number: " + roundNumber +"		" +  container.getVolumeUsage() + "%");						
//						this.writeToFile(solution, "Partial_Solution_"+roundNumber);							
//					}
//				
//				}
				
//				if(result_solution.contains("14")) {
//					Solution solution = new Solution();
//					List<Container> containerList = new ArrayList<>();
//					containerList.add(container);
//					solution.setContainerList(containerList);					
//					boolean isFounded = false;
//					
//					for(PartialSolution parSolution : solutionList) {						
//						if(parSolution.getPlacedBoxes().getBoxNumber() != container.getCurrentSolution().getPlacedBoxes().getBoxNumber()) {
//							continue;
//						}
//						
//						
//						//System.out.println("partial id list: " + parSolution.getIdList());
//						//System.out.println("        id list: " + container.getCurrentSolution().getIdList());
//						
//						boolean isDifferent = false;
//						
//						for(int i = 0; i < parSolution.getPlacedBoxes().getBoxes().size(); i++) {
//							Box newBox = parSolution.getPlacedBoxes().getBoxes().get(i);												
//							Box box = container.getCurrentSolution().getPlacedBoxes().getBoxes().get(i);
//							
//							if(box.getMinimum().compare(newBox.getMinimum()) == false || box.getMaximum().compare(newBox.getMaximum()) == false) {
//								isDifferent = true;						
//								break;
//							}
//						}			
//						
//						if(isDifferent == false) {
//							isFounded = true;
//							break;
//						}
//					}
//					
//					if(isFounded == false) {
//						solution.showResult();
//						PartialSolution p = new PartialSolution(container.getCurrentSolution());
//						
//						solutionList.add(p);
//						logger.info("number of solution: " + solutionList.size());
//						writeToFile(solution, "partial_solution"+roundNumber);
//					}
//					
//					
//				}
				
				if(result_solution.trim().split(" ").length >= 5) {
//					Solution solution = new Solution(); 
//					List<Container> containerList = new ArrayList<>();
//					containerList.add(container);
//					solution.setContainerList(containerList);										
					boolean isFounded = false;
				
					for(PartialSolution parSolution : patternSolutionList) {						
						if(parSolution.getPlacedBoxes().getBoxNumber() != container.getCurrentSolution().getPlacedBoxes().getBoxNumber()) {
							continue;
						}
						
						
						//System.out.println("partial id list: " + parSolution.getIdList());
						//System.out.println("        id list: " + container.getCurrentSolution().getIdList());
						
						boolean isDifferent = false;
						
						for(int i = 0; i < parSolution.getPlacedBoxes().getBoxes().size(); i++) {
							Box newBox = parSolution.getPlacedBoxes().getBoxes().get(i);												
							Box box = container.getCurrentSolution().getPlacedBoxes().getBoxes().get(i);
							
							if(box.getMinimum().compare(newBox.getMinimum()) == false || box.getMaximum().compare(newBox.getMaximum()) == false) {
								isDifferent = true;						
								break;
							}
						}			
						
						if(isDifferent == false) {
							isFounded = true;
							break;
						}
					}
					
					if(isFounded == false) {
						Solution solution = new Solution(); 
						List<Container> containerList = new ArrayList<>();
						containerList.add(container);
						solution.setContainerList(containerList);	
						solution.showResult();
						PartialSolution p = new PartialSolution(container.getCurrentSolution());
						
						patternSolutionList.add(container.getCurrentSolution());
						logger.info("number of solution: " + patternSolutionList.size());						
						logger.info("Result_solution: " + result_solution + " round number: " + roundNumber +"		" +  container.getVolumeUsage() + "%");
						logger.info("Ya.Founded solution");
						this.writeToFile(solution, result_solution+"_"+roundNumber);
					}
					
				
					//break;
				}
								
				this.roundNumber++;								
			}			
			//System.out.println("Founded solution");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}catch (Exception e) {			
			//System.out.println("round number: " + this.roundNumber);
			e.printStackTrace();
		}
		
	}
	
	private boolean checkSolutionIsFeasible(List<Location> locationList) {				
		for(Location lc : locationList) {
			if(lc.isVisited() == false) {
				return false;		
			}
		}
		return true;
	}
	
	private boolean checkAllLocationIsChecked(List<Location> locationList) {				
		for(Location lc : locationList) {
			if(lc.isChecked() == false) {
				return false;		
			}
		}
		return true;
	} 
	
	private int getRandomNumber(int low, int height) {			
		//Random ran = new Random(ranLongId);						
		return this.ran.nextInt((height - low) + 1) + low;		
	}
	
	
	private boolean checkLocationIsFeasible(PartialSolution solution, Location lc, Greedy greedyInstance) {
		greedyInstance.setRoundNumber(this.roundNumber);
		greedyInstance.setAlgorithm(Utility.getInstance().getRandomGreedyAlgorithm());
		greedyInstance.setAvaiableSpaces(solution.getAvaiableSpaces());
		greedyInstance.setPlacedBoxes(solution.getPlacedBoxes());
		greedyInstance.setNotPlacedBoxes(lc.getBoxes());

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

			for (Box box : solution.getNotPlacedBoxes().getBoxes()) {
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

			//greedyInstance.showResult();
			return true;
		}

		return false;
	}
	
	private void updateLocation(Location lc,List<Location> locationList) {
		Location nextLocation = null;
		for (Location location : locationList) {
			if (location.getX() == lc.getX() && location.getY() == lc.getY()) {
				nextLocation = location;
				break;
			}
		}
		
		nextLocation.setVisited(true);
		nextLocation.setIsChecked(true);
		

		for (Location pLocation : locationList) {
			for (Location subLocation : pLocation.getLocationList()) {
				if (subLocation.getLocationID().equalsIgnoreCase(nextLocation.getLocationID())) {
					subLocation.setVisited(true);
					subLocation.setIsChecked(true);
				}
			}
		}
			
		//lc.setLocationList(this.locationList);
		
	}
	
	private boolean checkContainerFull(Container container, List<Location> locationList) {
		for (Location location : locationList) {
			if (location.isVisited()) {
				continue;
			}

			if (container.getCapacity() >= container.getCurrentCapacity() + location.getCapacity()) {
				return false;
			}

		}
		return true;
	}
	
	private void setCheckLocation(String id, List<Location> locationList) {
		for (Location l : locationList) {
			if (l.getLocationID().equalsIgnoreCase(id)) {
				int index = locationList.indexOf(l);
				locationList.get(index).setIsChecked(true);
			}
			for (Location sl : l.getLocationList()) {
				if (id.equalsIgnoreCase(sl.getLocationID())) {
					int index = l.getLocationList().indexOf(sl);
					l.getLocationList().get(index).setIsChecked(true);
				}
			}
		}

	}
	
	private void writeToFile(Solution solution,String name) {
		PrintWriter writer = null;
		String folderPath = Utility.getInstance().getConfigValue(Constant.OUTPUT_PATH);
		try {
						
			List<PartialSolution> solutions = new ArrayList<>();
			for(Container con :solution.getContainerList()) {
				solutions.add(con.getCurrentSolution());
			}
			String filePath = "";			
			//String fileName = String.format("%s_%s",new Object[] {this.getContainerLoading().getInstanceName(), name});
			filePath = folderPath + name;
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
				jsonFilePath = folderPath + name +"_json_"+ index;
				Utility.getInstance().writeJsonFile(s.getPlacedBoxes().getBoxes(), jsonFilePath);
			}
			writer.flush();
//			sIndex++;	

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}

