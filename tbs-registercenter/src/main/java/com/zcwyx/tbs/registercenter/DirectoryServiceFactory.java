package com.zcwyx.tbs.registercenter;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

import com.zcwyx.tbs.registercenter.client.Consumer;
import com.zcwyx.tbs.registercenter.server.Producer;

public class DirectoryServiceFactory {

	private static ConcurrentHashMap<RegisterCenterConfiguration, Consumer> consumerCache = new ConcurrentHashMap<RegisterCenterConfiguration, Consumer>();

	private static ConcurrentHashMap<RegisterCenterConfiguration, Producer> producerCache = new ConcurrentHashMap<RegisterCenterConfiguration, Producer>();

	private static final int CONSUMER_TIMEOUT = 20 * 1000;

	private static final int PRODUCER_TIMEOUT = 5000;

	public static Consumer getConsumer(RegisterCenterConfiguration config) throws IOException {
		Consumer consumer = consumerCache.get(config);

		if (consumer == null) {
			synchronized (config) {
				consumer = consumerCache.get(config);
				if (consumer == null) {
					consumer = new Consumer(config.getCluster(),
							CONSUMER_TIMEOUT);
					consumer.addAuthInfo(config.getClientUsername(), config.getClientPassword());
					consumer.setRoot(config.getRoot());
					consumerCache.putIfAbsent(config, consumer);
				}
			}
		}
		return consumer;
	}

	public static Producer getProducer(RegisterCenterConfiguration config) throws IOException {
		Producer producer = producerCache.get(config);

		if (producer == null) {
			synchronized (config) {
				producer = producerCache.get(config);
				if (producer == null) {
					producer = new Producer(config.getCluster(),
							PRODUCER_TIMEOUT);
					producer.addAuthInfo(config.getServerUsername(), config.getServerPassword());
					producer.setRoot(config.getRoot());
					producerCache.putIfAbsent(config, producer);
				}
			}
		}
		return producer;
	}
}
