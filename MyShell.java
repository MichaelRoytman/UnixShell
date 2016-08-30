import java.io.FileNotFoundException;
import java.util.*;

/**
 * A class that represents a simple shell and accepts a variety of commands.
 * @author michaelroytman
 */
public class MyShell {
	
	/**
	 * Main method that contains the sole functionality of the shell, namely
	 * the REPL.
	 * @param args
	 */
	public static void main(String[] args) {
		
		boolean flag = true; //exit flag
		CurrentDir dir = new CurrentDir(); //creates a current directory object to maintain reference to changing directory location
		CommandHistory history = new CommandHistory(); //creates a CommandHistory object, which maintains a list of commands input by the user
		Scanner input = new Scanner(System.in); //Scanner for user command input
		
		String commandString; //String of raw commands from the user
		CommandManager manager; //command manager that will manage the lifetime of the user's commands
		
		//until user specifies exit
		while (flag) {
			System.out.print("> ");
			commandString = input.nextLine(); //takes command input from the user
			
			//adds any command to the history, with exception of no input or input containing word "history"
			if (!commandString.isEmpty() && !commandString.equals("") && !commandString.equalsIgnoreCase("history")) {
				history.addHistory(commandString);
			}
			
			//if user specifies exit
			if (commandString.equalsIgnoreCase("exit")) {
				System.out.println("REPL exits. Bye.");
				input.close(); //closes Scanner
				flag = false; //sets exit flag to false; exits loop
			}
			else {
				manager = new CommandManager(commandString, history, dir); //creates new CommandManager to manage commands
				
				try {
					manager.parse(); //parse contains main functionality of CommandManager
				} 
				catch (FileNotFoundException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
