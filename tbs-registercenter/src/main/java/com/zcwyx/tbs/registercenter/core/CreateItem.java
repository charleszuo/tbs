package com.zcwyx.tbs.registercenter.core;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.Watcher;

public class CreateItem {

	private String path;
	private byte[] data;
	private CreateMode createMode;
	private Watcher watcher;

	public CreateItem(String path, byte[] data, CreateMode createMode){
		this.path = path;
		this.data = data;
		this.createMode = createMode;
	}
	
	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public byte[] getData() {
		return data;
	}

	public void setData(byte[] data) {
		this.data = data;
	}

	public CreateMode getCreateMode() {
		return createMode;
	}

	public void setCreateMode(CreateMode createMode) {
		this.createMode = createMode;
	}

	public Watcher getWatcher() {
		return watcher;
	}

	public void setWatcher(Watcher watcher) {
		this.watcher = watcher;
	}

}
