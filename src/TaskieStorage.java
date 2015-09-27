/**
 * The Taskie storage component
 * @author Qin Xueying
 *
 */
import java.util.*;
import java.io.*;
import java.util.regex.*;
import java.text.*;
import org.json.*;

public class TaskieStorage {
	private File eventDeadlineTask;
	private File floatTask;
	private ArrayList<Task> eventDeadlineTaskList;
	private ArrayList<Task> floatTaskList;
	private HashMap<Date, ArrayList<Task>> eventDeadlineStartDateMap;
	private HashMap<Date, ArrayList<Task>> eventDeadlineEndDateMap;
	private HashMap<Date, ArrayList<Task>> floatDateMap;
	private HashMap<TaskPriority, ArrayList<Task>> eventDeadlinePriorityMap;
	private HashMap<TaskPriority, ArrayList<Task>> floatPriorityMap;
	private Stack<HashMap<String, Object>> commandStack;
	private TaskComparator tc = new TaskComparator();
	
	
	
	public TaskieStorage(String pathName){
		eventDeadlineTask = new File(pathName + "/eventDeadline.json");
		floatTask = new File(pathName + "/floatTask.json");
		eventDeadlineTaskList = new ArrayList<Task>();
		floatTaskList = new ArrayList<Task>();
		eventDeadlineStartDateMap = new HashMap<Date, ArrayList<Task>>();
		eventDeadlineEndDateMap = new HashMap<Date, ArrayList<Task>>();
		floatDateMap = new HashMap<Date, ArrayList<Task>>();
		eventDeadlinePriorityMap = new HashMap<TaskPriority, ArrayList<Task>>();
		floatPriorityMap = new HashMap<TaskPriority, ArrayList<Task>>();
		commandStack = new Stack<HashMap<String, Object>>();
		
	}
	
	public void load(){
		
	}
	
	public ArrayList<Task> displayEventDeadline(){
		return this.eventDeadlineTaskList;
	}
	public ArrayList<Task> displayFloatTask(){
		return this.floatTaskList;
	}
	public ArrayList<Task> addEventDeadline(Task task){
		this.eventDeadlineTaskList.add(task);
		Collections.sort(this.eventDeadlineTaskList, tc);
		if(!this.eventDeadlineStartDateMap.containsKey(task.getStartTime())){
			ArrayList<Task> tasks = new ArrayList<Task>();
			tasks.add(task);
			this.eventDeadlineStartDateMap.put(task.getStartTime(), tasks);
		}
		else{
			this.eventDeadlineStartDateMap.get(task.getStartTime()).add(task);
		}
		if(!this.eventDeadlineEndDateMap.containsKey(task.getEndTime())){
			ArrayList<Task> tasks = new ArrayList<Task>();
			tasks.add(task);
			this.eventDeadlineEndDateMap.put(task.getEndTime(), tasks);
		}
		else{
			this.eventDeadlineEndDateMap.get(task.getEndTime()).add(task);
		}
		if(!this.eventDeadlinePriorityMap.containsKey(task.getPriority())){
			ArrayList<Task> tasks = new ArrayList<Task>();
			tasks.add(task);
			this.eventDeadlinePriorityMap.put(task.getPriority(), tasks);
		}
		else{
			this.eventDeadlinePriorityMap.get(task.getPriority()).add(task);
		}
		return this.eventDeadlineTaskList;
	}
	public ArrayList<Task> addFloat(Task task){
		this.floatTaskList.add(task);
		Collections.sort(this.floatTaskList, tc);
		if(!this.floatDateMap.containsKey(task.getStartTime())){
			ArrayList<Task> tasks = new ArrayList<Task>();
			tasks.add(task);
			this.floatDateMap.put(task.getStartTime(), tasks);
		}
		else{
			this.floatDateMap.get(task.getStartTime()).add(task);
		}
		if(!this.floatPriorityMap.containsKey(task.getPriority())){
			ArrayList<Task> tasks = new ArrayList<Task>();
			tasks.add(task);
			this.floatPriorityMap.put(task.getPriority(), tasks);
		}
		else{
			this.floatPriorityMap.get(task.getPriority()).add(task);
		}
		return this.floatTaskList;
	}
}

