package com.zcwyx.tbs.server.impl;

import java.util.concurrent.ThreadFactory;

public class TbsServiceThreadFactory implements ThreadFactory{

	@Override
	public Thread newThread(Runnable r) {
		return new Thread(r);
	}

}
