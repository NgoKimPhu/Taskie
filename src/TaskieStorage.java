/**
 * The Taskie storage component
 * @author Qin Xueying
 *
 */
import java.util.*;
import java.io.*;
import java.util.regex.*;

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
	private static final Pattern TASK_LINE_PATTERN = Pattern.compile("^(EVENT|DEADLINE|FLOAT)\\s(.+)\\s(.+)\\s(.+)\\s([12345])");
	
	
	public TaskieStorage(String pathName){
		eventDeadlineTask = new File(pathName + "/eventDeadline");
		floatTask = new File(pathName + "/floatTask");
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
	
}
