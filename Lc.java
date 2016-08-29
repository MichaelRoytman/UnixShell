import java.util.concurrent.BlockingQueue;

/**
 * A class that represents a lc command object that extends Filter class and
 * that contains main functionality of lc command.
 * @author michaelroytman
 */
public class Lc extends Filter {
	private int count; //line count
	private boolean countReturned; //boolean flag representing whether the 
								   //count has been returned
	
	/**
	 * Constructor that accepts an input and output blocking queue.
	 * @param in input blocking queue 
	 * @param out output blocking queue
	 */
	public Lc(BlockingQueue<Object> in, BlockingQueue<Object> out) {
		super(in, out);
		count = 0;
		countReturned = false;
	}
	
	/**
	 * @override
	 * Override run method for lc command that adds the countReturned condition
	 * to the functionality of the method.
	 */
	public void run() {
        Object o = null;
        
        while(! this.done) {
			
        	//if there is an input queue and if count has not been returned
        	if (in != null && !countReturned) {
	        	// read from input queue, may block
	            try {
					o = in.take();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
        	}

			// allow filter to change message
            o = transform(o); 

            //if there is an output queue and o is not null
            if (out != null && o != null) {
				// forward to output queue
	            try {
					out.put(o);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}    
            }
        }
	}

	/**
	 * @override
	 * Transform method that keeps track of line count and returns it to the 
	 * output queue at the end of input; once count is returned; an EndOfFileMarker
	 * is returned to signal end of output.
	 */
	public Object transform(Object o) {
		//if o is a String, line is incremented
		if (o instanceof String) {
			count++;
			return null;
		}
		// if o is an EndOfFileMarker, signal end of input
		else if (o instanceof EndOfFileMarker) {
			countReturned = true; //count is returned
			return ((Integer)count).toString(); //returns count as an Integer
		}
		//if count has been returned, returns EndOfFileMarker to output queue
		//to signal end of output
		else if (countReturned) {
			this.done = true;
			return new EndOfFileMarker();
		}
		return null;
	}
}