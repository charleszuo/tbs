package com.zcwyx.tbs.registry;

public interface TbsRegistry {

	public void registerNode(Service service, int shard, Node node);
	
	public Service queryService(String serviceId, String serviceVersion);
	
	public void destroy();
}
