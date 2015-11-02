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
	private static final SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm");
	private static File taskList;
	private static ArrayList<TaskieTask> allTasks;
	private static ArrayList<TaskieTask> eventTaskList;
	private static ArrayList<TaskieTask> deadlineTaskList;
	private static ArrayList<TaskieTask> floatTaskList;
	private static HashMap<TaskieEnum.TaskPriority, ArrayList<TaskieTask>> eventPriorityMap;
	private static HashMap<TaskieEnum.TaskPriority, ArrayList<TaskieTask>> deadlinePriorityMap;
	private static HashMap<TaskieEnum.TaskPriority, ArrayList<TaskieTask>> floatPriorityMap;
	private static EventComparator ec = new EventComparator();
	private static DeadlineComparator dc = new DeadlineComparator();
	private static FloatComparator fc = new FloatComparator();

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
	public static ArrayList<TaskieTask> displayAllTasks(){
		return allTasks;
	}

	public static ArrayList<TaskieTask> displayEventTasks() {
		return eventTaskList;
	}

	public static ArrayList<TaskieTask> displayDeadlineTasks() {
		return deadlineTaskList;
	}

	public static ArrayList<TaskieTask> displayFloatTasks() {
		return floatTaskList;
	}

	// add task
	public static IndexTaskPair addTask(TaskieTask task) {
		allTasks.add(task);
		int index = allTasks.indexOf(task);
		if (TaskieTask.isEvent(task)) {
			eventTaskList.add(task);
			Collections.sort(eventTaskList, ec);
			addToEventMap(task);
		} else if (TaskieTask.isDeadline(task)) {
			deadlineTaskList.add(task);
			Collections.sort(deadlineTaskList, dc);
			addToDeadlineMap(task);
		} else {// float
			floatTaskList.add(task);
			Collections.sort(floatTaskList, fc);
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
		floatTaskList = new ArrayList<TaskieTask>();
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
	public static ArrayList<IndexTaskPair> searchTask(Calendar start, Calendar end) throws Exception {
		if(start.after(end)){
			throw new Exception("end time should equals or after start time");
		}
		ArrayList<IndexTaskPair> searchResult = new ArrayList<IndexTaskPair>();
		for(TaskieTask task: allTasks){
			if(TaskieTask.isEvent(task)){
				if(!end.before(task.getStartTime())&&!start.after(task.getEndTime())){
					searchResult.add(new IndexTaskPair(allTasks.indexOf(task),task));
				}
			}
			if(TaskieTask.isDeadline(task)){
				if((task.getEndTime().before(end)||task.getEndTime().equals(end))&&(task.getEndTime().after(start)||task.getEndTime().equals(start))){
					searchResult.add(new IndexTaskPair(allTasks.indexOf(task),task));
				}
			}
		}
		return searchResult;
	}



	public static ArrayList<IndexTaskPair> searchTask(TaskieEnum.TaskPriority priority) {
		ArrayList<IndexTaskPair> searchResult = new ArrayList<IndexTaskPair>();
		if (eventPriorityMap.containsKey(priority)) {
			ArrayList<TaskieTask> events = eventPriorityMap.get(priority);
			for (TaskieTask task : events) {
				IndexTaskPair pair = new IndexTaskPair(allTasks.indexOf(task), task);
				searchResult.add(pair);
			}
		}
		if (deadlinePriorityMap.containsKey(priority)) {
			ArrayList<TaskieTask> deadlines = deadlinePriorityMap.get(priority);
			for (TaskieTask task : deadlines) {
				IndexTaskPair pair = new IndexTaskPair(allTasks.indexOf(task), task);
				searchResult.add(pair);
			}
		}
		if (floatPriorityMap.containsKey(priority)) {
			ArrayList<TaskieTask> floats = floatPriorityMap.get(priority);
			for (TaskieTask task : floats) {
				IndexTaskPair pair = new IndexTaskPair(allTasks.indexOf(task), task);
				searchResult.add(pair);
			}
		}
		return searchResult;

	}

	public static ArrayList<IndexTaskPair> searchTask(boolean done) {
		ArrayList<IndexTaskPair> searchResult = new ArrayList<IndexTaskPair>();
		for (TaskieTask task : allTasks) {
			if (TaskieTask.isEvent(task) && TaskieTask.isDone(task)) {
				IndexTaskPair pair = new IndexTaskPair(allTasks.indexOf(task), task);
				searchResult.add(pair);
			}
		}
		return searchResult;
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
	public static ArrayList<TaskieTask> updateFloatToDeadline(int index, Calendar end)
			throws IndexOutOfBoundsException {
		if (index >= allTasks.size()) {
			throw new IndexOutOfBoundsException("Ooops! index out of the bonds!");
		} else {
			TaskieTask task = allTasks.get(index);
			floatTaskList.remove(task);
			removeFromFloatMap(task);
			task.setToDeadline(end);
			deadlineTaskList.add(task);
			addToDeadlineMap(task);
			Collections.sort(deadlineTaskList, dc);
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
			Collections.sort(eventTaskList, ec);
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
			Collections.sort(floatTaskList, fc);
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
			Collections.sort(deadlineTaskList, dc);
			addToDeadlineMap(task);
			rewriteFile();
			return allTasks;
		}

	}

	public static ArrayList<TaskieTask> updateDeadlineToEvent(int index, Calendar start)
			throws IndexOutOfBoundsException {
		if (index >= allTasks.size()) {
			throw new IndexOutOfBoundsException("Ooops! index out of the bonds!");
		} else {
			TaskieTask task = allTasks.get(index);
			deadlineTaskList.remove(task);
			removeFromDeadlineMap(task);
			task.setToEvent(start, task.getEndTime());
			eventTaskList.add(task);
			Collections.sort(eventTaskList, ec);
			addToEventMap(task);
			rewriteFile();
			return allTasks;
		}
	}

	public static ArrayList<TaskieTask> updateEventDeadlineEnd(int index, Calendar end)
			throws IndexOutOfBoundsException {
		if (index >= allTasks.size()) {
			throw new IndexOutOfBoundsException("Ooops! index out of the bonds!");
		} else {
			TaskieTask task = allTasks.get(index);
			task.setEndTime(end);
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
				task.setStartTime(start);
				Collections.sort(eventTaskList, ec);
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
				task.setStartTime(start);
				task.setEndTime(end);	
				Collections.sort(eventTaskList, ec);
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
		TaskieEnum.TaskPriority priority = task.getPriority();
		
		if (!eventPriorityMap.containsKey(priority)) {
			eventPriorityMap.put(priority, new ArrayList<TaskieTask>());
		}
		eventPriorityMap.get(priority).add(task);
	}

	private static void addToDeadlineMap(TaskieTask task) {
		assert TaskieTask.isDeadline(task);
		TaskieEnum.TaskPriority priority = task.getPriority();
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
		TaskieEnum.TaskPriority priority = task.getPriority();
		eventPriorityMap.get(priority).remove(task);
		if (isEmpty(eventPriorityMap.get(priority))) {
			eventPriorityMap.remove(priority);
		}
	}

	private static void removeFromDeadlineMap(TaskieTask task) {
		TaskieEnum.TaskPriority priority = task.getPriority();
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

	private static void addToStartDateMap(HashMap<Integer, ArrayList<TaskieTask>> map, TaskieTask task) {
		if (TaskieTask.isEvent(task)) {
			int start = (sdf.format(task.getStartTime().getTime())).hashCode();
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

	private static void removeFromStartDateMap(HashMap<Integer, ArrayList<TaskieTask>> map, TaskieTask task) {
		if (TaskieTask.isEvent(task)) {
			int start = sdf.format(task.getStartTime().getTime()).hashCode();
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
			Collections.sort(eventTaskList, ec);
		} else if (type.equals(TaskieEnum.TaskType.DEADLINE)) {
			Collections.sort(deadlineTaskList, dc);
		} else {
			Collections.sort(floatTaskList, fc);
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
					JSONObject taskData = taskLine.getJSONObject("deadline");
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
					JSONObject taskData = taskLine.getJSONObject("float");
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
		int month = Integer.valueOf(dayMonthYear[1])-1;
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

// free slot table with seven days start from today
class FreeSlotTable{
	private ArrayList<ArrayList<CalendarPair>> table;
	private static Calendar today;
	public FreeSlotTable(){
		this.today = Calendar.getInstance();
		this.table = new ArrayList<ArrayList<CalendarPair>>();
		/* seven days
		   default block 24:00 to 6:00
		*/
		for(int i=0; i<7; i++){
			ArrayList<CalendarPair> day = new ArrayList<CalendarPair>();
			block(day, i);
			this.table.add(day);			
		}
	}
	public void block(Calendar start, Calendar end){
		
	}
	private static void block(ArrayList<CalendarPair> day, int daysAfter){
		Calendar start = Calendar.getInstance();
		start.clear();
		//00:00
		start.set(today.get(Calendar.YEAR), today.get(Calendar.MONTH), today.get(Calendar.DATE)+daysAfter, 0, 0, 0);
		Calendar end = Calendar.getInstance();
		end.clear();
		// before 6am in the morning
		end.set(today.get(Calendar.YEAR), today.get(Calendar.MONTH), today.get(Calendar.DATE)+daysAfter, 5, 59, 59);
		//today
		if(daysAfter == 0){
			if(today.after(end)){
				end = today;
			}
		}
		CalendarPair am = new CalendarPair(start, end);
		day.add(am);
	}
}
class CalendarPair{
	private Calendar start;
	private Calendar end;
	public CalendarPair(Calendar start, Calendar end){
		this.start = start;
		this.end = end;
	}
	public Calendar getStart(){
		return this.start;
	}
	public Calendar getEnd(){
		return this.end;
	}
	public void setStart(Calendar start){
		this.start = start;
	}
	public void setEnd(Calendar end){
		this.end = end;
	}
}