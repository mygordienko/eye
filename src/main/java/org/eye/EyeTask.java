package org.eye;

import org.eye.exceptions.EyeWatcherException;

import java.io.IOException;
import java.nio.file.Path;

/**
 * Created by mygordienko.
 * This class represents the one object(path) to watch
 */
public class EyeTask{
	private Path target;

	public EyeTask(Path target) throws EyeWatcherException {
		try {
			this.target = target.toRealPath();
		} catch (IOException e) {
			throw new EyeWatcherException(e);
		}
	}

	public Path getTarget() {
		return target;
	}

	@Override
	public int hashCode(){
		return this.target.hashCode();
	}

	@Override
	public boolean equals(Object o){
		if (o instanceof EyeTask){
			return ((EyeTask)o).getTarget().equals(this.target);
		}
		return false;
	}

	public String toString(){
		return this.target.toString();
	}
}