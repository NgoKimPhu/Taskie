package fancy4.taskie.model;

/**
 * The Taskie storage component
 * @author Qin Xueying
 *
 */
import java.util.*;
import java.io.*;
import java.text.*;
import java.util.logging.*;

import org.json.*;

public class TaskieStorage {

	private static File taskList;
	// private static File deadlineTask;
	// private static File floatTask;
	private static ArrayList<TaskieTask> allTasks;
	private static ArrayList<TaskieTask> eventTaskList;
	private static ArrayList<TaskieTask> deadlineTaskList;
	private static ArrayList<TaskieTask> floatTaskList;
	private static HashMap<Calendar, ArrayList<TaskieTask>> eventStartDateMap;
	private static HashMap<Calendar, ArrayList<TaskieTask>> eventEndDateMap;
	private static HashMap<Calendar, ArrayList<TaskieTask>> deadlineEndDateMap;
	private static HashMap<TaskieEnum.TaskPriority, ArrayList<TaskieTask>> eventPriorityMap;
	private static HashMap<TaskieEnum.TaskPriority, ArrayList<TaskieTask>> deadlinePriorityMap;
	private static HashMap<TaskieEnum.TaskPriority, ArrayList<TaskieTask>> floatPriorityMap;
	private static Stack<HashMap<String, Object>> commandStack;
	private static TaskComparator tc = new TaskComparator();

	public static void load(String pathName) throws Exception {
		File folder;
		if (pathName.trim().length() == 0) {
			pathName = "TaskieData";
			folder = new File(pathName);
		} else {
			File userPath = new File(pathName);
			if (!userPath.exists()) {
				throw new Exception("Ooops! Invalid user path.");
			} else {
				folder = new File(pathName + "/TaskieData");
				if (!folder.exists()) {
					folder.mkdir();
				}
			}
		}
		taskList = new File(folder, "/taskList.json");
		// System.out.println(folder.toPath());
		eventStartDateMap = new HashMap<Calendar, ArrayList<TaskieTask>>();
		eventEndDateMap = new HashMap<Calendar, ArrayList<TaskieTask>>();
		deadlineEndDateMap = new HashMap<Calendar, ArrayList<TaskieTask>>();
		eventPriorityMap = new HashMap<TaskieEnum.TaskPriority, ArrayList<TaskieTask>>();
		deadlinePriorityMap = new HashMap<TaskieEnum.TaskPriority, ArrayList<TaskieTask>>();
		floatPriorityMap = new HashMap<TaskieEnum.TaskPriority, ArrayList<TaskieTask>>();
		// commandStack = new Stack<HashMap<String, Object>>();
		if (taskList.exists()) {
			HashMap<String, ArrayList<TaskieTask>> tasks = FileHandler.readFile(taskList);
			allTasks = tasks.get("all");
			eventTaskList = tasks.get("event");
			deadlineTaskList = tasks.get("deadline");
			floatTaskList = tasks.get("float");
			for (TaskieTask task : eventTaskList) {
				addToEventMap(task);
			}
			for (TaskieTask task : deadlineTaskList) {
				addToDeadlineMap(task);
			}
			for (TaskieTask task : floatTaskList) {
				addToFloatMap(task);
			}
		} else {
			taskList.createNewFile();
			allTasks = new ArrayList<TaskieTask>();
			eventTaskList = new ArrayList<TaskieTask>();
			deadlineTaskList = new ArrayList<TaskieTask>();
			floatTaskList = new ArrayList<TaskieTask>();
		}
	}

	public static ArrayList<TaskieTask> displayEventTask() {
		return eventTaskList;
	}

	public static ArrayList<TaskieTask> displayDeadlineTask() {
		return deadlineTaskList;
	}

	public static ArrayList<TaskieTask> displayFloatTask() {
		return floatTaskList;
	}

	// add task
	public static IndexTaskPair addTask(TaskieTask task) {
		allTasks.add(task);
		int index = allTasks.indexOf(task);
		if (TaskieTask.isEvent(task)) {
			eventTaskList.add(task);
			Collections.sort(eventTaskList, tc);
			addToEventMap(task);
		} else if (TaskieTask.isDeadline(task)) {
			deadlineTaskList.add(task);
			Collections.sort(deadlineTaskList, tc);
			addToDeadlineMap(task);
		} else {// float
			floatTaskList.add(task);
			addToFloatMap(task);
		}
		rewriteFile();
		return new IndexTaskPair(index, task);
	}

