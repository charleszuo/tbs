package com.zcwyx.tbs.registry;

import java.util.ArrayList;
import java.util.List;

public class Shard {
	private Service service;
	
	private int value;
	
	private List<Node> nodeList = new ArrayList<Node>();

	public Service getService() {
		return service;
	}

	public void setService(Service service) {
		this.service = service;
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}

	public List<Node> getNodeList() {
		return nodeList;
	}

	public void setNodeList(List<Node> nodeList) {
		this.nodeList = nodeList;
	}
	
	public Shard(Service service, int value, List<Node> nodes){
		this.service = service;
		this.value = value;
		this.nodeList.addAll(nodes);
	}
}
