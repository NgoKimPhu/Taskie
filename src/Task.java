/** 
 * This the the Task class for this project.
 * @author Qin Xueying
 *
 */
import java.util.*;

public class Task {
	private TaskType type;
	private String title;
	private long id;
	private String description;
	private Date startTime;
	private Date endTime;
	// we provide 5 levels of priorities;
	private TaskPriority priority;
	// true==done, false==haven't done;
	private boolean status;
	// keep counting the no. of tasks create and be used as the task id.
	private static long count = 0;	
	// create float task.
	public Task(String title){
		this.count+=1;
		this.type = TaskType.FLOAT;
		this.title = title;
		this.id = this.count;
		this.description = new String();
		// start from the moment it is created
		this.startTime = new Date();
		this.endTime = null;
		// the new task's priority is the lowest
		this.priority = TaskPriority.VERY_LOW;
		this.status = false;
	}
	// create float task with specific priority.
	public Task(String title, TaskPriority priority){
		this.count+=1;
		this.type = TaskType.FLOAT;
		this.title = title;
		this.id = this.count;
		this.description = new String();
		this.startTime = new Date();
		this.endTime = null;
		this.priority = priority;
		this.status = false;
	}
	// create deadline task.
	public Task(String title, Date endTime){
		this.count+=1;
		this.type = TaskType.DEADLINE;
		this.title = title;
		this.id = this.count;
		this.description = new String();
		this.startTime = new Date();
		this.endTime = endTime;
		this.priority = TaskPriority.VERY_LOW;
		this.status = false;
	}
	// create deadline task with specific priority.
	public Task(String title, Date endTime, TaskPriority priority){
		this.count+=1;
		this.type = TaskType.DEADLINE;
		this.title = title;
		this.id = this.count;
		this.description = new String();
		this.startTime = new Date();
		this.endTime = endTime;
		this.priority = priority;
		this.status = false;
	}
	// create event.
	public Task(String title, Date startTime, Date endTime){
		this.count+=1;
		this.type = TaskType.EVENT;
		this.title = title;
		this.id = this.count;
		this.description = new String();
		this.startTime = startTime;
		this.endTime = endTime;
		this.priority = TaskPriority.VERY_LOW;
		this.status = false;
	}
	// create event with specific priority.
	public Task(String title, Date startTime, Date endTime, TaskPriority priority){
		this.count+=1;
		this.type = TaskType.EVENT;
		this.title = title;
		this.id = this.count;
		this.description = new String();
		this.startTime = startTime;
		this.endTime = endTime;
		this.priority = priority;
		this.status = false;
	}
	public void setTitle(String title){
		this.title = title;
	}
	public void setPriority(TaskPriority priority){
		this.priority = priority;
	}
	public void setToFloat(){
		this.endTime = null;
		this.type = TaskType.FLOAT;
	}
	public void setToDeadline(Date endTime){
		this.endTime = endTime;
		this.type = TaskType.DEADLINE;
	}
	public void setToEvent(Date startTime, Date endTime){
		this.startTime = startTime;
		this.endTime = endTime;
		this.type = TaskType.EVENT;
	}
	public boolean changeStartTime(Date startTime){
		// only reasonable to change the start time of an event;
		if(this.type.equals(TaskType.EVENT)){
			this.startTime = startTime;
			return true;
		}
		else{
			// the start time of float task and deadline task is the time 
			// it is created thus it should not be changed.
			return false;
		}
	}
	public boolean changeEndTime(Date endTime){
		if(this.type.equals(TaskType.EVENT) || this.type.equals(TaskType.DEADLINE)){
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
	public TaskType getTyte(){
		return this.type;
	}
	public String getTitle(){
		return this.title;
	}
	public long getId(){
		return this.id;
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
	public TaskPriority getPriority(){
		return this.priority;
	}
}

enum TaskType{
	EVENT, DEADLINE, FLOAT;
}
enum TaskPriority{
	VERY_HIGH, HIGH, MEDIUM, LOW, VERY_LOW;
}