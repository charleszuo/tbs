package com.zcwyx.tbs.client;

public interface IServiceFactory {

	
	public <T> T getService(Class<T> serviceInterface);
	
	public <T> T getService(Class<T> serviceInterface, long timeout);
}
