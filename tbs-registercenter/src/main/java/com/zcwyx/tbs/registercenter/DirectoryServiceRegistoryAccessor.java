package com.zcwyx.tbs.registercenter;

import java.io.IOException;
import java.util.List;

import com.zcwyx.tbs.registercenter.client.Consumer;
import com.zcwyx.tbs.registercenter.client.DataChangeListener;
import com.zcwyx.tbs.registercenter.client.NodeChangeListener;

public class DirectoryServiceRegistoryAccessor implements ServiceRegistryAccessor{
	
	private Consumer consumer;
	
	private RegisterCenterConfiguration config;
	
	private String service;
	
	private String versionReg;
	
	private int version = Integer.MIN_VALUE;
	
	public DirectoryServiceRegistoryAccessor(String service, String version, RegisterCenterConfiguration config){
		this.service = service;
		this.versionReg = version;
		this.config = config;
		try {
			this.consumer = DirectoryServiceFactory.getConsumer(config);
		} catch (IOException e) {
			new RuntimeException("Fail to connect zk");
		}
	}

	@Override
	public String getService() {
		return this.service;
	}

	@Override
	public int getVersion() {
		if(version == Integer.MIN_VALUE){
			version = consumer.getCurrentVersion(service, versionReg);
		}
		return version;
	}

	@Override
	public List<String> listShardsAndListenChange(NodeChangeListener listener) {
		return consumer.getShardsAndListenChange(service, String.valueOf(version), listener);
	}


	@Override
	public List<String> listNodesAndListenChange(String shard,
			NodeChangeListener listener) {
		return consumer.getAddressAndListenChange(service, String.valueOf(version), shard, listener);
	}

	@Override
	public byte[] getNodeAndListenChange(String shard, String node,
			DataChangeListener listener) {
		return consumer.getDataAndListenChange(service, String.valueOf(version), shard, node, listener);
	}

}
