package anhnh34.com.vn.main;

import java.io.IOException;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.PropertyConfigurator;

import anhnh34.com.vn.model.ContainerLoading;
import anhnh34.com.vn.model.Greedy;
import anhnh34.com.vn.model.SolutionMethod;

public class Main {
	public static void main(String args[]) { 		
		ContainerLoading container = new ContainerLoading();
		SolutionMethod solutionMethod = new SolutionMethod();
		
		Greedy greedy = new Greedy();						
		solutionMethod.setupLog4j();
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
