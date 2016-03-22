package com.zcwyx.tbs.client;

import com.zcwyx.tbs.client.transport.TbsTransport;

public interface ServiceRouter {
	public TbsTransport routeService(String serviceId, String version, String shard, long timeout) throws Exception;
	
	public void returnConnection(TbsTransport transport) throws Exception;
	
	public void serviceException(String serviceId, String version, Throwable e, TbsTransport transport);
}
