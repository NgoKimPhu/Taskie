/**
 * The Taskie storage component
 * @author Qin Xueying
 *
 */
import java.util.*;
public class TaskieStorage {
	private ArrayList<Task> EventDeadlineTaskList;
	private ArrayList<Task> FloatTaskList;
	private HashMap<Date, ArrayList<Task>> EventDeadlineStartDateMap;
	private HashMap<Date, ArrayList<Task>> EventDeadlineEndDateMap;
	private HashMap<Date, ArrayList<Task>> FloatDateMap;
	private HashMap<TaskPriority, ArrayList<Task>> EventDeadlinePriorityMap;
	private HashMap<TaskPriority, ArrayList<Task>> FloatPriorityMap;
	private Stack<HashMap<String, Object>> commandStack;

}
