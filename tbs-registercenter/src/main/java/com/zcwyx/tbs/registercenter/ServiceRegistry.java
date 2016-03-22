package com.zcwyx.tbs.registercenter;

public interface ServiceRegistry {

	public void publishService(String service, int version, String shard, Node node, boolean isTemp);
	
	public void updateService(String service, int version, String shard, Node node);
	
	public void destory();
}
