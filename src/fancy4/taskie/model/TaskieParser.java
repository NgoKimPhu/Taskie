package fancy4.taskie.model;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import fancy4.taskie.model.TaskieEnum.TaskType;

/**
 * @author Ngo Kim Phu
 */
public final class TaskieParser {
	private static final String MESSAGE_INVALID_COMMAND_FORMAT = "invalid command format : %1$s";
	private static final String PATTERN_TYPE = "\\b(?:(?:-)?float|event|deadline)\\s?";
	private static final String PATTERN_DAY = "\\b(tonight|(?:today|tomorrow|tmr)\\s?(?:night)?)|"
			+ "(?:(?:next\\s)?((?:Mon|Fri|Sun)(?:day)?|Tue(?:sday)?|Wed(?:nesday)?|"
			+ "Thu(?:rsday)?|Sat(?:urday)?))|"
			+ "(?:(\\d{1,2})\\s?[\\\\\\/-]\\s?(\\d{1,2}))|"
			+ "(?:(\\d{1,2}\\s?)?(Jan(?:uary)?|Feb(?:ruary)?|Mar(?:ch)?|Apr(?:il)?|May|"
			+ "Jun(?:e)?|Jul(?:y)?|Aug(?:ust)?|Sep(?:tember)?|Oct(?:ober)?|(?:Nov|Dec)(?:ember)?)"
			+ "\\s?(\\d{1,2})?)";
	private static final String PATTERN_TIME = "(?:\\b(?:(?<=fr(?:om)?|-|~|to|till|until)|at|by|due))"
			+ "?\\s?(\\d{1,2})\\s?"
			+ "(?=[.:h ]\\s?\\d{1,2}\\s?m?|am|pm|tonight|(?:today|tomorrow|tmr)\\s?(?:night)?)"
			+ "(?:[.:h ]\\s?(\\d{1,2})\\s?m?)?\\s?(am|pm)?\\s?"
			+ "(tonight|(?:today|tomorrow|tmr)\\s?(?:night)?)?\\b|"
			+ "(?:\\b(?:(?<=fr(?:om)?|-|~|to|till|until)|at|by|due))\\s?(\\d{1,2})\\b";
	private static final String PATTERN_TIMERANGE_FORMAT = "(?:fr(?:om)?\\s?)?"
			+ "(?:%1$s)\\s?(?:%2$s)?\\s?(?:-|~|to|till|until)\\s?(?:%2$s)?\\s?(?:%1$s)";
	
	private static final HashMap<String, Integer> weekDays = new HashMap<String, Integer>();
	private static final HashMap<String, Integer> months = new HashMap<String, Integer>();
	
	private static ArrayList<String>[] commandStrings;
	
	static private class TimeDetector {
		private TaskieEnum.TaskType taskType;
		private Calendar startTime, endTime;
		private Matcher matcher;
		
		public TimeDetector() {
			taskType = TaskType.FLOAT;
			
			startTime = Calendar.getInstance();
			endTime = Calendar.getInstance();
			startTime.set(Calendar.HOUR_OF_DAY, 12);
			startTime.set(Calendar.MINUTE, 0);
			startTime.set(Calendar.SECOND, 0);
			endTime.set(Calendar.HOUR_OF_DAY, 12);
			endTime.set(Calendar.MINUTE, 0);
			endTime.set(Calendar.SECOND, 0);
			
			matcher = Pattern.compile("").matcher("");
		}
		
		public void detectTime (String dataString) {
			System.err.println("v " + dataString);
			if (matchFound(getTimeRangePattern(PATTERN_DAY, PATTERN_TIME), dataString)) {
				System.out.println("Date range detected: "+matcher.group(0));
				taskType = TaskType.EVENT;
				setDate(startTime, 1);
				setDate(endTime, 18);
			} else if (matchFound(PATTERN_DAY, dataString)) {
				System.out.println("Date detected: "+matcher.group(0));
				taskType = TaskType.DEADLINE;
				setDate(startTime, 1);
				setDate(endTime, 1);
			} else {
				System.out.println("No match found for date!");
			}
			
			if (matchFound(getTimeRangePattern(PATTERN_TIME, PATTERN_DAY), dataString)) {
				System.out.println("Time range detected: "+matcher.group(0));
				taskType = TaskType.EVENT;
				setTime(startTime, 1);
				setTime(endTime, 20);
			} else if (matchFound(PATTERN_TIME, dataString)) {
				System.out.println("Time detected: "+matcher.group(0));
				if (taskType != TaskType.EVENT) {
					taskType = TaskType.DEADLINE;
				}
				setTime(endTime, 1);
			} else {
				System.out.println("No match found for time!");
			}
		}
		
		private boolean matchFound(String patternString, String dataString) {
			matcher.usePattern(Pattern.compile(patternString, Pattern.CASE_INSENSITIVE));
			matcher.reset(dataString);
			return matcher.find();
		}

