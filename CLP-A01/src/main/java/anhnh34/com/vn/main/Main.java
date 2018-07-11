package anhnh34.com.vn.main;

import java.io.IOException;

import org.apache.log4j.BasicConfigurator;

import anhnh34.com.vn.model.ContainerLoading;
import anhnh34.com.vn.model.Greedy;
import anhnh34.com.vn.model.SolutionMethod;

public class Main {
	public static void main(String args[]) {
		// Configs log4j
		BasicConfigurator.configure();
		ContainerLoading container = new ContainerLoading();
		SolutionMethod solutionMethod = new SolutionMethod();
		
		Greedy greedy = new Greedy();
		//greedy.setOptimizeAlgorithm(Greedy.ST_ALGORITHM);
		//greedy.setAlgorithm(Greedy.BS_ALGORITHM);
		
//		greedy.setNSupportRatio(Greedy.NOT_SUPPORT_RATIO);		
		solutionMethod.setGreedyInstance(greedy);		
		solutionMethod.setConLoading(container);
		try {
			solutionMethod.getConLoading().loadingData();
			greedy.setConLoading(container);
			greedy.loadParameters();		
			solutionMethod.run();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
