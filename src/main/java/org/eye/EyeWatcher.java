package org.eye;

import org.apache.log4j.Logger;
import org.eye.exceptions.EyeWatcherException;
import org.eye.util.DaemonThreadFactory;
import org.eye.util.FixedSizeSet;
import org.eye.util.MessageBlockQueue;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static java.nio.file.StandardWatchEventKinds.*;

/**
 * Created by mygordienko.
 * This class provides multithreding watching for multiple paths.
 * It designed with Builder pattern, so to get it ready to use the
 * {@link #setUpWatcher setUpWatcher} should be called;
 */
public class EyeWatcher {

	private static final int DEFAULT_TASKS_PER_THREAD = 3;
	private static final Logger logger = Logger.getLogger(EyeWatcher.class);

	private int taskPerThread = DEFAULT_TASKS_PER_THREAD;
	private Map<EyeTask,ScheduleKey> taskBindTable;
	private List<EyeMission> missions;
	private ExecutorService taskExecutor;
	private MessageBlockQueue<EyeEvent> messageQueue;
	private EyeWathcherStates state = EyeWathcherStates.CREATED;

	private static enum EyeWathcherStates{
		CREATED,READY,RUNNING,CLOSED;
	}

	/**
	 * Created by mygordienko.
	 * This class provides set of tasks for executing(watching) in one thread.
	 */
	private class EyeMission implements Runnable{

		private WatchService watchService;
		private FixedSizeSet<EyeTask> targetTasks;

		EyeMission() throws EyeWatcherException {
			try {
				this.watchService = FileSystems.getDefault().newWatchService();
			} catch (IOException e) {
				throw new EyeWatcherException(e);
			}
			this.targetTasks = new FixedSizeSet<EyeTask>(new HashSet(),EyeWatcher.this.taskPerThread);
			logger.info("Create mission with capacity: " + this.targetTasks.getLimit());
		}

		/**
		 * This class provides set of tasks for executing(watching) in one thread.
		 *
		 * @param task the one task to watch
		 * @return WatchKey the key that accordig to target path
		 */
		public WatchKey addTask(EyeTask task) {
			if (targetTasks.add(task)){
				try {
					return task.getTarget().register(this.watchService,ENTRY_MODIFY,ENTRY_CREATE,ENTRY_DELETE);
				} catch (IOException e) {
					targetTasks.remove(task);
					logger.error("Can't register path: " + e);
					return null;
				}
			}
			return null;
		}
		public void removeTask(EyeTask task){
			this.targetTasks.remove(task);
		}


		@Override
		public void run() {
			WatchKey watchKey;
			while (!Thread.interrupted() && !this.targetTasks.isEmpty()) {
				try {
					watchKey = this.watchService.take();
					/*According to spec, for StandardWatchEventKinds context always be a Path*/
					for(WatchEvent<?> event : watchKey.pollEvents()){
						try {
							Path basePath = (Path)watchKey.watchable();
							Path curPath = (Path)event.context();
							EyeWatcher.this.messageQueue.put(new EyePathEvent(event,curPath,basePath));
						}catch (ClassCastException e){
							logger.info("Event kind: "+event.kind()+" can't create Path event: " + e);
						} catch (EyeWatcherException e) {
							logger.info("Event kind: "+event.kind()+" can't create Path event: " + e);
						}
					}
					watchKey.reset();

				} catch (InterruptedException e) {
					logger.error("Mission "+this+" was interrupted, cause " + e);
					break;
				}
			}
		}
	}

	/**
	 * Created by mygordienko.
	 * The simple class for maintain relation between task and mission,
	 * where it executing.
	 */
	private static class ScheduleKey{
		private WatchKey watchKey;
		private EyeMission mission;

		private ScheduleKey(WatchKey watchKey, EyeMission mission) {
			this.watchKey = watchKey;
			this.mission = mission;
		}
		public WatchKey getWatchKey() {
			return watchKey;
		}
		public EyeMission getMission() {
			return mission;
		}
	}

	/**
	 * Set the max number of tasks to be watching in one thread
	 *
	 * @param taskPerThread max number of tasks to be watching in one thread
	 */
	public void setTaskPerThread(int taskPerThread) {
		if (taskPerThread <= 0){
			this.taskPerThread = DEFAULT_TASKS_PER_THREAD;
		}else{
			this.taskPerThread = taskPerThread;
		}

	}

