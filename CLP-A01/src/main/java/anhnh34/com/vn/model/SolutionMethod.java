package anhnh34.com.vn.model;

import java.util.Arrays;
import java.io.InputStream;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

public class SolutionMethod {
	final static Logger logger = Logger.getLogger(SolutionMethod.class);
	public ContainerLoading conLoading;
	private Greedy greedyInstance;
	
	public void setupLog4j() {
		InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("log4j.properties");
		PropertyConfigurator.configure(inputStream);
	}

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
		
		while (greedyInstance.getNotPlacedBoxes().getBoxes().size() > 0) {								

			logger.info("\n");
			logger.info(String.format("-------Start round: %d ----------", new Object[] { roundNumber }));			
			
			greedyInstance.setRoundNumber(roundNumber);
			
			String algorithm = Utility.getInstance().getConfigValue("algorithm");
			FeasibleObject feasibleItem;
		
			switch(algorithm) {
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
			
			Box[] boxList = Arrays.copyOf(objects,objects.length,Box[].class);
			Utility.getInstance().reOrderBox(boxList);
			greedyInstance.getNotPlacedBoxes().getBoxes().clear();
			greedyInstance.getNotPlacedBoxes().getBoxes().addAll(Arrays.asList(boxList));
			roundNumber++;
		}
		greedyInstance.showResult();
	}	
}
