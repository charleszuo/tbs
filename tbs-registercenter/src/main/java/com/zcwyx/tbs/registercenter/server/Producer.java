package com.zcwyx.tbs.registercenter.server;

import java.io.IOException;

import com.zcwyx.tbs.registercenter.core.ZKConnector;

public class Producer extends ZKConnector{

	public Producer(String clusterAddress, int sessionTimeout)
			throws IOException {
		super(clusterAddress, sessionTimeout);
	}
	
	public void publishService(String service, int version, String stat, String address, boolean isTmp) {
        publishService(service, version, stat, address, null, isTmp, false);
    }
	
	public void publishService(String service, int version, String stat, String address, byte[] config, boolean isTmp) {
        publishService(service, version, stat, address, config, isTmp, false);
    }
	
	public void publishService(String service, int version, String shard, String address, byte[] config, boolean isTemp, boolean isSequential){
		publishService(service, version, shard, address, config, isTemp, isSequential, true);
	}

	public void publishService(String service, int version, String shard, String address, byte[] config, boolean isTemp, boolean isSequential, boolean doWatch){
		if(root == null){
			throw new IllegalArgumentException("Root is not set");
		}
		
		if(version < 0){
			throw new IllegalArgumentException("Version must great thant 0");
		}
		
		StringBuilder path = new StringBuilder();
		path.append("/").append(root).append("/").append(service);
		path.append("/").append(version).append("/").append(shard);
		path.append("/").append(address);
		
		this.createNode(path.toString(), config, isTemp, isSequential, doWatch);
	}
	
	public void updateService(String service, int version, String shard, String address, byte[] config){
		if(root == null){
			throw new IllegalArgumentException("Root is not set");
		}
		
		if(version < 1){
			throw new IllegalArgumentException("Version must great thant 0");
		}
		
		StringBuilder path = new StringBuilder();
		path.append("/").append(root).append("/").append(service);
		path.append("/").append(version).append("/").append(shard);
		path.append("/").append(address);
		
		this.updateNode(path.toString(), config);
	}
	
	public void withdrawService(String service, int version, String shard, String address){
		if(root == null){
			throw new IllegalArgumentException("Root is not set");
		}
		
		if(version < 1){
			throw new IllegalArgumentException("Version must great thant 0");
		}
		
		StringBuilder path = new StringBuilder();
		path.append("/").append(root).append("/").append(service);
		path.append("/").append(version).append("/").append(shard);
		path.append("/").append(address);
		
		this.deleteNode(path.toString());
	}
}
