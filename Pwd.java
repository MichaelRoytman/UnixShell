import java.util.concurrent.BlockingQueue;

/**
 * A class that represents a pwd command object that extends Filter class and
 * that contains main functionality of pwd command. 
 * @author michaelroytman
 */
public class Pwd extends Filter {
	private CurrentDir dir; //current directory object
	private boolean pwdReturned = false; //boolean flag of whether directory was returned
	
	/**
	 * Constructor that accepts an output queue and the current directory
	 * object.
	 * @param out output BlockingQueue
	 * @param dir current directory object
	 */
	public Pwd(BlockingQueue<Object> out, CurrentDir dir) {
		super(null, out); //super constructor; no input queue
		this.dir = dir;
	}
	
	/**
	 * Transform method that alters the string in the manner relevant to the
	 * command; pwd just returns the current working directory with no
	 * change to o or an EndOfFileMarker
	 * @return o is the transformed String	
	 */
	public Object transform(Object o) {
		//if directory not yet returned
		if (!pwdReturned) {
			pwdReturned = true;
			return dir.getDir(); //returns directory
		}
		//if directory already returned
		else {
			this.done = true; //loop done
			return new EndOfFileMarker(); //signal end of output
		}
	}
}
