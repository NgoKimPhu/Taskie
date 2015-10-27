package fancy4.taskie.model;

import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class TimeDetector {
	private static final String PATTERN_TYPE = "(?:(?:-)?\\b(?:float|event|deadline))?\\s?";
	private static final String PATTERN_DAY = "\\b(tonight|(?:today|tomorrow|tmr)\\s?(?:night)?)|"
		+ "(?:(?:next\\s)?((?:Mon|Fri|Sun)(?:day)?|Tue(?:sday)?|Wed(?:nesday)?|"
		+ "Thu(?:rsday)?|Sat(?:urday)?))|"
		+ "(?:(\\d{1,2})\\s?[\\\\\\/-]\\s?(\\d{1,2}))|"
		+ "(?:(\\d{1,2}\\s?)?(Jan(?:uary)?|Feb(?:ruary)?|Mar(?:ch)?|Apr(?:il)?|May|"
		+ "Jun(?:e)?|Jul(?:y)?|Aug(?:ust)?|Sep(?:tember)?|Oct(?:ober)?|(?:Nov|Dec)(?:ember)?)"
		+ "\\s?(\\d{1,2})?)";
	private static final String PATTERN_TIME = "(?:\\b(?:(?<=fr(?:om)?|-|~|to|till|until)|at|by|due))?"
		+ "\\s?(?:(?:(\\d{1,2})\\s?"
		+ "(?=[.:h ]\\s?\\d{1,2}\\s?m?|am|pm|tonight|(?:today|tomorrow|tmr)\\s?(?:night)?)"
		+ "(?:[.:h ]\\s?(\\d{1,2})\\s?m?)?\\s?(am|pm)?\\s?"
		+ "(tonight|(?:today|tomorrow|tmr)\\s?(?:night)?)?)\\b|(now|(?:to|tmr |tomorrow )night)\\b)|"
		+ "(?:\\b(?:(?<=fr(?:om)?|-|~|to|till|until)|at|by|due))\\s?(\\d{1,2})\\b";
	private static final String PATTERN_TIMERANGE_FORMAT = "(?:fr(?:om)?\\s?)?"
		+ "(?:%1$s)\\s?(?:%2$s)?\\s?(?:-|~|to|till|until)?\\s?(?:%2$s)?\\s?(?:%1$s)";
	
	private  final Map<String, Integer> MAP_WEEKDAYS = initWeekdaysMap();
	private  final Map<String, Integer> MAP_MONTHS = initMonthsMap();
	
	private  Map<String, Integer> initWeekdaysMap() {
		Map<String, Integer> map = new HashMap<String, Integer>();
		map.put("mon", Calendar.MONDAY);
		map.put("tue", Calendar.TUESDAY);
		map.put("wed", Calendar.WEDNESDAY);
		map.put("thu", Calendar.THURSDAY);
		map.put("fri", Calendar.FRIDAY);
		map.put("sat", Calendar.SATURDAY);
		map.put("sun", Calendar.SUNDAY);
		return Collections.unmodifiableMap(map);
	}
	
	private  Map<String, Integer> initMonthsMap() {
		Map<String, Integer> map = new HashMap<String, Integer>();
		map.put("jan", Calendar.JANUARY);
		map.put("feb", Calendar.FEBRUARY);
		map.put("mar", Calendar.MARCH);
		map.put("apr", Calendar.APRIL);
		map.put("may", Calendar.MAY);
		map.put("jun", Calendar.JUNE);
		map.put("jul", Calendar.JULY);
		map.put("aug", Calendar.AUGUST);
		map.put("sep", Calendar.SEPTEMBER);
		map.put("oct", Calendar.OCTOBER);
		map.put("nov", Calendar.NOVEMBER);
		map.put("dec", Calendar.DECEMBER);
		return Collections.unmodifiableMap(map);
	}
	
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