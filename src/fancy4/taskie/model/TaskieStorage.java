package fancy4.taskie.model;
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

	private static File eventDeadlineTask;
	private static File floatTask;
	private static ArrayList<TaskieTask> eventDeadlineTaskList;
	private static ArrayList<TaskieTask> floatTaskList;
	private static HashMap<Date, ArrayList<TaskieTask>> eventStartDateMap;
	private static HashMap<Date, ArrayList<TaskieTask>> eventDeadlineEndDateMap;
	//private static HashMap<Date, ArrayList<TaskieTask>> floatDateMap;
	private static HashMap<TaskieEnum.TaskPriority, ArrayList<TaskieTask>> eventDeadlinePriorityMap;
	private static HashMap<TaskieEnum.TaskPriority, ArrayList<TaskieTask>> floatPriorityMap;
	private static Stack<HashMap<String, Object>> commandStack;
	private static TaskComparator tc = new TaskComparator();

	public static void load(String pathName) throws Exception {
		eventDeadlineTask = new File(pathName +"/eventDeadline.json");
		floatTask = new File(pathName+"/floatTask.json");
		eventStartDateMap = new HashMap<Date, ArrayList<TaskieTask>>();
		eventDeadlineEndDateMap = new HashMap<Date, ArrayList<TaskieTask>>();
		eventDeadlinePriorityMap = new HashMap<TaskieEnum.TaskPriority, ArrayList<TaskieTask>>();
		floatPriorityMap = new HashMap<TaskieEnum.TaskPriority, ArrayList<TaskieTask>>();
		commandStack = new Stack<HashMap<String, Object>>();
		if(eventDeadlineTask.exists()){
			eventDeadlineTaskList = FileHandler.readFile(eventDeadlineTask);
			for(TaskieTask task: eventDeadlineTaskList){
				// deal with event start time
				if(TaskieTask.isEvent(task)){
					//keep two copies of date-task pair in map, one with specific time one without
					Date start = task.getStartTime();
					Date startKey = createDateKey(start);
					if(!eventStartDateMap.containsKey(start)){
						eventStartDateMap.put(start, new ArrayList<TaskieTask>());
					}
					eventStartDateMap.get(start).add(task);
					if(!eventStartDateMap.containsKey(startKey)){
						eventStartDateMap.put(startKey, new ArrayList<TaskieTask>());
					}
					eventStartDateMap.get(startKey).add(task);	
				}
				//deal with end time
				Date end = task.getEndTime();
				Date endKey = createDateKey(end);
				if(!eventDeadlineEndDateMap.containsKey(end)){
					eventDeadlineEndDateMap.put(end, new ArrayList<TaskieTask>());
				}
				eventDeadlineEndDateMap.get(end).add(task);
				if(!eventDeadlineEndDateMap.containsKey(endKey)){
					eventDeadlineEndDateMap.put(endKey, new ArrayList<TaskieTask>());
				}
				eventDeadlineEndDateMap.get(endKey).add(task);
				//deal with priority
				TaskieEnum.TaskPriority priority = task.getPriority();
				if(!eventDeadlinePriorityMap.containsKey(priority)){
					eventDeadlinePriorityMap.put(priority, new ArrayList<TaskieTask>());
				}
				eventDeadlinePriorityMap.get(priority).add(task);
			}
			
		}
		else{
			eventDeadlineTaskList = new ArrayList<TaskieTask>();
		}
		if(floatTask.exists()){
			floatTaskList = FileHandler.readFile(floatTask);
			for(TaskieTask task: floatTaskList){
				TaskieEnum.TaskPriority priority = task.getPriority();
				if(!floatPriorityMap.containsKey(priority)){
					floatPriorityMap.put(priority, new ArrayList<TaskieTask>());
				}
				floatPriorityMap.get(priority).add(task);
			}
		}
		else{
			floatTaskList = new ArrayList<TaskieTask>();
		}
	}
	private static Date createDateKey(Date date){
		Calendar calendar = Calendar.getInstance();
		Calendar calendarForKey = Calendar.getInstance();
		calendar.setTime(date);
		calendarForKey.set(Calendar.YEAR, calendar.get(Calendar.YEAR));
		calendarForKey.set(Calendar.MONTH, calendar.get(Calendar.MONTH));
		calendarForKey.set(Calendar.DATE, calendar.get(Calendar.DATE));
		Date key = calendarForKey.getTime();
		return key;
	}

	// use to modify command stack after processing each command.
	public static Stack<HashMap<String, Object>> getCommandStack() {
		return commandStack;
	}

	public static boolean clearCommandStack() {
		commandStack.clear();
		return commandStack.isEmpty();
	}

	public static void pushCommand(
			HashMap<String, Object> reverseCommandAndContent) {
		commandStack.push(reverseCommandAndContent);
	}

	public static HashMap<String, Object> popCommand() {
		return commandStack.pop();
	}

	public static HashMap<String, Object> peekCommand() {
		return commandStack.peek();
	}

	public static ArrayList<TaskieTask> displayEventDeadline() {
		return eventDeadlineTaskList;
	}

	public static ArrayList<TaskieTask> displayFloatTask() {
		return floatTaskList;
	}
	
	// add task
	public static ArrayList<TaskieTask> addTask(TaskieTask task) {
		if (TaskieTask.isEvent(task)
				|| TaskieTask.isDeadline(task)) {
			eventDeadlineTaskList.add(task);
			Collections.sort(eventDeadlineTaskList, tc);
			FileHandler.clearFile(eventDeadlineTask);
			for(TaskieTask t: eventDeadlineTaskList){
				//System.out.println(t.getTitle());
				FileHandler.writeFile(eventDeadlineTask, t, t.getType());
			}
			if(TaskieTask.isEvent(task)){
				if (!eventStartDateMap.containsKey(task.getStartTime())) {
					ArrayList<TaskieTask> tasks = new ArrayList<TaskieTask>();

					tasks.add(task);
					eventStartDateMap.put(task.getStartTime(), tasks);
				} else {
					eventStartDateMap.get(task.getStartTime()).add(task);
				}
			}

			if (!eventDeadlineEndDateMap.containsKey(task.getEndTime())) {
				ArrayList<TaskieTask> tasks = new ArrayList<TaskieTask>();

				tasks.add(task);
				eventDeadlineEndDateMap.put(task.getEndTime(), tasks);
			} else {
				eventDeadlineEndDateMap.get(task.getEndTime()).add(task);
			}

			if (!eventDeadlinePriorityMap.containsKey(task.getPriority())) {
				ArrayList<TaskieTask> tasks = new ArrayList<TaskieTask>();

				tasks.add(task);
				eventDeadlinePriorityMap.put(task.getPriority(), tasks);
			} else {
				eventDeadlinePriorityMap.get(task.getPriority()).add(task);
			}
			return eventDeadlineTaskList;
		} else {
			floatTaskList.add(task);
			FileHandler.writeFile(floatTask, task, task.getType());

			if (!floatPriorityMap.containsKey(task.getPriority())) {
				ArrayList<TaskieTask> tasks = new ArrayList<TaskieTask>();

				tasks.add(task);
				floatPriorityMap.put(task.getPriority(), tasks);
			} else {
				floatPriorityMap.get(task.getPriority()).add(task);
			}
			return floatTaskList;
		}
	}

	public static ArrayList<TaskieTask> deleteTask(int index, TaskieEnum.TaskType type) {
		if (type.equals(TaskieEnum.TaskType.EVENT) || type.equals(TaskieEnum.TaskType.DEADLINE)) {
			TaskieTask task = eventDeadlineTaskList.remove(index);
<<<<<<< HEAD
			eventDeadlineStartDateMap.get(task.getStartTime()).remove(task);
			if (eventDeadlineStartDateMap.get(task.getStartTime()).size() == 0) {
				eventDeadlineStartDateMap.remove(task.getStartTime());
=======
			if(TaskieTask.isEvent(task)){
				eventStartDateMap.get(task.getStartTime()).remove(task);
				if (eventStartDateMap.get(task.getStartTime()).size() == 0) {
					eventStartDateMap.remove(task.getStartTime());
				}
>>>>>>> 1b703a4... edit load
			}
			eventDeadlineEndDateMap.get(task.getEndTime()).remove(task);
			if (eventDeadlineEndDateMap.get(task.getEndTime()).size() == 0) {
				eventDeadlineEndDateMap.remove(task.getEndTime());
			}
			eventDeadlinePriorityMap.get(task.getPriority()).remove(task);
			if (eventDeadlinePriorityMap.get(task.getPriority()).size() == 0) {
				eventDeadlinePriorityMap.remove(task.getPriority());
			}
			return eventDeadlineTaskList;
		} else {

			TaskieTask task = floatTaskList.get(index);
			floatPriorityMap.get(task.getPriority()).remove(task);
			if (floatPriorityMap.get(task.getPriority()).size() == 0) {
				floatPriorityMap.remove(task.getPriority());
			}
			return floatTaskList;
		}
	}

	// if you want to search all the tasks contains the key words, search twice
	public static ArrayList<IndexTaskPair> searchTask(
			ArrayList<String> keyWords, TaskieEnum.TaskType type) {
		for (String keyWord : keyWords) {
			keyWord = keyWord.toUpperCase();
		}
		if (type.equals(TaskieEnum.TaskType.EVENT) || type.equals(TaskieEnum.TaskType.DEADLINE)) {
			ArrayList<IndexTaskPair> searchResult = new ArrayList<IndexTaskPair>();
			for (TaskieTask task : eventDeadlineTaskList) {
				boolean check = true;
				String stringForCompare = new String(task.getTitle());
				for (String keyWord : keyWords) {
					if (!stringForCompare.toUpperCase().contains(keyWord)) {
						check = false;
					}
				}
				if (check == true) {
					IndexTaskPair pair = new IndexTaskPair(
							eventDeadlineTaskList.indexOf(task), task);
					searchResult.add(pair);
				}
			}
			return searchResult;
		} else {
			ArrayList<IndexTaskPair> searchResult = new ArrayList<IndexTaskPair>();
			for (TaskieTask task : floatTaskList) {
				boolean check = true;
				String stringForCompare = new String(task.getTitle());
				for (String keyWord : keyWords) {
					if (!stringForCompare.toUpperCase().contains(keyWord)) {
						check = false;
					}
				}
				if (check == true) {
					IndexTaskPair pair = new IndexTaskPair(
							floatTaskList.indexOf(task), task);
					searchResult.add(pair);
				}
			}
			return searchResult;
		}
	}

	public static void markDown(int index, TaskieEnum.TaskType type) {
		if (type.equals(TaskieEnum.TaskType.EVENT) || type.equals(TaskieEnum.TaskType.DEADLINE)) {
			eventDeadlineTaskList.get(index).setStatus(true);
		} else {
			floatTaskList.get(index).setStatus(true);
		}
	}

	public void update() {

	}
}

