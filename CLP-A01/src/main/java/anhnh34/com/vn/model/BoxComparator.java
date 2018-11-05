package anhnh34.com.vn.model;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

import org.apache.log4j.Logger;

public class BoxComparator implements Comparator<Box>{
	final static Logger logger = Logger.getLogger(BoxComparator.class);
	private List<String> boxOrderList = new ArrayList<String>();
	private static String orderStr;
	private int roundNumber;
	
	@Override
	public int compare(Box firstBox, Box secondBox) {		
//		if(orderStr == null || orderStr.isEmpty()) {
//			orderStr = Utility.getInstance().getConfigValue("item_sort_rule");
//		}		 
		//System.out.println("Box comparator round number: " + roundNumber);
		
//		if ((roundNumber % 2) == 0) {
//			orderStr = boxOrderList.get(0);
//		} else {
//			orderStr = boxOrderList.get(1);
//		}
		
		switch (this.orderStr) {
			case Constant.SEQ_FRA_SUP_VOLUME:
				return firstSortRule(firstBox, secondBox);		
			case Constant.SEQ_FRA_SUP_LENGTH:
				return secondSortRule(firstBox, secondBox);
			default:
				return 0;
		}		
	}
	
	public void setRoundNumber(int roundNumber) {
		this.roundNumber = roundNumber;
	}
	
	
	public BoxComparator() {
		boxOrderList = new ArrayList<String>();
		boxOrderList.add("SFLV");
		boxOrderList.add("SFSV");				
		this.orderStr = boxOrderList.get(0);
	}
	
	
	
	private int firstSortRule(Box firstBox, Box secondBox) {
		int checkConstraint = 0;
		
//		if((checkConstraint = sequenceConstraint(firstBox, secondBox)) != 0) {
//			return checkConstraint;
//		}
		
		if((checkConstraint = fragilityConstraint(firstBox, secondBox)) != 0) {
			return checkConstraint;
		}
		
		if((checkConstraint = supportConstraint(firstBox, secondBox)) != 0) {
			return checkConstraint;
		}
		
		if((checkConstraint = volumeConstraint(firstBox, secondBox)) != 0) {
			return checkConstraint;
		}
		
		return checkConstraint;
	}
	
	private int secondSortRule(Box firstBox, Box secondBox) {
		int checkConstraint = 0;
		
//		if((checkConstraint = sequenceConstraint(firstBox, secondBox)) != 0) {
//			return checkConstraint;
//		}
		
		if((checkConstraint = fragilityConstraint(firstBox, secondBox)) != 0) {
			return checkConstraint;
		}
		
		if((checkConstraint = lengthConstraint(firstBox, secondBox)) != 0) {
			return checkConstraint;
		}
		
		if((checkConstraint = volumeConstraint(firstBox, secondBox)) != 0) {
			return checkConstraint;
		}
		
		return checkConstraint;
	}


	private int sequenceConstraint(Box b1, Box b2) {
		if(b1.getSequenceNumber() > b2.getSequenceNumber()) {
			 return -1;
		}
		
		if(b1.getSequenceNumber() < b2.getSequenceNumber()) {
			return 1;
		}
		
		return 0;
	}
	
	private int volumeConstraint(Box b1, Box b2) {		
		if(b1.getVolume() < b2.getVolume()) {
			return 1;			
		}
		
		if(b1.getVolume() > b2.getVolume()) {
			return -1;
		}
		
		return 0;
	}
	
	private int compareLargestSurface(Box b1,Box b2) {
		if(b1.getLargestSurface() < b2.getLargestSurface()) {
			return 1;
		}
		
		if(b1.getLargestSurface() > b2.getLargestSurface()) {
			return -1;
		}
		
		return 0;
	}		
	
	private int lengthConstraint(Box b1, Box b2) {			
		double b1Lenght = b1.getLength() > b1.getWidth() ? b1.getLength() : b1.getWidth();
		double b2Length = b2.getLength() > b2.getWidth() ? b2.getLength() : b2.getWidth();
		
		if(b1Lenght < b2Length) {
			return 1;
		}
		
		if(b1Lenght > b2Length) {
			return -1;
		}	
		
		return 0;
	}
	
	private int fragilityConstraint(Box b1, Box b2) {
		if(b1.isFragile() && b2.isFragile() || !b1.isFragile() && !b2.isFragile()) {
			return 0;
		}
		
		if(b1.isFragile()) {
			return 1;
		}
		return -1;
	}
	
	private int supportConstraint(Box first, Box second) {
		double firstBaseArea = first.getWidth() * first.getHeight();
		double secondBaseArea = second.getWidth() * second.getHeight();
		
		if(firstBaseArea > secondBaseArea) {
			return -1;
		}
		
		if(firstBaseArea < secondBaseArea) {
			return 1;
		}
		
		return 0;
	}
	
	
	private int priorityConstraint(Box first, Box second) {
		if(first.getPriority() > second.getPriority()) {
			return 1;
		}	
		
		if(first.getPriority() < second.getPriority()) {
			return -1;
		}
		
		return 0 ;
	}
	
	public void reloadOrderString() {
		BoxComparator.orderStr = this.getBoxOrderString(0, boxOrderList.size()-1);
		//System.out.println("Order String: " + BoxComparator.orderStr);
	}
	
	private String getBoxOrderString(int low, int height) {
		Random random = new Random();
		int index = random.nextInt((height - low) + 1) + low;
		return boxOrderList.get(index);
	}
}
