package com.zcwyx.tbs.server.impl;

import java.util.concurrent.ConcurrentHashMap;

import org.apache.thrift.TException;

import com.zcwyx.tbs.server.IStatisticsProvider;

public class StatisticsProviderImpl implements IStatisticsProvider{

	@Override
	public String getMax(String methodName, int second) throws TException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getMean(String methodName) throws TException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setCallMethodInfo(String methodName, int time) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void initStatisticsThread() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void initStatisticsData() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public ConcurrentHashMap<String, Long> getData() {
		// TODO Auto-generated method stub
		return null;
	}

}
