package com.zcwyx.tbs.registercenter.core;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.EventType;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ZKConnector {
	protected static final Logger logger = LoggerFactory
			.getLogger(ZKConnector.class);

	private final String clusterAddress;

	private final Set<WatcherItem> watcherSet = Collections
			.synchronizedSet(new HashSet<WatcherItem>());

	private final Map<String, CreateItem> createItemMap = Collections
			.synchronizedMap(new HashMap<String, CreateItem>());

	private final int sessionTimeout;

	private byte[] auth;

	protected String root;

	protected ZooKeeper zk;

	private static final int WATCH_FAIL_LIMIT = 5;

	private static final int TIME_INTERVEL_TO_DO_WATCHER_SEC = 10 * 60;

	private static final int TIME_MAX_INTERVEL_TO_RECREATE_NODE_MILLS = 5 * 1000;

	private static final int TIME_RAND_LIMIT_MILLS = 100;

	private static volatile boolean connected = false;

	public ZKConnector(String clusterAddress, int sessionTimeout)
			throws IOException {
		this.clusterAddress = clusterAddress;
		this.sessionTimeout = sessionTimeout;
		if (sessionTimeout < 5000) {
			throw new IllegalArgumentException("sessionTimeout too small.");
		}
		
		connectToZooKeeper();
	}

	private void connectToZooKeeper() throws IOException {
		CountDownLatch latch = new CountDownLatch(1);
		this.zk = new ZooKeeper(clusterAddress, sessionTimeout,
				new ZKConnectWatcher(this, latch));
		try {
			latch.await();
		} catch (InterruptedException e) {
			throw new RuntimeException("Failed to connect ZK");
		}
		connected = true;
	}

	public void addAuthInfo(String userName, String password) {
		try {
			auth = (userName + ":" + password).getBytes("utf8");
			this.zk.addAuthInfo("digest", auth);
		} catch (UnsupportedEncodingException e) {
			logger.error("Failed to add auth");
		}

	}

	public void close() {
		try {
			this.zk.close();
			connected = false;
		} catch (Exception e) {
			logger.error("Failed to close");
		}
	}

	public void reconnect() {
		while (true) {
			try {
				close();
				connectToZooKeeper();
				if (auth != null) {
					this.zk.addAuthInfo("digest", auth);
				}
				// rebuild node task is running
				new Thread(new RebuildNodesTask()).start();
				rebuildWatcher();
				break;
			} catch (Exception e) {
				logger.error("Error occour when reconnecting, system will retry");
				try {
					Thread.sleep(sessionTimeout / 3);
				} catch (InterruptedException e1) {
				}
			}
		}
	}

	private void rebuildWatcher() {
		synchronized (watcherSet) {
			for (WatcherItem watcherItem : watcherSet) {
				if (!createItemMap.containsKey(watcherItem.getPath())) {
					try {
						watcherItem.setFailedTimes(0);
						doWatch(watcherItem);
					} catch (Exception e) {
						watcherItem.setFailedTimes(1);
					}
				}
			}
		}
	}

	class RebuildNodesTask implements Runnable {

		@Override
		public void run() {
			synchronized (createItemMap) {
				for (CreateItem item : createItemMap.values()) {
					int interval = new Random(System.currentTimeMillis())
							.nextInt(TIME_RAND_LIMIT_MILLS) + 1;
					while (connected) {
						try {
							TimeUnit.MILLISECONDS.sleep(interval);
						} catch (InterruptedException e) {
						}
						try {
							doCreateNode(item.getPath(), item.getData(),
									item.getCreateMode());
							logger.info("Recreate path : " + item.getPath()
									+ ", data: " + new String(item.getData()));
							doWatch(item.getPath(), item.getWatcher(), false);
							break;
						} catch (KeeperException.NodeExistsException e) {
							break;
						} catch (Exception e) {
							logger.error("Failed to recreate Node: " + item);
						}
						interval <<= 1;
						interval = interval > TIME_MAX_INTERVEL_TO_RECREATE_NODE_MILLS ? TIME_MAX_INTERVEL_TO_RECREATE_NODE_MILLS
								: interval;
					}
				}
			}

		}

	}

	private String doCreateNode(String path, byte[] data, CreateMode createMode)
			throws KeeperException, InterruptedException {
		return zk.create(path, data, Ids.OPEN_ACL_UNSAFE, createMode);
	}

	private Object doWatch(String path, Watcher watcher, boolean isChildren){
		try{
		if (isChildren) {
			return zk.getChildren(path, watcher);
		} else {
			return zk.getData(path, watcher, new Stat());
		}
		}catch(Exception e){
			throw new RuntimeException("Failed to do wath");
		}
	}

	private Object doWatch(WatcherItem item){
		return doWatch(item.getPath(), item.getWatcher(), item.isChildren());
	}
	
	public Object attachWatcher(String path, Watcher watcher, boolean isChildren){
		WatcherItem item = new WatcherItem(path, watcher, isChildren);
		Object r = doWatch(item);
		watcherSet.add(item);
		return r;
	}
	
	public boolean deleteWatcher(String path, Watcher watcher, boolean isChildren){
		return watcherSet.remove(new WatcherItem(path, watcher, isChildren));
	}
	
	public void createNode(String path, boolean isTemp){
		createNode(path, null, isTemp, false);
	}
	
	public String createNode(String path, byte[] data, boolean isTemp, boolean isSequential){
		return createNode(path, data, isTemp, isSequential, true);
	}
	
	public String createNode(String path, byte[] data, boolean isTemp, boolean isSequential, boolean doWatch){
		try{
			CreateMode createMode = null;
			if(isTemp){
				if(isSequential){
					createMode = CreateMode.EPHEMERAL_SEQUENTIAL;
				}else{
					createMode = CreateMode.EPHEMERAL;
				}
			}else{
				if(isSequential){
					createMode = CreateMode.PERSISTENT_SEQUENTIAL;
				}else{
					createMode = CreateMode.PERSISTENT;
				}
			}
			String s = doCreateNode(path, data, createMode);
			if(isTemp && doWatch){
				final CreateItem createItem = new CreateItem(path, data, createMode);
				createItem.setWatcher(new Watcher(){

					@Override
					public void process(WatchedEvent event) {
						if(event.getType() != EventType.NodeDataChanged){
							return;
						}
						try{
							
							byte[] data = zk.getData(event.getPath(), this, new Stat());
							if(!Arrays.equals(data, createItem.getData())){
								createItem.setData(data);
							}
						}catch(Exception e){
							logger.error("Failed to do watch");
						}
						
					}
					
				});
				createItemMap.put(path, createItem);
				attachWatcher(path, createItem.getWatcher(), false);
			}
			return s;
		}catch(Exception e){
			throw new RuntimeException("Failed to create node");
		}
		
	}
	
	public void updateNode(String path, byte[] data){
		try {
			zk.setData(path, data, -1);
			CreateItem item = createItemMap.get(path);
			if(item != null){
				item.setData(data);
			}
		} catch (Exception e) {
			logger.error("Failed to update node");
		} 
	}
	
	public boolean deleteNode(String path){
		boolean result = false;
		try {
			zk.delete(path, -1);
			createItemMap.remove(path);
			result = true;
		} catch (Exception e) {
			logger.error("Failed to delete node");
		} 
		
		return result;
	}

	public String getRoot() {
		return root;
	}

	public void setRoot(String root) {
		this.root = root;
	}
	
}
