package anhnh34.com.vn.model;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

public class Container {
	
	private static Logger logger = Logger.getLogger(Container.class);
	
	
	public boolean isFull() {
		return isFull;
	}
	
	public void setFull(boolean isFull) {
		this.isFull = isFull;
	}

	public void setContailerLoading(ContainerLoading contailerLoading) {
		this.contailerLoading = contailerLoading;
	}
	
	public ContainerLoading getContailerLoading() {
		return contailerLoading;
	}
	
	public double getCurrentCapacity() {
		return currentCapacity;
	}
	
	public void setCurrentCapacity(double currentCapacity) {
		this.currentCapacity = currentCapacity;
	}

	public List<PartialSolution> getSolutionList() {
		return solutionList;
	}

	public void setSolutionList(List<PartialSolution> solutionList) {
		this.solutionList.clear();
		for(PartialSolution s : solutionList) {
			PartialSolution nPartialSolution = new PartialSolution(s);
			this.solutionList.add(nPartialSolution);
		}		
	}

	public PartialSolution getCurrentSolution() {
		return this.currentSolution;
	}

	public void setCurrentSolution(PartialSolution currentSolution) {
		this.currentSolution = new PartialSolution(currentSolution);
		//this.currentSolution = currentSolution;
	}

	public List<Space> getAvaiableSpaces() {
		return avaiableSpaces;
	}

	public boolean checkCapacity(double additionCapacity) {
		if (this.currentCapacity + additionCapacity > this.capacity) {
			return false;
		}
		return true;
	}
  public void addCapacity(double additionCapacity) {
		this.currentCapacity = this.currentCapacity + additionCapacity;
		this.currentSolution.setCurrCapacity(this.currentCapacity);
	}

	public void setLength(double length) {
		this.length = length;
	}

	public double getLength() {
		return length;
	}

	public void setNodeList(List<Node> nodeList) {
		this.nodeList = nodeList;
	}

	public List<Node> getNodeList() {
		return nodeList;
	}

	public void addNode(Node node) {
		getNodeList().add(node);
	}

	public double getCapacity() {
		return capacity;
	}

	public void setHeight(double height) {
		this.height = height;
	}

	public void setCapacity(double capacity) {
		this.capacity = capacity;
	}

	public void setAvaiableSpaces(List<Space> avaiableSpaces) {
		this.avaiableSpaces.clear();
		for(Space space : avaiableSpaces) {
			this.avaiableSpaces.add(new Space(space));
		}
		//this.avaiableSpaces = avaiableSpaces;
	}

	public double getHeight() {
		return height;
	}

	public void setWidth(double width) {
		this.width = width;
	}

	public double getWidth() {
		return width;
	}

	public Container(double d, double w, double h) {
		this.length = d;
		this.width = w;
		this.height = h;
		this.volume = this.length * this.width * this.height;		
	}

	public void removeSubset() {

	}

	public void amalgamate() {

	}

	public void spaceSort() {

	}

	public Container() {		
	}

	public void loadingSpace() {
		Dimension minDimension = new Dimension(0, 0, 0);

		// init maximum dimension with corresponde coordinate X, Y, Z
		Dimension maxDimension = new Dimension(length, width, height);
		Space containerSpace = new Space(minDimension, maxDimension);
		containerSpace.setMaximumSupportX(maxDimension.getX());
		containerSpace.setMaximumSupportY(maxDimension.getY());

		if (this.getAvaiableSpaces().isEmpty()) {
			this.getAvaiableSpaces().add(containerSpace);
		}
	}

