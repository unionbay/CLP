package anhnh34.com.vn.main;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Date;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.PropertyConfigurator;

import anhnh34.com.vn.model.ContainerLoading;
import anhnh34.com.vn.model.Greedy;
import anhnh34.com.vn.model.GreedyHeuristic;
import anhnh34.com.vn.model.SolutionMethod;
import anhnh34.com.vn.model.TreeSearch;

public class Main {
	public static void main(String args[]) throws Exception { 		
		ContainerLoading container = new ContainerLoading();
		SolutionMethod solutionMethod = new SolutionMethod();
				
		
		//TreeSearch treeSearch = new TreeSearch();
		solutionMethod.setupLog4j();
		//solutionMethod.setGreedyInstance(greedy);		
		//solutionMethod.setConLoading(container);			
		
		try {
			//solutionMethod.getConLoading().loadingData();
			//container.loadingData();
			GreedyHeuristic greedyHeuristic = new GreedyHeuristic();
			greedyHeuristic.setStartDate(new Date());
			LocalDateTime startDate = LocalDateTime.now();
			greedyHeuristic.run();
			LocalDateTime finishedDate = LocalDateTime.now();
			
			Duration duration = Duration.between(startDate, finishedDate);
			System.out.println(duration.getSeconds() + " seconds");
			
			//treeSearch.setContainer(container);			
			//greedy.setConLoading(container);
			//greedy.loadParameters();		
			//treeSearch.setGreedyInstance(greedy);
			//treeSearch.run();
			//solutionMethod.run();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {			
			e.printStackTrace();
		}
	}
	
}
