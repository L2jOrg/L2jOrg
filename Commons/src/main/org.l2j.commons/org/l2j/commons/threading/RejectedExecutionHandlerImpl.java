package org.l2j.commons.threading;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

public final class RejectedExecutionHandlerImpl implements RejectedExecutionHandler, UncaughtExceptionHandler
{
	private static final Logger LOGGER = LoggerFactory.getLogger(RejectedExecutionHandlerImpl.class);
	
	@Override
	public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
		if (executor.isShutdown()) {
			return;
		}
		
		LOGGER.warn("{} from {} ", r, executor);
	}

	@Override
	public void uncaughtException(Thread t, Throwable e) {
		LOGGER.warn("Exception on Thread {}", t, e);
	}
}
