package org.eye.provider;

import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Names;
import org.eye.util.BlockQueueWrap;
import org.eye.util.MessageBlockQueue;

import java.util.ArrayDeque;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Defines injection bindings.
 */
public class InjectProvider extends AbstractModule {

	@Override
	protected void configure() {

		bind(MessageBlockQueue.class).to(BlockQueueWrap.class);

		// bind different containers for different type parameters
		bind(new TypeLiteral<Queue<String>>() {}).to(new TypeLiteral<LinkedList<String>>(){});
		bind(new TypeLiteral<Queue<Integer>>() {}).to(new TypeLiteral<ArrayDeque<Integer>>(){});

		bindConstant().annotatedWith(Names.named("COUNT")).to(10);
	}

}
