package com.zcwyx.tbs.client.impl;

import java.lang.reflect.InvocationHandler;

import com.zcwyx.tbs.client.ServiceRouter;
import com.zcwyx.tbs.client.definition.ClassDefinition;

public class DefaultServiceFactory extends AbstractServiceFactory{

	public DefaultServiceFactory(ServiceRouter serviceRouter) {
		super(serviceRouter);
	}

	@Override
	protected InvocationHandler createInvocationHandler(
			ClassDefinition classDefinition, long timeout) {
		// TODO Auto-generated method stub
		return new ServiceInvocationHandler(getServiceRouter(), classDefinition, timeout);
	}

}
