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
		ContainerLoading containerLoading = new ContainerLoading();
		SolutionMethod solutionMethod = new SolutionMethod();
		
		Greedy greedy = new Greedy();
		greedy.setSelectedAlgorithm(Greedy.ST_ALGORITHM);				
		greedy.setDefaultRatioSupport(Greedy.NOT_SUPPORT_RATIO);
		solutionMethod.setConLoading(containerLoading);		
		solutionMethod.setGreedyInstance(greedy);		
		try {
			solutionMethod.getConLoading().loadingData();
			greedy.setConLoading(containerLoading);
			//solutionMethod.GREEDY();
			solutionMethod.run();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
