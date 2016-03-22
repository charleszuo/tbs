package com.zcwyx.tbs.registercenter;

import java.io.IOException;

import com.zcwyx.tbs.registercenter.server.Producer;

public class DirectoryServiceRegistry implements ServiceRegistry{
	
	private RegisterCenterConfiguration config;
	
	private Producer producer;
	
	public DirectoryServiceRegistry(RegisterCenterConfiguration config){
		this.config = config;
		try {
			producer = DirectoryServiceFactory.getProducer(config);
		} catch (IOException e) {
			new RuntimeException("Faild to connect to zk");
		}
	}

	@Override
	public void publishService(String service, int version, String shard,
			Node node, boolean isTemp) {
		producer.publishService(service, version, shard, node.getName(), node.getData(), isTemp);
	}

	@Override
	public void updateService(String service, int version, String shard,
			Node node) {
		producer.updateService(service, version, shard, node.getName(), node.getData());
	}

	@Override
	public void destory() {
		producer.close();
	}

}
