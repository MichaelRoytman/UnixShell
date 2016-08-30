import java.util.concurrent.BlockingQueue;

/**
 * A class that represents a history command object that extends Filter class and
 * that contains main functionality of history command. 
 * @author michaelroytman
 */
public class History extends Filter {
	private int counter;
	private CommandHistory history;
	
	/**
	 * Constructor that accepts an output queue and a CommandHistory object.
	 * @param out output blocking queue
	 * @param history CommandHistory object containing user's history
	 */
	public History(BlockingQueue<Object> out, CommandHistory history) {
		super(null, out); //super constructor; no input queue
		counter = 0;
		this.history = history;
	}
	
	/**
	 * @override
	 * Transform method that sends out each line of history commands to
	 * output queue.
	 */
	public Object transform(Object o) {
		//if there are still lines of history to be sent to output
		if (counter < history.getHistory().size()) {
			String command = history.getHistory().get(counter);
			counter++;
			return command;
		}
		else {
			this.done = true; //end of loop
			return new EndOfFileMarker(); //signals end of output
		}	
	}
}
