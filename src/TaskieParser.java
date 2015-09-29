import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Ngo Kim Phu
 *
 */
public final class TaskieParser {
	private static final String MESSAGE_INVALID_COMMAND_FORMAT = "invalid command format : %1$s";
	private static final String timeRangePatternString = 
			"^\\d{1,2}\\s?(am|pm|([.:h ]\\s?\\d{1,2}\\s?m?)?)\\s?"
			+ "(-|~|to|till|until)\\s?"
			+ "\\d{1,2}\\s?(am|pm|([.:h ]\\s?\\d{1,2}\\s?m?)?)$";
	private static final String timePatternString = "^\\d{1,2}\\s?(am|pm|([.:h ]\\s?\\d{1,2}\\s?m?)?)$";
	
	private static ArrayList<String>[] commandStrings;
	
	public TaskieParser() {
		commandStrings = new ArrayList[TaskieEnum.Actions.values().length];
		for (int i = 0; i < commandStrings.length; i++){
			commandStrings[i] = new ArrayList<>();
	    }
		// TODO load values from raw text files
		commandStrings[TaskieEnum.Actions.ADD.ordinal()].add("add");
	}
	
	protected static TaskieAction parse (String inputString) {
		String command = inputString.trim();
		
		if (command.isEmpty()) {
			return new TaskieAction(TaskieEnum.Actions.INVALID, null);
		}

		String actionTypeString = getFirstWord(command);

		TaskieEnum.Actions actionType = determineTaskieAction(actionTypeString);
		String commandData = (actionType != TaskieEnum.Actions.INVALID) ? command : removeFirstWord(command);

		switch (actionType) {
			case ADD:
				Pattern timeRangePattern = Pattern.compile(timeRangePatternString);
				Matcher matcher = timeRangePattern.matcher(commandData);
				if (matcher.find()) {
					
				}
				return new TaskieAction(actionType, new TaskieTask(commandData));
				
			default:
				return new TaskieAction(TaskieEnum.Actions.INVALID, null);
		}
		
	}
	
	private static TaskieEnum.Actions determineTaskieAction(String actionTypeString) {
		if (actionTypeString == null) {
			throw new Error("command type string cannot be null!");
		}

		for (TaskieEnum.Actions action : TaskieEnum.Actions.values()) {
			if (commandStrings[action.ordinal()].contains(actionTypeString)) {
				return action;
			}
		}
		
		return TaskieEnum.Actions.INVALID;
	}
	
	private static String getFirstWord (String inputString) {
		return inputString.split("\\s+")[0];
	}
	
	private static String removeFirstWord(String inputString) {
		return inputString.substring(getFirstWord(inputString).length()).trim();
	}
}