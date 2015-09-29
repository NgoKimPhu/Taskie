import java.util.*;

import TextBuddy.CommandType;
import TextBuddy.TaskieAction;
import TextBuddy.TaskieEnum.Actions;

/**
 * @author Ngo Kim Phu
 *
 */
public final class TaskieParser {
	private static final String MESSAGE_INVALID_COMMAND_FORMAT = "invalid command format : %1$s";
	
	private static ArrayList<String>[] commandStrings;
	
	private TaskieParser() {
		commandStrings = new ArrayList[TaskieEnum.Actions.values().length];
		for (int i = 0; i < commandStrings.length; i++){
			commandStrings[i] = new ArrayList<>();
	    }
		// TODO load values from raw text files
		commandStrings[TaskieEnum.Actions.ADD].add("add");
	}
	
	protected static TaskieAction parse (String inputString) {
		String command = inputString.trim();
		
		if (command.isEmpty()) {
			return String.format(MESSAGE_INVALID_COMMAND_FORMAT, command);
		}

		String actionTypeString = getFirstWord(command);

		TaskieEnum.Actions actionType = determineCommandType(actionTypeString);

		return new TaskieAction(actionType, null);
	}
	
	private static TaskieEnum.Actions determineTaskieAction(String actionTypeString) {
		if (TaskieActionString == null) {
			throw new Error("command type string cannot be null!");
		}

		for (TaskieEnum.Actions action : TaskieEnum.Actions.values()) {
			if commandStrings[action].contains(actionTypeString) {
				return action;
		}
		
		return TaskieAction.INVALID;
	}
	
	private static String getFirstWord (String inputString) {
		return inputString.split("\\s+")[0];
	}
}