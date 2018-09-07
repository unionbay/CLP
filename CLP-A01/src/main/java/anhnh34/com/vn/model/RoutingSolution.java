package anhnh34.com.vn.model;

public class RoutingSolution {
	
	public RoutingSolution() {		
	
	}
	
	public void setSolution(PartialSolution solution, boolean isFitted) {
		this.solution = solution;
		this.isFited = isFitted;
	}
	
	public PartialSolution getSolution() {
		return solution;
	}
	
	
	public boolean isFited() {
		return isFited;
	}
	
	public void setFited(boolean isFited) {
		this.isFited = isFited;
	}
	
	private PartialSolution solution;
	private boolean isFited;
}
