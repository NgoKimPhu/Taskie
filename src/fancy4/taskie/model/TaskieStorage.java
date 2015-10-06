package fancy4.taskie.model;
/**
 * The Taskie storage component
 * @author Qin Xueying
 *
 */
import java.util.*;
import java.io.*;
import java.nio.file.Files;
import java.util.regex.*;
import java.text.*;

import org.json.*;

public class TaskieStorage {

	private static File eventDeadlineTask;
	private static File floatTask;
	private static ArrayList<TaskieTask> eventDeadlineTaskList;
	private static ArrayList<TaskieTask> floatTaskList;
	private static HashMap<Date, ArrayList<TaskieTask>> eventStartDateMap;
	private static HashMap<Date, ArrayList<TaskieTask>> eventEndDateMap;
	private static HashMap<Date, ArrayList<TaskieTask>> deadlineEndDateMap;
	private static HashMap<TaskieEnum.TaskPriority, ArrayList<TaskieTask>> eventPriorityMap;
	private static HashMap<TaskieEnum.TaskPriority, ArrayList<TaskieTask>> deadlinePriorityMap;
	private static HashMap<TaskieEnum.TaskPriority, ArrayList<TaskieTask>> floatPriorityMap;
	private static Stack<HashMap<String, Object>> commandStack;
	private static TaskComparator tc = new TaskComparator();

