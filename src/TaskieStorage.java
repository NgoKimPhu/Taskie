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
<<<<<<< HEAD
	private static File eventDeadlineTask;
	private static File floatTask;
	private static ArrayList<TaskieTask> eventDeadlineTaskList;
	private static ArrayList<TaskieTask> floatTaskList;
	private static HashMap<Date, ArrayList<TaskieTask>> eventDeadlineStartDateMap;
	private static HashMap<Date, ArrayList<TaskieTask>> eventDeadlineEndDateMap;
	private static HashMap<Date, ArrayList<TaskieTask>> floatDateMap;
	private static HashMap<TaskPriority, ArrayList<TaskieTask>> eventDeadlinePriorityMap;
	private static HashMap<TaskPriority, ArrayList<TaskieTask>> floatPriorityMap;
	private static Stack<HashMap<String, Object>> commandStack;
	private static TaskComparator tc = new TaskComparator();
=======
	private File eventDeadlineTask;
	private File floatTask;
	private ArrayList<TaskieTask> eventDeadlineTaskList;
	private ArrayList<TaskieTask> floatTaskList;
	private HashMap<Date, ArrayList<TaskieTask>> eventDeadlineStartDateMap;
	private HashMap<Date, ArrayList<TaskieTask>> eventDeadlineEndDateMap;
	private HashMap<Date, ArrayList<TaskieTask>> floatDateMap;
	private HashMap<TaskPriority, ArrayList<TaskieTask>> eventDeadlinePriorityMap;
	private HashMap<TaskPriority, ArrayList<TaskieTask>> floatPriorityMap;
	private Stack<HashMap<String, Object>> commandStack;
	private TaskComparator tc = new TaskComparator();
>>>>>>> origin/master
	
	
	
	public TaskieStorage(String pathName){
		eventDeadlineTask = new File(pathName + "/eventDeadline.json");
		floatTask = new File(pathName + "/floatTask.json");
		eventDeadlineTaskList = new ArrayList<TaskieTask>();
		floatTaskList = new ArrayList<TaskieTask>();
		eventDeadlineStartDateMap = new HashMap<Date, ArrayList<TaskieTask>>();
		eventDeadlineEndDateMap = new HashMap<Date, ArrayList<TaskieTask>>();
		floatDateMap = new HashMap<Date, ArrayList<TaskieTask>>();
		eventDeadlinePriorityMap = new HashMap<TaskPriority, ArrayList<TaskieTask>>();
		floatPriorityMap = new HashMap<TaskPriority, ArrayList<TaskieTask>>();
		commandStack = new Stack<HashMap<String, Object>>();
		load();
	}
	
	public void load(){
		
	}
	// use to modify command stack after processing each command.
	public static Stack<HashMap<String, Object>> getCommandStack(){
		return commandStack;
	}
	public static boolean clearCommandStack(){
		commandStack.clear();
		return commandStack.isEmpty();
	}
	public static void pushCommand(HashMap<String, Object> reverseCommandAndContent){
		commandStack.push(reverseCommandAndContent);
	}
	public static HashMap<String, Object> popCommand(){
		return commandStack.pop();
	}
	public static HashMap<String, Object> peekCommand(){
		return commandStack.peek();
	}
	