class FileHandler{	
	private static final Pattern EVENT_DEADLINE_TASK_LINE_PATTERN = Pattern.compile("^(EVENT|DEADLINE)\\s(.+)\\s(.+)\\s(.+)\\s([01234])\\s\\([01])\\n$");
	private static final Pattern FLOAT_TASK_LINE_PATTERN = Pattern.compile("^(FLOAT)\\s(.+)\\s(.+)\\s(.+)\\s([01234])\\s\\([01])\\n$");
	private static final SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm");
	public static ArrayList<Task> readFile(File file) {
		String fileName = file.getName();
		String line = new String();
		ArrayList<Task> fileContent = new ArrayList<Task>();
		try {
			BufferedReader in = new BufferedReader(new FileReader(fileName));
			line = in.readLine();
			while (line != null) {
				Matcher matcher1 = EVENT_DEADLINE_TASK_LINE_PATTERN.matcher(line);
				Task task = null;
				if(matcher1.matches()){
					System.out.println("true");
					TaskType taskType = getTaskType(matcher1.group(1));
					String taskTitle = matcher1.group(2);
					Date startTime = getDate(matcher1.group(3));
					Date endTime = getDate(matcher1.group(4));
					TaskPriority priority = getTaskPriority(matcher1.group(5));
					//task = new Task(); 
				}
				line = in.readLine();
			}
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return fileContent;
	}
	
	// Write content in to a file.
	public static void writeFile(File file, int fileSize, String content) {
		String fileName = file.getName();
		try {
			FileWriter writer = new FileWriter(fileName, true);
			// If it is an empty file, write into it directly.
			if(fileSize == 0){
				writer.write(content);
			}
			// If it is not an empty file, begin with a new line.
			else{
				writer.write("\n" + content);
			}
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	private static boolean isValidDate(String string){
		boolean isValid = true;
		return isValid;
	}
	private static Date getDate(String string){
		Date date = null;
		Calendar calendar = Calendar.getInstance();
		String[] splitString= string.split(" ");
		String[] dayMonthYear = splitString[0].split("-");
		String[] hourMinute = splitString[1].split(":");
		int day = Integer.valueOf(dayMonthYear[0]);
		int month = Integer.valueOf(dayMonthYear[1]);
		int year = Integer.valueOf(dayMonthYear[2]);
		int hour = Integer.valueOf(hourMinute[0]);
		int minute = Integer.valueOf(hourMinute[1]);
		calendar.set(year, month, day, hour, minute);
		date = calendar.getTime();
		return date;
	}
	private static TaskType getTaskType(String string){
		if(isEvent(string)){
			return TaskType.EVENT;
		}
		else if(isDeadline(string)){
			return TaskType.DEADLINE;
		}
		else{
			return TaskType.FLOAT;
		}
	}
	private static boolean isEvent(String string){
		return string.equalsIgnoreCase("EVENT");
	}
	private static boolean isDeadline(String string){
		return string.equalsIgnoreCase("DEADLINE");
	}
	private static boolean isFloat(String string){
		return string.equalsIgnoreCase("FLOAT");
	}
	private static TaskPriority getTaskPriority(String string) {
		if(string.equals("0")){
			return TaskPriority.VERY_HIGH;
		}
		else if(string.equals("1")){
			return TaskPriority.HIGH;
		}
		else if(string.equals("2")){
			return TaskPriority.MEDIUM;
		}
		else if(string.equals("3")){
			return TaskPriority.LOW;
		}
		else{
			return TaskPriority.VERY_LOW;
		}
	}
}
