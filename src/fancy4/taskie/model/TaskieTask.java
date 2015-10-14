package fancy4.taskie.model;

/** 
 * This the the Task class for this project.
 * @author Qin Xueying
 *
 */
import java.util.*;

public class TaskieTask {
	private TaskieEnum.TaskType type;
	private String title;
	//private long id;
	private String description;
	private Date startTime;
	private Date endTime;
	// we provide 5 levels of priorities;
	private TaskieEnum.TaskPriority priority;
	// true==done, false==haven't done;
	private boolean status;
	// keep counting the no. of tasks create and be used as the task id.
	//private static long count = 0;	
	// create float task.
	public TaskieTask(String title){
		this.type = TaskieEnum.TaskType.FLOAT;
		this.title = title;
		this.description = new String();
		this.startTime = null;
		this.endTime = null;
		// the new task's priority is the lowest
		this.priority = TaskieEnum.TaskPriority.VERY_LOW;
		this.status = false;
	}
	// create float task with specific priority.
	public TaskieTask(String title, TaskieEnum.TaskPriority priority){
		this.type = TaskieEnum.TaskType.FLOAT;
		this.title = title;
		this.description = new String();
		this.startTime = null;
		this.endTime = null;
		this.priority = priority;
		this.status = false;
	}
	// load existing float task
	public TaskieTask(String title, TaskieEnum.TaskType type, TaskieEnum.TaskPriority priority, boolean status, String description) throws Exception{
		//this.count+=1;
		if(!type.equals(TaskieEnum.TaskType.FLOAT)){
			throw new Exception("Task type not match.");
		}
		else{
			this.type = TaskieEnum.TaskType.FLOAT;
			this.title = title;
			this.description = description;
			this.startTime = null;
			this.endTime = null;
			this.priority = priority;
			this.status = status;
		}
	}
	// create deadline task.
	public TaskieTask(String title, Date endTime){
		this.type = TaskieEnum.TaskType.DEADLINE;
		this.title = title;
		this.description = new String();
		this.startTime = null;
		this.endTime = endTime;
		this.priority = TaskieEnum.TaskPriority.VERY_LOW;
		this.status = false;
	}
	// create deadline task with specific priority.
	public TaskieTask(String title, Date endTime, TaskieEnum.TaskPriority priority){
		this.type = TaskieEnum.TaskType.DEADLINE;
		this.title = title;
		this.description = new String();
		this.startTime = null;
		this.endTime = endTime;
		this.priority = priority;
		this.status = false;
	}
	// create event.
	public TaskieTask(String title, Date startTime, Date endTime){
		this.type = TaskieEnum.TaskType.EVENT;
		this.title = title;
		this.description = new String();
		this.startTime = startTime;
		this.endTime = endTime;
		this.priority = TaskieEnum.TaskPriority.VERY_LOW;
		this.status = false;
	}
	// create event with specific priority.
	public TaskieTask(String title, Date startTime, Date endTime, TaskieEnum.TaskPriority priority){
		this.type = TaskieEnum.TaskType.EVENT;
		this.title = title;
		this.description = new String();
		this.startTime = startTime;
		this.endTime = endTime;
		this.priority = priority;
		this.status = false;
	}
	// load deadline task
	public TaskieTask(String title, TaskieEnum.TaskType type, Date endTime, TaskieEnum.TaskPriority priority, boolean status, String description) throws Exception{
		if(type.equals(TaskieEnum.TaskType.FLOAT)){
			throw new Exception("Task type not match.");
		}
		else{
			this.type = type;
			this.title = title;
			this.description = description;
			this.startTime = null;
			this.endTime = endTime;
			this.priority = priority;
			this.status = status;
		}
	}
	//load event
	public TaskieTask(String title, TaskieEnum.TaskType type, Date startTime, Date endTime, TaskieEnum.TaskPriority priority, boolean status, String description) throws Exception{
		if(type.equals(TaskieEnum.TaskType.FLOAT)){
			throw new Exception("Task type not match.");
		}
		else{
			this.type = type;
			this.title = title;
			this.description = description;
			this.startTime = startTime;
			this.endTime = endTime;
			this.priority = priority;
			this.status = status;
		}
	}
	public void setTitle(String title){
		this.title = title;
	}
	public void setPriority(TaskieEnum.TaskPriority priority){
		this.priority = priority;
	}
	public void setToFloat(){
		this.startTime = null;
		this.endTime = null;
		this.type = TaskieEnum.TaskType.FLOAT;
	}
	public void setToDeadline(Date endTime){
		this.startTime = null;
		this.endTime = endTime;
		this.type = TaskieEnum.TaskType.DEADLINE;
	}
	public void setToEvent(Date startTime, Date endTime){
		this.startTime = startTime;
		this.endTime = endTime;
		this.type = TaskieEnum.TaskType.EVENT;
	}
	public boolean setStartTime(Date startTime){
		// only reasonable to change the start time of an event;
		if(this.type.equals(TaskieEnum.TaskType.EVENT)){
			this.startTime = startTime;
			return true;
		}
		else{
			// the start time of float task and deadline task is the time 
			// it is created thus it should not be changed.
			return false;
		}
	}
	public boolean setEndTime(Date endTime){
		if(this.type.equals(TaskieEnum.TaskType.EVENT) || this.type.equals(TaskieEnum.TaskType.DEADLINE)){
			this.endTime = endTime;
			return true;
		}
		else{
			return false;
		}
	}
	public void setStatus(boolean status){
		this.status = status;
	}
	public void setDescription(String description){
		this.description = description;
	}
	public TaskieEnum.TaskType getType(){
		return this.type;
	}
	public String getTitle(){
		return this.title;
	}
	public String getDescription(){
		return this.description;
	}
	public Date getStartTime(){
		return this.startTime;
	}
	public Date getEndTime(){
		return this.endTime;
	}
	public boolean getStatus(){
		return this.status;
	}
	public TaskieEnum.TaskPriority getPriority(){
		return this.priority;
	}
	public String toString(){
		return this.title;
	}
	public static boolean isEvent(TaskieTask task){
		return task.getType().equals(TaskieEnum.TaskType.EVENT);
	}
	public static boolean isDeadline(TaskieTask task){
		return task.getType().equals(TaskieEnum.TaskType.DEADLINE);
	}
	public static boolean isFloat(TaskieTask task){
		return task.getType().equals(TaskieEnum.TaskType.FLOAT);
	}
	public static boolean isDone(TaskieTask task){
		return task.getStatus()==true;
	}
}

