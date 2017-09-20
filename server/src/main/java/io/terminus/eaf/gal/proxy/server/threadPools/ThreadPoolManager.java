package io.terminus.eaf.gal.proxy.server.threadPools;

import io.terminus.eaf.gal.proxy.server.constants.Properties;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 线程池管理对象
 * 
 * @author qq
 *
 */
public class ThreadPoolManager {
	private ThreadPoolManager() {

	}

	/**
	 * 线程池(以后重构，属性)
	 * 
	 * 创建一个线程池,它可以执行Runnable/Callabel对象所代表的线程
	 */
	private static ExecutorService executorService = Executors.newFixedThreadPool(Properties.nThreads);

	/**
	 * 提交线程
	 * 
	 * @param thread
	 *            要执行的线程
	 */
	public static void execute(Thread thread) {
		executorService.execute(thread);
	}

}
