package anhnh34.com.vn.test;

import org.junit.Test;

import anhnh34.com.vn.model.Location;
import anhnh34.com.vn.model.PartialSolution;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class LoadingTest {

	@Test
	public void LoadingUnitTest() throws IOException {
//		ContainerLoading containerLoading = new ContainerLoading();
//
//		containerLoading.loadingData();
//		
//		assertEquals(containerLoading.getProblem().getNumOfItem(),
//				containerLoading.getNotPlacedBox().getBoxes().size());
	}
	@Test
	public void calculation() {
		PartialSolution partialSolution = new PartialSolution();
		List<Location> locationList =new  ArrayList<Location>();
		locationList.add(new Location("0",30,40,0));
		locationList.add(new Location("13",5,25,0));
		locationList.add(new Location("14",12,42,0));
		partialSolution.setLocationList(locationList);
		partialSolution.calculateCost();
		System.out.println(partialSolution.getCost());
		assertEquals(65.6503, partialSolution.getCost(),0.00002);
		
		
		
	}
}
