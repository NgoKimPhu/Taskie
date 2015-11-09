//@@author A0119390E
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
	private static File taskFilePath;
	private static ArrayList<TaskieTask> allTasks;
	private static ArrayList<TaskieTask> eventTaskList;
	private static ArrayList<TaskieTask> deadlineTaskList;
	private static ArrayList<TaskieTask> floatTaskList;
	private static EventComparator ec = new EventComparator();
	private static DeadlineComparator dc = new DeadlineComparator();
	private static FloatComparator fc = new FloatComparator();
	private static final String TASK_FILE_PATH_NAME = "taskFilePath.txt";
	private static final String TASKIE_DATA_FOLDER_NAME = "TaskieData";
	private static final String TASK_LIST_FILE_NAME = "taskList.json";
	private static final String INDEX_OUT_OF_BOUNDARY_EXCEPTION_MESSAGE = "Ooops! index out of the bonds!";
	private static final String START_AFTER_END_EXCEPTION_MESSAGE = "Start time should not after end time.";
	private static final String NULL_STRAT_END_EXCEPTION_MESSAGE = "Start time or end time cannot be null";
	
	/**
     * There is the method to load/create file from the user specified path.
     * @param pathName
     * 		The path specified by user in a string format.
     * @return NULL
     * @throws FileNotFoundException, IOException
     */
	public static void load(String pathName) throws Exception {
		String currentPath = new String();
		taskFilePath = new File(TASK_FILE_PATH_NAME);
		File existPath = null;
		if (taskFilePath.exists()) {
			BufferedReader reader = new BufferedReader(new FileReader(taskFilePath));
			currentPath = reader.readLine();
			if (currentPath == null || currentPath.trim().length() == 0) {
				existPath = new File(TASKIE_DATA_FOLDER_NAME);
				FileWriter writer = new FileWriter(taskFilePath);
				writer.write(existPath.getPath());
				writer.flush();
				writer.close();
			} else {
				existPath = new File(currentPath);
			}
		} else {
			taskFilePath.createNewFile();
			existPath = new File(TASKIE_DATA_FOLDER_NAME);
			FileWriter writer = new FileWriter(taskFilePath);
			writer.write(existPath.getPath());
			writer.flush();
			writer.close();
		}
		File folder;
		if (pathName.trim().length() == 0) {
			folder = existPath;
			if (!folder.exists()) {
				folder.mkdir();
			}
		} else {
			File userPath = new File(pathName);
			// the path is not exist, create new path
			if (!userPath.exists()) {
				userPath.mkdir();
			}
			// create data folder under that path
			folder = new File(pathName + "/" + TASKIE_DATA_FOLDER_NAME);
			if (!folder.exists()) {
				folder.mkdir();

			}
		}
		taskList = new File(folder, "/" + TASK_LIST_FILE_NAME);
		if (existPath.exists()) {
			if ((existPath != null) && !existPath.equals(folder)) {
				existPath.renameTo(folder);
			}
		}
		if (taskList.exists()) {
			HashMap<String, ArrayList<TaskieTask>> tasks = FileHandler.readFile(taskList);
			allTasks = tasks.get("all");
			eventTaskList = tasks.get("event");
			deadlineTaskList = tasks.get("deadline");
			floatTaskList = tasks.get("float");
		} else {
			taskList.createNewFile();
			allTasks = new ArrayList<TaskieTask>();
			eventTaskList = new ArrayList<TaskieTask>();
			deadlineTaskList = new ArrayList<TaskieTask>();
			floatTaskList = new ArrayList<TaskieTask>();
		}
		FileHandler.clearFile(taskFilePath);
		FileWriter writer = new FileWriter(taskFilePath, true);
		writer.write(folder.getPath());
		writer.close();

	}

	public static ArrayList<TaskieTask> displayAllTasks() {
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

	/**
     * There is a method to add a task into the task list.
     * @param task
     * 		A TaskieTask object which is going to be added.
     * @return 
     * 		An IndexTaskPair object which contains the task and its position
     * 		in allTasks ArrayList.
     */
	public static IndexTaskPair addTask(TaskieTask task) {
		allTasks.add(task);
		int index = allTasks.indexOf(task);
		if (TaskieTask.isEvent(task)) {
			eventTaskList.add(task);
			Collections.sort(eventTaskList, ec);
		} else if (TaskieTask.isDeadline(task)) {
			deadlineTaskList.add(task);
			Collections.sort(deadlineTaskList, dc);
		} else {// float
			floatTaskList.add(task);
			Collections.sort(floatTaskList, fc);
		}
		rewriteFile();
		return new IndexTaskPair(index, task);
	}
	
	/**
     * There is a method to delete a task from the task list.
     * @param index
     * 		A integer index which indicates the task which is going to be deleted.
     * @return 
     * 		An TaskieTask object which is deleted from the task lists.
     */
	public static TaskieTask deleteTask(int index) throws IndexOutOfBoundsException {
		// 0-based index
		if (index >= allTasks.size()) {
			throw new IndexOutOfBoundsException(INDEX_OUT_OF_BOUNDARY_EXCEPTION_MESSAGE);
		} else {
			TaskieTask deletedTask = allTasks.remove(index);
			if (TaskieTask.isEvent(deletedTask)) {
				eventTaskList.remove(deletedTask);
			} else if (TaskieTask.isDeadline(deletedTask)) {
				deadlineTaskList.remove(deletedTask);
			} else { // float
				floatTaskList.remove(deletedTask);
			}
			rewriteFile();
			return deletedTask;
		}

	}

	/**
     * This is a method to delete all tasks from the task list.
     * @return 
     * 		An Empty ArrayList.
     */
	public static ArrayList<TaskieTask> deleteAll() {
		FileHandler.clearFile(taskList);
		allTasks = new ArrayList<TaskieTask>();
		eventTaskList = new ArrayList<TaskieTask>();
		deadlineTaskList = new ArrayList<TaskieTask>();
		floatTaskList = new ArrayList<TaskieTask>();
		return allTasks;
	}

	
	/**
     * This is method to search tasks which contains keywords from the task list.
     * @param keyWords
     * 		An ArrayList of all the key words.
     * @return 
     * 		An ArrayList of IndexTaskPair objects which indicates the resulting 
     * 		tasks and their indices.
     */
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

	/**
     * This is method to search tasks within a period of time from the task list.
     * @param start end
     * 		Two calendar objects which indicate a period of time
     * @return 
     * 		An ArrayList of IndexTaskPair objects which indicates the resulting 
     * 		tasks and their indices.
     */
	public static ArrayList<IndexTaskPair> searchTask(Calendar start, Calendar end) throws Exception {
		if (start.after(end)) {
			throw new Exception(START_AFTER_END_EXCEPTION_MESSAGE);
		}
		if (start == null || end == null){
			throw new NullPointerException(NULL_STRAT_END_EXCEPTION_MESSAGE);
		}
		ArrayList<IndexTaskPair> searchResult = new ArrayList<IndexTaskPair>();
		for (TaskieTask task : allTasks) {
			if (TaskieTask.isEvent(task)) {
				// inclusive start and exclusive end
				if (end.after(task.getStartTime()) && !start.after(task.getEndTime())) {
					searchResult.add(new IndexTaskPair(allTasks.indexOf(task), task));
				}
			} else if (TaskieTask.isDeadline(task)) {
				if (!task.getEndTime().after(end) && !task.getEndTime().before(start)) {
					searchResult.add(new IndexTaskPair(allTasks.indexOf(task), task));
				}
			}
		}
		return searchResult;
	}

	/**
     * This is method to search tasks with certain priority from the task list.
     * @param priority
     * 		An enumerator which indicates the priority.
     * @return 
     * 		An ArrayList of IndexTaskPair objects which indicates the resulting 
     * 		tasks and their indices.
     */
	public static ArrayList<IndexTaskPair> searchTask(TaskieEnum.TaskPriority priority) {
		ArrayList<IndexTaskPair> searchResult = new ArrayList<IndexTaskPair>();
		for (TaskieTask task : allTasks) {
			if (task.getPriority().equals(priority)) {
				IndexTaskPair pair = new IndexTaskPair(allTasks.indexOf(task), task);
				searchResult.add(pair);
			}
		}
		return searchResult;
	}

	/**
     * This is method to search tasks with certain status from the task list.
     * @param status
     * 		A boolean indicates the task status
     * @return 
     * 		An ArrayList of IndexTaskPair objects which indicates the resulting 
     * 		tasks and their indices.
     */
	public static ArrayList<IndexTaskPair> searchTask(boolean status) {
		ArrayList<IndexTaskPair> searchResult = new ArrayList<IndexTaskPair>();
		for (TaskieTask task : allTasks) {
			if (task.getStatus() == status) {
				IndexTaskPair pair = new IndexTaskPair(allTasks.indexOf(task), task);
				searchResult.add(pair);
			}
		}
		return searchResult;
	}

	/**
     * This is method to change the status of a task.
     * @param index
     * 		An integer index which indicates the task which is going to be modified.
     * @return 
     * 		An ArrayList of all TaskieTask objects in task list.
     * @throws IndexOutOfBoundsException
     */
	public static ArrayList<TaskieTask> changeStatus(int index) throws IndexOutOfBoundsException {
		// 0-based index
		if (index >= allTasks.size()) {
			throw new IndexOutOfBoundsException(INDEX_OUT_OF_BOUNDARY_EXCEPTION_MESSAGE);
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

	/**
     * This is method to change the title of a task.
     * @param index
     * 		An integer index which indicates the task which is going to be modified.
     * @return 
     * 		An ArrayList of all TaskieTask objects in task list.
     * @throws IndexOutOfBoundsException
     */
	public static ArrayList<TaskieTask> updateTaskTitle(int index, String title) throws IndexOutOfBoundsException {
		if (index >= allTasks.size()) {
			throw new IndexOutOfBoundsException(INDEX_OUT_OF_BOUNDARY_EXCEPTION_MESSAGE);
		} else {
			TaskieTask task = allTasks.get(index);
			task.setTitle(title);
			sortList(task.getType());
			rewriteFile();
			return allTasks;
		}
	}

	/**
     * This is method to change the priority of a task.
     * @param index
     * 		An integer index which indicates the task which is going to be modified.
     * @return 
     * 		An ArrayList of all TaskieTask objects in task list.
     * @throws IndexOutOfBoundsException
     */
	public static ArrayList<TaskieTask> updateTaskPriority(int index, TaskieEnum.TaskPriority priority)
			throws IndexOutOfBoundsException {
		if (index >= allTasks.size()) {
			throw new IndexOutOfBoundsException(INDEX_OUT_OF_BOUNDARY_EXCEPTION_MESSAGE);
		} else {
			TaskieTask task = allTasks.get(index);
			if (TaskieTask.isEvent(task)) {
				task.setPriority(priority);
			} else if (TaskieTask.isDeadline(task)) {
				task.setPriority(priority);
			} else {// float
				task.setPriority(priority);
			}
			sortList(task.getType());
			rewriteFile();
			return allTasks;
		}
	}

	/**
     * This is method to change the description of a task. 
     * @param index
     * 		An integer index which indicates the task which is going to be modified.
     * @return 
     * 		An ArrayList of all TaskieTask objects in task list.
     * @throws IndexOutOfBoundsException
     */
	public static ArrayList<TaskieTask> updateTaskDescription(int index, String description)
			throws IndexOutOfBoundsException {
		if (index >= allTasks.size()) {
			throw new IndexOutOfBoundsException(INDEX_OUT_OF_BOUNDARY_EXCEPTION_MESSAGE);
		} else {
			TaskieTask task = allTasks.get(index);
			task.setDescription(description);
			rewriteFile();
			return allTasks;
		}
	}

	/**
     * This is method to change a float task into a deadline task.
     * @param index
     * 		An integer index which indicates the task which is going to be modified.
     * @return 
     * 		An ArrayList of all TaskieTask objects in task list.
     * @throws IndexOutOfBoundsException
     */
	public static ArrayList<TaskieTask> updateFloatToDeadline(int index, Calendar end)
			throws IndexOutOfBoundsException {
		if (index >= allTasks.size()) {
			throw new IndexOutOfBoundsException(INDEX_OUT_OF_BOUNDARY_EXCEPTION_MESSAGE);
		} else {
			TaskieTask task = allTasks.get(index);
			floatTaskList.remove(task);
			task.setToDeadline(end);
			deadlineTaskList.add(task);
			Collections.sort(deadlineTaskList, dc);
			rewriteFile();
			return allTasks;
		}
	}
	
	/**
     * This is method to change a floating task into a event.
     * @param index
     * 		An integer index which indicates the task which is going to be modified.
     * @return 
     * 		An ArrayList of all TaskieTask objects in task list.
     * @throws IndexOutOfBoundsException
     */
	public static ArrayList<TaskieTask> updateFloatToEvent(int index, Calendar start, Calendar end) throws Exception {
		if (index >= allTasks.size()) {
			throw new IndexOutOfBoundsException(INDEX_OUT_OF_BOUNDARY_EXCEPTION_MESSAGE);
		} else {
			TaskieTask task = allTasks.get(index);
			floatTaskList.remove(task);
			try {
				task.setToEvent(start, end);
			} catch (Exception e) {
				throw e;
			}
			eventTaskList.add(task);
			Collections.sort(eventTaskList, ec);
			rewriteFile();
			return allTasks;
		}
	}
	
	/**
     * This is method to change an event or a deadline task into a float task.
     * @param index
     * 		An integer index which indicates the task which is going to be modified.
     * @return 
     * 		An ArrayList of all TaskieTask objects in task list.
     * @throws IndexOutOfBoundsException
     */
	public static ArrayList<TaskieTask> updateEventDeadlineToFloat(int index) throws IndexOutOfBoundsException {
		if (index >= allTasks.size()) {
			throw new IndexOutOfBoundsException(INDEX_OUT_OF_BOUNDARY_EXCEPTION_MESSAGE);
		} else {
			TaskieTask task = allTasks.get(index);
			if (TaskieTask.isEvent(task)) {
				eventTaskList.remove(task);
			} else {
				deadlineTaskList.remove(task);
			}
			task.setToFloat();
			floatTaskList.add(task);
			Collections.sort(floatTaskList, fc);
			rewriteFile();
			return allTasks;
		}
	}
	
	/**
     * This is method to change an event into a deadline task.
     * @param index
     * 		An integer index which indicates the task which is going to be modified.
     * @return 
     * 		An ArrayList of all TaskieTask objects in task list.
     * @throws IndexOutOfBoundsException
     */
	public static ArrayList<TaskieTask> updateEventToDeadline(int index) throws IndexOutOfBoundsException {
		if (index >= allTasks.size()) {
			throw new IndexOutOfBoundsException(INDEX_OUT_OF_BOUNDARY_EXCEPTION_MESSAGE);
		} else {
			TaskieTask task = allTasks.get(index);
			eventTaskList.remove(task);
			task.setToDeadline(task.getEndTime());
			deadlineTaskList.add(task);
			Collections.sort(deadlineTaskList, dc);
			rewriteFile();
			return allTasks;
		}

	}
	
	/**
     * This is method to change a deadline task into an event .
     * @param index
     * 		An integer index which indicates the task which is going to be modified.
     * @return 
     * 		An ArrayList of all TaskieTask objects in task list.
     * @throws IndexOutOfBoundsException
     */
	public static ArrayList<TaskieTask> updateDeadlineToEvent(int index, Calendar start) throws Exception {
		if (index >= allTasks.size()) {
			throw new IndexOutOfBoundsException(INDEX_OUT_OF_BOUNDARY_EXCEPTION_MESSAGE);
		} else {
			TaskieTask task = allTasks.get(index);
			deadlineTaskList.remove(task);
			try {
				task.setToEvent(start, task.getEndTime());
			} catch (Exception e) {
				throw e;
			}
			eventTaskList.add(task);
			Collections.sort(eventTaskList, ec);
			rewriteFile();
			return allTasks;
		}
	}

	/**
     * This is method to change the end time of an event or a deadline task without changing its type .
     * @param index
     * 		An integer index which indicates the task which is going to be modified.
     * @return 
     * 		An ArrayList of all TaskieTask objects in task list.
     * @throws IndexOutOfBoundsException
     */
	public static ArrayList<TaskieTask> updateEventDeadlineEnd(int index, Calendar end)
			throws IndexOutOfBoundsException {
		if (index >= allTasks.size()) {
			throw new IndexOutOfBoundsException(INDEX_OUT_OF_BOUNDARY_EXCEPTION_MESSAGE);
		} else {
			TaskieTask task = allTasks.get(index);
			task.setEndTime(end);
			sortList(task.getType());
			rewriteFile();
			return allTasks;
		}
	}
	
	/**
     * This is method to change the start time of an event without changing its type .
     * @param index
     * 		An integer index which indicates the task which is going to be modified.
     * @return 
     * 		An ArrayList of all TaskieTask objects in task list.
     * @throws IndexOutOfBoundsException
     */
	public static ArrayList<TaskieTask> updateEventStart(int index, Calendar start) throws IndexOutOfBoundsException {
		if (index >= allTasks.size()) {
			throw new IndexOutOfBoundsException(INDEX_OUT_OF_BOUNDARY_EXCEPTION_MESSAGE);
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

	/**
     * This is method to change the start time and end time of an event without changing its type .
     * @param index
     * 		An integer index which indicates the task which is going to be modified.
     * @return 
     * 		An ArrayList of all TaskieTask objects in task list.
     * @throws IndexOutOfBoundsException
     */
	public static ArrayList<TaskieTask> updateEventStartEnd(int index, Calendar start, Calendar end)
			throws IndexOutOfBoundsException {
		if (index >= allTasks.size()) {
			throw new IndexOutOfBoundsException(INDEX_OUT_OF_BOUNDARY_EXCEPTION_MESSAGE);
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
	
	/**
     * This is method to get the description of a certain task.
     * @param index
     * 		An integer index which indicates the task which is going to be view.
     * @return 
     * 		A Sting which is the description of the task.
     * @throws IndexOutOfBoundsException
     */
	public static String viewTaskDescription(int index, TaskieEnum.TaskType type) throws IndexOutOfBoundsException {
		if (index >= allTasks.size()) {
			throw new IndexOutOfBoundsException(INDEX_OUT_OF_BOUNDARY_EXCEPTION_MESSAGE);
		} else {
			String description;
			description = allTasks.get(index).getDescription();
			return description;
		}
	}
	

	public static void editTaskDescription(int index, String description) throws IndexOutOfBoundsException {
		if (index >= allTasks.size()) {
			throw new IndexOutOfBoundsException(INDEX_OUT_OF_BOUNDARY_EXCEPTION_MESSAGE);
		} else {
			String oldDescription = allTasks.get(index).getDescription();
			String newDescription = oldDescription + " " + description;
			allTasks.get(index).setDescription(newDescription);
			rewriteFile();
		}
	}

	/* get the free slot in seven days start from current time. 00:00-06:00 is
	   auto blocked
	 */
	public static ArrayList<CalendarPair> getFreeSlots() {
		ArrayList<CalendarPair> slots = new ArrayList<CalendarPair>();
		CalendarPair slot;
		
		Calendar currentEndOfDay = Calendar.getInstance();
		currentEndOfDay.set(Calendar.HOUR_OF_DAY, 0);
		currentEndOfDay.set(Calendar.MINUTE, 0);
		currentEndOfDay.set(Calendar.SECOND, 0);

		Calendar currentSixAM = (Calendar) currentEndOfDay.clone();
		Calendar sixDaysLater = (Calendar) currentEndOfDay.clone();

		currentEndOfDay.add(Calendar.DATE, 1);
		currentEndOfDay.add(Calendar.SECOND, -1);
		currentSixAM.set(Calendar.HOUR_OF_DAY, 6);
		sixDaysLater.add(Calendar.DATE, 7);

		Calendar currentEnd = Calendar.getInstance().after(currentSixAM) ? Calendar.getInstance() : currentSixAM;

		for (TaskieTask task : eventTaskList) {
			if (!TaskieTask.isDone(task)) {
				while (currentEndOfDay.before(sixDaysLater) && 
					   currentEndOfDay.before(task.getStartTime())) {
					if (currentEnd.before(currentEndOfDay)) {
						slot = new CalendarPair(currentEnd, currentEndOfDay);
						slots.add(slot);
					}
					currentEndOfDay.add(Calendar.DATE, 1);
					currentSixAM.add(Calendar.DATE, 1);
					currentEnd = currentEnd.after(currentSixAM) ? currentEnd : currentSixAM;
				}
				
				if (currentEndOfDay.after(sixDaysLater)) {
					break;
				} else if (task.getEndTime().after(currentEnd)) {
					if (task.getStartTime().after(currentEnd)) {
						slot = new CalendarPair(currentEnd, task.getStartTime());
						slots.add(slot);
						currentEnd = task.getEndTime();
					} else {
						currentEnd = task.getEndTime();
					}
				}
			}
		}

		while (currentEndOfDay.before(sixDaysLater)) {
			if (currentEnd.before(currentEndOfDay)) {
				slot = new CalendarPair(currentEnd, currentEndOfDay);
				slots.add(slot);
			}
			currentEndOfDay.add(Calendar.DATE, 1);
			currentSixAM.add(Calendar.DATE, 1);
			currentEnd = currentEnd.after(currentSixAM) ? currentEnd : currentSixAM;
		}

		return slots;
	}

	private static boolean isOccupied(TaskieTask task, ArrayList<TaskieTask> events) {
		boolean check = false;
		Calendar start = task.getStartTime();
		Calendar end = task.getEndTime();
		for (TaskieTask event : eventTaskList) {
			if (end.before(event.getStartTime()) || !start.before(event.getEndTime())) {
				continue;
			} else {
				check = true;
				break;
			}
		}
		return check;
	}

	private static boolean isEmpty(ArrayList<TaskieTask> tasks) {
		return tasks.size() == 0;
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
			FileHandler.writeFile(taskList, t);
		}
		for (TaskieTask t : deadlineTaskList) {
			FileHandler.writeFile(taskList, t);
		}
		for (TaskieTask t : floatTaskList) {
			FileHandler.writeFile(taskList, t);
		}
	}
}

class FileHandler {
	private static final SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm");
	private static Logger logger = Logger.getLogger(FileHandler.class.getName());
	
	/**
     * This is method to read JSON objects from a file
     * @param file
     * 		The file which is going to be read
     * @return 
     * 		A HashMap object contains all tasks list, all events list, 
     * 		all deadline tasks list, all floating tasks list
     */
	public static HashMap<String, ArrayList<TaskieTask>> readFile(File file) {
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
					TaskieEnum.TaskType type;
					try {
						type = getTaskType(taskData.getInt("type"));
					} catch (Exception e) {
						type = TaskieEnum.TaskType.EVENT;
					}
					Calendar start = getDate(taskData.getString("start-time"));
					Calendar end = getDate(taskData.getString("end-time"));
					TaskieEnum.TaskPriority priority;
					try {
						priority = getTaskPriority(taskData.getInt("priority"));
					} catch (Exception e) {
						priority = TaskieEnum.TaskPriority.LOW;
					}
					boolean status = taskData.getBoolean("status");
					String description = taskData.getString("description");
					try {
						TaskieTask task = new TaskieTask(title, type, start, end, priority, status, description);
						events.add(task);
						all.add(task);
					} catch (Exception e) {
						continue;
					}
				} else if (taskLine.has("deadline")) {
					JSONObject taskData = taskLine.getJSONObject("deadline");
					String title = taskData.getString("title");
					TaskieEnum.TaskType type;
					try {
						type = getTaskType(taskData.getInt("type"));
					} catch (Exception e) {
						type = TaskieEnum.TaskType.DEADLINE;
					}
					Calendar end = getDate(taskData.getString("end-time"));
					TaskieEnum.TaskPriority priority;
					try {
						priority = getTaskPriority(taskData.getInt("priority"));
					} catch (Exception e) {
						priority = TaskieEnum.TaskPriority.LOW;
					}
					boolean status = taskData.getBoolean("status");
					String description = taskData.getString("description");
					try {
						TaskieTask task = new TaskieTask(title, type, end, priority, status, description);
						deadlines.add(task);
						all.add(task);
					} catch (Exception e) {
						continue;
					}
				} else if (taskLine.has("float")) {
					JSONObject taskData = taskLine.getJSONObject("float");
					String title = taskData.getString("title");
					TaskieEnum.TaskType type;
					try {
						type = getTaskType(taskData.getInt("type"));
					} catch (Exception e) {
						type = TaskieEnum.TaskType.FLOAT;
					}
					TaskieEnum.TaskPriority priority;
					try {
						priority = getTaskPriority(taskData.getInt("priority"));
					} catch (Exception e) {
						priority = TaskieEnum.TaskPriority.LOW;
					}
					boolean status = taskData.getBoolean("status");
					String description = taskData.getString("description");
					try {
						TaskieTask task = new TaskieTask(title, type, priority, status, description);
						floats.add(task);
						all.add(task);
					} catch (Exception e) {
						continue;
					}
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

	/**
     * This is method to write JSON objects a file
     * @param file, task
     * 		The file which is being written and the task data which is going 
     * 		to be written into the file.
     * @return NULL
     */
	public static void writeFile(File file, TaskieTask task) {
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
		int month = Integer.valueOf(dayMonthYear[1]) - 1;
		int year = Integer.valueOf(dayMonthYear[2]);
		int hour = Integer.valueOf(hourMinute[0]);
		int minute = Integer.valueOf(hourMinute[1]);
		calendar.set(year, month, day, hour, minute);
		return calendar;
	}

	private static TaskieEnum.TaskType getTaskType(int type) throws Exception {
		if (isEvent(type)) {
			return TaskieEnum.TaskType.EVENT;
		} else if (isDeadline(type)) {
			return TaskieEnum.TaskType.DEADLINE;
		} else if (isFloat(type)) {
			return TaskieEnum.TaskType.FLOAT;
		} else {
			throw new Exception("invalid task type");
		}
	}

	private static boolean isEvent(int type) {
		return type == 0;
	}

	private static boolean isDeadline(int type) {
		return type == 1;
	}

	private static boolean isFloat(int type) {
		return type == 2;
	}

	private static TaskieEnum.TaskPriority getTaskPriority(int priority) throws Exception {
		if (isLow(priority)) {
			return TaskieEnum.TaskPriority.LOW;
		} else if (isHigh(priority)) {
			return TaskieEnum.TaskPriority.HIGH;
		} else {
			throw new Exception("invalid priority");
		}
	}

	private static boolean isLow(int priority) {
		return priority == 0;
	}

	private static boolean isHigh(int priority) {
		return priority == 1;
	}
}

class CalendarPair {
	private Calendar start;
	private Calendar end;
	private static final SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm");

	public CalendarPair(Calendar start, Calendar end) {
		this.start = (Calendar) start.clone();
		this.end = (Calendar) end.clone();
	}

	public Calendar getStart() {
		return this.start;
	}

	public Calendar getEnd() {
		return this.end;
	}

	public void setStart(Calendar start) {
		this.start = start;
	}

	public void setEnd(Calendar end) {
		this.end = end;
	}

	public String toString() {
		String pair;
		pair = "(" + sdf.format(this.start.getTime()) + " - " + sdf.format(this.end.getTime()) + ")";
		return pair;
	}
}