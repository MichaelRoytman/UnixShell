import java.util.concurrent.*;

/*
 * This is the Filter class that all command implementations extend.
 */
//this is an abstract command
public abstract class Filter extends Thread {
	protected BlockingQueue<Object> in; //input queue
	protected BlockingQueue<Object> out; //output queue
	protected volatile boolean done; 

	/*
	 * The following flag is for Part 4.
	 */
	protected volatile boolean killed;

	public Filter (BlockingQueue<Object> in, BlockingQueue<Object> out) {
		this.in = in;
		this.out = out;
		this.done = false;
		this.killed = false;
	}

	/*
	 * This is for Part 4.
	 */
	public void cmdKill() {
		this.killed = true;
	}
	/*
	 * This method need to be overridden.
	 * @see java.lang.Thread#run()
	 */
	public void run() {
        Object o = null;
     
        while(! this.done) {
			
        	//if there is an input queue
        	if (in != null) {
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

	/*
	 * This method might need to be overridden.
	 */
	public abstract Object transform(Object o);
}