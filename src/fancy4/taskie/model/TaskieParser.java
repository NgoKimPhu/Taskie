package fancy4.taskie.model;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.*;

/**
 * @author Ngo Kim Phu
 */
public final class TaskieParser {
	private final String MESSAGE_INVALID_COMMAND_FORMAT = "invalid command format : %1$s";
	
	private final ArrayList<String>[] STRING_COMMANDS;
	
	private static TaskieParser parser;
	
	private class TaskCompiler {
		public TaskCompiler() {
		}
		
		public TaskieTask compileTask(String commandData) {
			TimeDetector timeDetector = new TimeDetector();
			timeDetector.detectTime(commandData);
			// TODO: error-prone
			String title = timeDetector.removeTime().replaceAll("\\s?-(\\w+)", "");
			System.err.println("Title: \"" + title + "\"");
			
			switch (timeDetector.getTaskType()) {
				case FLOAT:
					System.out.println("Float task\n");
					return new TaskieTask(title);
				case DEADLINE:
					System.out.println(timeDetector.getEndTime() + "\n");
					return new TaskieTask(title, timeDetector.getEndTime());
				case EVENT:
					System.out.println(timeDetector.getStartTime() + " ~> "
							+ timeDetector.getEndTime() + "\n");
					return new TaskieTask(title, timeDetector.getStartTime(), timeDetector.getEndTime());
				default:
					throw new Error("Fatal error in TaskieParser#timeDetector");
			}
		}
	}
	
	private class TaskSelectorDetector {
		private final String PATTERN_DELIMITER = "\\s+|(?<=\\D)(?=\\d)";
		private String taskDataString;
		private String screen = "left";
		private int index = -1;
		
		public TaskSelectorDetector(String dataString) {
			Scanner sc = new Scanner(dataString);
			sc.useDelimiter(PATTERN_DELIMITER);
				
			if (sc.hasNextInt()) {
				index = sc.nextInt();
			} else if (sc.hasNext("[-/]?(r|right|a|all)")) {
				sc.next();
				screen = "right";
				index = sc.nextInt();
			} else {
				sc.next();
				index = sc.nextInt();
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
	
	public static TaskieParser getInstance() {
		if (parser == null) {
			parser = new TaskieParser();
		}
		return parser;
	}
	
	private TaskieParser() {
		STRING_COMMANDS = new ArrayList[TaskieEnum.Actions.values().length];
		for (int i = 0; i < STRING_COMMANDS.length; i++){
			STRING_COMMANDS[i] = new ArrayList<>();
	    }

		try {
			Scanner commandStringScanner = new Scanner(new FileReader("CommandStrings.txt"));
			int i = 0;
			while (commandStringScanner.hasNext()) {
				Collections.addAll(STRING_COMMANDS[i++], commandStringScanner.nextLine().split("\\s+"));
			}
			commandStringScanner.close();
		} catch (FileNotFoundException e) {
			System.err.println("CommandStrings.txt is missing.");
			STRING_COMMANDS[TaskieEnum.Actions.ADD.ordinal()].add("add");		
		}
	}
	
	public TaskieAction parse(String inputString) {
		System.err.println("\"" + inputString + "\"");
		if (inputString == null) {
			return new TaskieAction(TaskieEnum.Actions.INVALID, null);
		}
		
		String command = inputString.trim();
		
		if (command.isEmpty()) {
			return new TaskieAction(TaskieEnum.Actions.INVALID, null);
		}
		
		String actionTypeString = getFirstWord(command);
		TaskieEnum.Actions actionType = determineTaskieAction(actionTypeString);
		
		String commandData;
		if (actionType == TaskieEnum.Actions.INVALID) {
			actionType = TaskieEnum.Actions.ADD;
			commandData = command;
		} else {
			commandData = removeFirstWord(command);
		}
		
		switch (actionType) {
			case ADD:
				return parseAdd(commandData);
			
			case DELETE:
				return parseDelete(commandData);
			
			case SEARCH:
				return parseSearch(commandData);
			
			case UPDATE:
				return parseUpdate(commandData);
			
			default:
				return new TaskieAction(actionType, null);
		}
		
	}

	private TaskieAction parseAdd(String commandData) throws Error {
		TaskCompiler tC = new TaskCompiler();
		TaskieTask task = tC.compileTask(commandData);

		return new TaskieAction(TaskieEnum.Actions.ADD, task);
	}

	private TaskieAction parseDelete(String commandData) {
		TaskSelectorDetector tSD = new TaskSelectorDetector(commandData);
		
		return new TaskieAction(TaskieEnum.Actions.DELETE, tSD.getScreen(), tSD.getIndex());
	}

	private TaskieAction parseSearch(String commandData) {
		TaskCompiler tC = new TaskCompiler();
		
		return new TaskieAction(TaskieEnum.Actions.SEARCH, tC.compileTask(commandData), commandData);
	}

	private TaskieAction parseUpdate(String commandData) {
		TaskSelectorDetector tSD = new TaskSelectorDetector(commandData);
		TaskCompiler tC = new TaskCompiler();
		TaskieTask task = tC.compileTask(tSD.getTaskDataString());
		
		if (tSD.getIndex() < 0) {
			return new TaskieAction(TaskieEnum.Actions.UPDATE, null);
		} else {
			return new TaskieAction(TaskieEnum.Actions.UPDATE, tSD.getScreen(), tSD.getIndex(), task);
		}
	}

	private TaskieEnum.Actions determineTaskieAction(String actionTypeString) {
		if (actionTypeString == null) {
			throw new Error(String.format(MESSAGE_INVALID_COMMAND_FORMAT, actionTypeString));
		}

		for (TaskieEnum.Actions action : TaskieEnum.Actions.values()) {
			if (STRING_COMMANDS[action.ordinal()].contains(actionTypeString)) {
				return action;
			}
		}
		
		return TaskieEnum.Actions.INVALID;
	}
	
	private String getFirstWord (String inputString) {
		return inputString.split("\\s+|(?<=[+-])(?=\\w)")[0];
	}
	
	private String removeFirstWord(String inputString) {
		return inputString.substring(getFirstWord(inputString).length()).trim();
	}
}