package anhnh34.com.vn.model;

import java.util.Comparator;

public class LocationComparator implements Comparator<Location>{

	@Override
	public int compare(Location fLocation, Location sLocation) {
		if(fLocation.getDistance() > sLocation.getDistance()) {
			return 1;
		}
		
		if(fLocation.getDistance() < sLocation.getDistance()) {
			return -1;
		}
		
		return 0;
	}

}