		private void setDate(Calendar time, int groupOffset) {
			if (matcher.group(groupOffset) != null) { // (today|tomorrow|tmr)
				if (!matcher.group(groupOffset).contains("today")) {
					time.add(Calendar.DATE, 1);
				}
			} else if (matcher.group(groupOffset + 1) != null) { // weekday
				time.set(Calendar.DAY_OF_WEEK, 
						weekDays.get(matcher.group(groupOffset + 1).substring(0, 3).toLowerCase()));
				if (time.before(Calendar.getInstance())) {
					time.add(Calendar.DATE, 7);
				}
			} else if (matcher.group(groupOffset + 2) != null) { // ((\d{1,2})\s?[\\/.-]?(\d{1,2}))
				int dateInt1 = Integer.parseInt(matcher.group(groupOffset + 2));
				int dateInt2 = Integer.parseInt(matcher.group(groupOffset + 3));
				if (dateInt2 > 12) {
					time.set(Calendar.DATE, dateInt2);
					time.set(Calendar.MONTH, dateInt1 - 1);
				} else {
					time.set(Calendar.DATE, dateInt1);
					time.set(Calendar.MONTH, dateInt2 - 1);
				}
			} else { // (\d{1,2}) (month) (\d{1,2})
				time.set(Calendar.MONTH, 
						months.get(matcher.group(groupOffset + 5).substring(0, 3).toLowerCase()));
				int date;
				if (matcher.group(groupOffset + 4) != null) {
					date = Integer.parseInt(matcher.group(groupOffset + 4));
				} else {
					date = Integer.parseInt(matcher.group(groupOffset + 6));
				}
				time.set(Calendar.DATE, date);
			}
		}
		
		private void setTime(Calendar time, int groupOffset) {
			if (matcher.group(groupOffset) != null) { // (\d{1,2})
				int hour = Integer.parseInt(matcher.group(groupOffset));
				if (matcher.group(groupOffset + 2) != null) { // (am|pm)
					time.set(Calendar.HOUR, hour);
					time.set(Calendar.AM_PM, (matcher.group(groupOffset + 2).toLowerCase().equals("am"))
											? Calendar.AM : Calendar.PM);
				} else if (matcher.group(groupOffset + 3) != null &&
						matcher.group(groupOffset + 3).contains("night")) {
					// tonight|(?:today|tomorrow|tmr)\\s?(?:night)?
					time.set(Calendar.HOUR, hour);
					time.set(Calendar.AM_PM, Calendar.PM);
				} else {
					time.set(Calendar.HOUR_OF_DAY, hour);
					if (time.get(Calendar.HOUR) < 7) {
						time.set(Calendar.AM_PM, Calendar.PM);
					}
				}
				
				if (matcher.group(groupOffset + 1) != null) { // (?:[.:h ]\s?(\d{1,2})\s?m?)
					int minute = Integer.parseInt(matcher.group(groupOffset + 1));
					time.set(Calendar.MINUTE, minute);
				}
			} else {
				int hour = Integer.parseInt(matcher.group(groupOffset + 4));
				time.set(Calendar.HOUR_OF_DAY, hour);
				if (time.get(Calendar.HOUR) < 7) {
					time.set(Calendar.AM_PM, Calendar.PM);
				}
			}
		}
		
		private String getTimeRangePattern (String patternString) {
			return String.format(PATTERN_TIMERANGE_FORMAT, patternString, "");
		}
		
		private String getTimeRangePattern (String patternString, String skippedString) {
			return String.format(PATTERN_TIMERANGE_FORMAT, patternString, skippedString);
		}

		public TaskieEnum.TaskType getTaskType() {
			return taskType;
		}

		public Date getStartTime() {
			return startTime.getTime();
		}

		public Date getEndTime() {
			return endTime.getTime();
		}
		
	}
	
	// TODO consider public TaskieParser getInstance() return/create the sole instance
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
		TimeDetector timeDetector = new TimeDetector();
		
		switch (actionType) {
			case ADD:
				timeDetector.detectTime(commandData);
				
				switch (timeDetector.getTaskType()) {
					case FLOAT:
						return new TaskieAction(actionType, new TaskieTask(commandData));
					case DEADLINE:
						System.out.println(printTime(timeDetector.endTime));
						return new TaskieAction(actionType, 
								new TaskieTask(commandData, timeDetector.getEndTime()));
					case EVENT:
						System.out.println(printTime(timeDetector.startTime)
								+ " till " + printTime(timeDetector.endTime));
						return new TaskieAction(actionType, 
								new TaskieTask(commandData, 
										timeDetector.getStartTime(), timeDetector.getEndTime()));
					default:
						throw new Error();
				}
			
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
			
			case UNDO:
				return new TaskieAction(actionType, new TaskieTask(commandData));
			
			default:
				return new TaskieAction(TaskieEnum.Actions.INVALID, (TaskieTask) null);
		}
		
	}

	private static String printTime(Calendar time) {
		int year = time.get(Calendar.YEAR);
		int month = time.get(Calendar.MONTH);      // 0 to 11
		int day = time.get(Calendar.DAY_OF_MONTH);
		int hour = time.get(Calendar.HOUR_OF_DAY);
		int minute = time.get(Calendar.MINUTE);
		int second = time.get(Calendar.SECOND);
		
		return String.format("%4d/%02d/%02d %02d:%02d:%02d",
				year, month+1, day, hour, minute, second);
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
	
	private static String getFirstWord (String inputString) {
		return inputString.split("\\s+")[0];
	}
	
	private static String removeFirstWord(String inputString) {
		return inputString.substring(getFirstWord(inputString).length()).trim();
	}
}