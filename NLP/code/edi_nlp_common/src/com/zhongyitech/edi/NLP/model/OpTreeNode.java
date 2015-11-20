package omsaTest.model;

public class OpTreeNode {
	private int node_id;
	private OpElement node_element;
	private int parents_id;
//	节点深度
	private int node_depth;
	private boolean isLeaf;
	
	public OpTreeNode(){
		this.node_id = 0;
		this.node_element = null;
		this.parents_id = -1;
		this.node_depth = 0;
		this.isLeaf = false;
	}
	
	public OpTreeNode(int i){
		this.node_id = i;
		this.isLeaf = false;
	}

	public void setNodeInfos(OpElement node_element, String parents_id, String node_depth, String isLeaf) {
		
		if(node_element!=null)
			this.node_element =node_element;
		if(parents_id!=null && !parents_id.equals(""))
			this.parents_id =Integer.parseInt(parents_id);
		if(node_depth!=null && !node_depth.equals(""))
			this.node_depth =Integer.parseInt(node_depth);
		if(isLeaf!=null && !isLeaf.equals(""))
			this.isLeaf =Boolean.parseBoolean(isLeaf);
	}

	public int getNode_id() {
		return node_id;
	}

	public void setNode_id(int node_id) {
		this.node_id = node_id;
	}

	public OpElement getNode_element() {
		return node_element;
	}

	public void setNode_element(OpElement node_element) {
		this.node_element = node_element;
	}

	public int getParents_id() {
		return parents_id;
	}

	public void setParents_id(int parents_id) {
		this.parents_id = parents_id;
	}

	public int getNode_depth() {
		return node_depth;
	}

	public void setNode_depth(int node_depth) {
		this.node_depth = node_depth;
	}

	public boolean isLeaf() {
		return isLeaf;
	}

	public void setLeaf(boolean isLeaf) {
		this.isLeaf = isLeaf;
	}
	
}
