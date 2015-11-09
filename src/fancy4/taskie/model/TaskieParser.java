// @@author A0126586W
package fancy4.taskie.model;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStream;
import java.util.*;

/**
 *  TaskieParser is the component that takes user's input data in the form of a String,
 *  analyzes then builds and returns a TaskieAction object with Action type and optional
 *  attributes like TaskieTask or index depending on its type of Action.
 *  
 *  @author Ngo Kim Phu
 */
public final class TaskieParser {
	private static final String MESSAGE_INVALID_COMMAND_FORMAT = "invalid command format : %1$s";
	private static final String PATTERN_INVALID_PATH_CHARACTERS = "[^A-Za-z0-9 /.\\[\\]\\(\\)~]| $|.$";
	private static final String PATTERN_DONE = "(marked )?done|finished";
	private static final String PATTERN_UNDONE = "(marked )?(not |un)(done|finished)|pending";
	private static final String PATTERN_COMMAND_DELIMITER = "\\s+|(?<=[+-])(?=\\w)";
	
	// A dictionary used in recognizing predefined String keywords of command
	private final ArrayList<String>[] STRING_COMMANDS;
	
	private static TaskieParser parser; // The Singleton instance
	
	/**
	 * A class reading the data, compiling and then returning a TaskieTask object
	 * with title and time if applicable
	 */
	private class TaskCompiler {
		private static final String PATTERN_FORCE_FLOAT = ".*[-\\\\\\/]float.*";
		private static final String PATTERN_SWITCH = "\\s?-\\w+";
		private static final String PATTERN_CONTAIN_ESCAPE = ".*\".*\".*";
		private static final String PATTERN_ESCAPED_SUBSTR = "\".*\"";
		private static final String PATTERN_DOUBLE_SPACE = "\\s(?=\\s)";
		private static final String STRING_REPLACED = "`@`";
		private static final String STRING_FATAL_TIMEDETECTOR = "Fatal error in TimeDetector compiling ";
		
		public TaskCompiler() {
		}
		
		/**
		 * Compile a new TaskieTask from the data String commandData
		 * 
		 * @param commandData
		 * 		The String data to be read and compiled from
		 * @return
		 * 		A TaskieTask object with title, task type and time if applicable
		 */
		public TaskieTask compileTask(String commandData) {
			if (commandData.matches(PATTERN_FORCE_FLOAT)) {
				return new TaskieTask(commandData.replaceAll(PATTERN_SWITCH, ""));
			}
			
			TimeDetector timeDetector = new TimeDetector();
			String title = commandData.matches(PATTERN_CONTAIN_ESCAPE) ? commandData.substring(
					commandData.indexOf('\"') + 1, commandData.lastIndexOf('\"')) : "";
			commandData = commandData.replaceAll(PATTERN_ESCAPED_SUBSTR, STRING_REPLACED);
			
			timeDetector.detectTime(commandData);
			title = timeDetector.removeTime().replace(STRING_REPLACED, title).
					replaceAll(PATTERN_SWITCH + "|" + PATTERN_DOUBLE_SPACE, "").trim();
			
			switch (timeDetector.getTaskType()) {
				case FLOAT:
					return new TaskieTask(title);
				
				case DEADLINE:
					return new TaskieTask(title, timeDetector.getEndTime());
				
				case EVENT:
					try {
						return new TaskieTask(title, timeDetector.getStartTime(), timeDetector.getEndTime());
					} catch (Exception e) {
						e.printStackTrace();
					}
				
				default: // should never reach here
					throw new Error(STRING_FATAL_TIMEDETECTOR + "\"" + commandData + "\"");
			}
		}
	}
	
	/**
	 * A class parsing a String to get the index of which TaskieTask to be referred to
	 */
	private class TaskSelectorDetector {
		private static final String PATTERN_DELIMITER = "\\s+|(?<=\\D)(?=\\d)";
		private static final String PATTERN_RIGHT_WINDOW = "[-/]?(r|right|a|all)";
		private String taskDataString;
		private String screen = "left";
		private int index = -1;
		
