package com.zcwyx.tbs.server;



public class TbsServiceConfig {

	private String serviceId;
	
	private int serviceVersion;
	
	private int serviceShard = 0;
	
	private String serviceClass;
	
	private int port;
	
	private int threadPoolMaxSize;
	
	private int threadPoolCoreSize;

	private String initListenerClass;
	
	
	/** 当线程池的工作线程饱和时，请求等待队列的最大长度  */
	private int requestQueueLimit = 8192;
	
	/** 请求等待队列的报警阀值*/
	private float warnRatio = 0.8f;
	
	private int slowThreashold = 100;

	public String getServiceId() {
		return serviceId;
	}

	public void setServiceId(String serviceId) {
		this.serviceId = serviceId;
	}

	public int getServiceVersion() {
		return serviceVersion;
	}

	public void setServiceVersion(int serviceVersion) {
		this.serviceVersion = serviceVersion;
	}

	public int getServiceShard() {
		return serviceShard;
	}

	public void setServiceShard(int serviceShard) {
		this.serviceShard = serviceShard;
	}

	public String getServiceClass() {
		return serviceClass;
	}

	public void setServiceClass(String serviceClass) {
		this.serviceClass = serviceClass;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public int getThreadPoolMaxSize() {
		return threadPoolMaxSize;
	}

	public void setThreadPoolMaxSize(int threadPoolMaxSize) {
		this.threadPoolMaxSize = threadPoolMaxSize;
	}

	public int getThreadPoolCoreSize() {
		return threadPoolCoreSize;
	}

	public void setThreadPoolCoreSize(int threadPoolCoreSize) {
		this.threadPoolCoreSize = threadPoolCoreSize;
	}

	public String getInitListenerClass() {
		return initListenerClass;
	}

	public void setInitListenerClass(String initListenerClass) {
		this.initListenerClass = initListenerClass;
	}

	public int getRequestQueueLimit() {
		return requestQueueLimit;
	}

	public void setRequestQueueLimit(int requestQueueLimit) {
		this.requestQueueLimit = requestQueueLimit;
	}

	public float getWarnRatio() {
		return warnRatio;
	}

	public void setWarnRatio(float warnRatio) {
		if(warnRatio < 1 && warnRatio >= 0){
			this.warnRatio = warnRatio;
		}
	}

	public int getSlowThreashold() {
		return slowThreashold;
	}

	public void setSlowThreashold(int slowThreashold) {
		this.slowThreashold = slowThreashold;
	}
	
	public String toString(){
		return new StringBuilder().append("Service Id ").append(serviceId).toString();
	}
}
