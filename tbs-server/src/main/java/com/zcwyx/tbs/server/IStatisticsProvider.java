package com.zcwyx.tbs.server;

import java.util.concurrent.ConcurrentHashMap;

import org.apache.thrift.TException;

public interface IStatisticsProvider {

	public String getMax(String methodName, int second) throws TException;
	
	public String getMean(String methodName) throws TException;
	
	public void setCallMethodInfo(String methodName, int time);
	
	public void initStatisticsThread();
	
	public void initStatisticsData();
	
	public ConcurrentHashMap<String, Long> getData();
}