	public static TaskieTask deleteTask(int index) throws IndexOutOfBoundsException {
		// 0-based index
		if (index >= allTasks.size()) {
			throw new IndexOutOfBoundsException("Ooops! index out of the bonds!");
		} else {
			TaskieTask deletedTask = allTasks.remove(index);
			if (TaskieTask.isEvent(deletedTask)) {
				eventTaskList.remove(deletedTask);
				removeFromEventMap(deletedTask);
			} else if (TaskieTask.isDeadline(deletedTask)) {
				deadlineTaskList.remove(deletedTask);
				removeFromDeadlineMap(deletedTask);
			} else {// float
				floatTaskList.remove(deletedTask);
				removeFromFloatMap(deletedTask);
			}
			rewriteFile();
			return deletedTask;
		}

	}

	// delete all,return value index 0--eventDeadlineTask, 1--floatTask
	public static ArrayList<TaskieTask> deleteAll() {
		FileHandler.clearFile(taskList);
		allTasks = new ArrayList<TaskieTask>();
		eventTaskList = new ArrayList<TaskieTask>();
		deadlineTaskList = new ArrayList<TaskieTask>();
		;
		floatTaskList = new ArrayList<TaskieTask>();
		eventStartDateMap = new HashMap<Calendar, ArrayList<TaskieTask>>();
		eventEndDateMap = new HashMap<Calendar, ArrayList<TaskieTask>>();
		deadlineEndDateMap = new HashMap<Calendar, ArrayList<TaskieTask>>();
		eventPriorityMap = new HashMap<TaskieEnum.TaskPriority, ArrayList<TaskieTask>>();
		deadlinePriorityMap = new HashMap<TaskieEnum.TaskPriority, ArrayList<TaskieTask>>();
		floatPriorityMap = new HashMap<TaskieEnum.TaskPriority, ArrayList<TaskieTask>>();
		return allTasks;
	}

	// if you want to search all the tasks contains the key words, search twice
	public static ArrayList<IndexTaskPair> searchTask(ArrayList<String> keyWords) {
		ArrayList<IndexTaskPair> searchResult = new ArrayList<IndexTaskPair>();
		for (TaskieTask task : allTasks) {
			boolean check = true;
			String stringForCompare = new String(task.getTitle());
			for (String keyWord : keyWords) {
				if (!stringForCompare.toLowerCase().contains(keyWord.toLowerCase())) {
					check = false;
				}
			}
			if (check == true) {
				IndexTaskPair pair = new IndexTaskPair(allTasks.indexOf(task), task);
				searchResult.add(pair);
			}
		}
		return searchResult;
	}

	// search event based on start time
	public static ArrayList<IndexTaskPair> searchTask(Date start) {
		ArrayList<IndexTaskPair> searchResult = new ArrayList<IndexTaskPair>();
		if (!eventStartDateMap.containsKey(start)) {
			return searchResult;
		} else {
			ArrayList<TaskieTask> tasks = eventStartDateMap.get(start);
			for (TaskieTask task : tasks) {
				IndexTaskPair pair = new IndexTaskPair(eventTaskList.indexOf(task), task);
				searchResult.add(pair);
			}
			return searchResult;
		}
	}

	// search event/deadline based on end time
	public static ArrayList<IndexTaskPair> searchTask(Date end, TaskieEnum.TaskType type) {
		ArrayList<IndexTaskPair> searchResult = new ArrayList<IndexTaskPair>();
		if (type.equals(TaskieEnum.TaskType.EVENT)) {
			if (!eventEndDateMap.containsKey(end)) {
				return searchResult;
			} else {
				ArrayList<TaskieTask> tasks = eventEndDateMap.get(end);
				for (TaskieTask task : tasks) {
					IndexTaskPair pair = new IndexTaskPair(eventTaskList.indexOf(task), task);
					searchResult.add(pair);
				}
				return searchResult;
			}
		} else if (type.equals(TaskieEnum.TaskType.DEADLINE)) {
			if (!deadlineEndDateMap.containsKey(end)) {
				return searchResult;
			} else {
				ArrayList<TaskieTask> tasks = deadlineEndDateMap.get(end);
				for (TaskieTask task : tasks) {
					IndexTaskPair pair = new IndexTaskPair(eventTaskList.indexOf(task), task);
					searchResult.add(pair);
				}
				return searchResult;
			}
		} else {
			return searchResult;
		}
	}

