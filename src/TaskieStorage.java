/**
 * The Taskie storage component
 * @author Qin Xueying
 *
 */
import java.util.*;
import java.io.*;

public class TaskieStorage {
	private File taskFile;
	private ArrayList<Task> eventDeadlineTaskList;
	private ArrayList<Task> floatTaskList;
	private HashMap<Date, ArrayList<Task>> eventDeadlineStartDateMap;
	private HashMap<Date, ArrayList<Task>> eventDeadlineEndDateMap;
	private HashMap<Date, ArrayList<Task>> floatDateMap;
	private HashMap<TaskPriority, ArrayList<Task>> eventDeadlinePriorityMap;
	private HashMap<TaskPriority, ArrayList<Task>> floatPriorityMap;
	private Stack<HashMap<String, Object>> commandStack;
		
	public TaskieStorage(String pathName){
		taskFile = new File(pathName);
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
	
}
