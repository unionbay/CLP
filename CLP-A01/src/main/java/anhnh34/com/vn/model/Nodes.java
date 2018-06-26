package anhnh34.com.vn.model;

import java.util.List;

public class Nodes {		
	private List<Node> nodeList;	
	
	public void setNodeList(List<Node> nodeList) {
		this.nodeList = nodeList;
	}
	
	public List<Node> getNodeList() {
		return nodeList;
	}
	
	public Nodes(List<Node> nodes){
		setNodeList(nodes);
	}
}