	public static ArrayList<IndexTaskPair> searchTask(TaskieEnum.TaskPriority priority, TaskieEnum.TaskType type) {
		ArrayList<IndexTaskPair> searchResult = new ArrayList<IndexTaskPair>();
		if (type.equals(TaskieEnum.TaskType.EVENT)) {
			if (!eventPriorityMap.containsKey(priority)) {
				return searchResult;
			} else {
				ArrayList<TaskieTask> tasks = eventPriorityMap.get(priority);
				for (TaskieTask task : tasks) {
					IndexTaskPair pair = new IndexTaskPair(eventTaskList.indexOf(task), task);
					searchResult.add(pair);
				}
				return searchResult;
			}
		} else if (type.equals(TaskieEnum.TaskType.DEADLINE)) {
			if (!deadlinePriorityMap.containsKey(priority)) {
				return searchResult;
			} else {
				ArrayList<TaskieTask> tasks = deadlinePriorityMap.get(priority);
				for (TaskieTask task : tasks) {
					IndexTaskPair pair = new IndexTaskPair(eventTaskList.indexOf(task), task);
					searchResult.add(pair);
				}
				return searchResult;
			}
		} else {
			return searchResult;
		}
	}

	public static ArrayList<IndexTaskPair> searchTask(boolean done, TaskieEnum.TaskType type) {
		ArrayList<IndexTaskPair> searchResult = new ArrayList<IndexTaskPair>();
		if (type.equals(TaskieEnum.TaskType.EVENT)) {
			for (TaskieTask task : eventTaskList) {
				if (TaskieTask.isEvent(task) && TaskieTask.isDone(task)) {
					IndexTaskPair pair = new IndexTaskPair(eventTaskList.indexOf(task), task);
					searchResult.add(pair);
				}
			}
			return searchResult;
		} else if (type.equals(TaskieEnum.TaskType.DEADLINE)) {
			for (TaskieTask task : eventTaskList) {
				if (TaskieTask.isDeadline(task) && TaskieTask.isDone(task)) {
					IndexTaskPair pair = new IndexTaskPair(eventTaskList.indexOf(task), task);
					searchResult.add(pair);
				}
			}
			return searchResult;
		} else if (type.equals(TaskieEnum.TaskType.FLOAT)) {
			for (TaskieTask task : floatTaskList) {
				if (TaskieTask.isDone(task)) {
					IndexTaskPair pair = new IndexTaskPair(eventTaskList.indexOf(task), task);
					searchResult.add(pair);
				}
			}
			return searchResult;
		} else {
			return searchResult;
		}
	}

	public static ArrayList<TaskieTask> markDone(int index) throws IndexOutOfBoundsException {
		// 0-based index
		if (index >= allTasks.size()) {
			throw new IndexOutOfBoundsException("Ooops! index out of the bonds!");
		} else {
			TaskieTask task = allTasks.get(index);
			boolean currentStatus = task.getStatus();
			if (currentStatus == true) {
				task.setStatus(false);
			} else {
				task.setStatus(true);
			}
			sortList(task.getType());
			rewriteFile();
			return allTasks;
		}

	}

	public static ArrayList<TaskieTask> updateTaskTitle(int index, String title) throws IndexOutOfBoundsException {
		if (index >= allTasks.size()) {
			throw new IndexOutOfBoundsException("Ooops! index out of the bonds!");
		} else {
			TaskieTask task = allTasks.get(index);
			task.setTitle(title);
			sortList(task.getType());
			rewriteFile();
			return allTasks;
		}
	}

	public static ArrayList<TaskieTask> updateTaskPriority(int index, TaskieEnum.TaskPriority priority)
			throws IndexOutOfBoundsException {
		if (index >= allTasks.size()) {
			throw new IndexOutOfBoundsException("Ooops! index out of the bonds!");
		} else {
			TaskieTask task = allTasks.get(index);
			if (TaskieTask.isEvent(task)) {
				removeFromPriorityMap(eventPriorityMap, task);
				task.setPriority(priority);
				addToPriorityMap(eventPriorityMap, task);
			} else if (TaskieTask.isDeadline(task)) {
				removeFromPriorityMap(deadlinePriorityMap, task);
				task.setPriority(priority);
				addToPriorityMap(deadlinePriorityMap, task);
			} else {// float
				removeFromPriorityMap(floatPriorityMap, task);
				task.setPriority(priority);
				addToPriorityMap(floatPriorityMap, task);
			}
			sortList(task.getType());
			rewriteFile();
			return allTasks;
		}
	}

