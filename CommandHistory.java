import java.util.*;

/**
 * A class that represents the history of user input from MyShell.
 * @author michaelroytman
 */
public class CommandHistory {
	private List<String> commandHistory; //list representing commands previously inputed by the user
	
	/**
	 * Constructor for CommandHistory object.
	 */
	public CommandHistory() {
		commandHistory = new LinkedList<String>();
	}
	
	/**
	 * Getter method for CommandHistory.
	 * @return CommandHistory object
	 */
	public List<String> getHistory() {
		return commandHistory;
	}
	
	/**
	 * Setter method for CommandHistory; adds a command to the history.
	 * @param command Command to be added
	 */
	public void addHistory(String command) {
		commandHistory.add(command);
	}
}