class FileHandler {
	private static final SimpleDateFormat sdf = new SimpleDateFormat(
			"dd-MM-yyyy HH:mm");

	public static ArrayList<TaskieTask> readFile(File file) throws Exception {
		String fileName = file.getName();
		String line = new String();
		ArrayList<TaskieTask> fileContent = new ArrayList<TaskieTask>();
		try {
			BufferedReader in = new BufferedReader(new FileReader(fileName));
			line = in.readLine();
			while (line != null) {
				JSONObject taskLine= new JSONObject(line);
				JSONObject taskData = taskLine.getJSONObject("task");
				String title= taskData.getString("title");
				TaskieEnum.TaskType type = getTaskType(taskData.getInt("type"));
				Date start = type.equals(TaskieEnum.TaskType.DEADLINE)? null:getDate(taskData.getString("start-time"));
				Date end = getDate(taskData.getString("end-time"));
				TaskieEnum.TaskPriority priority = getTaskPriority(taskData.getInt("priority"));
				boolean status = taskData.getBoolean("status");
				String description = taskData.getString("description");
				TaskieTask task = new TaskieTask(title, type, start, end, priority, status, description);
				fileContent.add(task);
				line = in.readLine();
			}
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return fileContent;
	}
	public static ArrayList<TaskieTask> readFloatFile(File file) throws Exception {
		String fileName = file.getName();
		String line = new String();
		ArrayList<TaskieTask> fileContent = new ArrayList<TaskieTask>();
		try {
			BufferedReader in = new BufferedReader(new FileReader(fileName));
			line = in.readLine();
			while (line != null) {
				JSONObject taskLine= new JSONObject(line);
				JSONObject taskData = taskLine.getJSONObject("task");
				String title= taskData.getString("title");
				TaskieEnum.TaskType type = getTaskType(taskData.getInt("type"));
				//Date start = getDate(taskData.getString("start-time"));
				TaskieEnum.TaskPriority priority = getTaskPriority(taskData.getInt("priority"));
				boolean status = taskData.getBoolean("status");
				String description = taskData.getString("description");
				TaskieTask task = new TaskieTask(title, type,  priority, status, description);
				fileContent.add(task);
				line = in.readLine();
			}
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return fileContent;
	}

	// Write content in to a file.
	public static void writeFile(File file, TaskieTask task, TaskieEnum.TaskType type) {
		String fileName = file.getName();
		if (type.equals(TaskieEnum.TaskType.EVENT) || type.equals(TaskieEnum.TaskType.DEADLINE)) {
			try {
				FileWriter writer = new FileWriter(fileName, true);
				JSONWriter jWriter = new JSONWriter(writer);
				jWriter.object();
				jWriter.key("task");
				jWriter.object();
				jWriter.key("title");
				jWriter.value(task.getTitle());
				
				jWriter.key("type");
				jWriter.value(task.getType().ordinal());
				
				jWriter.key("start-time");
				jWriter.value(type.equals(TaskieEnum.TaskType.DEADLINE)? "null": sdf.format(task.getStartTime()));
				
				jWriter.key("end-time");
				jWriter.value(sdf.format(task.getEndTime()));
				
				jWriter.key("priority");
				jWriter.value(task.getPriority().ordinal());
			
				jWriter.key("status");
				jWriter.value(task.getStatus());
				
				jWriter.key("description");
				jWriter.value(task.getDescription() == null ? "null" : task
						.getDescription());
			
				jWriter.endObject();
				jWriter.endObject();
				writer.write("\n");
				writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		else{
			try {
				FileWriter writer = new FileWriter(fileName, true);
				JSONWriter jWriter = new JSONWriter(writer);
				jWriter.object();
				jWriter.key("task");
				
				jWriter.object();
				jWriter.key("title");
				jWriter.value(task.getTitle());
				
				jWriter.key("type");
				jWriter.value(task.getType().ordinal());
				
				//jWriter.key("start-time");
				//jWriter.value(sdf.format(task.getStartTime()));
				
				jWriter.key("priority");
				jWriter.value(task.getPriority().ordinal());
			
				jWriter.key("status");
				jWriter.value(task.getStatus());
				
				jWriter.key("description");
				jWriter.value(task.getDescription() == null ? "null" : task
						.getDescription());
			
				jWriter.endObject();
				jWriter.endObject();
				writer.write("\n");
				writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static void clearFile(File file){
		String fileName = file.getName();
		try {
			FileWriter writer = new FileWriter(fileName);
			writer.write("");
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static boolean isValidDate(String string) {
		boolean isValid = true;
		return isValid;
	}

	private static Date getDate(String string) {
		Date date = null;
		Calendar calendar = Calendar.getInstance();
		String[] splitString = string.split(" ");
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

	private static TaskieEnum.TaskType getTaskType(int type) {
		if (isEvent(type)) {
			return TaskieEnum.TaskType.EVENT;
		} else if (isDeadline(type)) {
			return TaskieEnum.TaskType.DEADLINE;
		} else {
			return TaskieEnum.TaskType.FLOAT;
		}
	}

	private static boolean isEvent(int type) {
		return type==0;
	}

	private static boolean isDeadline(int type) {
		return type == 1;
	}

	private static boolean isFloat(int type) {
		return type == 2;
	}

	private static TaskieEnum.TaskPriority getTaskPriority(int priority) {
		if (priority==0) {
			return TaskieEnum.TaskPriority.VERY_HIGH;
		} else if (priority==1) {
			return TaskieEnum.TaskPriority.HIGH;
		} else if (priority==2) {
			return TaskieEnum.TaskPriority.MEDIUM;
		} else if (priority==3) {
			return TaskieEnum.TaskPriority.LOW;
		} else {
			return TaskieEnum.TaskPriority.VERY_LOW;
		}
	}
}