package anhnh34.com.vn.model;

public class Node {
	private String id;
	private double xAxis;
	private double yAxis;
	private int demand;
	private Boxes box;

	public double getxAxis() {
		return xAxis;
	}

	public void setxAxis(double xAxis) {
		this.xAxis = xAxis;
	}

	public double getyAxis() {
		return yAxis;
	}

	public void setyAxis(double yAxis) {
		this.yAxis = yAxis;
	}

	public int getDemand() {
		return demand;
	}

	public void setDemand(int demand) {
		this.demand = demand;
	}

	public Boxes getBox() {
		return box;
	}

	public void setBox(Boxes box) {
		this.box = box;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Node(int id, Boxes box) {
		setId(String.valueOf(id));
		setBox(box);
	}

	public Node(String id, String axisX, String axisY, String demand) {
		setId(id);
		setxAxis(Double.valueOf(axisX));
		setyAxis(Double.valueOf(axisY));
		setDemand(Integer.valueOf(demand));
	}
}
