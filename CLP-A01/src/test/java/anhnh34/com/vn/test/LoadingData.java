package anhnh34.com.vn.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.BasicConfigurator;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import anhnh34.com.vn.model.Box;
import anhnh34.com.vn.model.Boxes;
import anhnh34.com.vn.model.Dimension;
import anhnh34.com.vn.model.Node;
import anhnh34.com.vn.model.Nodes;
import anhnh34.com.vn.model.Utility;

public class LoadingData {
	private List<Box> boxes;
	private List<Node> nodeList;

	public LoadingData() {
		boxes = new ArrayList<Box>();
		nodeList = new ArrayList<Node>();
	}

	public static void main(String arg[]) {
		BasicConfigurator.configure();		
		LoadingData loadingData = new LoadingData();
		loadingData.loadingData(30, 40);
	}

	public void loadingData(int startIndex, int endIndex) {
		String resultDataPath = Utility.getInstance().getConfigValue("standar_result");
		Charset charset = Charset.forName("US-ASCII");
		try {
			Path file = Paths.get(resultDataPath);
			BufferedReader reader = Files.newBufferedReader(file, charset);
			String line = null;
			int lineIndex = 0;
			while ((line = reader.readLine()) != null) {
				lineIndex++;
				if ((lineIndex >= startIndex) && (lineIndex <= endIndex)) {
					Integer[] intArray = extractInformation(line);
					Box box = new Box(intArray[3], intArray[1], intArray[2]);
					Dimension minimumDimension = new Dimension(intArray[6], intArray[4], intArray[5]);
					Dimension maximumDimension = new Dimension(intArray[9], intArray[7], intArray[8]);
					box.setMinimum(minimumDimension);
					box.setMaximum(maximumDimension);
					box.setSequenceNumber(1);
					boxes.add(box);
				}
			}
			
			this.WriteToFile();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private Integer[] extractInformation(String line) {
		String pattern = "(\\s\\d+)";
		Pattern r = Pattern.compile(pattern);

		// Now create matcher object.
		Matcher m = r.matcher(line);
		Integer[] integerArray = new Integer[10];
		int index = 0;
		while (m.find()) {						
			integerArray[index] = Integer.valueOf(m.group(0).trim());
			index++;
		}
		return integerArray;
	}

	private void WriteToFile() {
		Boxes[] boxArr = new Boxes[boxes.size()];
		for (int i = 0; i < boxes.size(); i++) {
			Box b = boxes.get(i);
			Boxes box = new Boxes(b.getMinimum(), b.getLength(), b.getWidth(), b.getHeight(), b.getSequenceNumber());
			boxArr[i] = box;
		}	

		Utility.getInstance().sortBox(boxArr);
		for (int i = 0; i < boxArr.length; i++) {
			Node node = new Node(i, boxArr[i]);
			nodeList.add(node);
		}
		Nodes sortedNodes = new Nodes(nodeList);
		ObjectMapper mapper = new ObjectMapper();
		
		// Object to Json in file
				try {
					String outputPath = Utility.getInstance().getConfigValue("output_path") + "standar_result.json";					
					mapper.writeValue(new File(outputPath), sortedNodes);				
				} catch (JsonGenerationException e) {
					e.printStackTrace();
				} catch (JsonMappingException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
	}
}
