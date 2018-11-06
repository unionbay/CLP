package anhnh34.com.vn.model;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

public class SpaceComparator implements Comparator<Space> {
	private String orderStr;
	private List<String> orderStringList;
	private Random random;	

	public SpaceComparator(Random r) {
		this.random = r;
		orderStringList = new ArrayList<String>();
		orderStringList.add("XYZ");
		orderStringList.add("YXZ");
		orderStringList.add("ZYX");
		this.orderStr = orderStringList.get(0);
	}

	@Override
	public int compare(Space firstSpace, Space secondSpace) {		
		switch (orderStr) {
		case Constant.SPACE_SORT_ZXY:
			return zxySortRule(firstSpace, secondSpace);
		case Constant.SPACE_SORT_ZYX:
			return zyxSortRule(firstSpace, secondSpace);
		case Constant.SPACE_SORT_YXZ:
			return yxzSortRule(firstSpace, secondSpace);
		case Constant.SPACE_SORT_XYZ:
			return xyzSortRule(firstSpace, secondSpace);
		case Constant.SPACE_SORT_YZX:
			return fourthSortRule(firstSpace, secondSpace);
		default:
			return 0;
		}
	}

	private int zxySortRule(Space firstSpace, Space secondSpace) {
		Dimension firstSpaceMinimum = firstSpace.getMinimum();
		Dimension secondSpaceMinimum = secondSpace.getMinimum();

		if (firstSpaceMinimum.getZ() > secondSpaceMinimum.getZ()) {
			return 1;
		}

		if (firstSpaceMinimum.getZ() < secondSpaceMinimum.getZ()) {
			return -1;
		}

		if (firstSpaceMinimum.getX() > secondSpaceMinimum.getX()) {
			return -1;
		}

		if (firstSpaceMinimum.getX() < secondSpaceMinimum.getX()) {
			return 1;
		}

		if (firstSpaceMinimum.getY() > secondSpaceMinimum.getY()) {
			return 1;
		}

		if (firstSpaceMinimum.getY() < secondSpaceMinimum.getY()) {
			return -1;
		}

		return 0;
	}

	private int yxzSortRule(Space firstSpace, Space secondSpace) {
		Dimension firstSpaceMinimum = firstSpace.getMinimum();
		Dimension secondSpaceMinimum = secondSpace.getMinimum();

		if (firstSpaceMinimum.getY() > secondSpaceMinimum.getY()) {
			return 1;
		}
		
		if (firstSpaceMinimum.getY() < secondSpaceMinimum.getY()) {
			return -1;
		}		
		
		if (firstSpaceMinimum.getX() > secondSpaceMinimum.getX()) {
			return -1;
		}
		
		if (firstSpaceMinimum.getX() < secondSpaceMinimum.getX()) {
			return 1;
		}	
				
		if (firstSpaceMinimum.getZ() > secondSpaceMinimum.getZ()) {
			return 1;
		}	
	
		if (firstSpaceMinimum.getZ() < secondSpaceMinimum.getZ()) {
			return -1;
		}
		return 0;
	}

	private int xyzSortRule(Space firstSpace, Space secondSpace) {

		Dimension firstSpaceMinimum = firstSpace.getMinimum();
		Dimension secondSpaceMinimum = secondSpace.getMinimum();

		if (firstSpaceMinimum.getX() > secondSpaceMinimum.getX()) {
			return 1;
		}
		
		if (firstSpaceMinimum.getX() < secondSpaceMinimum.getX()) {
			return -1;
		}

		if (firstSpaceMinimum.getY() > secondSpaceMinimum.getY()) {
			return -1;
		}
		
		if (firstSpaceMinimum.getY() < secondSpaceMinimum.getY()) {
			return 1;
		}

		if (firstSpaceMinimum.getZ() > secondSpaceMinimum.getZ()) {
			return 1;
		}

		if (firstSpaceMinimum.getZ() < secondSpaceMinimum.getZ()) {
			return -1;
		}
		return 0;
	}

	private int fourthSortRule(Space firstSpace, Space secondSpace) {

		Dimension firstSpaceMinimum = firstSpace.getMinimum();
		Dimension secondSpaceMinimum = secondSpace.getMinimum();

		if (firstSpaceMinimum.getY() > secondSpaceMinimum.getY()) {
			return 1;
		}

		if (firstSpaceMinimum.getY() < secondSpaceMinimum.getY()) {
			return -1;
		}

		if (firstSpaceMinimum.getZ() > secondSpaceMinimum.getZ()) {
			return -1;
		}

		if (firstSpaceMinimum.getZ() < secondSpaceMinimum.getZ()) {
			return 1;
		}

		if (firstSpaceMinimum.getX() > secondSpaceMinimum.getX()) {
			return 1;
		}

		if (firstSpaceMinimum.getX() < secondSpaceMinimum.getX()) {
			return -1;
		}

		return 0;
	}
	
	private int zyxSortRule(Space firstSpace, Space secondSpace) {
		Dimension firstSpaceMinimum = firstSpace.getMinimum();
		Dimension secondSpaceMinimum = secondSpace.getMinimum();
		
		if (firstSpaceMinimum.getZ() > secondSpaceMinimum.getZ()) {
			return 1;
		}

		if (firstSpaceMinimum.getZ() < secondSpaceMinimum.getZ()) {
			return -1;
		}


		if (firstSpaceMinimum.getY() > secondSpaceMinimum.getY()) {
//			if(firstSpace.getMinimum().getX() <= secondSpace.getMinimum().getX()) {
//				return 1;
//			}
			return -1;
		}

		if (firstSpaceMinimum.getY() < secondSpaceMinimum.getY()) {
//			if(firstSpace.getMinimum().getX() <= secondSpace.getMinimum().getX()) {
//				return 1;
//			}
			return -1;			
		}

		
		if (firstSpaceMinimum.getX() > secondSpaceMinimum.getX()) {
			return 1;
		}

		if (firstSpaceMinimum.getX() < secondSpaceMinimum.getX()) {
			return -1;
		}

		return 0;
	}
	
	
	public void reloadOrderString() {
		orderStr = this.getSpaceOrderString(0, orderStringList.size()-1);
		//System.out.println("Order String: " + this.orderStr);
	}

	private String getSpaceOrderString(int low, int height) {		
		int index = this.random.nextInt((height - low) + 1) + low;
		return orderStringList.get(index);
	}
}
