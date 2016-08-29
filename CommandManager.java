
import java.io.*;
import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * A class that manages the lifetime of commands by parsing through raw input, validating commands, creating the commands,
 * executing the commands, and suspending/stopping/resuming the commands.
 * @author michaelroytman
 */
public class CommandManager {
	private String rawCommands; //String representing raw command from the user
	private List<Filter> commandObjects; //a list of Filter objects
	private List<String[]> commandStrings; //a list of String arrays representing the commands and their arguments
	private boolean validCommands = true; //boolean representing whether all commands inputed by the user are valid
	private CurrentDir dir; //current directory object
	private CommandHistory history; //command history object
		
	/**
	 * Constructor for CommandManager, which takes a String of raw input from the user.
	 * @param rawCommands String representing the user's raw input
	 * @param history a CommandHistory object representing the user's input history
	 * @param dir CurrentDir object representing current working directory
	 */
	public CommandManager(String rawCommands, CommandHistory history, CurrentDir dir) {
		this.rawCommands = rawCommands;
		commandObjects = new LinkedList<Filter>(); //creates new LinkedList to contain command objects
		commandStrings = new LinkedList<String[]>(); //creates new LinkedList to contain commands and their arguments as String arrays
		this.dir = dir;
		this.history = history;
	}
		
	/**
	 * Parse method that parses the raw input String by splitting over relevant delimeters, validating the command/argument
	 * order, and creating the relevant command objects.
	 * @throws FileNotFoundException if file does not exist
	 */
	public void parse() throws FileNotFoundException {
	
		rawCommands = rawCommands.replace(">", "|>"); //replaces > with |> to be able to split >
													  //over a pipe
		
		String[] pipeArray = rawCommands.split("\\|"); //splits raw input over pipes
		
		//removes any excess whitespace (more than one space between words) and trims each command/argument pair
		for (int j = 0; j < pipeArray.length; j++) {
			pipeArray[j] = pipeArray[j].replaceAll("\\s+", " ");
			pipeArray[j] = pipeArray[j].trim();
		}
		
		//loops through each command/argument pair in pipeArray
		for (int i = 0; i < pipeArray.length; i++) {

			String[] spaceArray = pipeArray[i].split(" "); //splits each command/argument pair over spaces
			commandStrings.add(spaceArray); //adds spaceArray for each command/argument pair to commandSrings
			
			validCommands = validate(spaceArray); //calls validate method, which validates each command/argument pair
			
			//if invalid; exit loop
			if (!validCommands) {
				break;
			}
		}
		
		//if commands are all valid
		if (validCommands) {
			//if pipe order is valid
			if (validatePipeOrder()) {
				
				Iterator<String[]> commandIter = commandStrings.iterator();
				
				//creates commands from each command/argument pair
				while (commandIter.hasNext()) {
					createCommands((String[])commandIter.next());
				}
			}
		}
		
		//if there are commands and the last command in the list of commands is either
		//not shell sink, not a cd command (which does not need a shell sink), or not a redirect, add shell sink
		if (!commandObjects.isEmpty()) {
			if (!(commandObjects.get(commandObjects.size()-1) instanceof Cd) && !(commandObjects.get(commandObjects.size()-1) instanceof ShellSink) && !(commandObjects.get(commandObjects.size()-1) instanceof Redirect)) {
				addShellSink();
			}
		}
	
		manageCommands(); //method call to start and join all command threads
	}

	/**
	 * Method that validates the ordering of commands in the raw string with
	 * respect to pipe placement; prints relevant errors if necessary.
	 * @return boolean; true if pipe order of entire raw string is valid; false otherwise
	 */
	public boolean validatePipeOrder() {
		String temp = null;
		boolean validOrder = true;
		
		//traverses entire list of String arrays representing command/argument pairs
		for (int counter = 0; counter < commandStrings.size(); counter++) {
			
			temp = commandStrings.get(counter)[0]; //temp command
			
			//if command is exit
			if (temp.equalsIgnoreCase("exit")) {
				//exit cannot be in a raw command String with other commands
				System.out.println("invalid pipe order");
				validOrder = false;
				break;
			}
			//if command is cd
			else if (temp.equalsIgnoreCase("cd")) {
				//if cd is not the first in the raw command string or if 
				//cd has one or more arguments, invalid pipe order
				if (counter != 0 || commandStrings.size() > 1) {
					System.out.println("invalid pipe order");
					validOrder = false;
					break;
				}
			}
			//if command is pwd, ls, cat, history, or sleep
			else if (temp.equalsIgnoreCase("pwd") || temp.equalsIgnoreCase("ls") || temp.equalsIgnoreCase("cat") || temp.equalsIgnoreCase("history") || temp.equalsIgnoreCase("sleep")) {
				//if these commands are not the first in the raw command string, invalid pipe order
				if (counter != 0) {
					System.out.println("invalid pipe order");
					validOrder = false;
					break;
				}
			}
			//if command is grep or lc
			else if (temp.equalsIgnoreCase("grep") || temp.equalsIgnoreCase("lc")) {
				//if these commands are first in the raw command string or if 
				//they have more than one argument, invalid pipe order
				if (counter == 0 || commandStrings.size() < 2) {
					System.out.println("invalid pipe order");
					validOrder = false;
					break;
				}
			}	
		}	
		return validOrder; 
	}
	
