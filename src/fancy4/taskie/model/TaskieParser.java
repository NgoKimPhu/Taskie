package fancy4.taskie.model;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Ngo Kim Phu
 *
 */
public final class TaskieParser {
	private static final String MESSAGE_INVALID_COMMAND_FORMAT = "invalid command format : %1$s";
	// TODO make a class
	private static final String PATTERN_DAY = "\\b(today|tomorrow|tmr)|"
			+ "(?:(?:next\\s)?((?:Mon|Fri|Sun)(?:day)?|Tue(?:sday)?|Wed(?:nesday)?|"
			+ "Thu(?:rsday)?|Sat(?:urday)?))|"
			+ "((\\d{1,2})\\s?[\\\\/.-]\\s?(\\d{1,2}))|"
			+ "(?:(\\d{1,2}\\s?)?(Jan(?:uary)?|Feb(?:ruary)?|Mar(?:ch)?|Apr(?:il)?|May|"
			+ "Jun(?:e)?|Jul(?:y)?|Aug(?:ust)?|Sep(?:tember)?|Oct(?:ober)?|(?:Nov|Dec)(?:ember)?)"
			+ "\\s?(\\d{1,2})?)";
	private static final String PATTERN_TIMERANGE_FORMAT = "(?:%1$s)\\s?(?:-|~|to|till|until)\\s?(?:%1$s)";
	private static final String PATTERN_TIME = "\\b\\d{1,2}\\s?(am|pm|([.:h ]\\s?\\d{1,2}\\s?m?)?)";
	
	private static final HashMap<String, Integer> weekDays = new HashMap<String, Integer>();
	private static final HashMap<String, Integer> months = new HashMap<String, Integer>();
	
	private static ArrayList<String>[] commandStrings;
	
	private TaskieParser() {
	}
	
	static {
		weekDays.put("mon", Calendar.MONDAY);
		weekDays.put("tue", Calendar.TUESDAY);
		weekDays.put("wed", Calendar.WEDNESDAY);
		weekDays.put("thu", Calendar.THURSDAY);
		weekDays.put("fri", Calendar.FRIDAY);
		weekDays.put("sat", Calendar.SATURDAY);
		weekDays.put("sun", Calendar.SUNDAY);
		
		months.put("jan", Calendar.JANUARY);
		months.put("feb", Calendar.FEBRUARY);
		months.put("mar", Calendar.MARCH);
		months.put("apr", Calendar.APRIL);
		months.put("may", Calendar.MAY);
		months.put("jun", Calendar.JUNE);
		months.put("jul", Calendar.JULY);
		months.put("aug", Calendar.AUGUST);
		months.put("sep", Calendar.SEPTEMBER);
		months.put("oct", Calendar.OCTOBER);
		months.put("nov", Calendar.NOVEMBER);
		months.put("dec", Calendar.DECEMBER);
		
		commandStrings = new ArrayList[TaskieEnum.Actions.values().length];
		for (int i = 0; i < commandStrings.length; i++){
			commandStrings[i] = new ArrayList<>();
	    }

		try {
			Scanner commandStringScanner = new Scanner(new FileReader("CommandStrings.txt"));
			int i = 0;
			while (commandStringScanner.hasNext()) {
				Collections.addAll(commandStrings[i++], commandStringScanner.nextLine().split("\\s+"));
			}
			commandStringScanner.close();
		} catch (FileNotFoundException e) {
			System.err.println("CommandStrings.txt is missing.");
			commandStrings[TaskieEnum.Actions.ADD.ordinal()].add("add");		
		}
	}
	
	protected static TaskieAction parse (String inputString) {
		String command = inputString.trim();
		
		if (command.isEmpty()) {
			return new TaskieAction(TaskieEnum.Actions.INVALID, (TaskieTask) null);
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

		int index;
		
		switch (actionType) {
			case ADD:
				Pattern timeRangePattern = Pattern.compile(getTimeRangePattern(PATTERN_DAY), 
						Pattern.CASE_INSENSITIVE);
				System.out.println(getTimeRangePattern(PATTERN_DAY));
				Matcher matcher = timeRangePattern.matcher(commandData);
				if (matcher.find()) {
					Calendar startTime = Calendar.getInstance();
					Calendar endTime = Calendar.getInstance();
					System.out.println(matcher.group(0));
					if (matcher.group(1) != null) { // (today|tomorrow|tmr)
						if (!matcher.group(1).equals("today")) {
							startTime.add(Calendar.DATE, 1);
						}
					} else if (matcher.group(2) != null) { // weekday
						startTime.set(Calendar.DAY_OF_WEEK, 
								weekDays.get(matcher.group(1).substring(0, 3).toLowerCase()));
					} else if (matcher.group(3) != null) { // ((\d{1,2})\s?[\\/.-]?(\d{1,2}))
						int dateInt1 = Integer.parseInt(matcher.group(4));
						int dateInt2 = Integer.parseInt(matcher.group(5));
						if (dateInt2 > 12) {
							startTime.set(Calendar.DATE, dateInt2);
							startTime.set(Calendar.MONTH, dateInt1);
						} else {
							startTime.set(Calendar.DATE, dateInt1);
							startTime.set(Calendar.MONTH, dateInt2);
						}
					} else { // (\d{1,2}) (month) (\d{1,2})
						startTime.set(Calendar.MONTH, 
								months.get(matcher.group(7).substring(0, 3).toLowerCase()));
						int date;
						if (matcher.group(6) != null) {
							date = Integer.parseInt(matcher.group(6));
						} else {
							date = Integer.parseInt(matcher.group(8));
						}
						startTime.set(Calendar.DATE, date);
					}
				} else {
					System.out.println("No match found!\n");
				}
				return new TaskieAction(actionType, new TaskieTask(commandData));
			case DELETE:
				try {
					index = Integer.parseInt(commandData);
				} catch (NumberFormatException e) {
					return new TaskieAction(TaskieEnum.Actions.INVALID, new TaskieTask(commandData));
				}
				return new TaskieAction(actionType, new TaskieTask(commandData), index);
			case SEARCH:
				return new TaskieAction(actionType, new TaskieTask(commandData), commandData);
			case UPDATE:
				try {
					index = Integer.parseInt(getFirstWord(commandData));
				} catch (NumberFormatException e) {
					return new TaskieAction(TaskieEnum.Actions.INVALID, new TaskieTask(commandData));
				}
				return new TaskieAction(actionType, new TaskieTask(removeFirstWord(commandData)), index);
			default:
				return new TaskieAction(TaskieEnum.Actions.INVALID, (TaskieTask) null);
		}
		
	}
	
	private static TaskieEnum.Actions determineTaskieAction(String actionTypeString) {
		if (actionTypeString == null) {
			throw new Error(String.format(MESSAGE_INVALID_COMMAND_FORMAT, actionTypeString));
		}

		for (TaskieEnum.Actions action : TaskieEnum.Actions.values()) {
			if (commandStrings[action.ordinal()].contains(actionTypeString)) {
				return action;
			}
		}
		
		return TaskieEnum.Actions.INVALID;
	}
	
	private static String getTimeRangePattern (String patternString) {
		return String.format(PATTERN_TIMERANGE_FORMAT, patternString);
	}
	
	private static String getFirstWord (String inputString) {
		return inputString.split("\\s+")[0];
	}
	
	private static String removeFirstWord(String inputString) {
		return inputString.substring(getFirstWord(inputString).length()).trim();
	}
}