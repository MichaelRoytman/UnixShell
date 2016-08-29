import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.concurrent.BlockingQueue;

/**
 * A class that represents a cat command object that extends Filter class and
 * that contains main functionality of cat command. 
 * @author michaelroytman
 */
public class Cat extends Filter{
	private List<String> files; //list of files to be concatenated
	int fileCount; //number of files in the list
	boolean fileTraversing; //boolean flag representing whether a file is currently in the process of being accessed
	File currentFile; //file currently being accessed
	Scanner currentFileScan; //Scanner of current file
	private CurrentDir dir; //current directory
	
	
	/**
	 * Cat class constructor
	 * @param in input BlockingQueue
	 * @param out output BlockingQueue
	 * @param files list of files to be concatenated
	 */
	public Cat(BlockingQueue<Object> out, List<String> files, CurrentDir dir) {
		super(null, out); //super constructor; cat has no input queue
		this.files = files;
		fileCount = 0;
		fileTraversing = false;
		this.dir = dir; //sets current directory object
	}
	
	/**
	 * @override
	 * Transform method that sends contents of file(s) line by line to output queue
	 */
	public Object transform(Object o) {
		//if there are still files to access
		if (fileCount < files.size()) {		
			
			String line = null;
			
			if (fileTraversing == false) {
				//file object representing the file to be concatenated
				currentFile = new File(dir.getDir() + System.getProperty("file.separator") + files.get(fileCount));
				
				try {
					currentFileScan = new Scanner(currentFile); //Scanner for file
				} 
				catch (FileNotFoundException e) {
					e.printStackTrace();
				}
				fileTraversing = true; //file is being accessed
			}	
			//if file is being accessed
			else {
				//if files still have unread lines
				if (currentFileScan.hasNextLine()) {
					line = currentFileScan.nextLine(); //next line of file
				}
				else {
					fileTraversing = false; //file has been traversed and is no longer accessed
					fileCount++; //moves onto next file
				}
			}
			return line; //returns line to be put into output queue
		}
		//if there are no more files to access
		else {
			this.done = true; //signals end of output
			currentFileScan.close(); //closes Scanner
			return new EndOfFileMarker(); //returns EndOfFileMarker
		}
	}
}
