package com.zcwyx.tbs.registercenter;

import java.util.List;

import com.zcwyx.tbs.registercenter.client.DataChangeListener;
import com.zcwyx.tbs.registercenter.client.NodeChangeListener;

public interface ServiceRegistryAccessor {
	
	public String getService();
	
	public int getVersion();
	
	public List<String> listShardsAndListenChange(NodeChangeListener listener);
	
	public List<String> listNodesAndListenChange(String shard, NodeChangeListener listener);
	
	public byte[] getNodeAndListenChange(String shard, String node, DataChangeListener listener);
}
