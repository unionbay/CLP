package anhnh34.com.vn.model;

import java.util.Comparator;

public class SolutionComparator implements Comparator<Solution>{

	@Override
	public int compare(Solution fSolution, Solution sSolution) {
		if(fSolution.getTotalCost() > sSolution.getTotalCost()) {
			return 1;
		}
		
		if(fSolution.getTotalCost() < sSolution.getTotalCost()) {
			return -1;
		}
		
		return 0;
	}

}
