package com.zcwyx.tbs.server.impl;

import java.lang.reflect.Constructor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.thrift.TProcessor;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TThreadedSelectorServer;
import org.apache.thrift.transport.TNonblockingServerSocket;
import org.apache.thrift.transport.TNonblockingServerTransport;
import org.apache.thrift.transport.TServerTransport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zcwyx.tbs.server.TbsService;
import com.zcwyx.tbs.server.TbsServiceConfig;
import com.zcwyx.tbs.server.base.AbstractBaseService;

public class ServiceBuilderImpl extends AbstractServiceBuilder{

	private static final Logger logger = LoggerFactory.getLogger(ServiceBuilderImpl.class);
	
	private final long RPC_DATA_LENGTH_MAX = 16384000;
	
	private final String SERVICE_NAME_SUFFIX = "Impl";
	
	@Override
	protected TProcessor buildProcessor(TbsServiceConfig config,
			TbsService service) {
		
		String className = config.getServiceClass();
		if(!className.endsWith(SERVICE_NAME_SUFFIX)){
			throw new RuntimeException("Service class name must end with Impl");
		}
		
		String interfaceName = className.substring(0, className.length() - SERVICE_NAME_SUFFIX.length());
		try{
			Class<?> ifaceClass = Class.forName(interfaceName + "$Iface");
			Class<?> processorClass = Class.forName(interfaceName + "$Processor");
			Constructor<?> constructor = processorClass.getConstructor(ifaceClass);
			Object serviceImpl = Class.forName(className).newInstance();
			TProcessor processor = (TProcessor) constructor.newInstance(serviceImpl);
			
			if(serviceImpl instanceof AbstractBaseService){
				((AbstractBaseService)serviceImpl).setService(service);
				service.setHasBase(true);
			}
			
			return processor;
		}catch(Exception e){
			logger.error("Faild to build TProcessor, error msg: " + e.getMessage());
		}
		return null;
	}

	@Override
	protected TServerTransport buildServerTransport(TbsServiceConfig config) {
		int port = config.getPort();
		
		try{
//			XoaServerTransport serverTransport = new XoaServerTransport(port, 0);
//			
//			int bindPort = serverTransport.getPort();
//			config.setPort(bindPort);
//			return serverTransport;
			return new TNonblockingServerSocket(port);
		}catch(Exception e){
			
		}
		return null;
	}

	private ExecutorService createExecutorService(TbsServiceConfig config){
		int threadPoolMaxSize = config.getThreadPoolMaxSize();
		int threadPoolCoreSize = config.getThreadPoolCoreSize();
		int requestQueueLimit = config.getRequestQueueLimit();
		
		if(threadPoolMaxSize <= 0 || threadPoolCoreSize <= 0){
			threadPoolCoreSize = 2 * Runtime.getRuntime().availableProcessors();
			threadPoolMaxSize = 2 * threadPoolCoreSize;
			config.setThreadPoolCoreSize(threadPoolCoreSize);
			config.setThreadPoolMaxSize(threadPoolMaxSize);
		}
		
		LinkedBlockingQueue<Runnable> requestQueue = new LinkedBlockingQueue<Runnable>(requestQueueLimit);
		TbsServiceThreadFactory threadFactory = new TbsServiceThreadFactory();
		
		return new ThreadPoolExecutor(threadPoolCoreSize, threadPoolMaxSize, 60, TimeUnit.SECONDS, requestQueue, threadFactory);
	}
	
	@Override
	protected TServer buildServer(TbsServiceConfig config,
			TServerTransport serverTransport, TProcessor processor) {
		TThreadedSelectorServer.Args args = new TThreadedSelectorServer.Args((TNonblockingServerTransport)serverTransport);
		args.maxReadBufferBytes = RPC_DATA_LENGTH_MAX;
//		args.setQueueLimit(config.getRequestQueueLimit());
//		args.setWarnRatio(config.getWarnRatio());
		args.protocolFactory(new TBinaryProtocol.Factory(true, true));
		
		args.executorService(createExecutorService(config));
		args.processor(processor);
		
		
		return new TThreadedSelectorServer(args);
	}

}
