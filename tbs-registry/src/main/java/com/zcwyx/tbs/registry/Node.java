package com.zcwyx.tbs.registry;

public class Node implements Comparable<Node> {
	private String host;

	private int port;

	private boolean disabled;

	private boolean healthy;
	
	public Node(String host, int port, boolean disabled, boolean healthy){
		this.host = host;
		this.port = port;
		this.disabled = disabled;
		this.healthy = healthy;		
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public boolean isDisabled() {
		return disabled;
	}

	public void setDisabled(boolean disabled) {
		this.disabled = disabled;
	}

	public boolean isHealthy() {
		return healthy;
	}

	public void setHealthy(boolean healthy) {
		this.healthy = healthy;
	}

	@Override
	public int compareTo(Node o) {
		int cmp = this.host.compareTo(o.getHost());
		if(cmp != 0){
			return cmp;
		}
		
		if(this.port < o.getPort()){
			return -1;
		}else if(this.port > o.getPort()){
			return 1;
		}
		return 0;
	}

	public int hashCode(){
		int prime = 31;
		int result = 1;
		result = prime * result + ((host == null) ? 0 : host.hashCode());
		result = prime * result + port;
		return result;
	}
	
	public boolean equals(Object obj){
		if(this == obj) return true;
		if(obj == null) return false;
		if(getClass() != obj.getClass()) return false;
		
		Node node = (Node)obj;
		
		return this.compareTo(node) == 0;
	}
	
	public String getNodeKey(){
		return new StringBuilder().append(host).append(":").append(port).toString();
	}
	
	public static String generateNodeKey(String host, int port){
		return new StringBuilder().append(host).append(":").append(port).toString();
	}
	
	public static String generateNodeKey(String host, String port){
		return new StringBuilder().append(host).append(":").append(port).toString();
	}
	
	public static String[] parseNodeKey(String nodeKey){
		return nodeKey.split(":", 2);
	}
	
}
