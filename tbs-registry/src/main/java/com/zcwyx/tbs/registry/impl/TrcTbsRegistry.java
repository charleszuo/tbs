package com.zcwyx.tbs.registry.impl;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zcwyx.tbs.registercenter.NodeData;
import com.zcwyx.tbs.registercenter.ServiceRegistry;
import com.zcwyx.tbs.registercenter.ServiceRegistryAccessor;
import com.zcwyx.tbs.registercenter.ServiceRegistryFactory;
import com.zcwyx.tbs.registercenter.SystemPropertyConfiguration;
import com.zcwyx.tbs.registercenter.client.DataChangeListener;
import com.zcwyx.tbs.registercenter.client.NodeChangeListener;
import com.zcwyx.tbs.registry.Node;
import com.zcwyx.tbs.registry.NodeFactory;
import com.zcwyx.tbs.registry.Service;
import com.zcwyx.tbs.registry.Shard;

public class TrcTbsRegistry extends AbstractTbsRegistry{
	
	private static final Logger logger = LoggerFactory.getLogger(TrcTbsRegistry.class);
	
	private ServiceRegistry serviceRegistry;
	
	private ServiceRegistryFactory serviceRegistryFactory;
	
	private ConcurrentHashMap<String, ServiceRegistryAccessor> accessorMap;
	
	public TrcTbsRegistry(ServiceRegistryFactory serviceRegistryFactory){
		this.serviceRegistryFactory = serviceRegistryFactory;
		accessorMap = new ConcurrentHashMap<String, ServiceRegistryAccessor>();
		serviceRegistry = serviceRegistryFactory.getServiceRegistry(new SystemPropertyConfiguration());
	}

	public String generateAccessorKey(String serviceId, String serviceVersion){
		return new StringBuilder().append(serviceId).append("-").append(serviceVersion).toString();
	}
	
	
	@Override
	public void registerNode(Service service, int shard, Node node) {
		String state = String.valueOf(shard);
		com.zcwyx.tbs.registercenter.Node n = new com.zcwyx.tbs.registercenter.Node(Node.generateNodeKey(node.getHost(),
                node.getPort()), new NodeData(state, node.isDisabled(), node.isHealthy()).toBytes());
		
		try {
			serviceRegistry.publishService(service.getServiceId(), service.getVersion(), state, n, true);
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage());
		}
		
	}


	@Override
	public void destroy() {
		serviceRegistry.destory();
	}
	
	

	@Override
	protected Service loadService(String serviceId, String version) {
		String accessorKey = generateAccessorKey(serviceId, version);
		ServiceRegistryAccessor serviceRegistryAccessor = accessorMap.get(generateAccessorKey(serviceId, version));
		if(serviceRegistryAccessor == null){
			serviceRegistryAccessor = serviceRegistryFactory.getServiceRegistryAccessor(serviceId, version, new SystemPropertyConfiguration());
			accessorMap.putIfAbsent(accessorKey, serviceRegistryAccessor);
		}
		
		final Service service = new Service(serviceId, serviceRegistryAccessor.getVersion(), null);
		
		List<String> states;
		
		try {
			states = serviceRegistryAccessor.listShardsAndListenChange(null);
		} catch (Exception e) {
			logger.error("ZK error: " + e.getMessage());
			states = Collections.emptyList();
		}
		for(String st : states){
			int tempSt = 0;
			try {
				tempSt = Integer.parseInt(st);
			} catch (NumberFormatException e) {
				logger.error("Invalid state config");
				continue;
			}
			final int shard = tempSt;
			List<String> nodeList = null;
			final ServiceRegistryAccessor registryAccessor = serviceRegistryAccessor;
			try {
				nodeList = serviceRegistryAccessor.listNodesAndListenChange(String.valueOf(shard), 
						new NodeChangeListener(){

							@Override
							public void listChanged(List<String> children) {
								Shard sss = prepareShard(service, shard);
								List<Node> nodes = sss.getNodeList();
								
								HashSet<String> oldNodes = new HashSet<String>();
								HashSet<String> newNodes = new HashSet<String>(children);
								for(Node n: nodes){
									oldNodes.add(n.getNodeKey());
								}
								
								HashSet<String> node2Add = new HashSet<String>();
								node2Add.addAll(newNodes);
								node2Add.removeAll(oldNodes);
								
								HashSet<String> node2Del = new HashSet<String>();
								node2Del.addAll(oldNodes);
								node2Del.removeAll(newNodes);
								
								if(node2Add.size() > 0){
									addNode(service, shard, registryAccessor, node2Add);
								}
								if(node2Del.size() > 0){
									for(String nodeKey: node2Del){
										nodes.remove(NodeFactory.getNode(nodeKey));
									}
								}
								
							}
					
				});
			} catch (Exception e) {
				logger.error(e.getMessage());
			}
			addNode(service, shard, registryAccessor, new HashSet<String>(nodeList));
		}
		return service;
	}
	
	private void addNode(Service service, int shard, final ServiceRegistryAccessor accessor, Set<String> node2Add){
		Shard s = prepareShard(service, shard);
		for(String nodeKey: node2Add){
			final Node node = NodeFactory.getNode(nodeKey);
			byte[] data;
			try {
				data = accessor.getNodeAndListenChange(String.valueOf(shard), nodeKey, 
						new DataChangeListener(){

							@Override
							public void dataChange(byte[] data) {
								updateNodeData(node, data);
							}

				});
			} catch (Exception e) {
				logger.error(e.getMessage());
				return;
			}
			updateNodeData(node, data);
			s.getNodeList().add(node);
		}
	}

	private void updateNodeData(Node node, byte[] data){
		NodeData nodeData;
		try{
			nodeData = NodeData.valueOf(data);
			if(nodeData.isDisabled() != node.isDisabled() || nodeData.isHealthy() != node.isHealthy()){
				node.setHealthy(nodeData.isHealthy());
				node.setDisabled(nodeData.isHealthy());
			}
		}catch(Exception e){
			
		}
	}
	
	private Shard prepareShard(Service service, Integer shard){
		Shard s = service.getSharding().get(shard);
		if(s == null){
			s = new Shard(service, shard, null);
			service.getSharding().put(shard, s);
		}
		return s;
	}
}
