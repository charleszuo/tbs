package com.zcwyx.tbs.client.impl;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zcwyx.tbs.client.ServiceRouter;
import com.zcwyx.tbs.client.definition.ClassDefinition;
import com.zcwyx.tbs.client.definition.MethodDefinition;
import com.zcwyx.tbs.client.transport.TbsTransport;

public class ServiceInvocationHandler implements InvocationHandler {

	private static Logger logger = LoggerFactory
			.getLogger(ServiceInvocationHandler.class);

	private ClassDefinition serviceDefinition;

	private ServiceRouter serviceRouter;

	private long timeout;

	private ConcurrentHashMap<Method, MethodDefinition> methodCache = new ConcurrentHashMap<Method, MethodDefinition>();

	public ServiceInvocationHandler(ServiceRouter serviceRouter,
			ClassDefinition serviceDefinition, long timeout) {
		if (serviceRouter == null || serviceDefinition == null) {
			throw new IllegalArgumentException(
					"Service router or class definition can't be null");
		}
		this.serviceRouter = serviceRouter;
		this.serviceDefinition = serviceDefinition;
		this.timeout = timeout;
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args)
			throws Throwable {
		return round(proxy, method, args);
	}

	protected Object round(Object proxy, Method method, Object[] args)
			throws Throwable {
		try{
			before(proxy, method, args);
			return doInvoke(proxy, method, args);
		}finally{
			after(proxy, method, args);
		}
	}

	protected Object doInvoke(Object proxy, Method method, Object[] args)
			throws Throwable {
		String serviceId = serviceDefinition.getServiceId();
		String version = serviceDefinition.getVersion();
		
		TbsTransport tbsTransport = null;
		try{
			tbsTransport = serviceRouter.routeService(serviceId, version, null, timeout);
		}catch(Exception e){
			throw new RuntimeException("Failed to route service " + serviceId + " version " + version);
		}
		
		if(tbsTransport == null){
			throw new RuntimeException("No transport available for service " + serviceId + " version " + version);
		}
		
		Object result = null;
		Throwable serviceException = null;
		try {
			TProtocol protocol = new TBinaryProtocol(tbsTransport.getTransport());
			Object client = serviceDefinition.getServiceClientConstructor().newInstance(protocol);
			result = getRealMethod(method).getMethod().invoke(client, args);
			return result;
		} catch (Exception e) {
			serviceException = e;
			throw new RuntimeException("Invoke error");
		}finally{
			if(serviceException != null){
				serviceRouter.serviceException(serviceId, version, serviceException, tbsTransport);
			}else{
				// return connection to pool
				serviceRouter.returnConnection(tbsTransport);
			}
		}
	}
	
	protected void before(Object proxy, Method method, Object[] args)
			throws Throwable {

	}

	protected void after(Object proxy, Method method, Object[] args)
			throws Throwable {

	}
	
	public MethodDefinition getRealMethod(Method method) throws Exception{
		MethodDefinition methodDefinition = methodCache.get(method);
		if(methodDefinition != null){
			return methodDefinition;
		}
		
		synchronized (method) {
			methodDefinition = methodCache.get(method);
			if(methodDefinition == null){
				Method realMethod = serviceDefinition.getServiceClientClass().getDeclaredMethod(method.getName(), method.getParameterTypes());
				methodDefinition = new MethodDefinition(realMethod);
				methodCache.put(method, methodDefinition);
			}
		}
		
		return methodDefinition;
	}

}
