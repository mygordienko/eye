package org.eye;

import org.apache.log4j.Logger;
import org.eye.exceptions.EyeWatcherException;

import static java.nio.file.StandardWatchEventKinds.*;

import java.nio.file.WatchEvent;

/**
 * Created by mygordienko.
 * Wrap the event, that was received from {@code WatchService}
 */
public class EyeEvent {

	private static final Logger logger = Logger.getLogger(EyeEvent.class);

	public enum EyeEventKind{
		EYE_ENTRY_CREATE,EYE_ENTRY_MODIFY,EYE_ENTRY_DELETE,EYE_OTHER;
	}

	protected WatchEvent<?>  watchEvent;
	protected EyeEventKind kind;
	protected Object context;

	/**
	 * Decode standard {@code WatchEvent} into {code EyeEventKind}
	 */
	EyeEvent(WatchEvent<?> watchEvent) {
		if (watchEvent.kind() == ENTRY_MODIFY){
			this.kind = EyeEventKind.EYE_ENTRY_MODIFY;
		}else if(watchEvent.kind() == ENTRY_DELETE){
			this.kind = EyeEventKind.EYE_ENTRY_DELETE;
		}else if(watchEvent.kind() == ENTRY_CREATE) {
			this.kind = EyeEventKind.EYE_ENTRY_CREATE;
		}else{
			this.kind = EyeEventKind.EYE_OTHER;
		}
		this.context = watchEvent.context();
		logger.info("Received message context class" + this.context.getClass());
	}
	public Object getContext() {
		return context;
	}

	public EyeEventKind getEventKind(){
		return this.kind;
	}

	public String getStringMessage() throws EyeWatcherException {
		return "[" +this.kind +"] "+ this.getContext();
	}
}
