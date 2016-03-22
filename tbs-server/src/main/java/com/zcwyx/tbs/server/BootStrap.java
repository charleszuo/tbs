package com.zcwyx.tbs.server;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.xml.DOMConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zcwyx.tbs.registry.Node;
import com.zcwyx.tbs.registry.NodeFactory;
import com.zcwyx.tbs.registry.Service;
import com.zcwyx.tbs.registry.TbsRegistry;
import com.zcwyx.tbs.registry.TbsRegistryFactory;
import com.zcwyx.tbs.registry.util.IPAddressUtil;
import com.zcwyx.tbs.server.conf.Configuration;
import com.zcwyx.tbs.server.conf.ConfigurationFactory;
import com.zcwyx.tbs.server.impl.ServiceBuilderImpl;

public class BootStrap {
	static final Logger Log = LoggerFactory
			.getLogger(BootStrap.class);
	static final int MONITOR_TIME_INTERVAL = 100;

	public static final String TBS_LOG4J_CONFIG = "tbs.log4j.config";

	private List<Thread> threadList;
	private List<TbsService> serviceList;
	private TbsRegistryFactory tbsRegistryFactory;

	public BootStrap() {
		threadList = new ArrayList<Thread>();
		serviceList = new ArrayList<TbsService>();
		tbsRegistryFactory = TbsRegistryFactory.getInstance();
	}

	public void startServices(String configPath) {
		Configuration configuration = ConfigurationFactory
				.newConfiguration(configPath);
		for (TbsServiceConfig serviceConfig : configuration
				.getServiceConfigList()) {
			IServiceBuilder serviceBuilder = new ServiceBuilderImpl();
			TbsService service = serviceBuilder.build(serviceConfig);
			Thread curThread = new Thread(service, serviceConfig.getServiceId());
			threadList.add(curThread);
			serviceList.add(service);
			curThread.start();
			service.setThreadList(threadList);
			service.setServiceList(serviceList);

			TbsRegistry registry = tbsRegistryFactory.getDefaultTbsRegistry();
			String localIP = IPAddressUtil.getLocalhostIp();

			Node node = NodeFactory.getNode(localIP, serviceConfig.getPort(),
					true, true);
			registry.registerNode(new Service(serviceConfig.getServiceId(),
					serviceConfig.getServiceVersion(), null),
					serviceConfig.getServiceShard(), node);
			service.setNode(node);
			service.setTbsRegistry(registry);
			service.setConfigPath(configPath);
		}
	}

	public boolean foundInvalidService() {
		boolean invalid = false;
		for (Thread t : threadList) {
			if (!t.isAlive()) {
				invalid = true;
				break;
			}
		}
		return invalid;
	}

	public void haltAll() {
		for (TbsService service : serviceList) {
			if (service.getServer().isServing()) {
				service.stopServer();
			}
			service.getServerTransport().close();
		}
		try {
			Thread.sleep(MONITOR_TIME_INTERVAL);
		} catch (InterruptedException e) {
			Log.error("Thread interrupted: " + e.getMessage());
		}
	}

	public static void main(String[] args) {
		if (args.length != 1) {
			System.out.println("Must set config file path");
			System.exit(1);
		}

		Log.error("test log config");
		// 加载tbs框架的log4j配置
		String serverConfig = System.getProperty(TBS_LOG4J_CONFIG);
		if (serverConfig != null) {
		     DOMConfigurator.configure(serverConfig);
		}

		
		String configPath = args[0];
		BootStrap bootstrap = new BootStrap();
		try {
			bootstrap.startServices(configPath);
			while (true) {
				Thread.currentThread().join(MONITOR_TIME_INTERVAL);
				if (bootstrap.foundInvalidService()) {
					break;
				}
			}
		} catch (Exception e) {
			Log.error("Error found " + e.getMessage());
		} finally {
			bootstrap.haltAll();
			Log.error("Some services are failed. Stop the server");
			System.exit(-1);
		}
	}

}
