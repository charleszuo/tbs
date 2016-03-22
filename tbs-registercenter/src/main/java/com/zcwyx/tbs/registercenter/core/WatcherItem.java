package com.zcwyx.tbs.registercenter.core;

import org.apache.zookeeper.Watcher;

public class WatcherItem {
	private String path;
	private Watcher watcher;
	private boolean isChildren;
	private int failedTimes;
	
	public WatcherItem(String path, Watcher watcher, boolean isChildren){
		this.path = path;
		this.watcher = watcher;
		this.isChildren = isChildren;
		this.failedTimes = 0;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public Watcher getWatcher() {
		return watcher;
	}

	public void setWatcher(Watcher watcher) {
		this.watcher = watcher;
	}

	public boolean isChildren() {
		return isChildren;
	}

	public void setChildren(boolean isChildren) {
		this.isChildren = isChildren;
	}

	public int getFailedTimes() {
		return failedTimes;
	}

	public void setFailedTimes(int failedTimes) {
		this.failedTimes = failedTimes;
	}

	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (isChildren ? 1231 : 1237);
		result = prime * result + ((path == null) ? 0 : path.hashCode());
		result = prime * result + ((watcher == null) ? 0 : watcher.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
	    if (obj instanceof WatcherItem) {
	        WatcherItem other = (WatcherItem) obj;
	        return other.isChildren == this.isChildren && 
	            this.watcher.equals(other.watcher) && path.equals(other.path);
	    } else {
	        return false;	        
	    }
	}
}
