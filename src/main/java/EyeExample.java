import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import org.apache.log4j.Logger;
import org.eye.EyeEvent;
import org.eye.EyeTask;
import org.eye.EyeWatcher;
import org.eye.exceptions.EyeWatcherException;
import org.eye.provider.InjectProvider;
import org.eye.util.MessageBlockQueue;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Queue;

/**
 * Created by mygordienko on 04.10.2014.
 */
public class EyeExample {

	private static final Logger logger = Logger.getLogger(EyeExample.class);

	public static void main(String[] args) throws Exception {

		Injector injector = Guice.createInjector(new InjectProvider());

		EyeWatcher watcher = new EyeWatcher();

		// using dependency injection
		MessageBlockQueue<EyeEvent> messages = injector.getInstance(MessageBlockQueue.class);
		Queue<String> argsQueue = injector.getInstance(new Key<Queue<String>>() {});

		argsQueue.addAll(Arrays.asList(args));

		if(argsQueue.size() < 2){
			throw new Exception("Illegal number of arguments");
		}

		int messagesToRead = Integer.parseInt(argsQueue.poll());

		try {
			watcher.setTaskPerThread(2);
			watcher.setMessageQueue(messages);
			watcher.setUpWatcher();

			for(String path : argsQueue){
				try {
					watcher.addTask(new EyeTask(Paths.get(path).toRealPath()));
				} catch (IOException e) {
					logger.error("Can't create path: "+e);
				}
			}
			watcher.startWatch();

			while(messagesToRead > 0){
				System.out.println("[" + messagesToRead + "] " + messages.get().getStringMessage());
				messagesToRead--;
			}
		} catch (EyeWatcherException | InterruptedException e) {
			logger.error(e);
		} finally {
			watcher.stopWatch();
		}

	}
}
