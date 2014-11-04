package org.eye.util;

import java.util.LinkedList;

/**
 * Created by mygordienko on 29.05.2014
 */
public class SimpleMessageBlockQueue<T> implements MessageBlockQueue<T> {
	private final LinkedList<T> queue = new LinkedList<>();
	private final int MAX_CAPACITY;

	public SimpleMessageBlockQueue(int maxCapacity) {
		this.MAX_CAPACITY = maxCapacity;
	}

	public synchronized void put(T o) throws InterruptedException {
		while(queue.size() >= MAX_CAPACITY){
			this.wait();
		}
		this.queue.add(o);
		this.notifyAll();
	}

	public synchronized T get() throws InterruptedException {
		while(queue.size() == 0){
			this.wait();
		}
		T retElem = this.queue.poll();
		this.notifyAll();
		return retElem;
	}

	@Override
	public int getMaxCapacity() {
		return MAX_CAPACITY;
	}
}
