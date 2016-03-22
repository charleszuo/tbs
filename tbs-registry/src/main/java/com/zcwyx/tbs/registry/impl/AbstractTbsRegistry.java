package com.zcwyx.tbs.registry.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zcwyx.tbs.registry.Node;
import com.zcwyx.tbs.registry.NodeFactory;
import com.zcwyx.tbs.registry.Service;
import com.zcwyx.tbs.registry.Shard;
import com.zcwyx.tbs.registry.TbsRegistry;

public abstract class AbstractTbsRegistry implements TbsRegistry{
	protected Logger logger = LoggerFactory.getLogger(getClass());
	private ConcurrentHashMap<String, Service> serviceMap = new ConcurrentHashMap<String, Service>();
	
	private Service getService(String serviceId, String version){
		String serviceUid = serviceId + version;
		Service service = serviceMap.get(serviceUid);
		
		if(service == null){
			synchronized (serviceId.intern()) {
				service = serviceMap.get(serviceUid);
				if(service == null){
					service = loadService(serviceId, version);
					if(service != null){
						serviceMap.putIfAbsent(serviceUid, service);
					}
				}
			}
		}
		return service;
	}
	
	public final static String HOST_BINDING_PREFIX = "trc.register";

    public final static String REGISTER_SIKP = "trc.register.skip";
    
	private Service getHostConfigFromSysProp(String serviceId){
		Service service = new Service(serviceId, 1, null);
		boolean hashService = false;
		Map<?, ?> props = System.getProperties();
		for(Entry<?, ?> entry: props.entrySet()){
			String key = (String)entry.getKey();
			String value = (String)entry.getValue();
			if(!key.equals(REGISTER_SIKP) && key.equals(HOST_BINDING_PREFIX)){
				String prefix = key.replace(serviceId, "");
				String[] prefixParts = prefix.split("\\.");
				List<Node> nodes = new ArrayList<Node>();
				
				Integer shard = 0;
				if(prefixParts.length == 3){
					try{
						shard = Integer.parseInt(prefixParts[2]);
					}catch(Exception e){
						continue;
					}
				}
				
				if(value != null){
					String[] hostVec = value.split(",");
					for(String host: hostVec){
						String[] ss = host.split(":");
						if(ss.length == 2){
							Node node = NodeFactory.getNode(host, true,true);
							nodes.add(node);
						}
					}
					if(nodes.size() > 0){
						hashService = true;
						Map<Integer, Shard> shards = service.getSharding();
						Shard sd = shards.get(shard);
						if(sd == null){
							shards.put(shard, new Shard(service, shard, nodes));
						}else{
							sd.getNodeList().addAll(nodes);
						}
					}
				}
			}
		}
		return hashService ? service: null;
	}
	
	protected abstract Service loadService(String serviceId, String version);


	@Override
	public Service queryService(String serviceId, String serviceVersion) {
		Service service = getHostConfigFromSysProp(serviceId);
		if(service == null){
			service = getService(serviceId, serviceVersion);
		}
		return service;
	}

	
}
