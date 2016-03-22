package com.zcwyx.tbs.client.transport;

import org.apache.thrift.transport.TTransport;

import com.zcwyx.tbs.registry.Node;


public class TbsTransport {
	private TTransport transport;
	private Node node;
	private boolean isDisabled;

	public TTransport getTransport() {
		return transport;
	}

	public void setTransport(TTransport transport) {
		this.transport = transport;
	}

	public Node getNode() {
		return node;
	}

	public void setNode(Node node) {
		this.node = node;
	}

	public boolean isDisabled() {
		return isDisabled;
	}

	public void setDisabled(boolean isDisabled) {
		this.isDisabled = isDisabled;
	}
	
}
