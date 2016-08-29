import java.util.concurrent.BlockingQueue;
import java.io.*;

/**
 * A class that represents a ls command object that extends Filter class and
 * that contains main functionality of ls command.
 * @author michaelroytman
 */
public class Ls extends Filter {
	private int counter; //count of how many files have been sent to output queue
	File location; //current working directory
	String[] filesAndDirs; //an array of Strings representing the files and directories in the
						   //in the current working directory
	
	/**
	 * Constructor for Ls object, which takes input and output BlockingQueues, and a String representing the current directory
	 * @param in input BlockingQueue from which Ls takes input
	 * @param out output BlockingQueue to which Ls sends output
	 * @param currentDir String representing the current directory
	 */
	public Ls(BlockingQueue<Object> out, CurrentDir dir) {
		super(null, out); //calls super constructor of Filter class; no input queue
		counter = 0;
		location = new File(dir.getDir()); //creates a file object in the current working directory
		filesAndDirs = location.list(); //creates an array of Strings representing the files and directories in location
	}
	
	/**
	 * @overrun
	 * Transform method that sends String representation of files and 
	 * directories in the location one by one; returns an EndOfFileMarker
	 * to signal end of output otherwise.
	 * @return Object o is a file in the current directory
	 */
	public Object transform(Object o) {	
		//if there are still files/directories not sent to output queue
		if (counter < filesAndDirs.length) {
			o = filesAndDirs[counter];
			counter++;
			return o;
		}
		//all files/directories sent to output queue
		else {
			this.done = true; //end of loop
			return new EndOfFileMarker(); //signals end of output
		}
	}
}