<<<<<<< HEAD
	public static ArrayList<TaskieTask> displayEventDeadline(){
		return eventDeadlineTaskList;
	}
	public static ArrayList<TaskieTask> displayFloatTask(){
		return floatTaskList;
	}
	public static ArrayList<TaskieTask> addTask(TaskieTask task){
		if(task.getType().equals(TaskType.EVENT)||task.getType().equals(TaskType.DEADLINE)){
			eventDeadlineTaskList.add(task);
			Collections.sort(eventDeadlineTaskList, tc);
			if(!eventDeadlineStartDateMap.containsKey(task.getStartTime())){
				ArrayList<TaskieTask> tasks = new ArrayList<TaskieTask>();
=======
	public ArrayList<TaskieTask> displayEventDeadline(){
		return this.eventDeadlineTaskList;
	}
	public ArrayList<TaskieTask> displayFloatTask(){
		return this.floatTaskList;
	}
	public ArrayList<TaskieTask> addTask(TaskieTask task, TaskType type){
		if(type.equals(TaskType.EVENT)||type.equals(TaskType.DEADLINE)){
			this.eventDeadlineTaskList.add(task);
			Collections.sort(this.eventDeadlineTaskList, tc);
			if(!this.eventDeadlineStartDateMap.containsKey(task.getStartTime())){
				ArrayList<TaskieTask> tasks = new ArrayList<TaskieTask>();
>>>>>>> origin/master
				tasks.add(task);
				eventDeadlineStartDateMap.put(task.getStartTime(), tasks);
			}
			else{
				eventDeadlineStartDateMap.get(task.getStartTime()).add(task);
			}
<<<<<<< HEAD
			if(!eventDeadlineEndDateMap.containsKey(task.getEndTime())){
				ArrayList<TaskieTask> tasks = new ArrayList<TaskieTask>();
=======
			if(!this.eventDeadlineEndDateMap.containsKey(task.getEndTime())){
				ArrayList<TaskieTask> tasks = new ArrayList<TaskieTask>();
>>>>>>> origin/master
				tasks.add(task);
				eventDeadlineEndDateMap.put(task.getEndTime(), tasks);
			}
			else{
				eventDeadlineEndDateMap.get(task.getEndTime()).add(task);
			}
<<<<<<< HEAD
			if(!eventDeadlinePriorityMap.containsKey(task.getPriority())){
				ArrayList<TaskieTask> tasks = new ArrayList<TaskieTask>();
=======
			if(!this.eventDeadlinePriorityMap.containsKey(task.getPriority())){
				ArrayList<TaskieTask> tasks = new ArrayList<TaskieTask>();
>>>>>>> origin/master
				tasks.add(task);
				eventDeadlinePriorityMap.put(task.getPriority(), tasks);
			}
			else{
				eventDeadlinePriorityMap.get(task.getPriority()).add(task);
			}
			return eventDeadlineTaskList;
		}
		else{
<<<<<<< HEAD
			floatTaskList.add(task);
			Collections.sort(floatTaskList, tc);
			if(!floatDateMap.containsKey(task.getStartTime())){
				ArrayList<TaskieTask> tasks = new ArrayList<TaskieTask>();
=======
			this.floatTaskList.add(task);
			Collections.sort(this.floatTaskList, tc);
			if(!this.floatDateMap.containsKey(task.getStartTime())){
				ArrayList<TaskieTask> tasks = new ArrayList<TaskieTask>();
>>>>>>> origin/master
				tasks.add(task);
				floatDateMap.put(task.getStartTime(), tasks);
			}
			else{
				floatDateMap.get(task.getStartTime()).add(task);
			}
<<<<<<< HEAD
			if(!floatPriorityMap.containsKey(task.getPriority())){
				ArrayList<TaskieTask> tasks = new ArrayList<TaskieTask>();
=======
			if(!this.floatPriorityMap.containsKey(task.getPriority())){
				ArrayList<TaskieTask> tasks = new ArrayList<TaskieTask>();
>>>>>>> origin/master
				tasks.add(task);
				floatPriorityMap.put(task.getPriority(), tasks);
			}
			else{
				floatPriorityMap.get(task.getPriority()).add(task);
			}
			return floatTaskList;
		}
	}
	
<<<<<<< HEAD
	public static ArrayList<TaskieTask> deleteTask(int index, TaskType type){
		if(type.equals(TaskType.EVENT) || type.equals(TaskType.DEADLINE)){
			Task task = eventDeadlineTaskList.remove(index);
			eventDeadlineStartDateMap.get(task.getStartTime()).remove(task);
			if(eventDeadlineStartDateMap.get(task.getStartTime()).size()==0){
				eventDeadlineStartDateMap.remove(task.getStartTime());
=======
	public ArrayList<TaskieTask> deleteTask(int index, TaskType type){
		if(type.equals(TaskType.EVENT) || type.equals(TaskType.DEADLINE)){
			TaskieTask task = this.eventDeadlineTaskList.remove(index);
			this.eventDeadlineStartDateMap.get(task.getStartTime()).remove(task);
			if(this.eventDeadlineStartDateMap.get(task.getStartTime()).size()==0){
				this.eventDeadlineStartDateMap.remove(task.getStartTime());
>>>>>>> origin/master
			}
			eventDeadlineEndDateMap.get(task.getEndTime()).remove(task);
			if(eventDeadlineEndDateMap.get(task.getEndTime()).size()==0){
				eventDeadlineEndDateMap.remove(task.getEndTime());
			}
			eventDeadlinePriorityMap.get(task.getPriority()).remove(task);
			if(eventDeadlinePriorityMap.get(task.getPriority()).size()==0){
				eventDeadlinePriorityMap.remove(task.getPriority());
			}
			return eventDeadlineTaskList;
		}
		else{
<<<<<<< HEAD
			Task task = floatTaskList.get(index);
			floatDateMap.get(task.getStartTime()).remove(task);
			if(floatDateMap.get(task.getStartTime()).size()==0){
				floatDateMap.remove(task.getStartTime());
=======
			TaskieTask task = this.floatTaskList.get(index);
			this.floatDateMap.get(task.getStartTime()).remove(task);
			if(this.floatDateMap.get(task.getStartTime()).size()==0){
				this.floatDateMap.remove(task.getStartTime());
>>>>>>> origin/master
			}
			floatPriorityMap.get(task.getPriority()).remove(task);
			if(floatPriorityMap.get(task.getPriority()).size()==0){
				floatPriorityMap.remove(task.getPriority());
			}
			return floatTaskList;
		}
	}
	
	/*
	public static ArrayList<TaskieTask> deleteTask(String keyWord, TaskType type){
		keyWord = keyWord.toUpperCase();
		if(type.equals(TaskType.EVENT) || type.equals(TaskType.DEADLINE)){
<<<<<<< HEAD
			for(Task task: eventDeadlineTaskList){
=======
			for(TaskieTask task: this.eventDeadlineTaskList){
>>>>>>> origin/master
				String stringForCompare = new String(task.getTitle());
				if(stringForCompare.toUpperCase().contains(keyWord)){
					eventDeadlineTaskList.remove(task);
					eventDeadlineStartDateMap.get(task.getStartTime()).remove(task);
					if(eventDeadlineStartDateMap.get(task.getStartTime()).size()==0){
						eventDeadlineStartDateMap.remove(task.getStartTime());
					}
					eventDeadlineEndDateMap.get(task.getEndTime()).remove(task);
					if(eventDeadlineEndDateMap.get(task.getEndTime()).size()==0){
						eventDeadlineEndDateMap.remove(task.getEndTime());
					}
					eventDeadlinePriorityMap.get(task.getPriority()).remove(task);
					if(eventDeadlinePriorityMap.get(task.getPriority()).size()==0){
						eventDeadlinePriorityMap.remove(task.getPriority());
					}
				}
			}
			return eventDeadlineTaskList;
		}
		else{
<<<<<<< HEAD
			for(Task task: floatTaskList){
=======
			for(TaskieTask task: this.floatTaskList){
>>>>>>> origin/master
				String stringForCompare = new String(task.getTitle());
				if(stringForCompare.toUpperCase().contains(keyWord)){
					floatTaskList.remove(task);
					if(floatDateMap.get(task.getStartTime()).size()==0){
						floatDateMap.remove(task.getStartTime());
					}
					floatPriorityMap.get(task.getPriority()).remove(task);
					if(floatPriorityMap.get(task.getPriority()).size()==0){
						floatPriorityMap.remove(task.getPriority());
					}
				}
			}
			return floatTaskList;
		}
	}
	public ArrayList<TaskieTask> deleteTask(TaskPriority priority, TaskType type){
		if(type.equals(TaskType.EVENT) || type.equals(TaskType.DEADLINE)){
			ArrayList<TaskieTask> taskListToDelete = this.eventDeadlinePriorityMap.get(priority);
			this.eventDeadlinePriorityMap.remove(priority, taskListToDelete);
			for(Task task: taskListToDelete){
				this.eventDeadlineTaskList.remove(task);
			}
			return this.eventDeadlineTaskList;
		}
		else{
			ArrayList<TaskieTask> taskListToDelete = this.floatPriorityMap.get(priority);
			this.floatPriorityMap.remove(priority, taskListToDelete);
			for(Task task: taskListToDelete){
				this.floatTaskList.remove(task);
			}
			return this.floatTaskList;
		}
	}
	*/
	
	// if you want to search all the tasks contains the key words, search twice
	public static ArrayList<IndexTaskPair> searchTask(ArrayList<String> keyWords, TaskType type){
		for(String keyWord: keyWords){
			keyWord = keyWord.toUpperCase();
		}
		if(type.equals(TaskType.EVENT) || type.equals(TaskType.DEADLINE)){
			ArrayList<IndexTaskPair> searchResult = new ArrayList<IndexTaskPair>();
			for(Task task: eventDeadlineTaskList){
				boolean check = true;
				String stringForCompare = new String(task.getTitle());
				for(String keyWord: keyWords){
					if(!stringForCompare.toUpperCase().contains(keyWord)){
						check = false;
					}
				}
				if(check == true){
					IndexTaskPair pair = new IndexTaskPair(eventDeadlineTaskList.indexOf(task), task);
					searchResult.add(pair);
				}
			}
			return searchResult;
		}
		else{
			ArrayList<IndexTaskPair> searchResult = new ArrayList<IndexTaskPair>();
			for(Task task: floatTaskList){
				boolean check = true;
				String stringForCompare = new String(task.getTitle());
				for(String keyWord: keyWords){
					if(!stringForCompare.toUpperCase().contains(keyWord)){
						check = false;
					}
				}
				if(check == true){
					IndexTaskPair pair = new IndexTaskPair(floatTaskList.indexOf(task), task);
					searchResult.add(pair);
				}
			}
			return searchResult;
		}
	}
	public static void markDown(int index, TaskType type){
		if(type.equals(TaskType.EVENT) || type.equals(TaskType.DEADLINE)){
			eventDeadlineTaskList.get(index).setStatus(true);
		} 
		else{
			floatTaskList.get(index).setStatus(true);
		}
	}
	public void update(){
		
	}
}


class FileHandler{	
	private static final Pattern EVENT_DEADLINE_TASK_LINE_PATTERN = Pattern.compile("^(EVENT|DEADLINE)\\s(.+)\\s(.+)\\s(.+)\\s([01234])\\s\\([01])\\n$");
	private static final Pattern FLOAT_TASK_LINE_PATTERN = Pattern.compile("^(FLOAT)\\s(.+)\\s(.+)\\s(.+)\\s([01234])\\s\\([01])\\n$");
	private static final SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm");
	public static ArrayList<TaskieTask> readFile(File file) {
		String fileName = file.getName();
		String line = new String();
		ArrayList<TaskieTask> fileContent = new ArrayList<TaskieTask>();
		try {
			BufferedReader in = new BufferedReader(new FileReader(fileName));
			line = in.readLine();
			while (line != null) {
				Matcher matcher1 = EVENT_DEADLINE_TASK_LINE_PATTERN.matcher(line);
				TaskieTask task = null;
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
