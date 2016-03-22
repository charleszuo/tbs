package com.zcwyx.tbs.client.util;


public class ClassUtils {
	public static ClassLoader getDefaultClassLoader(){
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		
		if(loader == null){
			loader = ClassUtils.class.getClassLoader();
		}
		return loader;
	}
}
