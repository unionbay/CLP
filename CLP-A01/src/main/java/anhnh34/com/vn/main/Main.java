package anhnh34.com.vn.main;

import java.io.IOException;

import org.apache.log4j.BasicConfigurator;

import anhnh34.com.vn.model.ContainerLoading;
import anhnh34.com.vn.model.SolutionMethod;

public class Main {
	public static void main(String args[]) {
		BasicConfigurator.configure();
		ContainerLoading containerLoading = new ContainerLoading();
		SolutionMethod solutionMethod = new SolutionMethod();
		solutionMethod.setConLoading(containerLoading);
		
		try {
			solutionMethod.getConLoading().loadingData();
			solutionMethod.GREEDY();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
