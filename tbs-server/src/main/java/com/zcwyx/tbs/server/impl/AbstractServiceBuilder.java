package com.zcwyx.tbs.server.impl;

import org.apache.thrift.TProcessor;
import org.apache.thrift.server.TServer;
import org.apache.thrift.transport.TServerTransport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zcwyx.tbs.server.IServiceBuilder;
import com.zcwyx.tbs.server.TbsService;
import com.zcwyx.tbs.server.TbsServiceConfig;

public abstract class AbstractServiceBuilder implements IServiceBuilder{

	private static  final Logger logger = LoggerFactory.getLogger(AbstractServiceBuilder.class);
	
	protected abstract TProcessor buildProcessor(TbsServiceConfig config, TbsService service);
	
	protected abstract TServerTransport buildServerTransport(TbsServiceConfig config);
	
	protected abstract TServer buildServer(TbsServiceConfig config, TServerTransport serverTransport, TProcessor processor);
	
	@Override
	public TbsService build(TbsServiceConfig serviceConfig) {
		if(logger.isDebugEnabled()){
			logger.debug("Start to build service");
		}
		
		TbsService service = new TbsService();
		service.setConfig(serviceConfig);
		
		TProcessor processor = buildProcessor(serviceConfig, service);
		
		if(processor != null){
			service.setProcessor(processor);
		}else{
			logger.error("Failed to build processor, please chech the config");
			// Fail fast
			System.exit(1);
		}
		
		TServerTransport serverTransport = buildServerTransport(serviceConfig);
		if(serverTransport != null){
			service.setServerTransport(serverTransport);
		}else{
			logger.error("Failed to build serverTransport, please chech the config");
			// Fail fast
			System.exit(1);
		}
		
		TServer server = buildServer(serviceConfig, serverTransport, processor);
		if(server != null){
			service.setServer(server);
		}else{
			logger.error("Failed to build server, please chech the config");
			// Fail fast
			System.exit(1);
		}
		
		if(logger.isDebugEnabled()){
			logger.debug("TbsService build success");
		}
		return service;
	}

}
