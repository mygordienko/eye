package org.eye.util;

/**
 * Created by mygordienko on 09.06.2014.
 */
public interface MessageBlockQueue<T> {
	public   void put(T o) throws InterruptedException;
	public  T get() throws InterruptedException;
	public int getMaxCapacity();
}