		/**
		 * Parse the input String and store the parsed data into class's fields
		 * 
		 * @param dataString
		 * 		A data String
		 * @throws UnrecognisedCommandException
		 * 		Invalid tokens for MARKDONE command, ADD command will be used instead 
		 */
		public TaskSelectorDetector(String dataString) throws UnrecognisedCommandException {
			Scanner sc = new Scanner(dataString);
			sc.useDelimiter(PATTERN_DELIMITER);
				
			if (sc.hasNextInt()) {
				index = sc.nextInt();
			} else if (sc.hasNext(PATTERN_RIGHT_WINDOW)) {
				sc.next();
				screen = "right";
				if (sc.hasNextInt()) {
					index = sc.nextInt();
				} else {
					sc.close();
					throw new UnrecognisedCommandException("Invalid markdone command, using add instead");
				}
			} else {
				sc.next();
				if (sc.hasNextInt()) {
					index = sc.nextInt();
				} else {
					sc.close();
					throw new UnrecognisedCommandException("Invalid markdone command, using add instead");
				}
			}

			this.taskDataString = sc.hasNext() ? sc.nextLine().trim() : "";
			sc.close();
		}

		public String getScreen() {
			return screen;
		}

		public int getIndex() {
			return index;
		}

		public String getTaskDataString() {
			return taskDataString;
		}
		
	}
	
	/**
	 * Public method to get/initialize the Singleton instance
	 * 
	 * @return
	 * 		The Singleton instance of TaskieParser
	 */
	public static TaskieParser getInstance() {
		if (parser == null) {
			parser = new TaskieParser();
		}
		return parser;
	}
	
	/**
	 * Private constructor
	 * Initializes STRING_COMMANDS map for keyword recognition
	 */
	@SuppressWarnings("unchecked")
	private TaskieParser() {
		final String COMMAND_STRINGS_FILENAME = "CommandStrings.txt";

		STRING_COMMANDS = new ArrayList[TaskieEnum.Actions.values().length];
		for (int i = 0; i < STRING_COMMANDS.length; i++){
			STRING_COMMANDS[i] = new ArrayList<>();
	    }

		try {
			// getResourceAsStream is used when Taskie is run as a jar
			InputStream commandStringFile = TaskieParser.class.getResourceAsStream(COMMAND_STRINGS_FILENAME);
			Scanner commandStringScanner;
			if (commandStringFile == null) {
				// Use FileReader when Taskie is run as class package, in IDE, test units...
				commandStringScanner = new Scanner(new FileReader(COMMAND_STRINGS_FILENAME));
			} else {
				commandStringScanner = new Scanner(commandStringFile);
			}
			
			int i = 0;
			while (commandStringScanner.hasNext()) {
				Collections.addAll(STRING_COMMANDS[i++], commandStringScanner.nextLine().split("\\s+"));
			}
			commandStringScanner.close();
		} catch (FileNotFoundException e) {
			System.err.println(COMMAND_STRINGS_FILENAME + " is missing.");
		}
	}
	