	public static ArrayList<TaskieTask> updateTaskDescription(int index, String description)
			throws IndexOutOfBoundsException {
		if (index >= allTasks.size()) {
			throw new IndexOutOfBoundsException("Ooops! index out of the bonds!");
		} else {
			TaskieTask task = allTasks.get(index);
			task.setDescription(description);
			rewriteFile();
			return allTasks;
		}
	}

	// index 0-eventdeadline 1-float
	public static ArrayList<TaskieTask> updateFloatToDeadline(int index, Calendar end) throws IndexOutOfBoundsException {
		if (index >= allTasks.size()) {
			throw new IndexOutOfBoundsException("Ooops! index out of the bonds!");
		} else {
			TaskieTask task = allTasks.get(index);
			floatTaskList.remove(task);
			removeFromFloatMap(task);
			task.setToDeadline(end);
			deadlineTaskList.add(task);
			addToDeadlineMap(task);
			Collections.sort(deadlineTaskList, tc);
			rewriteFile();
			return allTasks;
		}
	}

	public static ArrayList<TaskieTask> updateFloatToEvent(int index, Calendar start, Calendar end)
			throws IndexOutOfBoundsException {
		if (index >= allTasks.size()) {
			throw new IndexOutOfBoundsException("Ooops! index out of the bonds!");
		} else {
			TaskieTask task = allTasks.get(index);
			floatTaskList.remove(task);
			removeFromFloatMap(task);
			task.setToEvent(start, end);
			eventTaskList.add(task);
			addToEventMap(task);
			Collections.sort(eventTaskList, tc);
			rewriteFile();
			return allTasks;
		}
	}

	public static ArrayList<TaskieTask> updateEventDeadlineToFloat(int index) throws IndexOutOfBoundsException {
		if (index >= allTasks.size()) {
			throw new IndexOutOfBoundsException("Ooops! index out of the bonds!");
		} else {
			TaskieTask task = allTasks.get(index);
			if (TaskieTask.isEvent(task)) {
				removeFromEventMap(task);
				eventTaskList.remove(task);
			} else {
				removeFromDeadlineMap(task);
				deadlineTaskList.remove(task);
			}
			task.setToFloat();
			floatTaskList.add(task);
			addToFloatMap(task);
			Collections.sort(floatTaskList, tc);
			rewriteFile();
			return allTasks;
		}
	}

	public static ArrayList<TaskieTask> updateEventToDeadline(int index) throws IndexOutOfBoundsException {
		if (index >= allTasks.size()) {
			throw new IndexOutOfBoundsException("Ooops! index out of the bonds!");
		} else {
			TaskieTask task = allTasks.get(index);
			eventTaskList.remove(task);
			removeFromEventMap(task);
			task.setToDeadline(task.getEndTime());
			deadlineTaskList.add(task);
			Collections.sort(deadlineTaskList, tc);
			addToDeadlineMap(task);
			rewriteFile();
			return allTasks;
		}

	}

	public static ArrayList<TaskieTask> updateDeadlineToEvent(int index, Calendar start) throws IndexOutOfBoundsException {
		if (index >= allTasks.size()) {
			throw new IndexOutOfBoundsException("Ooops! index out of the bonds!");
		} else {
			TaskieTask task = allTasks.get(index);
			deadlineTaskList.remove(task);
			removeFromDeadlineMap(task);
			task.setToEvent(start, task.getEndTime());
			eventTaskList.add(task);
			Collections.sort(eventTaskList, tc);
			addToEventMap(task);
			rewriteFile();
			return allTasks;
		}
	}

	public static ArrayList<TaskieTask> updateEventDeadlineEnd(int index, Calendar end) throws IndexOutOfBoundsException {
		if (index >= allTasks.size()) {
			throw new IndexOutOfBoundsException("Ooops! index out of the bonds!");
		} else {
			TaskieTask task = allTasks.get(index);
			if (TaskieTask.isEvent(task)) {
				removeFromEndDateMap(eventEndDateMap, task);
			} else {
				removeFromEndDateMap(deadlineEndDateMap, task);
			}
			task.setEndTime(end);
			if (TaskieTask.isEvent(task)) {
				addToEndDateMap(eventEndDateMap, task);
			} else {
				addToEndDateMap(deadlineEndDateMap, task);
			}
			sortList(task.getType());
			rewriteFile();
			return allTasks;
		}
	}

