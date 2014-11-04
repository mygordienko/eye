package org.eye;

import junit.framework.TestCase;
import org.eye.exceptions.EyeWatcherException;
import org.eye.util.BlockQueueWrap;
import org.eye.util.MessageBlockQueue;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class EyeWathcherTest {


	private static EyeWatcher eyeWatcher;
	private static MessageBlockQueue<EyeEvent> messages;

	@Before
	public void setUp() throws Exception {
		messages = new BlockQueueWrap<>(10);
		eyeWatcher = new EyeWatcher();
		eyeWatcher.setMessageQueue(messages);
		eyeWatcher.setTaskPerThread(2);
		eyeWatcher.setUpWatcher();
	}

	@Test(expected = EyeWatcherException.class)
	public void addingNotExistPathTest() throws EyeWatcherException {
		Path path = Paths.get("E:\\not_exists_path");
		eyeWatcher.addTask(new EyeTask(path));

	}

	@Test
	public void addingTheSameTaskTest() throws EyeWatcherException {
		eyeWatcher.addTask(new EyeTask(Paths.get("E:\\temp")));
		eyeWatcher.addTask(new EyeTask(Paths.get("E:\\downloads")));
		eyeWatcher.addTask(new EyeTask(Paths.get("E:\\temp")));
		assertEquals(2,eyeWatcher.getNumberOfTasks());
	}

	@Test
	public void missionProduceTest() throws EyeWatcherException {

		List<EyeTask> tasks = new LinkedList<>();
		tasks.add(new EyeTask(Paths.get("E:\\temp")));
		tasks.add(new EyeTask(Paths.get("E:\\downloads")));
		tasks.add(new EyeTask(Paths.get("E:\\programming")));

		for(EyeTask task : tasks){
			eyeWatcher.addTask(task);
		}
		assertEquals(2,eyeWatcher.getNumberOfMissions());
		eyeWatcher.removeTask(tasks.get(0));
		assertEquals(2,eyeWatcher.getNumberOfMissions());
	}

	@After
	public void tearDown() throws Exception {

	}
}