	/**
	 * Parses the action's type, then compiles and returns a new TaskieAction
	 * 
	 * @param inputString
	 * 		An input String
	 * @return
	 * 		New TaskieAction with corresponding TaskieAction type and parsed data
	 */
	public TaskieAction parse(String inputString) {
		if (inputString == null || inputString.trim().isEmpty()) {
			return new TaskieAction(TaskieEnum.Actions.INVALID, null);
		}
		
		String command = inputString.trim();
		
		String actionTypeString = getFirstWord(command);
		TaskieEnum.Actions actionType = determineTaskieAction(actionTypeString);
		
		String commandData;
		if (actionType == TaskieEnum.Actions.INVALID) {
			actionType = TaskieEnum.Actions.ADD;
			commandData = command;
		} else {
			commandData = removeFirstWord(command);
		}
		
		try {
			switch (actionType) {
				case ADD:
					return parseAdd(commandData);
				
				case DELETE:
					return parseDelete(commandData);
				
				case SEARCH:
					return parseSearch(commandData);
				
				case MARKDONE:
					return parseMarkDone(commandData);
				
				case UPDATE:
					return parseUpdate(commandData);
				
				case SETPATH:
					return parseSetPath(commandData);
				
				default:
					return new TaskieAction(actionType, null);
			}
		} catch (UnrecognisedCommandException e) {
			return parseAdd(command);
		}
		
	}

	private TaskieAction parseAdd(String commandData) throws Error {
		TaskCompiler tC = new TaskCompiler();
		TaskieTask task = tC.compileTask(commandData);

		return new TaskieAction(TaskieEnum.Actions.ADD, task);
	}

	private TaskieAction parseDelete(String commandData) throws UnrecognisedCommandException {
		if (commandData.matches("a|all")) {
			return new TaskieAction(TaskieEnum.Actions.DELETEALL, null);
		}
		TaskSelectorDetector tSD = new TaskSelectorDetector(commandData);
		
		return new TaskieAction(TaskieEnum.Actions.DELETE, tSD.getScreen(), tSD.getIndex());
	}

	private TaskieAction parseSearch(String commandData) {
		if (commandData.matches(PATTERN_DONE)) {
			return new TaskieAction(TaskieEnum.Actions.SEARCH, null, true);
		} else if (commandData.matches(PATTERN_UNDONE)) {
			return new TaskieAction(TaskieEnum.Actions.SEARCH, null, false);
		}
		
		TaskCompiler tC = new TaskCompiler();
		TaskieTask task = tC.compileTask(commandData);
		return new TaskieAction(TaskieEnum.Actions.SEARCH, task, task.getTitle());
	}

	private TaskieAction parseMarkDone(String commandData) throws UnrecognisedCommandException {
		TaskSelectorDetector tSD = new TaskSelectorDetector(commandData);
		
		return new TaskieAction(TaskieEnum.Actions.MARKDONE, tSD.getScreen(), tSD.getIndex());
	}

	private TaskieAction parseUpdate(String commandData) throws UnrecognisedCommandException {
		TaskSelectorDetector tSD = new TaskSelectorDetector(commandData);
		TaskCompiler tC = new TaskCompiler();
		TaskieTask task = tC.compileTask(tSD.getTaskDataString());
		
		if (tSD.getIndex() < 0) {
			return new TaskieAction(TaskieEnum.Actions.UPDATE, null);
		} else {
			return new TaskieAction(TaskieEnum.Actions.UPDATE, tSD.getScreen(), tSD.getIndex(), task);
		}
	}
	
	private TaskieAction parseSetPath(String commandData) {
		String path = commandData.replaceAll(PATTERN_INVALID_PATH_CHARACTERS, "");
		
		return new TaskieAction(TaskieEnum.Actions.SETPATH, null, path);
	}

	private TaskieEnum.Actions determineTaskieAction(String actionTypeString) {
		if (actionTypeString == null) {
			throw new Error(String.format(MESSAGE_INVALID_COMMAND_FORMAT, actionTypeString));
		}

		for (TaskieEnum.Actions action : TaskieEnum.Actions.values()) {
			if (STRING_COMMANDS[action.ordinal()].contains(actionTypeString.toLowerCase())) {
				return action;
			}
		}
		
		return TaskieEnum.Actions.INVALID;
	}
	
	private String getFirstWord (String inputString) {
		return inputString.split(PATTERN_COMMAND_DELIMITER)[0];
	}
	
	private String removeFirstWord(String inputString) {
		return inputString.substring(getFirstWord(inputString).length()).trim();
	}
}