package com.zcwyx.tbs.registercenter;

public interface ServiceRegistryFactory {
	
	public ServiceRegistry getServiceRegistry(RegisterCenterConfiguration config);
	
	public ServiceRegistryAccessor getServiceRegistryAccessor(String service, String version, RegisterCenterConfiguration config);
	
}
