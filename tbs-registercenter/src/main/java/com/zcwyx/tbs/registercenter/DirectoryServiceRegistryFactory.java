package com.zcwyx.tbs.registercenter;

public class DirectoryServiceRegistryFactory implements ServiceRegistryFactory{

	@Override
	public ServiceRegistry getServiceRegistry(RegisterCenterConfiguration config) {
		return new DirectoryServiceRegistry(config);
	}

	@Override
	public ServiceRegistryAccessor getServiceRegistryAccessor(String service, String version,
			RegisterCenterConfiguration config) {
		return new DirectoryServiceRegistoryAccessor(service, version, config);
	}

}