// This comparator will only be used when we sorting event and float tasks
class TaskComparator implements Comparator<TaskieTask> {
	public int compare(TaskieTask task1, TaskieTask task2){
		if(TaskieTask.isEvent(task1) && TaskieTask.isEvent(task2)){
			if(!task1.getStatus() && task2.getStatus()){
				return -1;
			}
			else if(task1.getStatus() && !task2.getStatus()){
				return 1;
			} 
			else{
				if(task1.getStartTime().before(task2.getStartTime())){
					return -1;
				}
				else if(task1.getStartTime().after(task2.getStartTime())){
					return 1;
				}
				else{
					return task1.getTitle().compareTo(task2.getTitle());
				}
			}
		}
		else if(TaskieTask.isDeadline(task1) && TaskieTask.isDeadline(task2)){
			if(!task1.getStatus() && task2.getStatus()){
				return -1;
			}
			else if(task1.getStatus() && !task2.getStatus()){
				return 1;
			} 
			else{
				if(task1.getEndTime().before(task2.getEndTime())){
					return -1;
				}
				else if(task1.getEndTime().after(task2.getEndTime())){
					return 1;
				}
				else{
					return task1.getTitle().compareTo(task2.getTitle());
				}
			}
		}
		else if(TaskieTask.isEvent(task1) && TaskieTask.isDeadline(task2)){
			if(!task1.getStatus() && task2.getStatus()){
				return -1;
			}
			else if(task1.getStatus() && !task2.getStatus()){
				return 1;
			} 
			else{
				if(task1.getStartTime().before(task2.getEndTime())){
					return -1;
				}
				else if(task1.getStartTime().after(task2.getEndTime())){
					return 1;
				}
				else{
					return task1.getTitle().compareTo(task2.getTitle());
				}
			}
		}
		else if(TaskieTask.isDeadline(task1) && TaskieTask.isFloat(task2)){
			if(!task1.getStatus() && task2.getStatus()){
				return -1;
			}
			else if(task1.getStatus() && !task2.getStatus()){
				return 1;
			} 
			else{
				if(task1.getEndTime().before(task2.getStartTime())){
					return -1;
				}
				else if(task1.getEndTime().after(task2.getStartTime())){
					return 1;
				}
				else{
					return task1.getTitle().compareTo(task2.getTitle());
				}
			}
		}
		// float
		else{
			if(!task1.getStatus() && task2.getStatus()){
				return -1;
			}
			else if(task1.getStatus() && !task2.getStatus()){
				return 1;
			} 
			else{
				return task1.getTitle().compareTo(task2.getTitle());
			}
		}
	}
	
}

class IndexTaskPair {
	private int index;
	private TaskieTask task;
	
	public IndexTaskPair(int index, TaskieTask task){
		this.index = index;
		this.task = task;
	}
	
	public int getIndex(){
		return this.index;
	}
	
	public TaskieTask getTask(){
		return this.task;
	}
	
	public void setIndex(int index){
		this.index = index;
	}
	
	public void setTask(TaskieTask task){
		this.task = task;
	}
}