package org.eye;

import org.apache.log4j.Logger;
import org.eye.exceptions.EyeWatcherException;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.WatchEvent;

/**
 * Created by mygordienko.
 * Represent the Path events.
 */
public class EyePathEvent extends EyeEvent {

	private static final Logger logger = Logger.getLogger(EyeEvent.class);
	Path targetPath;

	/**
	 * trying to determine real path - the source of event
	 *
	 * @throws org.eye.exceptions.EyeWatcherException if can't determine the source path
	 */
	EyePathEvent(WatchEvent<?> watchEvent,Path curpath, Path basePath) throws EyeWatcherException {
		super(watchEvent);
		try {
			this.targetPath = basePath.resolve(curpath).toRealPath();
		} catch (IOException e) {
			throw new EyeWatcherException(e);
		}
	}
	@Override
	public String getStringMessage() throws EyeWatcherException {
		return "[" +this.kind +"] "+ (Path)this.targetPath;
	}
}
