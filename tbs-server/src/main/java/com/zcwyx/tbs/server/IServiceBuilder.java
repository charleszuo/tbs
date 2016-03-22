package com.zcwyx.tbs.server;


/**
 * 根据Service配置信息构建Service实例
 * @author Charles
 *
 */
public interface IServiceBuilder {

	/**
	 * @param serviceConfig
	 * @return 
	 */
	public TbsService build(TbsServiceConfig serviceConfig);
}
