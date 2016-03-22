package com.zcwyx.tbs.server.conf;

import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.lang.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.zcwyx.tbs.server.TbsServiceConfig;

public class ConfigurationFactory {
	public static Configuration newConfiguration(String configPath){
		if(StringUtils.isBlank(configPath)){
			throw new RuntimeException("Config file path is empty");
		}
		
		Configuration configuration = new Configuration();
		
		try{
			Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(configPath);
			NodeList serviceNodes = document.getElementsByTagName(Configuration.SERVICE_TAG);
			if(serviceNodes == null || serviceNodes.getLength() == 0){
				Configuration.logger.error("No service definition found");
				throw new RuntimeException("No service definition found");
			}
			
			for(int i=0; i < serviceNodes.getLength(); i++){
				Node serviceNode = serviceNodes.item(i);
				NamedNodeMap attributes = serviceNode.getAttributes();
				TbsServiceConfig serviceConfig = new TbsServiceConfig();
				serviceConfig.setServiceId(attributes.getNamedItem(Configuration.ID_ATTRIBUTE).getNodeValue());
				serviceConfig.setServiceClass(attributes.getNamedItem(Configuration.CLASS_ATTRIBUTE).getNodeValue());
				serviceConfig.setPort(Integer.parseInt(attributes.getNamedItem(Configuration.PORT_ATTRIBUTE).getNodeValue()));
				
				Node versionAttr = attributes.getNamedItem(Configuration.VERSION_ATTRIBUTE);
				if(versionAttr != null){
					serviceConfig.setServiceVersion(Integer.parseInt(versionAttr.getNodeValue()));
				}
				
				if(serviceNode.getChildNodes() != null){
					NodeList childNodes = serviceNode.getChildNodes();
					for(int j=0; j < childNodes.getLength(); j++){
						Node childNode = childNodes.item(j);
						String tagName = childNode.getNodeName();
						if(Configuration.THREAD_POOL_TAG.equals(tagName)){
							serviceConfig.setThreadPoolCoreSize(Integer.parseInt(childNode.getAttributes().getNamedItem(Configuration.CORE_SIZE_ATTRIBUTE).getNodeValue()));
							serviceConfig.setThreadPoolMaxSize(Integer.parseInt(childNode.getAttributes().getNamedItem(Configuration.MAX_SIZE_ATTRIBUTE).getNodeValue()));
						}else if(Configuration.REQUEST_QUEUE_TAG.equals(tagName)){
							Node requestQueueLimitAttr = childNode.getAttributes().getNamedItem(Configuration.REQUEST_QUEUE_LIMIT_ATTRIBUTE);
							if(requestQueueLimitAttr != null){
								serviceConfig.setRequestQueueLimit(Integer.parseInt(requestQueueLimitAttr.getNodeValue()));
							}
							
							Node warnRatioAttr = childNode.getAttributes().getNamedItem(Configuration.WARN_RATIO_ATTRIBUTE);
							if(warnRatioAttr != null){
								serviceConfig.setWarnRatio(Integer.parseInt(warnRatioAttr.getNodeValue()));
							}
						}else if(Configuration.SERVICE_SLOW_TAG.equals(tagName)){
							Node threasholdAttr = childNode.getAttributes().getNamedItem(Configuration.THREASHOLD_ATTRIBUTE);
							if(threasholdAttr != null){
								serviceConfig.setSlowThreashold(Integer.parseInt(threasholdAttr.getNodeValue()));
							}
						}
					}
				}
				configuration.addServiceConfig(serviceConfig);
			}
			
		}catch(Exception e){
			
		}
		
		return configuration;
	}
}
