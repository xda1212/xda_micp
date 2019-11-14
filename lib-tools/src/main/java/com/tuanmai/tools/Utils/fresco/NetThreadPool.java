package com.tuanmai.tools.Utils.fresco;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 网络请求线程池
 */
public final class NetThreadPool {

	private static NetThreadPool instance;

	public static NetThreadPool getInstance() {
		if (instance == null) {
			synchronized (NetThreadPool.class) {
				if (instance == null) {
					instance = new NetThreadPool();
				}
			}
		}
		return instance;
	}

	private NetThreadPool() {
		if (executor == null) {
			executor = Creator.newService();
		}
	}

	// ---------------------------------------------------------------------------------

	private ExecutorService executor;

	public ExecutorService createService() {
		if (executor == null || executor.isShutdown()) {
			executor = Creator.newService();
		}
		return executor;
	}

	private static class Creator {
		private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();
		private static final int CORE_POOL_SIZE = CPU_COUNT + 1;
		private static final int MAXIMUM_POOL_SIZE = CPU_COUNT * 2 + 1;// IO密集型
		//private static final int MAXIMUM_POOL_SIZE_ = CPU_COUNT + 1;// CPU密集型
		private static final int KEEP_ALIVE = 1;

		private static ExecutorService newService() {
			ThreadFactory threadFactory = new ThreadFactory() {

				private final AtomicInteger count = new AtomicInteger(1);

				public Thread newThread(Runnable runnable) {
					return new Thread(runnable, "NetThreadPool-" + count.getAndIncrement());
				}
			};
			BlockingQueue<Runnable> queue = new LinkedBlockingQueue<>(128);
			ExecutorService executorService = new ThreadPoolExecutor(CORE_POOL_SIZE,
					MAXIMUM_POOL_SIZE,
					KEEP_ALIVE,
					TimeUnit.SECONDS,
					queue,
					threadFactory) {

				@Override
				public void execute(Runnable command) {
					super.execute(command);
				}
			};
			return executorService;
		}
	}
}