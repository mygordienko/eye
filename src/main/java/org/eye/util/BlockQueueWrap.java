package org.eye.util;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by mygordienko on 09.06.2014.
 */
public class BlockQueueWrap<T> implements MessageBlockQueue<T> {

	private final BlockingQueue<T> queue;
	private final int MAX_CAPACITY;

	public BlockQueueWrap(int maxCapacity) {
		this.MAX_CAPACITY = maxCapacity;
		queue = new LinkedBlockingQueue<>(MAX_CAPACITY);
	}

	public void put(T o) throws InterruptedException {
		this.queue.put(o);
	}

	public T get() throws InterruptedException {
		T retElem = this.queue.take();
		return retElem;
	}

	@Override
	public int getMaxCapacity() {
		return MAX_CAPACITY;
	}
}
