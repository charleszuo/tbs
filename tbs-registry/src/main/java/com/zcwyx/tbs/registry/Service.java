package com.zcwyx.tbs.registry;

import java.util.HashMap;
import java.util.Map;

public class Service {
	private String serviceId;
	
	private int version;
	
	private Map<Integer, Shard> sharding = new HashMap<Integer, Shard>();
	
	public Service(String serviceId, int version, Map<Integer, Shard> shards){
		this.serviceId = serviceId;
		this.version = version;
		if(shards != null){
			this.sharding.putAll(shards);
		}
	}

	public String getServiceId() {
		return serviceId;
	}

	public void setServiceId(String serviceId) {
		this.serviceId = serviceId;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public Map<Integer, Shard> getSharding() {
		return sharding;
	}

	public void setSharding(Map<Integer, Shard> sharding) {
		this.sharding = sharding;
	}
	
	
}
