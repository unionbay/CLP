package anhnh34.com.vn.model;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Utility {

	final static Logger logger = Logger.getLogger(Utility.class);
	private static Utility instance;

	public static Utility getInstance() {
		if (instance == null) {
			instance = new Utility();
		}
		return instance;
	}

	private Utility() {
	};

	public void sortBox(Boxes[] dPlacedBoxes) {
		this.mergeSort(0, dPlacedBoxes.length - 1, dPlacedBoxes);
	}

	private void mergeSort(int startIndex, int endIndex, Boxes[] boxList) {
		if (endIndex - startIndex > 0) {
			int midIndex = startIndex + (endIndex - startIndex) / 2;
			mergeSort(startIndex, midIndex, boxList);
			mergeSort(midIndex + 1, endIndex, boxList);
			this.merge(startIndex, endIndex, midIndex, boxList);
		}
	}

	private Boxes[] merge(int start, int end, int mid, Boxes[] boxList) {
		Boxes[] helperList = new Boxes[boxList.length];

		for (int i = start; i <= end; i++) {
			helperList[i] = boxList[i];
		}

		int i = start;
		int k = start;
		int j = mid + 1;

		while (i <= mid && j <= end) {
			if (compareBoxPosition(helperList[i], helperList[j]) == 1) {
				boxList[k] = helperList[i];
				i++;
			} else {
				boxList[k] = helperList[j];
				if (boxList[i].getRoot().getX() == 0 && boxList[i].getRoot().getY() == 14
						&& boxList[i].getRoot().getZ() == 0) {
				}
				j++;

//				for (Boxes b : boxList) {
//					String boxStr = String.format("sequence number: %d, root: %f %f %f", new Object[] {
//							b.getSequenceNumber(), b.getRoot().getX(), b.getRoot().getY(), b.getRoot().getZ() });
//					logger.info(boxStr);
//				}
			}
		//	logger.info("i: " + i + " j: " + j + " k: " + k);
			k++;
		}

		while (i <= mid) {
			boxList[k] = helperList[i];
			i++;
			k++;
		}

		return helperList;
	}

	private int compareBoxPosition(Boxes a, Boxes b) {
	//	logger.info("Start compareBoxPosition");
		//logger.info(String.format("x: %s %s %s",
		//		new Object[] { a.getRoot().getX(), a.getRoot().getY(), a.getRoot().getZ() }));
		//logger.info(String.format("y: %s %s %s",
			//	new Object[] { b.getRoot().getX(), b.getRoot().getY(), b.getRoot().getZ() }));

		int result = this.compareDimension(a.getRoot(), b.getRoot());
		//logger.info("Result: " + result);
		//logger.info("End compareBoxPosition");
		return result;
	}

	private int compareDimension(Dimension x, Dimension y) {
		double xLength = Math.sqrt(Math.pow(x.getX(), 2) + Math.pow(x.getY(), 2));
		double yLength = Math.sqrt(Math.pow(y.getX(), 2) + Math.pow(y.getY(), 2));

		if (xLength <= yLength) {
			if (x.getZ() > y.getZ() && xLength <= yLength) {
				return -1;
			}

			if ((x.getZ() == y.getZ()) && (x.getY() > y.getY())) {
				if (x.getX() < y.getY())
					return 1;
				return -1;
			}
			return 1;
		}

		if (x.getY() < y.getY()) {
			return 1;
		}
		return -1;
	}

	public String getConfigValue(String propertyName) {
		Properties prop = new Properties();
		try {
			prop.load(this.getClass().getClassLoader().getResourceAsStream("config.properties"));
		} catch (IOException e) {
			e.printStackTrace();
			return "";
		}
		return prop.getProperty(propertyName);
	}

	public void writeResultToFile(List<Box> boxes) {
		Boxes[] outBoxList = new Boxes[boxes.size()];
		Boxes[] sortedBoxList = new Boxes[boxes.size()];
		List<Node> nodeList = new ArrayList<Node>();
		List<Node> sortedNodeList = new ArrayList<Node>();

		for (int i = 0; i < boxes.size(); i++) {
			Box selectedBox = boxes.get(i);
			Boxes oBox = new Boxes(selectedBox.getMinimum(), selectedBox.getLength(), selectedBox.getWidth(),
					selectedBox.getHeight(), selectedBox.getSequenceNumber());
			outBoxList[i] = oBox;
			sortedBoxList[i] = oBox;
		}

		Utility.getInstance().sortBox(sortedBoxList);

		for (int i = 0; i < outBoxList.length; i++) {
			Node node = new Node(i, outBoxList[i]);
			nodeList.add(node);
		}

		for (int i = 0; i < sortedBoxList.length; i++) {
			Node node = new Node(i, sortedBoxList[i]);
			sortedNodeList.add(node);
		}

		Nodes nodes = new Nodes(nodeList);
		Nodes sortedNodes = new Nodes(sortedNodeList);

		ObjectMapper mapper = new ObjectMapper();

		// Object to Json in file
		try {
			String outputPath = Utility.getInstance().getConfigValue("output_path");
			String jsonResultPath = outputPath + "result.json";
			String sortResultPath = outputPath + "sort_result.json";
			mapper.writeValue(new File(jsonResultPath), nodes);
			mapper.writeValue(new File(sortResultPath), sortedNodes);
		} catch (JsonGenerationException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