	public static void load(String pathName) throws Exception {
		File folder = new File(pathName);
		if(!folder.exists()){
			folder.mkdir();
		}
		eventDeadlineTask = new File(folder, "/eventDeadline.json");
		floatTask = new File(folder,  "/floatTask.json");
		System.out.println(folder.toPath());
		eventStartDateMap = new HashMap<Date, ArrayList<TaskieTask>>();
		eventEndDateMap = new HashMap<Date, ArrayList<TaskieTask>>();
		deadlineEndDateMap = new HashMap<Date, ArrayList<TaskieTask>>();
		eventPriorityMap = new HashMap<TaskieEnum.TaskPriority, ArrayList<TaskieTask>>();
		deadlinePriorityMap = new HashMap<TaskieEnum.TaskPriority, ArrayList<TaskieTask>>();
		floatPriorityMap = new HashMap<TaskieEnum.TaskPriority, ArrayList<TaskieTask>>();
		commandStack = new Stack<HashMap<String, Object>>();
		if(eventDeadlineTask.exists()){
			eventDeadlineTaskList = FileHandler.readEventDeadlineFile(eventDeadlineTask);
			for(TaskieTask task: eventDeadlineTaskList){
				// deal with event start time
				if(TaskieTask.isEvent(task)){
					//keep two copies of date-task pair in map, one with specific time one without
					//event start time
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
					//event end time
					Date end = task.getEndTime();
					Date endKey = createDateKey(end);
					if(!eventEndDateMap.containsKey(end)){
						eventEndDateMap.put(end, new ArrayList<TaskieTask>());
					}
					eventEndDateMap.get(end).add(task);
					if(!eventEndDateMap.containsKey(endKey)){
						eventEndDateMap.put(endKey, new ArrayList<TaskieTask>());
					}
					eventEndDateMap.get(endKey).add(task);	
					// event priority
					TaskieEnum.TaskPriority priority = task.getPriority();
					if(!eventPriorityMap.containsKey(priority)){
						eventPriorityMap.put(priority, new ArrayList<TaskieTask>());
					}
					eventPriorityMap.get(priority).add(task);
				}
				
				else{
					//deal with deadline end time
					Date end = task.getEndTime();
					Date endKey = createDateKey(end);
					if(!deadlineEndDateMap.containsKey(end)){
						deadlineEndDateMap.put(end, new ArrayList<TaskieTask>());
					}
					deadlineEndDateMap.get(end).add(task);
					if(!deadlineEndDateMap.containsKey(endKey)){
						deadlineEndDateMap.put(endKey, new ArrayList<TaskieTask>());
					}
					deadlineEndDateMap.get(endKey).add(task);
					//deadline priority
					TaskieEnum.TaskPriority priority = task.getPriority();
					if(!deadlinePriorityMap.containsKey(priority)){
						deadlinePriorityMap.put(priority, new ArrayList<TaskieTask>());
					}
					deadlinePriorityMap.get(priority).add(task);
				}
				
				
			}
			
		}
		else{
			eventDeadlineTask.createNewFile();
			eventDeadlineTaskList = new ArrayList<TaskieTask>();
		}
		if(floatTask.exists()){
			floatTaskList = FileHandler.readFloatFile(floatTask);
			for(TaskieTask task: floatTaskList){
				TaskieEnum.TaskPriority priority = task.getPriority();
				if(!floatPriorityMap.containsKey(priority)){
					floatPriorityMap.put(priority, new ArrayList<TaskieTask>());
				}
				floatPriorityMap.get(priority).add(task);
			}
		}
		else{
			floatTask.createNewFile();
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
				FileHandler.writeFile(eventDeadlineTask, t);
			}
			//add event map
			if(TaskieTask.isEvent(task)){
				if (!eventStartDateMap.containsKey(task.getStartTime())) {
					ArrayList<TaskieTask> tasks = new ArrayList<TaskieTask>();

					tasks.add(task);
					eventStartDateMap.put(task.getStartTime(), tasks);
				} else {
					eventStartDateMap.get(task.getStartTime()).add(task);
				}
				if (!eventEndDateMap.containsKey(task.getEndTime())) {
					ArrayList<TaskieTask> tasks = new ArrayList<TaskieTask>();

					tasks.add(task);
					eventEndDateMap.put(task.getEndTime(), tasks);
				} else {
					eventEndDateMap.get(task.getEndTime()).add(task);
				}
				if (!eventPriorityMap.containsKey(task.getPriority())) {
					ArrayList<TaskieTask> tasks = new ArrayList<TaskieTask>();
					tasks.add(task);
					eventPriorityMap.put(task.getPriority(), tasks);
				} else {
					eventPriorityMap.get(task.getPriority()).add(task);
				}
			}
			// add deadline map
			else{
				if (!deadlineEndDateMap.containsKey(task.getEndTime())) {
					ArrayList<TaskieTask> tasks = new ArrayList<TaskieTask>();
					tasks.add(task);
					deadlineEndDateMap.put(task.getEndTime(), tasks);
				} else {
					deadlineEndDateMap.get(task.getEndTime()).add(task);
				}
				if (!deadlinePriorityMap.containsKey(task.getPriority())) {
					ArrayList<TaskieTask> tasks = new ArrayList<TaskieTask>();
					tasks.add(task);
					deadlinePriorityMap.put(task.getPriority(), tasks);
				} else {
					deadlinePriorityMap.get(task.getPriority()).add(task);
				}
			}

			
			return eventDeadlineTaskList;
		} else {
			floatTaskList.add(task);
			FileHandler.writeFile(floatTask, task);

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
			TaskieTask task = eventDeadlineTaskList.get(index);
			if(task.getType().equals(type)){
				task = eventDeadlineTaskList.remove(index);
				FileHandler.clearFile(eventDeadlineTask);
				for(TaskieTask remainingTask: eventDeadlineTaskList){
					FileHandler.writeFile(eventDeadlineTask, remainingTask);
				}
				// delete from event maps
				if(TaskieTask.isEvent(task)){
					eventStartDateMap.get(task.getStartTime()).remove(task);
					if (eventStartDateMap.get(task.getStartTime()).size() == 0) {
						eventStartDateMap.remove(task.getStartTime());
					}
					eventEndDateMap.get(task.getEndTime()).remove(task);
					if (eventEndDateMap.get(task.getEndTime()).size() == 0) {
						eventEndDateMap.remove(task.getEndTime());
					}
					eventPriorityMap.get(task.getPriority()).remove(task);
					if (eventPriorityMap.get(task.getPriority()).size() == 0) {
						eventPriorityMap.remove(task.getPriority());
					}
				}
				// delete from deadline maps
				else{
					deadlineEndDateMap.get(task.getEndTime()).remove(task);
					if (deadlineEndDateMap.get(task.getEndTime()).size() == 0) {
						deadlineEndDateMap.remove(task.getEndTime());
					}
					deadlinePriorityMap.get(task.getPriority()).remove(task);
					if (deadlinePriorityMap.get(task.getPriority()).size() == 0) {
						deadlinePriorityMap.remove(task.getPriority());
					}
				}
				
			}
			return eventDeadlineTaskList;
		} else {
			TaskieTask task = floatTaskList.remove(index);
			floatPriorityMap.get(task.getPriority()).remove(task);
			FileHandler.clearFile(floatTask);
			for(TaskieTask remainingTask: floatTaskList){
				FileHandler.writeFile(floatTask, remainingTask);
			}
			if (floatPriorityMap.get(task.getPriority()).size() == 0) {
				floatPriorityMap.remove(task.getPriority());
			}
			return floatTaskList;
		}
	}
	//delete all,return value index 0--eventDeadlineTask, 1--floatTask
	public static ArrayList<ArrayList<TaskieTask>> deleteAll(){
		FileHandler.clearFile(eventDeadlineTask);
		FileHandler.clearFile(floatTask);
		eventDeadlineTaskList = new ArrayList<TaskieTask>();
		floatTaskList =  new ArrayList<TaskieTask>();
		eventStartDateMap = new HashMap<Date, ArrayList<TaskieTask>>();
		eventEndDateMap = new HashMap<Date, ArrayList<TaskieTask>>();
		deadlineEndDateMap = new HashMap<Date, ArrayList<TaskieTask>>();
		eventPriorityMap = new HashMap<TaskieEnum.TaskPriority, ArrayList<TaskieTask>>();
		deadlinePriorityMap = new HashMap<TaskieEnum.TaskPriority, ArrayList<TaskieTask>>();
		floatPriorityMap = new HashMap<TaskieEnum.TaskPriority, ArrayList<TaskieTask>>();
		ArrayList<ArrayList<TaskieTask>> returnList = new ArrayList<ArrayList<TaskieTask>>();
		returnList.add(eventDeadlineTaskList);
		returnList.add(floatTaskList);
		return returnList;
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
	
	public static ArrayList<IndexTaskPair> searchTask(Date start){
		ArrayList<IndexTaskPair> searchResult = new ArrayList<IndexTaskPair>();
		if(!eventStartDateMap.containsKey(start)){
			return searchResult;
		}
		else{
			ArrayList<TaskieTask> tasks = eventStartDateMap.get(start);
			for(TaskieTask task: tasks){
				IndexTaskPair pair = new IndexTaskPair(eventDeadlineTaskList.indexOf(task), task);
				searchResult.add(pair);
			}
			return searchResult;
		}
	}
	public static ArrayList<IndexTaskPair> searchTask(Date end, TaskieEnum.TaskType type){
		ArrayList<IndexTaskPair> searchResult = new ArrayList<IndexTaskPair>();
		if(type.equals(TaskieEnum.TaskType.EVENT)){
			if(!eventEndDateMap.containsKey(end)){
				return searchResult;
			}
			else{
				ArrayList<TaskieTask> tasks = eventEndDateMap.get(end);
				for(TaskieTask task: tasks){
					IndexTaskPair pair = new IndexTaskPair(eventDeadlineTaskList.indexOf(task), task);
					searchResult.add(pair);
				}
				return searchResult;
			}
		}
		else if(type.equals(TaskieEnum.TaskType.DEADLINE)){
			if(!deadlineEndDateMap.containsKey(end)){
				return searchResult;
			}
			else{
				ArrayList<TaskieTask> tasks = deadlineEndDateMap.get(end);
				for(TaskieTask task: tasks){
					IndexTaskPair pair = new IndexTaskPair(eventDeadlineTaskList.indexOf(task), task);
					searchResult.add(pair);
				}
				return searchResult;
			}
		}
		else{
			return searchResult;
		}
	}
	public static ArrayList<IndexTaskPair> searchTask(TaskieEnum.TaskPriority priority, TaskieEnum.TaskType type){
		ArrayList<IndexTaskPair> searchResult = new ArrayList<IndexTaskPair>();
		if(type.equals(TaskieEnum.TaskType.EVENT)){
			if(!eventPriorityMap.containsKey(priority)){
				return searchResult;
			}
			else{
				ArrayList<TaskieTask> tasks = eventPriorityMap.get(priority);
				for(TaskieTask task: tasks){
					IndexTaskPair pair = new IndexTaskPair(eventDeadlineTaskList.indexOf(task), task);
					searchResult.add(pair);
				}
				return searchResult;
			}
		}
		else if(type.equals(TaskieEnum.TaskType.DEADLINE)){
			if(!deadlinePriorityMap.containsKey(priority)){
				return searchResult;
			}
			else{
				ArrayList<TaskieTask> tasks = deadlinePriorityMap.get(priority);
				for(TaskieTask task: tasks){
					IndexTaskPair pair = new IndexTaskPair(eventDeadlineTaskList.indexOf(task), task);
					searchResult.add(pair);
				}
				return searchResult;
			}
		}
		else{
			return searchResult;
		}	
	}
	
	public static ArrayList<IndexTaskPair> searchTask(boolean done, TaskieEnum.TaskType type){
		ArrayList<IndexTaskPair> searchResult = new ArrayList<IndexTaskPair>();
		if(type.equals(TaskieEnum.TaskType.EVENT)){
			for(TaskieTask task: eventDeadlineTaskList){
				if(TaskieTask.isEvent(task) && TaskieTask.isDone(task)){
					IndexTaskPair pair = new IndexTaskPair(eventDeadlineTaskList.indexOf(task), task);
					searchResult.add(pair);
				}
			}
			return searchResult;
		}
		else if(type.equals(TaskieEnum.TaskType.DEADLINE)){
			for(TaskieTask task: eventDeadlineTaskList){
				if(TaskieTask.isDeadline(task) && TaskieTask.isDone(task)){
					IndexTaskPair pair = new IndexTaskPair(eventDeadlineTaskList.indexOf(task), task);
					searchResult.add(pair);
				}
			}
			return searchResult;
		}
		else if(type.equals(TaskieEnum.TaskType.FLOAT)){
			for(TaskieTask task: floatTaskList){
				if(TaskieTask.isDone(task)){
					IndexTaskPair pair = new IndexTaskPair(eventDeadlineTaskList.indexOf(task), task);
					searchResult.add(pair);
				}
			}
			return searchResult;
		}
		else{
			return searchResult;
		}
	} 
	

	public static ArrayList<TaskieTask> markDown(int index, TaskieEnum.TaskType type) {
		if (type.equals(TaskieEnum.TaskType.EVENT) || type.equals(TaskieEnum.TaskType.DEADLINE)) {
			eventDeadlineTaskList.get(index).setStatus(true);
			Collections.sort(eventDeadlineTaskList, tc);
			return eventDeadlineTaskList;
		} else {
			floatTaskList.get(index).setStatus(true);
			Collections.sort(floatTaskList, tc);
			return floatTaskList;
		}
	}

	public static ArrayList<TaskieTask> updateTaskTitle(int index, TaskieEnum.TaskType type, String title){
		if(type.equals(TaskieEnum.TaskType.EVENT) || type.equals(TaskieEnum.TaskType.DEADLINE)){
			TaskieTask task = eventDeadlineTaskList.get(index);
			task.setTitle(title);
			Collections.sort(eventDeadlineTaskList, tc);
			return eventDeadlineTaskList;
		}
		else{
			TaskieTask task = floatTaskList.get(index);
			task.setTitle(title);
			Collections.sort(floatTaskList, tc);
			return floatTaskList;
		}
	}
	public static ArrayList<TaskieTask> updateTaskPriority(int index, TaskieEnum.TaskType type, TaskieEnum.TaskPriority priority){
		if(type.equals(TaskieEnum.TaskType.EVENT) || type.equals(TaskieEnum.TaskType.DEADLINE)){
			TaskieTask task = eventDeadlineTaskList.get(index);
			task.setPriority(priority);
			Collections.sort(eventDeadlineTaskList, tc);
			return eventDeadlineTaskList;
		}
		else{
			TaskieTask task = floatTaskList.get(index);
			task.setPriority(priority);
			Collections.sort(floatTaskList, tc);
			return floatTaskList;
		}
	}
	public static ArrayList<TaskieTask> updateTaskDescription(int index, TaskieEnum.TaskType type, String description){
		if(type.equals(TaskieEnum.TaskType.EVENT) || type.equals(TaskieEnum.TaskType.DEADLINE)){
			TaskieTask task = eventDeadlineTaskList.get(index);
			task.setDescription(description);
			return eventDeadlineTaskList;
		}
		else{
			TaskieTask task = floatTaskList.get(index);
			task.setDescription(description);
			return floatTaskList;
		}
	}
	//index 0-eventdeadline 1-float
	public static ArrayList<ArrayList<TaskieTask>> updateFloatToDeadline(int index, Date end){
		ArrayList<ArrayList<TaskieTask>> returnResult = new ArrayList<ArrayList<TaskieTask>>();
		TaskieTask task = floatTaskList.remove(index);
		task.setToDeadline(end);
		eventDeadlineTaskList.add(task);
		Collections.sort(eventDeadlineTaskList, tc);
		returnResult.add(eventDeadlineTaskList);
		returnResult.add(floatTaskList);
		return returnResult;
	}
	public static ArrayList<ArrayList<TaskieTask>> updateFloatToEvent(int index, Date start, Date end){
		ArrayList<ArrayList<TaskieTask>> returnResult = new ArrayList<ArrayList<TaskieTask>>();
		TaskieTask task = floatTaskList.remove(index);
		task.setToEvent(start, end);
		eventDeadlineTaskList.add(task);
		Collections.sort(eventDeadlineTaskList, tc);
		returnResult.add(eventDeadlineTaskList);
		returnResult.add(floatTaskList);
		return returnResult;
	}
	public static ArrayList<ArrayList<TaskieTask>> updateEventDeadlineToFloat(int index){
		ArrayList<ArrayList<TaskieTask>> returnResult = new ArrayList<ArrayList<TaskieTask>>();
		TaskieTask task = eventDeadlineTaskList.remove(index);
		task.setToFloat();
		Collections.sort(floatTaskList, tc);
		returnResult.add(eventDeadlineTaskList);
		returnResult.add(floatTaskList);
		return returnResult;
	}
	public static ArrayList<TaskieTask> updateEventToDeadline(int index){
		TaskieTask task = eventDeadlineTaskList.get(index);
		task.setToDeadline(task.getEndTime());
		Collections.sort(eventDeadlineTaskList, tc);
		return eventDeadlineTaskList;
	}
	public static ArrayList<TaskieTask> updateDeadlineToEvent(int index, Date start){
		TaskieTask task = eventDeadlineTaskList.get(index);
		task.setToEvent(start, task.getEndTime());
		Collections.sort(eventDeadlineTaskList, tc);
		return eventDeadlineTaskList;
	}
	public static ArrayList<TaskieTask> updateEventDeadlineEnd(int index, Date end){
		TaskieTask task = eventDeadlineTaskList.get(index);
		task.setEndTime(end);
		Collections.sort(eventDeadlineTaskList, tc);
		return eventDeadlineTaskList;
	}
	public static ArrayList<TaskieTask> updateEventStart(int index, Date start){	
		TaskieTask task = eventDeadlineTaskList.get(index);
		if(TaskieTask.isEvent(task)){
			task.setStartTime(start);
			Collections.sort(eventDeadlineTaskList, tc);
		}
		return eventDeadlineTaskList;
	}
	public static ArrayList<TaskieTask> updateEventStartEnd(int index, Date start, Date end){
		TaskieTask task = floatTaskList.remove(index);
		if(TaskieTask.isEvent(task)){
			task.setStartTime(start);
			task.setEndTime(end);
			Collections.sort(eventDeadlineTaskList, tc);
		}
		return eventDeadlineTaskList;
	}	
}