	/**
	 * Set the target message queue for this watcher
	 *
	 * @param messageQueue the queue to send events
	 */
	public void setMessageQueue(MessageBlockQueue<EyeEvent> messageQueue) {
		this.messageQueue = messageQueue;
	}

	/**
	 * Set up watcher, initialize internal objects, checks conditions
	 *
	 * @throws org.eye.exceptions.EyeWatcherException if any error occured.
	 */
	public void setUpWatcher() throws EyeWatcherException {
		this.taskBindTable = new HashMap<>();
		this.missions = new LinkedList<>();
		this.taskExecutor  = Executors.newCachedThreadPool(new DaemonThreadFactory());
		if (this.messageQueue == null){
			logger.error("Message queue is nul");
			throw new EyeWatcherException("Message queue is null");
		}
		logger.info("Watcher is ready now");
		this.state = EyeWathcherStates.READY;
	}

	/**
	 * Add task to watcher. Checks if there are any mission that can get one
	 * more task. If no one found, then attempts to create new mission.
	 * if watcher in  RUNNING state, then submin task to executor.
	 *
	 * @param task the one task to watch
	 * @throws org.eye.exceptions.EyeWatcherException if task required to create a new mission, and
	 * creation failes for some reason.
	 */
	public void addTask(EyeTask task) throws EyeWatcherException {
		if (!this.taskBindTable.containsKey(task)) {
			/*Attempt to add task to any existing mission*/
			for(EyeMission mission : missions){
				WatchKey key = mission.addTask(task);
				if(key != null){
					taskBindTable.put(task,new ScheduleKey(key,mission));
					logger.info("Task "+task+"has been added to existing mission");
					return;
				}
				logger.warn("Can't add task to mission: " + this);
			}
			/*Trying to create new mission*/
			logger.info("Need to create new mission for Task "+task);
			EyeMission mission = new EyeMission();
			WatchKey key = mission.addTask(task);
			if(key != null){
				missions.add(mission);
				taskBindTable.put(task,new ScheduleKey(key,mission));
				if (this.state == EyeWathcherStates.RUNNING){
					this.taskExecutor.submit(mission);
				}
			}else{
				logger.error("Can't create mission for Task "+task);
			}
		}
	}

	/**
	 * remove task from watcher
	 *
	 * @param task the one task to watch
	 */
	public void removeTask(EyeTask task){
		ScheduleKey scheduleKey = this.taskBindTable.remove(task);
		if (scheduleKey != null){
			scheduleKey.getWatchKey().cancel();
			scheduleKey.getMission().removeTask(task);
		}
	}

	/**
	 * Get number of currently watchable tasks
	 *
	 * @return number of currently watchable tasks
	 */
	public int getNumberOfTasks(){
		return this.taskBindTable.size();
	}

	/**
	 * Get number of threads using by this watcher
	 *
	 * @return number of threads
	 */
	public int getNumberOfMissions(){
		return this.missions.size();
	}

	/**
	 * Starts executing of all tasks.
	 *
	 * @throws org.eye.exceptions.EyeWatcherException if watcher not in READY state.
	 */
	public void startWatch() throws EyeWatcherException {
		switch (this.state){
			case READY:
				if (this.taskBindTable.size() > 0){
					for (ScheduleKey scheduleKey : this.taskBindTable.values()){
						this.taskExecutor.submit(scheduleKey.getMission());
					}
					this.state = EyeWathcherStates.RUNNING;
				}else{
					logger.warn("EyeWatcher has no tasks");
				}
				break;
			case CREATED:
				logger.error("EyeWatcher is not in "+EyeWathcherStates.READY+" state");
				throw new EyeWatcherException("EyeWatcher is not set up!");
			case CLOSED:
				logger.error("EyeWatcher already in "+EyeWathcherStates.CLOSED+" state");
				throw new EyeWatcherException("EyeWatcher has been closed!");
		}
	}

	/**
	 * Stops executing of all tasks. Do not wait for tasks to complete
	 *
	 */
	public void stopWatch() {
		switch (this.state){
			case READY:
				this.taskExecutor.shutdownNow();
				this.state = EyeWathcherStates.READY;
				break;
			case CREATED:
				logger.error("EyeWatcher is not in "+EyeWathcherStates.READY+" state");
			case CLOSED:
				logger.error("EyeWatcher already in "+EyeWathcherStates.CLOSED+" state");
		}
	}
}
