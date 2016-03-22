package com.zcwyx.tbs.server;

import java.lang.reflect.Method;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.thrift.TException;
import org.apache.thrift.TProcessor;
import org.apache.thrift.server.TServer;
import org.apache.thrift.transport.TServerTransport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zcwyx.tbs.registry.Node;
import com.zcwyx.tbs.registry.Service;
import com.zcwyx.tbs.registry.TbsRegistry;
import com.zcwyx.tbs.server.impl.StatisticsProviderFactory;

public class TbsService implements Runnable{
	
	private static final Logger logger = LoggerFactory.getLogger(TbsService.class);
	
	private TProcessor processor;
	private TServerTransport serverTransport;
	private TServer server;
	private TbsServiceConfig config;
	private List<Thread> threadList;
	private List<TbsService> serviceList;
	private Node node;
	private TbsRegistry registry;
	private String configPath;
	        
	private IStatisticsProvider statisticsProvider;
	private boolean hasBase;
	
	private final ConcurrentHashMap<String, String> serviceMethodOptions = new ConcurrentHashMap<String, String>();
	
	public TbsService(){};
	
	@Override
	public void run() {
		try{
			logger.info(config + " start");
			if(hasBase){
				statisticsProvider = StatisticsProviderFactory.getInstance().getDefaultStatisticsProvider();
				statisticsProvider.initStatisticsThread();
			}
			server.serve();
			logger.info(config + "stop");
		}catch(Exception e){
			
		}
	}

	public TProcessor getProcessor() {
		return processor;
	}

	public void setProcessor(TProcessor processor) {
		this.processor = processor;
	}

	public TServerTransport getServerTransport() {
		return serverTransport;
	}

	public void setServerTransport(TServerTransport serverTransport) {
		this.serverTransport = serverTransport;
	}

	public TServer getServer() {
		return server;
	}

	public void setServer(TServer server) {
		this.server = server;
	}

	public TbsServiceConfig getConfig() {
		return config;
	}

	public void setConfig(TbsServiceConfig config) {
		this.config = config;
	}

	public List<Thread> getThreadList() {
		return threadList;
	}

	public void setThreadList(List<Thread> threadList) {
		this.threadList = threadList;
	}

	public List<TbsService> getServiceList() {
		return serviceList;
	}

	public void setServiceList(List<TbsService> serviceList) {
		this.serviceList = serviceList;
	}

	public Node getNode() {
		return node;
	}

	public void setNode(Node node) {
		this.node = node;
	}

	public TbsRegistry geTbstRegistry() {
		return registry;
	}

	public void setTbsRegistry(TbsRegistry registry) {
		this.registry = registry;
	}

	public String getConfigPath() {
		return configPath;
	}

	public void setConfigPath(String configPath) {
		this.configPath = configPath;
	}

	public IStatisticsProvider getStatisticsProvider() {
		return statisticsProvider;
	}

	public void setStatisticsProvider(IStatisticsProvider statisticsProvider) {
		this.statisticsProvider = statisticsProvider;
	}

	public boolean isHasBase() {
		return hasBase;
	}

	public void setHasBase(boolean hasBase) {
		this.hasBase = hasBase;
	}

	public String getServiceMethodOption(String key) {
		return serviceMethodOptions.get(key);
	}

	public void setServiceMethodOption(String key, String value){
		this.serviceMethodOptions.put(key, value);
	}
	
	public void connectZK() throws TException{
		this.registry.registerNode(new Service(config.getServiceId(), 
				config.getServiceVersion(), null), config.getServiceShard(), node);
	}
	
	public void destoryZK() throws TException{
		this.registry.destroy();
	}
	
	public void stopServer(){
		this.server.stop();
	}
	
	public void setCallMethodInfo(String methodName, int time){
		if(isMethodNeedStatistic(methodName)){
			statisticsProvider.setCallMethodInfo(methodName, time);
		}
	}
	
	public long getData(String key){
		return statisticsProvider.getData().get(key);
	}
	
	public boolean isMethodNeedStatistic(String methodName){
		if(statisticsProvider.getData().containsKey(methodName)){
			return true;
		}
		
		try {
			Class<?> clazz = Class.forName(config.getServiceClass());
			Method[] methods = clazz.getDeclaredMethods();
			for(Method method: methods){
				if(method.getName().equals(methodName)){
					return true;
				}
			}
		} catch (ClassNotFoundException e) {
			logger.info(config.getServiceClass() + " not found");
		}
		
		
		return false;
	}
}
