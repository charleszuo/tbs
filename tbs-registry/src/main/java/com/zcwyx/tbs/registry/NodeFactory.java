package com.zcwyx.tbs.registry;

import java.util.concurrent.ConcurrentHashMap;

public class NodeFactory {
	private static final ConcurrentHashMap<String, Node> nodeMap = new ConcurrentHashMap<String, Node>();
	
	public static Node getNode(String host, int port, boolean disabled, boolean healthy){
		String nodeKey = Node.generateNodeKey(host, port);
		Node node = nodeMap.get(nodeKey);
		if(node == null){
			nodeMap.putIfAbsent(nodeKey, new Node(host, port, disabled, healthy));
			node = nodeMap.get(nodeKey);
		}
		
		return node;
	}
	
	public static Node getNode(String nodeKey, boolean disabled, boolean healthy){
		String[] hostPort = Node.parseNodeKey(nodeKey);
		return getNode(hostPort[0], Integer.parseInt(hostPort[1]), disabled, healthy);
	}
	
	public static Node getNode(String nodeKey){
		return getNode(nodeKey, true, true);
	}
}
