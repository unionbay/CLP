package anhnh34.com.vn.model;

import java.util.Comparator;

public class SpaceComparator implements Comparator<Space> {	
	private static String orderStr;
	@Override
	public int compare(Space firstSpace, Space secondSpace) {	
		if(orderStr == null || orderStr.isEmpty()) {
			orderStr = Utility.getInstance().getConfigValue("space_sort_rule");
		}
				
		switch (orderStr) {
			case Constant.SPACE_SORT_ZXY:
				return firstSortRule(firstSpace, secondSpace);		
			case Constant.SPACE_SORT_YXZ:
				return secondSortRule(firstSpace, secondSpace);
			case Constant.SPACE_SORT_XYZ:
				return thirdSortRule(firstSpace, secondSpace);
			case Constant.SPACE_SORT_YZX:
				return fourthSortRule(firstSpace, secondSpace);
			default:
				return 0;
		}										
	}
	
	private int firstSortRule(Space firstSpace, Space secondSpace) {
		Dimension firstSpaceMinimum = firstSpace.getMinimum();
		Dimension secondSpaceMinimum = secondSpace.getMinimum();
		
		if(firstSpaceMinimum.getZ() > secondSpaceMinimum.getZ()) {
			return 1;			
		}
		
		if(firstSpaceMinimum.getZ() < secondSpaceMinimum.getZ()) {
			return -1;			
		}
		
		if(firstSpaceMinimum.getX() > secondSpaceMinimum.getX()) {
			return 1;			
		}
		
		if(firstSpaceMinimum.getX() < secondSpaceMinimum.getX()) {
			return -1;			
		}
		
		if(firstSpaceMinimum.getY() > secondSpaceMinimum.getY()) {
			return 1;			
		}
		
		if(firstSpaceMinimum.getY() < secondSpaceMinimum.getY()) {
			return -1;			
		}
		
		return 0;			
	}
	
	
	private int secondSortRule(Space firstSpace, Space secondSpace) {
		Dimension firstSpaceMinimum = firstSpace.getMinimum();
		Dimension secondSpaceMinimum = secondSpace.getMinimum();
		
		if(firstSpaceMinimum.getY() > secondSpaceMinimum.getY()) {
			return 1;			
		}
		
		if(firstSpaceMinimum.getY() < secondSpaceMinimum.getY()) {
			return -1;			
		}
		
		if(firstSpaceMinimum.getX() > secondSpaceMinimum.getX()) {
			return 1;			
		}
		
		if(firstSpaceMinimum.getX() < secondSpaceMinimum.getX()) {
			return -1;			
		}
				
		
		if(firstSpaceMinimum.getZ() > secondSpaceMinimum.getZ()) {
			return 1;			
		}
		
		if(firstSpaceMinimum.getZ() < secondSpaceMinimum.getZ()) {
			return -1;			
		}					
		return 0;			
	}
	
	private int thirdSortRule(Space firstSpace, Space secondSpace) {

		Dimension firstSpaceMinimum = firstSpace.getMinimum();
		Dimension secondSpaceMinimum = secondSpace.getMinimum();
		
		
		if(firstSpaceMinimum.getX() > secondSpaceMinimum.getX()) {
			return 1;			
		}
		
		if(firstSpaceMinimum.getX() < secondSpaceMinimum.getX()) {
			return -1;			
		}
				
		
		if(firstSpaceMinimum.getY() > secondSpaceMinimum.getY()) {
			return 1;			
		}
		
		if(firstSpaceMinimum.getY() < secondSpaceMinimum.getY()) {
			return -1;			
		}
		
		
		
		if(firstSpaceMinimum.getZ() > secondSpaceMinimum.getZ()) {
			return 1;			
		}
		
		if(firstSpaceMinimum.getZ() < secondSpaceMinimum.getZ()) {
			return -1;			
		}					
		return 0;			
	}
	
	private int fourthSortRule(Space firstSpace, Space secondSpace) {

		Dimension firstSpaceMinimum = firstSpace.getMinimum();
		Dimension secondSpaceMinimum = secondSpace.getMinimum();
		

		if(firstSpaceMinimum.getY() > secondSpaceMinimum.getY()) {
			return 1;			
		}
		
		if(firstSpaceMinimum.getY() < secondSpaceMinimum.getY()) {
			return -1;			
		}
		
		if(firstSpaceMinimum.getZ() > secondSpaceMinimum.getZ()) {
			return 1;			
		}
		
		if(firstSpaceMinimum.getZ() < secondSpaceMinimum.getZ()) {
			return -1;			
		}		
		
		if(firstSpaceMinimum.getX() > secondSpaceMinimum.getX()) {
			return 1;			
		}
		
		if(firstSpaceMinimum.getX() < secondSpaceMinimum.getX()) {
			return -1;			
		}
							
		return 0;			
	}
}
