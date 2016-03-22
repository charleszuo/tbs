package com.zcwyx.tbs.client.impl;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.concurrent.ConcurrentHashMap;

import org.junit.Assert;

import com.zcwyx.tbs.client.IServiceFactory;
import com.zcwyx.tbs.client.ServiceRouter;
import com.zcwyx.tbs.client.definition.ClassDefinition;
import com.zcwyx.tbs.client.util.ClassUtils;


public abstract class AbstractServiceFactory implements IServiceFactory{
	private ConcurrentHashMap<Class<?>, Object> serviceCache = new ConcurrentHashMap<Class<?>, Object>();
	
	private ServiceRouter serviceRouter;
	
	public AbstractServiceFactory(ServiceRouter serviceRouter){
		Assert.assertNotNull("ServiceRouter can't be null", serviceRouter);
		this.serviceRouter = serviceRouter;
	}
	
	protected ServiceRouter getServiceRouter() {
		return serviceRouter;
	}
	
	public <T> T getService(Class<T> serviceClass){
		return getService(serviceClass, 250);
	}
	
	public <T> T getService(Class<T> serviceClass, long timeout) {
		try {
			Object serviceInstance = serviceCache.get(serviceClass);
			if(serviceInstance != null){
				return (T)serviceInstance;
			}
					
			synchronized (serviceClass) {
				serviceInstance = serviceCache.get(serviceClass);
				if(serviceInstance == null){
					ClassDefinition classDefinition = new ClassDefinition(serviceClass);
					InvocationHandler invocationHandler = createInvocationHandler(classDefinition, timeout);
					T proxy = (T)Proxy.newProxyInstance(ClassUtils.getDefaultClassLoader(), new Class<?>[]{serviceClass}, invocationHandler);
					serviceCache.putIfAbsent(serviceClass, proxy);
				}
			}
			return (T)serviceCache.get(serviceClass);
		} catch (Exception e) {
			throw new RuntimeException("Failed to get Proxy");
		}
	}
	
	protected abstract InvocationHandler createInvocationHandler(ClassDefinition classDefinition, long timeout);
}