	public static ArrayList<TaskieTask> updateEventStart(int index, Calendar start) throws IndexOutOfBoundsException {
		if (index >= allTasks.size()) {
			throw new IndexOutOfBoundsException("Ooops! index out of the bonds!");
		} else {
			TaskieTask task = allTasks.get(index);
			if (TaskieTask.isEvent(task)) {
				removeFromStartDateMap(eventStartDateMap, task);
				task.setStartTime(start);
				addToStartDateMap(eventStartDateMap, task);
				Collections.sort(eventTaskList, tc);
				rewriteFile();
			}
			return allTasks;
		}
	}

	public static ArrayList<TaskieTask> updateEventStartEnd(int index, Calendar start, Calendar end)
			throws IndexOutOfBoundsException {
		if (index >= allTasks.size()) {
			throw new IndexOutOfBoundsException("Ooops! index out of the bonds!");
		} else {
			TaskieTask task = allTasks.get(index);
			if (TaskieTask.isEvent(task)) {
				removeFromStartDateMap(eventStartDateMap, task);
				removeFromEndDateMap(eventEndDateMap, task);
				task.setStartTime(start);
				task.setEndTime(end);
				addToStartDateMap(eventStartDateMap, task);
				addToEndDateMap(eventEndDateMap, task);
				Collections.sort(eventTaskList, tc);
				rewriteFile();
			}
			return allTasks;
		}
	}

	public static String viewTaskDescription(int index, TaskieEnum.TaskType type) throws IndexOutOfBoundsException {
		if (index >= allTasks.size()) {
			throw new IndexOutOfBoundsException("Ooops! index out of the bonds!");
		} else {
			String description;
			description = allTasks.get(index).getDescription();
			return description;
		}
	}

	public static void editTaskDescription(int index, String description) throws IndexOutOfBoundsException {
		if (index >= allTasks.size()) {
			throw new IndexOutOfBoundsException("Ooops! index out of the bonds!");
		} else {
			String oldDescription = allTasks.get(index).getDescription();
			String newDescription = oldDescription + " " + description;
			allTasks.get(index).setDescription(newDescription);
			rewriteFile();
		}

	}

	private static Calendar createDateKey(Calendar calendar) {
		Calendar calendarForKey = Calendar.getInstance();
		calendarForKey.clear();
		calendarForKey.set(Calendar.YEAR, calendar.get(Calendar.YEAR));
		calendarForKey.set(Calendar.MONTH, calendar.get(Calendar.MONTH));
		calendarForKey.set(Calendar.DATE, calendar.get(Calendar.DATE));
		return calendarForKey;
	}

	private static boolean isEmpty(ArrayList<TaskieTask> tasks) {
		return tasks.size() == 0;
	}

	private static void addToEventMap(TaskieTask task) {
		assert TaskieTask.isEvent(task);
		Calendar start = task.getStartTime();
		Calendar end = task.getEndTime();
		TaskieEnum.TaskPriority priority = task.getPriority();
		if (!eventStartDateMap.containsKey(start)) {
			eventStartDateMap.put(start, new ArrayList<TaskieTask>());
		}
		eventStartDateMap.get(start).add(task);
		Calendar startKey = createDateKey(start);
		if (!eventStartDateMap.containsKey(startKey)) {
			eventStartDateMap.put(startKey, new ArrayList<TaskieTask>());
		}
		eventStartDateMap.get(startKey).add(task);
		if (!eventEndDateMap.containsKey(end)) {
			eventEndDateMap.put(end, new ArrayList<TaskieTask>());
		}
		eventEndDateMap.get(end).add(task);
		Calendar endKey = createDateKey(task.getStartTime());
		if (!eventStartDateMap.containsKey(endKey)) {
			eventStartDateMap.put(endKey, new ArrayList<TaskieTask>());
		}
		eventStartDateMap.get(endKey).add(task);
		if (!eventPriorityMap.containsKey(priority)) {
			eventPriorityMap.put(priority, new ArrayList<TaskieTask>());
		}
		eventPriorityMap.get(priority).add(task);
	}

