package com.zcwyx.tbs.server.impl;

import com.zcwyx.tbs.server.IStatisticsProvider;

public class StatisticsProviderFactory {
	private static StatisticsProviderFactory instance = new StatisticsProviderFactory();
	
	public static StatisticsProviderFactory getInstance(){
		return instance;
	}
	
	private IStatisticsProvider statisticProvider;
	
	private StatisticsProviderFactory(){
		statisticProvider = new StatisticsProviderImpl();
	}
	
	public IStatisticsProvider getDefaultStatisticsProvider(){
		return statisticProvider;
	}
}
