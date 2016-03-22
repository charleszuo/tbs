package com.zcwyx.tbs.registercenter.client;

import java.io.IOException;
import java.util.List;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.EventType;
import org.apache.zookeeper.data.Stat;

import com.zcwyx.tbs.registercenter.client.Util.VersionRange;
import com.zcwyx.tbs.registercenter.core.ZKConnector;

public class Consumer extends ZKConnector{

	public Consumer(String clusterAddress, int sessionTimeout)
			throws IOException {
		super(clusterAddress, sessionTimeout);
	}

	public int getCurrentVersion(String service, String version){
		VersionRange versionRange = Util.validateVersion(version);
		if(!versionRange.isValid()){
			throw new IllegalArgumentException("Version not recognised.");
		}
		if(this.root == null){
			throw new IllegalStateException("Root is not set");
		}
		StringBuilder path = new StringBuilder();
		path.append("/").append(root).append("/").append(service);
		int ver = versionRange.getLow();
		if(!versionRange.isRange()){
			return ver;
		}
		try {
			List<String> verList = zk.getChildren(path.toString(), false);
			int maxV = -1;
			for(String s: verList){
				int cv = Integer.parseInt(s);
				if(cv > maxV && cv < versionRange.getHigh()){
					maxV = cv;
				}
			}
			if(maxV < ver){
				throw new IllegalArgumentException("Version range not matched: " + maxV);
			}
			
			return maxV;
		} catch (Exception e) {
			throw new RuntimeException("Failed to get zk stat");
		} 
	}
	
	public List<String> getShardsAndListenChange(String service, String version, NodeChangeListener listener){
		StringBuilder path = new StringBuilder();
		path.append("/").append(root).append("/").append(service).append("/").append(getCurrentVersion(service, version));
		
		List<String>  list = (List<String>)this.attachWatcher(path.toString(), new NodeWatcher(listener), true);
		return list;
	}
	
	public List<String> getAddressAndListenChange(String service, String version, String shard, NodeChangeListener listener){
		StringBuilder path = new StringBuilder();
		path.append("/").append(root).append("/").append(service).append("/").append(getCurrentVersion(service, version)).append("/").append(shard);
		
		List<String>  list = (List<String>)this.attachWatcher(path.toString(), new NodeWatcher(listener), true);
		return list;
	}
	
	public byte[] getDataAndListenChange(String service, String version, String shard, String address, DataChangeListener listener){
		StringBuilder path = new StringBuilder();
		path.append("/").append(root).append("/").append(service).append("/").append(getCurrentVersion(service, version));
		path.append("/").append(shard).append("/").append(address);
		
		byte[] data = (byte[])this.attachWatcher(path.toString(), new DataWatcher(listener), false);
		return data;
	}
	
	protected class NodeWatcher implements Watcher{
		private NodeChangeListener listener;

		public NodeWatcher(NodeChangeListener listener){
			this.listener = listener;
		}
		
		@Override
		public void process(WatchedEvent event) {
			if(event.getType() != EventType.NodeChildrenChanged){
				return;
			}
			try {
				List<String> ls = zk.getChildren(event.getPath(), this);
				listener.listChanged(ls);
			} catch (Exception e) {
				logger.error("Failed to do watch");
			} 
		}
		
	}
	
	protected class DataWatcher implements Watcher{
		private DataChangeListener listener;
		
		public DataWatcher(DataChangeListener listener){
			this.listener = listener;
		}
		
		@Override
		public void process(WatchedEvent event) {
			if(event.getType() != EventType.NodeDataChanged){
				return;
			}
			try {
				byte[] data = zk.getData(event.getPath(), this, new Stat());
				listener.dataChange(data);
			} catch (Exception e) {
				logger.error("Failed to do watch");
			} 
		}
		
	}
}