	public void updateContainer(Greedy greedy, Location lc) {				
		//this.getSolutionList().add(currentSolution);
		PartialSolution  nCurrSolution;
		if(this.currentSolution == null) {
			nCurrSolution = new PartialSolution(greedy.getAvaiableSpaces(),
					greedy.getPlacedBoxes(), greedy.getNotPlacedBoxes(),new ArrayList<String>(),new ArrayList<Location>());
		}else {
			nCurrSolution = new PartialSolution(greedy.getAvaiableSpaces(),
					greedy.getPlacedBoxes(), greedy.getNotPlacedBoxes(),currentSolution.getIdList(),currentSolution.getLocationList());
		}		
		
		nCurrSolution.setCapacity(this.getCapacity());
		nCurrSolution.setCurrCapacity(this.getCurrentCapacity() + lc.getCapacity());
		this.setCurrentCapacity(nCurrSolution.getCurrCapacity());
		
		
//		for(Location l : currentSolution.getLocationList()) {
//			logger.info("current location id: " + l.getLocationID());
//		}
		
		//logger.info("\n\n");
		nCurrSolution.getIdList().add(lc.getLocationID());
		nCurrSolution.addLocation(lc);
		
//		for(Location l : nCurrSolution.getLocationList()) {
//			logger.info("new location id: " + l.getLocationID());
//		}
		this.getSolutionList().add(nCurrSolution);
		
		this.currentSolution = nCurrSolution;

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
	
	public double getVolumeUsage() {
		return this.getCurrentSolution().getVolumeUtitlisation()/this.getVolume() * 100;
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
		// Since we are sorting in-place any leftover elements from the right
		// side
		// are already at the right position.
		return spaceList;
	}
	
	public void initiliaze(ContainerLoading containerLoading) {
		this.currentSolution = new PartialSolution();
		this.avaiableSpaces = new ArrayList<Space>();
		this.nodeList = new ArrayList<Node>();		
		this.solutionList = new ArrayList<PartialSolution>();
		this.avaiableSpaces = new ArrayList<Space>();
		
		Dimension minDimension = new Dimension(0, 0, 0);
		// init maximum dimension with corresponde coordinate X, Y, Z
		Dimension maxDimension = new Dimension(length, width, height);
		Space containerSpace = new Space(minDimension, maxDimension);
		containerSpace.setMaximumSupportX(maxDimension.getX());
		containerSpace.setMaximumSupportY(maxDimension.getY());

		if (this.getAvaiableSpaces().isEmpty()) {			
			this.currentSolution.getAvaiableSpaces().add(containerSpace);							
		}		
		
		this.setContailerLoading(containerLoading);
		this.currentSolution.getPlacedBoxes().setBoxes(new ArrayList<Box>());
		this.currentSolution.getNotPlacedBoxes().setBoxes(containerLoading.getNotPlacedBox().getBoxes());
	}
	
	private void initialize() {
		this.currentSolution = new PartialSolution();
		this.avaiableSpaces = new ArrayList<Space>();
		this.nodeList = new ArrayList<Node>();		
		this.solutionList = new ArrayList<PartialSolution>();
		this.avaiableSpaces = new ArrayList<Space>();
		
		Dimension minDimension = new Dimension(0, 0, 0);		
		// init maximum dimension with corresponde coordinate X, Y, Z
		Dimension maxDimension = new Dimension(length, width, height);
		
		Space containerSpace = new Space(minDimension, maxDimension);
		containerSpace.setMaximumSupportX(maxDimension.getX());
		containerSpace.setMaximumSupportY(maxDimension.getY());
		if(this.getAvaiableSpaces().isEmpty()) {
			this.currentSolution.getAvaiableSpaces().add(containerSpace);			
		}
		this.currentSolution.getPlacedBoxes().setBoxes(new ArrayList<Box>());
		this.currentSolution.getNotPlacedBoxes().setBoxes(this.getContailerLoading().getNotPlacedBox().getBoxes());
	}
	
	public Container (Container container) {
		this.setContailerLoading(container.getContailerLoading());
		this.initialize();		
		this.setLength(container.getLength());
		this.setWidth(container.getWidth());
		this.setHeight(container.getHeight());		
		this.setAvaiableSpaces(container.getAvaiableSpaces());		
		this.setCurrentSolution(new PartialSolution(container.getCurrentSolution()));
		this.setSolutionList(container.getSolutionList());
		this.setCurrentCapacity(container.getCurrentCapacity());
		this.setCapacity(container.getCapacity());	
		this.volume = container.getVolume();
		this.setFull(container.isFull());
					
	}
	
	
	
	public double getVolume() {
		if(this.volume == 0) {
			this.volume = this.length * this.width * this.height;
		}
		return this.volume;
	}
	
	private double length;
	private double width;
	private double height;
	private double capacity;
	private double currentCapacity;
	private List<PartialSolution> solutionList;
	private PartialSolution currentSolution;
	private List<Space> avaiableSpaces;
	private List<Node> nodeList;
	private ContainerLoading contailerLoading;
	private boolean isFull = false;
	private double volume;
}
