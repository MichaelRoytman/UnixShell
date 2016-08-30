/**
 * A class that represents the current working directory of the shell.
 * @author michaelroytman
 */
public class CurrentDir {
	private String currentDir; //String representing current working directory path
		
	/**
	 * Constructor; sets initial directory to where shell class is located.
	 */
	public CurrentDir() {
		currentDir = System.getProperty("user.dir"); 
	}
	
	/**
	 * Getter method for current working directory.
	 * @return
	 */
	public String getDir() {
		return currentDir;
	}

	/**
	 * Setter method for current working directory.
	 * @param destination destination of new working directory
	 * @param append if append is true, destination is added on to current working
	 * directory; if append is false, current working directory is set to destination.
	 */
	public void setDir(String destination, Boolean append) {
		if (append) {
			currentDir = currentDir + destination;
		}
		else {
			currentDir = destination;
		}
	}
}