	private static void addToDeadlineMap(TaskieTask task) {
		assert TaskieTask.isDeadline(task);
		Calendar end = task.getEndTime();
		Calendar endKey = createDateKey(end);
		TaskieEnum.TaskPriority priority = task.getPriority();
		if (!deadlineEndDateMap.containsKey(end)) {
			deadlineEndDateMap.put(end, new ArrayList<TaskieTask>());
		}
		deadlineEndDateMap.get(end).add(task);
		if (!deadlineEndDateMap.containsKey(endKey)) {
			deadlineEndDateMap.put(endKey, new ArrayList<TaskieTask>());
		}
		deadlineEndDateMap.get(endKey).add(task);
		if (!deadlinePriorityMap.containsKey(priority)) {
			deadlinePriorityMap.put(priority, new ArrayList<TaskieTask>());
		}
		deadlinePriorityMap.get(priority).add(task);
	}

	private static void addToFloatMap(TaskieTask task) {
		assert TaskieTask.isFloat(task);
		TaskieEnum.TaskPriority priority = task.getPriority();
		if (!floatPriorityMap.containsKey(priority)) {
			floatPriorityMap.put(priority, new ArrayList<TaskieTask>());
		}
		floatPriorityMap.get(priority).add(task);
	}

	private static void removeFromEventMap(TaskieTask task) {
		Calendar start = task.getStartTime();
		Calendar end = task.getEndTime();
		TaskieEnum.TaskPriority priority = task.getPriority();
		eventStartDateMap.get(start).remove(task);
		if (isEmpty(eventStartDateMap.get(start))) {
			eventStartDateMap.remove(start);
		}
		eventEndDateMap.get(end).remove(task);
		if (isEmpty(eventEndDateMap.get(end))) {
			eventEndDateMap.remove(end);
		}
		eventPriorityMap.get(priority).remove(task);
		if (isEmpty(eventPriorityMap.get(priority))) {
			eventPriorityMap.remove(priority);
		}
	}

	private static void removeFromDeadlineMap(TaskieTask task) {
		Calendar end = task.getEndTime();
		TaskieEnum.TaskPriority priority = task.getPriority();
		deadlineEndDateMap.get(end).remove(task);
		if (isEmpty(deadlineEndDateMap.get(end))) {
			deadlineEndDateMap.remove(end);
		}
		deadlinePriorityMap.get(priority).remove(task);
		if (isEmpty(deadlinePriorityMap.get(priority))) {
			deadlinePriorityMap.remove(priority);
		}
	}

	private static void removeFromFloatMap(TaskieTask task) {
		TaskieEnum.TaskPriority priority = task.getPriority();
		floatPriorityMap.get(priority).remove(task);
		if (isEmpty(floatPriorityMap.get(priority))) {
			floatPriorityMap.remove(priority);
		}
	}

	private static void addToStartDateMap(HashMap<Calendar, ArrayList<TaskieTask>> map, TaskieTask task) {
		if (TaskieTask.isEvent(task)) {
			Calendar start = task.getStartTime();
			if (!map.containsKey(start)) {
				map.put(start, new ArrayList<TaskieTask>());
			}
			map.get(start).add(task);
		}
	}

	private static void addToEndDateMap(HashMap<Calendar, ArrayList<TaskieTask>> map, TaskieTask task) {
		Calendar end = task.getEndTime();
		if (!map.containsKey(end)) {
			map.put(end, new ArrayList<TaskieTask>());
		}
		map.get(end).add(task);
	}

	private static void addToPriorityMap(HashMap<TaskieEnum.TaskPriority, ArrayList<TaskieTask>> map, TaskieTask task) {
		TaskieEnum.TaskPriority priority = task.getPriority();
		if (!map.containsKey(priority)) {
			map.put(priority, new ArrayList<TaskieTask>());
		}
		map.get(priority).add(task);
	}

	private static void removeFromStartDateMap(HashMap<Calendar, ArrayList<TaskieTask>> map, TaskieTask task) {
		if (TaskieTask.isEvent(task)) {
			Calendar start = task.getStartTime();
			map.get(start).remove(task);
			if (isEmpty(map.get(start))) {
				map.remove(start);
			}
		}
	}

	private static void removeFromEndDateMap(HashMap<Calendar, ArrayList<TaskieTask>> map, TaskieTask task) {
		Calendar end = task.getStartTime();
		map.get(end).remove(task);
		if (isEmpty(map.get(end))) {
			map.remove(end);
		}
	}

