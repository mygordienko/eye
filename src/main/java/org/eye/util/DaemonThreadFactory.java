package org.eye.util;

import java.util.concurrent.ThreadFactory;

/**
 * Created by mygordienko on 05.10.2014.
 */
public class DaemonThreadFactory implements ThreadFactory {
	@Override
	public Thread newThread(Runnable r) {
		Thread thread = new Thread(r);
		thread.setDaemon(true);
		return thread;
	}
}
