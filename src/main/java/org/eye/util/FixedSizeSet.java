package org.eye.util;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * Created by mygordienko on 04.10.2014.
 */
public class FixedSizeSet<T> implements SetEnhancer<T> {
	private Set<T> elements;
	private int limit;

	public int getLimit() {
		return limit;
	}

	public FixedSizeSet(Set<T> originSet, int limit) {
		int cnt = 0;
		if (originSet.size() > limit){
			for(T elem : originSet){
				cnt++;
				if (cnt > limit){
					elements.remove(elem);
				}
			}
		}
		this.elements = originSet;
		this.limit = limit;
	}

	@Override
	public int size() {
		return elements.size();
	}

	@Override
	public boolean isEmpty() {
		return elements.isEmpty();
	}

	@Override
	public boolean contains(Object o) {
		return elements.contains(o);
	}

	@Override
	public Iterator<T> iterator() {
		return elements.iterator();
	}

	@Override
	public Object[] toArray() {
		return elements.toArray();
	}

	@Override
	public <T1> T1[] toArray(T1[] a) {
		return elements.toArray(a);
	}

	public boolean add(T t) {
		if (elements.size() < this.limit){
			return elements.add(t);
		}
		return false;
	}

	@Override
	public boolean remove(Object o) {
		return elements.remove(o);
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		return elements.containsAll(c);
	}

	public boolean addAll(Collection<? extends T> c) {
		return elements.addAll(c);
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		return elements.retainAll(c);
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		return elements.removeAll(c);
	}

	@Override
	public void clear() {
		elements.clear();
	}

	@Override
	public boolean equals(Object o) {
		return elements.equals(o);
	}

	@Override
	public int hashCode() {
		return elements.hashCode();
	}

	@Override
	public Spliterator<T> spliterator() {
		return elements.spliterator();
	}

	public boolean removeIf(Predicate<? super T> filter) {
		return elements.removeIf(filter);
	}

	@Override
	public Stream<T> stream() {
		return elements.stream();
	}

	@Override
	public Stream<T> parallelStream() {
		return elements.parallelStream();
	}

	public void forEach(Consumer<? super T> action) {
		elements.forEach(action);
	}
}
