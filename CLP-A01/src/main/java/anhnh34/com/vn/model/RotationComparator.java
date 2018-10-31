package anhnh34.com.vn.model;


import java.util.Comparator;

public class RotationComparator implements Comparator<Rotation> {
	private static String orderStr;

	public RotationComparator() {
		if (orderStr == null || orderStr.isEmpty()) {
			orderStr = Utility.getInstance().getConfigValue("selected_rotation");
		}
	}

	@Override
	public int compare(Rotation r1, Rotation r2) {
		switch (orderStr) {
		case Constant.ROTATION_SORT_ZYX:
			return firstSortRule(r1, r2);
		case Constant.ROTATION_SORT_ZXY:
			return secondSortRule(r1, r2);
		case Constant.ROTATION_SORT_YXZ:
			return thirdSortRule(r1, r2);
		default:
			return 0;

		}
	}

	private int thirdSortRule(Rotation r1, Rotation r2) {		
		
		if(r1.getWidth() > r2.getWidth()) {
			return -1;
		}
		
		if(r1.getWidth() < r2.getWidth()) {
			return 1;
		}
		
		
		if(r1.getLength() > r2.getLength()) {
			return -1;
		}
		
		if(r1.getLength() < r2.getLength()) {
			return 1;
		}		
				
		if(r1.getHeight() > r2.getHeight()) {
			return -1;
		}
		
		if(r1.getHeight() < r2.getHeight()) {
			return 1;
		}
				
		return 0;
	}

	private int secondSortRule(Rotation r1, Rotation r2) {
		
		if(r1.getHeight() > r2.getHeight()) {
			return -1;
		}
		
		if(r1.getHeight() < r2.getHeight()) {
			return 1;
		}
		
		if(r1.getLength() > r2.getLength()) {
			return -1;
		}
		
		if(r1.getLength() < r2.getLength()) {
			return 1;
		}		
		
		if(r1.getWidth() > r2.getWidth()) {
			return -1;
		}
		
		if(r1.getWidth() < r2.getWidth()) {
			return 1;
		}
		
		return 0;
	}

	private int firstSortRule(Rotation r1, Rotation r2) {
		if(r1.getHeight() > r2.getHeight()) {
			return -1;
		}
		
		if(r1.getHeight() < r2.getHeight()) {
			return 1;
		}
		
		if(r1.getWidth() > r2.getWidth()) {
			return -1;
		}
		
		if(r1.getWidth() < r2.getWidth()) {
			return 1;
		}
		
		if(r1.getLength() > r2.getLength()) {
			return -1;
		}
		
		if(r1.getLength() < r2.getLength()) {
			return 1;
		}		
		
		return 0;
	}

}
