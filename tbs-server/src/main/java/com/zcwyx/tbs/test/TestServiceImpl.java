package com.zcwyx.tbs.test;

import org.apache.thrift.TException;

public class TestServiceImpl implements TestService.Iface{

	@Override
	public String getName() throws TException {
		return "It's Test Service";
	}

	@Override
	public String ping() throws TException {
		return "PONG";
	}

}