class FileHandler {
	private static final SimpleDateFormat sdf = new SimpleDateFormat(
			"dd-MM-yyyy HH:mm");

	public static ArrayList<TaskieTask> readEventDeadlineFile(File file) throws Exception {
		String line = new String();
		ArrayList<TaskieTask> fileContent = new ArrayList<TaskieTask>();
		try {
			BufferedReader in = new BufferedReader(new FileReader(file));
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
			for(TaskieTask t: fileContent){
				System.out.println(t.getTitle());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return fileContent;
	}
	public static ArrayList<TaskieTask> readFloatFile(File file) throws Exception {
		String line = new String();
		ArrayList<TaskieTask> fileContent = new ArrayList<TaskieTask>();
		try {
			BufferedReader in = new BufferedReader(new FileReader(file));
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
	public static void writeFile(File file, TaskieTask task) {
		//String fileName = file.getName();
		if (TaskieTask.isEvent(task) || TaskieTask.isDeadline(task)) {
			try {
				FileWriter writer = new FileWriter(file, true);
				JSONWriter jWriter = new JSONWriter(writer);
				jWriter.object();
				jWriter.key("task");
				jWriter.object();
				jWriter.key("title");
				jWriter.value(task.getTitle());
				
				jWriter.key("type");
				jWriter.value(task.getType().ordinal());
				
				jWriter.key("start-time");
				jWriter.value(TaskieTask.isDeadline(task)? "null": sdf.format(task.getStartTime()));
				
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
				FileWriter writer = new FileWriter(file, true);
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
		//String fileName = file.getName();
		try {
			FileWriter writer = new FileWriter(file);
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