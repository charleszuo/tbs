package com.zcwyx.tbs.client.definition;

import java.lang.reflect.Constructor;

import org.apache.thrift.protocol.TProtocol;

import com.zcwyx.tbs.annotation.TbsService;

public class ClassDefinition {
	private String serviceId;
	private String version;
	private Class<?> serviceClientClass;
	private Constructor<?> serviceClientConstructor;
	
	public ClassDefinition(Class<?> serviceInterface) throws Exception{
		String clientClassName = resolveClientClassName(serviceInterface);
		this.serviceClientClass = Class.forName(clientClassName);
		serviceClientConstructor = serviceClientClass.getConstructor(TProtocol.class);
		resolveServiceId(serviceInterface);
	}
	
	private String resolveClientClassName(Class<?> serviceClass){
		String packageName = serviceClass.getPackage().getName();
		String simpleClassName = serviceClass.getSimpleName().substring(1);
		
		return new StringBuilder().append(packageName).append(".").append(simpleClassName).append("$Client").toString();
	}
	
	private void resolveServiceId(Class<?> serviceInterface){
		TbsService tbsService = serviceInterface.getAnnotation(TbsService.class);
		this.serviceId = tbsService.value() != null? tbsService.value().trim(): "";
		this.version = tbsService.version() != null? tbsService.version().trim() : "";
	}

	public String getServiceId() {
		return serviceId;
	}

	public void setServiceId(String serviceId) {
		this.serviceId = serviceId;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public Class<?> getServiceClientClass() {
		return serviceClientClass;
	}

	public void setServiceClientClass(Class<?> serviceClientClass) {
		this.serviceClientClass = serviceClientClass;
	}

	public Constructor<?> getServiceClientConstructor() {
		return serviceClientConstructor;
	}

	public void setServiceClientConstructor(Constructor<?> serviceClientConstructor) {
		this.serviceClientConstructor = serviceClientConstructor;
	}
	
	
	
}