	private static void removeFromPriorityMap(HashMap<TaskieEnum.TaskPriority, ArrayList<TaskieTask>> map,
			TaskieTask task) {
		TaskieEnum.TaskPriority priority = task.getPriority();
		map.get(priority).remove(task);
		if (isEmpty(map.get(priority))) {
			map.remove(priority);
		}
	}

	private static void sortList(TaskieEnum.TaskType type) {
		if (type.equals(TaskieEnum.TaskType.EVENT)) {
			Collections.sort(eventTaskList, tc);
		} else if (type.equals(TaskieEnum.TaskType.DEADLINE)) {
			Collections.sort(deadlineTaskList, tc);
		} else {
			Collections.sort(floatTaskList, tc);
		}
	}

	private static void rewriteFile() {
		FileHandler.clearFile(taskList);
		for (TaskieTask t : eventTaskList) {
			// System.out.println(t.getTitle());
			FileHandler.writeFile(taskList, t);
		}
		for (TaskieTask t : deadlineTaskList) {
			// System.out.println(t.getTitle());
			FileHandler.writeFile(taskList, t);
		}
		for (TaskieTask t : floatTaskList) {
			// System.out.println(t.getTitle());
			FileHandler.writeFile(taskList, t);
		}
	}

}

class FileHandler {
	private static final SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm");
	private static Logger logger = Logger.getLogger(FileHandler.class.getName());

	public static HashMap<String, ArrayList<TaskieTask>> readFile(File file) throws Exception {
		String line = new String();
		ArrayList<TaskieTask> events = new ArrayList<TaskieTask>();
		ArrayList<TaskieTask> deadlines = new ArrayList<TaskieTask>();
		ArrayList<TaskieTask> floats = new ArrayList<TaskieTask>();
		ArrayList<TaskieTask> all = new ArrayList<TaskieTask>();
		assert file.exists() && file != null;
		try {
			BufferedReader in = new BufferedReader(new FileReader(file));
			line = in.readLine();
			while (line != null) {
				JSONObject taskLine = new JSONObject(line);
				if (taskLine.has("event")) {
					JSONObject taskData = taskLine.getJSONObject("event");
					String title = taskData.getString("title");
					TaskieEnum.TaskType type = getTaskType(taskData.getInt("type"));
					Calendar start = getDate(taskData.getString("start-time"));
					Calendar end = getDate(taskData.getString("end-time"));
					TaskieEnum.TaskPriority priority = getTaskPriority(taskData.getInt("priority"));
					boolean status = taskData.getBoolean("status");
					String description = taskData.getString("description");
					TaskieTask task = new TaskieTask(title, type, start, end, priority, status, description);
					events.add(task);
					all.add(task);
				} else if (taskLine.has("deadline")) {
					JSONObject taskData = taskLine.getJSONObject("event");
					String title = taskData.getString("title");
					TaskieEnum.TaskType type = getTaskType(taskData.getInt("type"));
					Calendar end = getDate(taskData.getString("end-time"));
					TaskieEnum.TaskPriority priority = getTaskPriority(taskData.getInt("priority"));
					boolean status = taskData.getBoolean("status");
					String description = taskData.getString("description");
					TaskieTask task = new TaskieTask(title, type, end, priority, status, description);
					deadlines.add(task);
					all.add(task);
				} else if (taskLine.has("float")) {
					JSONObject taskData = taskLine.getJSONObject("event");
					String title = taskData.getString("title");
					TaskieEnum.TaskType type = getTaskType(taskData.getInt("type"));
					TaskieEnum.TaskPriority priority = getTaskPriority(taskData.getInt("priority"));
					boolean status = taskData.getBoolean("status");
					String description = taskData.getString("description");
					TaskieTask task = new TaskieTask(title, type, priority, status, description);
					floats.add(task);
					all.add(task);
				}
				line = in.readLine();
			}
			in.close();
		} catch (IOException e) {
			logger.log(Level.SEVERE, "Cannot read from event and deadline file.", e);
		}
		HashMap<String, ArrayList<TaskieTask>> fileContent = new HashMap<String, ArrayList<TaskieTask>>();
		fileContent.put("event", events);
		fileContent.put("deadline", deadlines);
		fileContent.put("float", floats);
		fileContent.put("all", all);
		return fileContent;
	}

