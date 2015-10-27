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
	private final String MESSAGE_INVALID_COMMAND_FORMAT = "invalid command format : %1$s";
	private final String PATTERN_TYPE = "(?:(?:-)?\\b(?:float|event|deadline))?\\s?";
	private final String PATTERN_DAY = "\\b(tonight|(?:today|tomorrow|tmr)\\s?(?:night)?)|"
			+ "(?:(?:next\\s)?((?:Mon|Fri|Sun)(?:day)?|Tue(?:sday)?|Wed(?:nesday)?|"
			+ "Thu(?:rsday)?|Sat(?:urday)?))|"
			+ "(?:(\\d{1,2})\\s?[\\\\\\/-]\\s?(\\d{1,2}))|"
			+ "(?:(\\d{1,2}\\s?)?(Jan(?:uary)?|Feb(?:ruary)?|Mar(?:ch)?|Apr(?:il)?|May|"
			+ "Jun(?:e)?|Jul(?:y)?|Aug(?:ust)?|Sep(?:tember)?|Oct(?:ober)?|(?:Nov|Dec)(?:ember)?)"
			+ "\\s?(\\d{1,2})?)";
	private final String PATTERN_TIME = "(?:\\b(?:(?<=fr(?:om)?|-|~|to|till|until)|at|by|due))?"
			+ "\\s?(?:(?:(\\d{1,2})\\s?"
			+ "(?=[.:h ]\\s?\\d{1,2}\\s?m?|am|pm|tonight|(?:today|tomorrow|tmr)\\s?(?:night)?)"
			+ "(?:[.:h ]\\s?(\\d{1,2})\\s?m?)?\\s?(am|pm)?\\s?"
			+ "(tonight|(?:today|tomorrow|tmr)\\s?(?:night)?)?)\\b|(now|(?:to|tmr |tomorrow )night)\\b)|"
			+ "(?:\\b(?:(?<=fr(?:om)?|-|~|to|till|until)|at|by|due))\\s?(\\d{1,2})\\b";
	private final String PATTERN_TIMERANGE_FORMAT = "(?:fr(?:om)?\\s?)?"
			+ "(?:%1$s)\\s?(?:%2$s)?\\s?(?:-|~|to|till|until)?\\s?(?:%2$s)?\\s?(?:%1$s)";
	
	private final HashMap<String, Integer> MAP_WEEKDAYS = new HashMap<String, Integer>();
	private final HashMap<String, Integer> MAP_MONTHS = new HashMap<String, Integer>();
	
	private ArrayList<String>[] STRING_COMMANDS;
	
	private static TaskieParser parser;
	
	private class TimeDetector {
		private String dataString;
		private TaskieEnum.TaskType taskType;
		private Calendar startTime, endTime;
		private Matcher matcher;
		private int matchStartPos, matchEndPos;
		
		public TimeDetector() {
			this("");
		}
		
		public TimeDetector(String dataString) {
			this.dataString = dataString;
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
		
		public void setDataString(String dataString) {
			this.dataString = dataString;
		}
		
		public void detectTime(String dataString) {
			setDataString(dataString);
			detectTime();
		}
		
		// expected taskType: FLOAT / DEADLINE or EVENT
		public void detectTime() {
			if (!dataString.equals("")) {
				matchStartPos = dataString.length()-1;
			}
			matchEndPos = 0;
			if (isMatchFound(getTimeRangePattern(PATTERN_DAY, PATTERN_TIME), dataString)) {
				System.out.println("Date range detected: \"" + matcher.group() + "\"");
				taskType = TaskType.EVENT;
				setDate(startTime, 1);
				setDate(endTime, 20);
			} else if (isMatchFound(PATTERN_DAY, dataString)) {
				System.out.println("Date detected: \"" + matcher.group() + "\"");
				taskType = TaskType.DEADLINE;
				setDate(startTime, 1);
				setDate(endTime, 1);
			} else {
				System.out.println("No match found for date!");
			}
			
			if (isMatchFound(getTimeRangePattern(PATTERN_TIME, PATTERN_DAY), dataString)) {
				System.out.println("Time range detected: \"" + matcher.group() + "\"");
				taskType = TaskType.EVENT;
				setTime(startTime, 1);
				setTime(endTime, 21);
			} else if (isMatchFound(PATTERN_TIME, dataString)) {
				System.out.println("Time detected: \"" + matcher.group() + "\"");
				if (taskType != TaskType.EVENT) {
					taskType = TaskType.DEADLINE;
				}
				setTime(endTime, 1);
			} else {
				System.out.println("No match found for time!");
			}
			
			if (taskType != TaskieEnum.TaskType.EVENT) {
				startTime = null;
			}
			if (taskType == TaskieEnum.TaskType.FLOAT) {
				endTime = null;
			}
		}
		
		public String removeTime() {
			String dataWithoutTime = dataString;
			if (matchStartPos < matchEndPos) {
				dataWithoutTime = dataWithoutTime.replaceAll(PATTERN_TYPE + timeMatchSubstr(), " ");
			}
			return dataWithoutTime.trim().replaceAll("\\s{2,}", " ");
		}
		
		private boolean isMatchFound(String patternString, String dataString) {
			matcher.usePattern(Pattern.compile(patternString, Pattern.CASE_INSENSITIVE));
			matcher.reset(dataString);
			boolean matchFound = matcher.find();
			if (matchFound) {
				if (matcher.start() < matchStartPos) {
					matchStartPos = matcher.start();
				}
				if (matcher.end() > matchEndPos) {
					matchEndPos = matcher.end();
				}
			}
			return matchFound;
		}

		private void setDate(Calendar time, int groupOffset) {
			if (matcher.group(groupOffset) != null) { // (today|tomorrow|tmr)
				if (!matcher.group(groupOffset).contains("today")) {
					time.add(Calendar.DATE, 1);
				}
			} else if (matcher.group(groupOffset + 1) != null) { // weekday
				time.set(Calendar.DAY_OF_WEEK, 
						MAP_WEEKDAYS.get(matcher.group(groupOffset + 1).substring(0, 3).toLowerCase()));
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
						MAP_MONTHS.get(matcher.group(groupOffset + 5).substring(0, 3).toLowerCase()));
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
			} else if (matcher.group(groupOffset + 4) != null) { // now|(?:to|tmr |tomorrow )night
				if (matcher.group(groupOffset + 4).equals("now")) {
					time.setTime(Calendar.getInstance().getTime());
				} else {
					time.set(Calendar.HOUR_OF_DAY, 19); // TODO magic 7pm
				}
			} else { // \d{1,2}
				int hour = Integer.parseInt(matcher.group(groupOffset + 5));
				time.set(Calendar.HOUR_OF_DAY, hour);
				if (time.get(Calendar.HOUR) < 7) {
					time.set(Calendar.AM_PM, Calendar.PM);
				}
			}
		}
		
		private String timeMatchSubstr() {
			return dataString.substring(matchStartPos, matchEndPos);
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
					System.out.println(printTime(timeDetector.endTime) + "\n");
					return new TaskieTask(title, timeDetector.getEndTime());
				case EVENT:
					System.out.println(printTime(timeDetector.startTime)
							+ " till " + printTime(timeDetector.endTime) + "\n");
					return new TaskieTask(title, timeDetector.getStartTime(), timeDetector.getEndTime());
				default:
					throw new Error("Fatal error in TaskieParser#timeDetector");
			}
		}
	}
	
	private class TaskSelectorDetector {
		private final String PATTERN_DELIMITER = "\\s+|(?<=\\D)(?=\\d)";
		private String taskDataString;
		private TaskieEnum.TaskType taskType = TaskieEnum.TaskType.UNKNOWN;
		private int index = -1;
		
		public TaskSelectorDetector(String dataString) {
			Scanner sc = new Scanner(dataString);
			sc.useDelimiter(PATTERN_DELIMITER);
				
			if (sc.hasNextInt()) {
				taskType = null;
				index = sc.nextInt();
			} else if (sc.hasNext("[-/]?d")) {
				sc.next();
				taskType = TaskieEnum.TaskType.DEADLINE;
				index = sc.nextInt();
			} else if (sc.hasNext("[-/]?f")) {
				sc.next();
				taskType = TaskieEnum.TaskType.FLOAT;
				index = sc.nextInt();
			}

			this.taskDataString = sc.hasNext() ? sc.nextLine().trim() : "";
			sc.close();
		}

		public TaskieEnum.TaskType getTaskType() {
			return taskType;
		}

		public int getIndex() {
			return index;
		}

		public String getTaskDataString() {
			return taskDataString;
		}
		
	}
	
	protected static TaskieParser getInstance() {
		if (parser == null) {
			parser = new TaskieParser();
		}
		return parser;
	}
	
	private TaskieParser() {
		MAP_WEEKDAYS.put("mon", Calendar.MONDAY);
		MAP_WEEKDAYS.put("tue", Calendar.TUESDAY);
		MAP_WEEKDAYS.put("wed", Calendar.WEDNESDAY);
		MAP_WEEKDAYS.put("thu", Calendar.THURSDAY);
		MAP_WEEKDAYS.put("fri", Calendar.FRIDAY);
		MAP_WEEKDAYS.put("sat", Calendar.SATURDAY);
		MAP_WEEKDAYS.put("sun", Calendar.SUNDAY);
		
		MAP_MONTHS.put("jan", Calendar.JANUARY);
		MAP_MONTHS.put("feb", Calendar.FEBRUARY);
		MAP_MONTHS.put("mar", Calendar.MARCH);
		MAP_MONTHS.put("apr", Calendar.APRIL);
		MAP_MONTHS.put("may", Calendar.MAY);
		MAP_MONTHS.put("jun", Calendar.JUNE);
		MAP_MONTHS.put("jul", Calendar.JULY);
		MAP_MONTHS.put("aug", Calendar.AUGUST);
		MAP_MONTHS.put("sep", Calendar.SEPTEMBER);
		MAP_MONTHS.put("oct", Calendar.OCTOBER);
		MAP_MONTHS.put("nov", Calendar.NOVEMBER);
		MAP_MONTHS.put("dec", Calendar.DECEMBER);
		
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
	
	protected TaskieAction parse(String inputString) {
		System.err.println("\"" + inputString + "\"");
		if (inputString == null) {
			return new TaskieAction(TaskieEnum.Actions.INVALID, (TaskieTask) null);
		}
		
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
		
		if (tSD.getTaskType() == TaskieEnum.TaskType.UNKNOWN) {
			return new TaskieAction(TaskieEnum.Actions.DELETE, null);
		} else {
			return new TaskieAction(TaskieEnum.Actions.DELETE, tSD.getTaskType(), tSD.getIndex());
		}
	}

	private TaskieAction parseSearch(String commandData) {
		TaskCompiler tC = new TaskCompiler();
		
		return new TaskieAction(TaskieEnum.Actions.SEARCH, tC.compileTask(commandData), commandData);
	}

	private TaskieAction parseUpdate(String commandData) {
		TaskSelectorDetector tSD = new TaskSelectorDetector(commandData);
		TaskCompiler tC = new TaskCompiler();
		TaskieTask task = tC.compileTask(tSD.getTaskDataString());
		
		if (tSD.getTaskType() == TaskieEnum.TaskType.UNKNOWN) {
			return new TaskieAction(TaskieEnum.Actions.UPDATE, null);
		} else {
			return new TaskieAction(TaskieEnum.Actions.UPDATE, tSD.getTaskType(), tSD.getIndex(), task);
		}
	}

	private String printTime(Calendar time) {
		int year = time.get(Calendar.YEAR);
		int month = time.get(Calendar.MONTH);      // 0 to 11
		int day = time.get(Calendar.DAY_OF_MONTH);
		int hour = time.get(Calendar.HOUR_OF_DAY);
		int minute = time.get(Calendar.MINUTE);
		int second = time.get(Calendar.SECOND);
		
		return String.format("%4d/%02d/%02d %02d:%02d:%02d",
				year, month+1, day, hour, minute, second);
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