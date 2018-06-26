package anhnh34.com.vn.test;

import org.junit.Test;

import anhnh34.com.vn.model.Container;
import anhnh34.com.vn.model.ContainerLoading;

import static org.junit.Assert.*;

import java.io.IOException;

public class LoadingTest {

	@Test
	public void LoadingUnitTest() throws IOException {
		ContainerLoading containerLoading = new ContainerLoading();

		containerLoading.loadingData();
		
		assertEquals(containerLoading.getProblem().getNumOfItem(),
				containerLoading.getNotPlacedBox().getBoxes().size());
	}

	private void initBox(String path) {

	}
}
