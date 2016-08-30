import java.util.concurrent.BlockingQueue;

/**
 * A class that represents a shell sink, which functions as an implicit
 * object that sends input to the console.
 * @author michaelroytman
 */
public class ShellSink extends Filter {

	/**
	 * Constructor that accepts an input queue.
	 * @param in input blocking queue
	 */
	public ShellSink(BlockingQueue<Object> in) {
		super(in, null); //super constructor, no output queue
	}
	
	/**
	 * @override
	 * Transform method that prints input, line by line.
	 */
	public Object transform(Object o) {
		//if o is a String, prints o to the console.
		if (o instanceof String) {
			System.out.println(o);
		}
		//if o is an EndOfFileMarker, signaling end of input
		else if (o instanceof EndOfFileMarker) {
			this.done = true; //end of loop
		}	
		return o;
	}
}
