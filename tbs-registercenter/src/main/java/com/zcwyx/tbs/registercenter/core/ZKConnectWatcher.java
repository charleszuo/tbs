package com.zcwyx.tbs.registercenter.core;

import java.util.concurrent.CountDownLatch;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ZKConnectWatcher implements Watcher{
	
	private static final Logger logger = LoggerFactory.getLogger(Watcher.class);
	
	private ZKConnector connector;
	
	private CountDownLatch latch;
	
	public ZKConnectWatcher(ZKConnector connector, CountDownLatch latch){
		this.connector = connector;
		this.latch = latch;
	}

	@Override
	public void process(WatchedEvent event) {
		logger.info("ZK connect state: " + event.getState());
		if(event.getState() == KeeperState.SyncConnected){
			latch.countDown();
		}else if(event.getState() == KeeperState.Expired){
			connector.reconnect();
		}
	}

}
