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
	private static HashMap<Date, ArrayList<TaskieTask>> eventDeadlineStartDateMap;
	private static HashMap<Date, ArrayList<TaskieTask>> eventDeadlineEndDateMap;
	private static HashMap<Date, ArrayList<TaskieTask>> floatDateMap;
	private static HashMap<TaskieEnum.TaskPriority, ArrayList<TaskieTask>> eventDeadlinePriorityMap;
	private static HashMap<TaskieEnum.TaskPriority, ArrayList<TaskieTask>> floatPriorityMap;
	private static Stack<HashMap<String, Object>> commandStack;
	private static TaskComparator tc = new TaskComparator();

	public static void load(String pathName) throws Exception {
		eventDeadlineTask = new File(pathName +"/eventDeadline.json");
		floatTask = new File(pathName+"/floatTask.json");
		if(eventDeadlineTask.exists()){
			eventDeadlineTaskList = FileHandler.readFile(eventDeadlineTask);
		}
		else{
			eventDeadlineTaskList = new ArrayList<TaskieTask>();
		}
		if(floatTask.exists()){
			floatTaskList = FileHandler.readFile(floatTask);
		}
		else{
			floatTaskList = new ArrayList<TaskieTask>();
		}
		eventDeadlineStartDateMap = new HashMap<Date, ArrayList<TaskieTask>>();
		eventDeadlineEndDateMap = new HashMap<Date, ArrayList<TaskieTask>>();
		floatDateMap = new HashMap<Date, ArrayList<TaskieTask>>();
		eventDeadlinePriorityMap = new HashMap<TaskieEnum.TaskPriority, ArrayList<TaskieTask>>();
		floatPriorityMap = new HashMap<TaskieEnum.TaskPriority, ArrayList<TaskieTask>>();
		commandStack = new Stack<HashMap<String, Object>>();
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

	public static ArrayList<TaskieTask> addTask(TaskieTask task) {
		if (task.getType().equals(TaskieEnum.TaskType.EVENT)
				|| task.getType().equals(TaskieEnum.TaskType.DEADLINE)) {
			eventDeadlineTaskList.add(task);
			Collections.sort(eventDeadlineTaskList, tc);
			for(TaskieTask t: eventDeadlineTaskList){
				//System.out.println(t.getTitle());
				FileHandler.writeFile(eventDeadlineTask, t, t.getType());
			}
			if (!eventDeadlineStartDateMap.containsKey(task.getStartTime())) {
				ArrayList<TaskieTask> tasks = new ArrayList<TaskieTask>();

				tasks.add(task);
				eventDeadlineStartDateMap.put(task.getStartTime(), tasks);
			} else {
				eventDeadlineStartDateMap.get(task.getStartTime()).add(task);
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
			Collections.sort(floatTaskList, tc);
			Collections.sort(eventDeadlineTaskList, tc);
			for(TaskieTask t: floatTaskList){
				//System.out.println(t.getTitle());
				FileHandler.writeFile(floatTask, t, t.getType());
			}
			if (!floatDateMap.containsKey(task.getStartTime())) {
				ArrayList<TaskieTask> tasks = new ArrayList<TaskieTask>();

				tasks.add(task);
				floatDateMap.put(task.getStartTime(), tasks);
			} else {
				floatDateMap.get(task.getStartTime()).add(task);
			}

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
			eventDeadlineStartDateMap.get(task.getStartTime()).remove(task);
			if (eventDeadlineStartDateMap.get(task.getStartTime()).size() == 0) {
				eventDeadlineStartDateMap.remove(task.getStartTime());
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
			floatDateMap.get(task.getStartTime()).remove(task);
			if (floatDateMap.get(task.getStartTime()).size() == 0) {
				floatDateMap.remove(task.getStartTime());

			}
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
	/*
	private static final Pattern EVENT_DEADLINE_TASK_LINE_PATTERN = Pattern
			.compile("^(EVENT|DEADLINE)\\s(.+)\\s(.+)\\s(.+)\\s([01234])\\s\\([01])\\n$");
	private static final Pattern FLOAT_TASK_LINE_PATTERN = Pattern
			.compile("^(FLOAT)\\s(.+)\\s(.+)\\s(.+)\\s([01234])\\s\\([01])\\n$");
	*/
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
				Date start = getDate(taskData.getString("start-time"));
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
				Date start = getDate(taskData.getString("start-time"));
				TaskieEnum.TaskPriority priority = getTaskPriority(taskData.getInt("priority"));
				boolean status = taskData.getBoolean("status");
				String description = taskData.getString("description");
				TaskieTask task = new TaskieTask(title, type,  priority, start, status, description);
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
				jWriter.value(sdf.format(task.getStartTime()));
				
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
				
				jWriter.key("start-time");
				jWriter.value(sdf.format(task.getStartTime()));
				
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