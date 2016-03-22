package com.zcwyx.tbs.client.definition;

import java.lang.reflect.Method;

public class MethodDefinition {
	private Method method;
	
	public MethodDefinition(Method method){
		this.method = method;
	}

	public Method getMethod() {
		return method;
	}

	public void setMethod(Method method) {
		this.method = method;
	}
	
	
}
