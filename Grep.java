import java.util.concurrent.BlockingQueue;

/**
 * A class that represents a grep command object that extends Filter class and
 * that contains main functionality of grep command.
 * @author michaelroytman
 */
public class Grep extends Filter {
	private String pattern; //pattern to match by grep command
	
	/**
	 * Constructor that accepts an input and output blocking queue and a pattern
	 * as a String
	 * @param in input blocking queue
	 * @param out output blocking queue
	 * @param pattern String against which grep matches
	 */
	public Grep(BlockingQueue<Object> in, BlockingQueue<Object> out, String pattern) {
		super(in, out); //super constructor
		this.pattern = pattern;
	}
	
	/**
	 * @override
	 * Transform method that sends matches Strings from the input queue to
	 * the pattern String and sends them out to the output queue if they match.
	 */
	public Object transform(Object o) {
		Object input = o;
		
		//if not the end of input
		if (!(input instanceof EndOfFileMarker)) {
			//if input contains the pattern string
			if (((String) input).contains(pattern)) {
				return input; //returns String to output
			}
			//if input does not contain pattern string
			else {
				return null;
			}
		}
		//if end of input signaled with EndOfFileMarker
		else {
			this.done = true; //loop done
			return o; //returns EndOfFileMarker to output queue
		}
	}
}
