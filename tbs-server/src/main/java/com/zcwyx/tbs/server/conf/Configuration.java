package com.zcwyx.tbs.server.conf;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zcwyx.tbs.server.TbsServiceConfig;

public class Configuration {
	static final Logger logger = LoggerFactory.getLogger(Configuration.class);
	
	public static final String SERVICE_TYPE_TBS = "tbs";
	
	public static final String SERVICE_TAG = "service";
	public static final String ID_ATTRIBUTE = "id";
	public static final String VERSION_ATTRIBUTE = "version";
	public static final String TYPE_ATTRIBUTE = "type";
	public static final String CLASS_ATTRIBUTE = "class";
	public static final String PORT_ATTRIBUTE = "port";
	
	public static final String THREAD_POOL_TAG = "threadPool";
	public static final String CORE_SIZE_ATTRIBUTE = "coreSize";
	public static final String MAX_SIZE_ATTRIBUTE = "maxSize";
	
	public static final String REQUEST_QUEUE_TAG = "requestQueue";
	public static final String REQUEST_QUEUE_LIMIT_ATTRIBUTE = "limit";
	public static final String WARN_RATIO_ATTRIBUTE = "warnRatio";
	
	public static final String SERVICE_SLOW_TAG = "serviceSlow";
	public static final String THREASHOLD_ATTRIBUTE = "threashold";
	
	private final List<TbsServiceConfig> serviceConfigList = new ArrayList<TbsServiceConfig>();
	
	public void addServiceConfig(TbsServiceConfig config){
		serviceConfigList.add(config);
	}
	
	public List<TbsServiceConfig> getServiceConfigList(){
		return Collections.unmodifiableList(serviceConfigList);
	}
}