	// Write content in to a file.
	public static void writeFile(File file, TaskieTask task) {
		// String fileName = file.getName();
		if (TaskieTask.isEvent(task)) {
			try {
				FileWriter writer = new FileWriter(file, true);
				JSONWriter jWriter = new JSONWriter(writer);
				jWriter.object();
				jWriter.key("event");
				jWriter.object();
				jWriter.key("title");
				jWriter.value(task.getTitle());

				jWriter.key("type");
				jWriter.value(task.getType().ordinal());

				jWriter.key("start-time");
				jWriter.value(sdf.format(task.getStartTime().getTime()));

				jWriter.key("end-time");
				jWriter.value(sdf.format(task.getEndTime().getTime()));

				jWriter.key("priority");
				jWriter.value(task.getPriority().ordinal());

				jWriter.key("status");
				jWriter.value(task.getStatus());

				jWriter.key("description");
				jWriter.value(task.getDescription() == null ? "null" : task.getDescription());

				jWriter.endObject();
				jWriter.endObject();
				writer.write("\n");
				writer.close();
			} catch (IOException e) {
				logger.log(Level.SEVERE, "Cannot write to event file.", e);
			}
		} else if (TaskieTask.isDeadline(task)) {
			try {
				FileWriter writer = new FileWriter(file, true);
				JSONWriter jWriter = new JSONWriter(writer);
				jWriter.object();
				jWriter.key("deadline");
				jWriter.object();
				jWriter.key("title");
				jWriter.value(task.getTitle());

				jWriter.key("type");
				jWriter.value(task.getType().ordinal());

				jWriter.key("end-time");
				jWriter.value(sdf.format(task.getEndTime().getTime()));

				jWriter.key("priority");
				jWriter.value(task.getPriority().ordinal());

				jWriter.key("status");
				jWriter.value(task.getStatus());

				jWriter.key("description");
				jWriter.value(task.getDescription() == null ? "null" : task.getDescription());

				jWriter.endObject();
				jWriter.endObject();
				writer.write("\n");
				writer.close();
			} catch (IOException e) {
				logger.log(Level.SEVERE, "Cannot write to deadline file.", e);
			}
		} else {// floating task
			try {
				FileWriter writer = new FileWriter(file, true);
				JSONWriter jWriter = new JSONWriter(writer);
				jWriter.object();
				jWriter.key("float");

				jWriter.object();
				jWriter.key("title");
				jWriter.value(task.getTitle());

				jWriter.key("type");
				jWriter.value(task.getType().ordinal());

				jWriter.key("priority");
				jWriter.value(task.getPriority().ordinal());

				jWriter.key("status");
				jWriter.value(task.getStatus());

				jWriter.key("description");
				jWriter.value(task.getDescription() == null ? "null" : task.getDescription());

				jWriter.endObject();
				jWriter.endObject();
				writer.write("\n");
				writer.close();
			} catch (IOException e) {
				logger.log(Level.SEVERE, "Cannot write to floating task file.", e);
			}
		}
	}

	public static void clearFile(File file) {
		// String fileName = file.getName();
		assert file.exists() && file != null;
		try {
			FileWriter writer = new FileWriter(file);
			writer.write("");
			writer.close();
		} catch (IOException e) {
			logger.log(Level.SEVERE, "Cannot clear file.", e);
		}
	}

	private static Calendar getDate(String string) {
		Calendar calendar = Calendar.getInstance();
		calendar.clear();
		String[] splitString = string.split(" ");
		String[] dayMonthYear = splitString[0].split("-");
		String[] hourMinute = splitString[1].split(":");
		int day = Integer.valueOf(dayMonthYear[0]);
		int month = Integer.valueOf(dayMonthYear[1]);
		int year = Integer.valueOf(dayMonthYear[2]);
		int hour = Integer.valueOf(hourMinute[0]);
		int minute = Integer.valueOf(hourMinute[1]);
		calendar.set(year, month, day, hour, minute);
		return calendar;
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
		return type == 0;
	}

	private static boolean isDeadline(int type) {
		return type == 1;
	}

	private static TaskieEnum.TaskPriority getTaskPriority(int priority) {
		if (priority == 0) {
			return TaskieEnum.TaskPriority.VERY_HIGH;
		} else if (priority == 1) {
			return TaskieEnum.TaskPriority.HIGH;
		} else if (priority == 2) {
			return TaskieEnum.TaskPriority.MEDIUM;
		} else if (priority == 3) {
			return TaskieEnum.TaskPriority.LOW;
		} else {
			return TaskieEnum.TaskPriority.VERY_LOW;
		}
	}
}