import java.io.*;

/**
 * A class that represents a cd command object that extends Filter class and
 * that contains main functionality of cd command. 
 * @author michaelroytman
 */
public class Cd extends Filter {
	private CurrentDir dir; //current directory object
	private String destination; //destination of directory
	
	public Cd(String destination, CurrentDir dir) {
		super(null, null); //super constructor; no input or output queues
		this.dir = dir;
		this.destination = destination;
	}
	
	/**
	 * @override
	 * Transform method that sets the current directory object to new path.
	 */
	public Object transform(Object o) {
		//moves current directory to parent
		if (destination.equals("..")) {		
			File file = new File(dir.getDir()); //file representing path of current directory
			String fileString;
			fileString = file.getParent(); //parent of current directory 
			dir.setDir(fileString, false); //sets current directory to new directory
		}
		//does nothing
		else if (destination.equals(".")) {
			
		}
		//if an actual destination directory is specified
		else {
			//appends current directory to include new directory
			dir.setDir(System.getProperty("file.separator") + destination, true);
		}
	
		this.done = true; //loop done
		return null; //no output queue; need to return something
	}
}
