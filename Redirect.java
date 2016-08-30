import java.io.*;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * A class that represents a redirect command object that extends Filter class and
 * that contains main functionality of the redirect command. 
 * @author michaelroytman
 */
public class Redirect extends Filter {
	private FileWriter writeFile;
	private Writer printFile;
	private File file;
	
	/**
	 * Constructor that accepts an input and output queue and a current directory
	 * object.
	 * @param in input blocking queue
	 * @param destination destination where to store redirected output
	 * @param dir current working directory
	 */
	public Redirect(LinkedBlockingQueue<Object> in, String destination, CurrentDir dir) {
		super(in, null); //super constructor; no output queue
		file = new File(destination); //new file object at destination
		
		//creates a file writer in the specified location
		try {
			writeFile = new FileWriter(dir.getDir() + System.getProperty("file.separator") + file);
		} 
		catch (IOException e) {
			e.printStackTrace();
		}
		
		//creates new buffered writer from file writer
		printFile = new BufferedWriter(writeFile);
	}
	
	/**
	 * @override
	 * Transform method that writes output of one command to destination,
	 * line by line.
	 */
	public Object transform(Object o) {
		//if o is a String
		if (o instanceof String) {
			//writes line of input to the file
			try {
				printFile.write(o.toString() + "\n");
				printFile.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}	
		}
		//if o is an EndOfFileMarker, signaling end of input
		else if (o instanceof EndOfFileMarker) {
			this.done = true; //end of loop

			try {
				printFile.close(); //closes buffered writer
			}
			catch (IOException e) {
				e.printStackTrace();
			}
		}	
		return null;
	}
}