	/**
	 * Method that adds a shell sink command to the end of the commandObjects list
	 */
	public void addShellSink() {
		//if last command in the commandObjects list has an output queue
		if (commandObjects.get(commandObjects.size()-1).out != null) {
			
			//creates new ShellSink using the previous command's output queue and adds it to commandObjects list
			commandObjects.add(new ShellSink(commandObjects.get(commandObjects.size()-1).out));
		}
	}
	
	/**
	 * Method that starts and joins all threads in the commandObjects list.
	 */
	public void manageCommands() {
		//for each command in commandObjects
		for (Filter f : commandObjects) {
			f.start();
		}
		
		//for each command in commandObjects
		for (Filter f : commandObjects) {
			try {
				f.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Method that validates command/argument pair by calling relevant method.
	 * @param temp String array containing a command and its subsequent arguments, if any.
	 * @return boolean; true if command/argument pair is valid; false otherwise
	 * @throws FileNotFoundException
	 */
	public boolean validate(String[] temp) throws FileNotFoundException {
		boolean isValid = true;
		
		int i = 0;
		
		if (temp[i].equalsIgnoreCase("pwd") || temp[i].equalsIgnoreCase("ls") || temp[i].equalsIgnoreCase("lc") || temp[i].equalsIgnoreCase("history")) {
			isValid = validateGroup(temp);
		}
		else if (temp[i].equals(">")) {
			isValid = validateRedirect(temp);
		}
		else if (temp[i].equalsIgnoreCase("cat")) {
			isValid = validateCat(temp);
		}
		else if (temp[i].equalsIgnoreCase("cd")) {
			isValid = validateCd(temp);
		}
		else if (temp[i].equalsIgnoreCase("grep")) {
			isValid = validateGrep(temp);
		}
		else if (temp[i].equalsIgnoreCase("sleep")) {
			isValid = validateSleep(temp);
		}
		else if (!temp[i].equalsIgnoreCase("exit")){
			System.out.println(temp[i] + ": invalid command");
			isValid = false;
		}
			
		return isValid;
	}
	
	/**
	 * Method that validates the command/argument pair for redirect command.
	 * @param commandAndArgs command and its argument.
	 * @return boolean; true if command/argument pair is valid; false otherwise.
	 * @throws FileNotFoundException
	 */
	public boolean validateRedirect(String[] commandAndArgs) throws FileNotFoundException {
		
		//if redirect has no argument
		if (commandAndArgs.length == 1) {
			System.out.println(">: missing argument");
			return false;
		}
		//if redirect has more than one argument
		else if (commandAndArgs.length > 2) {
			System.out.println(">: invalid argument");
			return false;
		}
		else {
			return true; //valid pair
		}
	}
	
	/**
	 * Method that validates the command/argument pair for cat command.
	 * @param commandAndArgs command and its argument.
	 * @return boolean; true if command/argument pair is valid; false otherwise.
	 */
	public boolean validateCat(String[] commandAndArgs) {
		//if cat has no argument
		if (commandAndArgs.length == 1) {
			System.out.println("cat: missing argument");
			return false;
		}
		else {
			//checks existence of each specified file
			for (int i = 1; i < commandAndArgs.length; i++) {
				String path = commandAndArgs[i];
				File file = new File(dir.getDir() + System.getProperty("file.separator") + path);
				
				//if file does not exist
				if (!file.isFile()) {
					System.out.println("cat: file not found");
					return false;
				}
			}
			return true;
		}
	}
	
	/**
	 * Method that validates the command/argument pair for multiple commands
	 * that share similarities in argument types.
	 * @param commandAndArgs command and its argument.
	 * @return boolean; true if command/argument pair is valid; false otherwise.
	 */
	public boolean validateGroup(String[] commandAndArgs) {
		//if commands have an argument
		if (commandAndArgs.length > 1) {
			System.out.println(commandAndArgs[0] + ": invalid argument");
			return false;
		}
		
		return true;
	}
	
	/**
	 * Method that validates the command/argument pair for cd command.
	 * @param commandAndArgs command and its argument.
	 * @return boolean; true if command/argument pair is valid; false otherwise.
	 * @throws FileNotFoundException
	 */
	public boolean validateCd(String[] commandAndArgs) throws FileNotFoundException {
		//if cd doesn't have an argument
		if (commandAndArgs.length == 1) {
			System.out.println("cd: missing argument");
			return false;
		}
		//if cd has more than one argument
		else if (commandAndArgs.length > 2) {
			System.out.println("cd: invalid argument");
			return false;
		}
		//if cd's arguments are .. or .
		else if (commandAndArgs[1].equals("..") || commandAndArgs[1].equals(".")) {
			return true;
		}
		//if cd's argument is a potential valid directory
		else {
			String path = commandAndArgs[1]; 
			File file = new File(dir.getDir() + System.getProperty("file.separator") + path);
			
			//if specified directory exists
			if (file.isDirectory()) {
				return true;
			}
			//directory invalid
			else {
				System.out.println("cd: directory not found");
				return false;
			}
		}
	}
	
	/**
	 * TMethod that validates the command/argument pair for grep command.
	 * @param commandAndArgs String array with command grep and possible arguments
	 * @return boolean; true if command/argument pair is valid; false otherwise.
	 */
	public boolean validateGrep(String[] commandAndArgs) {
		//if there is no argument
		if (commandAndArgs.length == 1) {
			System.out.println("grep: missing argument");
			return false;
		}
		//if there is more than one argument
		else if (commandAndArgs.length > 2) {
			System.out.println("grep: invalid argument");
			return false;
		}
		else {
			return true;
		}
	}
	
	/**
	 * Method that validates the command/argument pair for sleep command.
	 * @param commandAndArgs String array with command sleep and possible arguments
	 * @return boolean; true if command/argument pair is valid; false otherwise.
	 */
	public boolean validateSleep(String[] commandAndArgs) {
		//if the length of the array with command and argument is 1, then there are no arguments
		if (commandAndArgs.length == 1) {
			System.out.println("sleep: missing argument");
			return false; //invalid
		}
		//if the length of the array with command and argument is greater than 2, then there are too many arguments
		else if (commandAndArgs.length > 2) {
			System.out.println("sleep: invalid argument");
			return false; //invalid
		}
		//if length of array appears valid
		else {
			int sleep; 
			try {
				sleep = Integer.parseInt(commandAndArgs[1]); //if duration is an integer
			}
			catch (NumberFormatException exception) {
				System.out.println(" sleep: invalid argument"); //duration is not an integer
				return false;
			}
			
			//if duration is less than 0 seconds; invalid
			if (sleep < 0) {
				System.out.println("sleep: invalid argument");
				return false;
			} 
			else {
				return true;
			}
		}
	}
	
	/**
	 * Method that creates commands from a command/argument pair.
	 * @param spaceArray
	 */
	public void createCommands(String[] spaceArray) {
		String j = spaceArray[0];
		
		//creates new pwd command
		if (j.equalsIgnoreCase("pwd")) {
			commandObjects.add(new Pwd(new LinkedBlockingQueue<Object>(), dir));
		}
		//creates new ls command
		else if (j.equalsIgnoreCase("ls")) {
			commandObjects.add(new Ls(new LinkedBlockingQueue<Object>(), dir));
		}
		//creates new lc command
		else if (j.equalsIgnoreCase("lc")) {
			//temp blocking queue is previous command's output queue; becomes lc's input queue
			LinkedBlockingQueue<Object> temp = (LinkedBlockingQueue<Object>) commandObjects.get(commandObjects.size()-1).out; 
			
			commandObjects.add(new Lc(temp, new LinkedBlockingQueue<Object>()));
		}
		//creates new history command
		else if (j.equalsIgnoreCase("history")) {
			commandObjects.add(new History(new LinkedBlockingQueue<Object>(), history));
		}
		//creates new cat command
		else if (j.equalsIgnoreCase("cat")) {
			//creates a list of files that cat will concatenate
			List<String> files = Arrays.asList(Arrays.copyOfRange(spaceArray, 1, spaceArray.length));
			
			commandObjects.add(new Cat(new LinkedBlockingQueue<Object>(), files, dir));
		}
		//creates new cd command
		else if (j.equalsIgnoreCase("cd")) {
			commandObjects.add(new Cd(spaceArray[1], dir));
		}
		//creates new grep command
		else if (j.equalsIgnoreCase("grep")) {
			//temp blocking queue is previous command's output queue; becomes grep's input queue
			LinkedBlockingQueue<Object> temp = (LinkedBlockingQueue<Object>) commandObjects.get(commandObjects.size()-1).out; 
			
			commandObjects.add(new Grep(temp, new LinkedBlockingQueue<Object>(), spaceArray[1]));
		}
		//creates new sleep command
		else if (j.equalsIgnoreCase("sleep")) {
			int sleep = 0; 
			
			try {
				sleep = Integer.parseInt(spaceArray[1]);
			}
			catch (NumberFormatException exception) {
			}
			
			commandObjects.add(new SubCmd_Sleep(new LinkedBlockingQueue<Object>(), sleep));
		}
		//creates new redirect command
		else if (j.equals(">")) {
			//temp blocking queue is previous command's output queue; becomes redirect's input queue
			LinkedBlockingQueue<Object> temp = (LinkedBlockingQueue<Object>) commandObjects.get(commandObjects.size()-1).out; 
			
			commandObjects.add(new Redirect(temp, spaceArray[1], dir));
		}
	}
		
	/*
	 * This is for Part 4
	 */
	public void kill() {	
	}
}
