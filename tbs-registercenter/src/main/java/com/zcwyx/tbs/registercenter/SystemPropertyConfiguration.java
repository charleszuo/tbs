package com.zcwyx.tbs.registercenter;


public class SystemPropertyConfiguration extends PropertiesFileConfiguration{
	
	public static String parseConfigurationPath(){
		String serverConfig = System.getProperty("trc.config");
		if(serverConfig == null){
			throw new RuntimeException("No trc configuration file specified in system property");
		}
		return serverConfig;
	}

	public SystemPropertyConfiguration() {
		super(parseConfigurationPath());
	}

}
