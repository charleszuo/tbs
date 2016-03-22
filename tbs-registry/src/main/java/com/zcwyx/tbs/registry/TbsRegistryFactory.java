package com.zcwyx.tbs.registry;

import com.zcwyx.tbs.registercenter.DirectoryServiceRegistryFactory;
import com.zcwyx.tbs.registry.impl.TrcTbsRegistry;

public class TbsRegistryFactory {
	private static TbsRegistryFactory instance = new TbsRegistryFactory();
	
	public static TbsRegistryFactory getInstance(){
		return instance;
	}
	
	private TbsRegistry tbsRegistry;
	
	private TbsRegistryFactory(){
		tbsRegistry = new TrcTbsRegistry(new DirectoryServiceRegistryFactory());
	}
	
	public TbsRegistry getDefaultTbsRegistry(){
		return tbsRegistry;
	}
}
