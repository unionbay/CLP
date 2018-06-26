package anhnh34.com.vn.model;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class MainModel {	
	private List<Batch> placedBatch;
	private List<Batch> nPlaceBatch;
	private Container container;
	private List<Box> boxes;
	private List<Space> currentSpaces;

	public void initModel(String filePath) throws IOException {
		resetModel();
		loadingBox(filePath);
	}
	
	public List<Space> getCurrentSpaces() {
		return currentSpaces;
	}
	
	public void setCurrentSpaces(List<Space> currentSpaces) {
		this.currentSpaces = currentSpaces;
	}
	
	private void loadingBox(String filePath) throws IOException {
		BufferedInputStream bufferInput = new BufferedInputStream(new FileInputStream(filePath));
		BufferedReader reader = new BufferedReader(new InputStreamReader(bufferInput, StandardCharsets.UTF_8));
		int index = 0;
		String line = "";
		while ((line = reader.readLine()) != null) {
			if (index == 0) {
				//Read container info
				this.readContainerInfo(line.split(" "));
				continue;
			}
			this.readBoxInfo(line.split(" "));
		}

	}

	private void readBoxInfo(String[] data) {
		List<Box> boxes = new ArrayList<Box>();
		int numberOfBox = Integer.valueOf(data[0]);
		long depth = Long.valueOf(data[1]);
		long width = Long.valueOf(data[2]);
		long height = Long.valueOf(data[3]);
		int priority = Integer.valueOf(data[4]);

		for (int i = 0; i < numberOfBox; i++) {
			int id = i + 1;
			Box box = new Box(depth, width, height);
			box.setId(id);
			box.setPriority(priority);
			boxes.add(box);
		}

		// Add boxes into not placed Batches.
		Batch newBatch = new Batch(boxes);
		nPlaceBatch.add(newBatch);
	}

	private void readContainerInfo(String[] data) {
		this.container = new Container(Long.valueOf(data[0]), Long.valueOf(data[1]), Long.valueOf(data[2]));
	}

	private void resetModel() {

	}

	public List<Box> getBoxes() {
		return boxes;
	}

	public void setBoxes(List<Box> boxes) {
		this.boxes = boxes;
	}

	public List<Batch> getnPlaceBatch() {
		return nPlaceBatch;
	}

	public void setnPlaceBatch(List<Batch> nPlaceBatch) {
		this.nPlaceBatch = nPlaceBatch;
	}

	public void setPlacedBatch(List<Batch> placedBatch) {
		this.placedBatch = placedBatch;
	}

	public List<Batch> getPlacedBatch() {
		return placedBatch;
	}

	public Container getContainer() {
		return container;
	}

	public void setContainer(Container container) {
		this.container = container;
	}
		
}
