package org.eye.util;

import com.google.inject.Inject;

import javax.inject.Named;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by mygordienko on 09.06.2014.
 */
public class BlockQueueWrap<T> implements MessageBlockQueue<T> {

	private final BlockingQueue<T> queue;
	private final int MAX_CAPACITY;

	@Inject
	public BlockQueueWrap(@Named( "COUNT" )int maxCapacity) {
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
