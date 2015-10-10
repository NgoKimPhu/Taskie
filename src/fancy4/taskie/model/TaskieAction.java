package fancy4.taskie.model;
/**
 * @author Qin_ShiHuang
 *
 */
public class TaskieAction {
	
	private TaskieEnum.Actions type;
	private TaskieEnum.TaskType taskType; // used to select Task among the 3 display windows
											// null = main, deadline = deadline+event, float = float
	private TaskieTask task;
	private	int index;
	private Object keyword;
	
	public TaskieAction(TaskieEnum.Actions type, TaskieTask task) {
		this.type = type;
		this.task = task;
	}
	
	public TaskieAction(TaskieEnum.Actions type, int index, TaskieTask task) {
		this(type, task);
		this.index = index;
	}
	
	public TaskieAction(TaskieEnum.Actions type, TaskieEnum.TaskType taskType, int index, TaskieTask task) {
		this(type, index, task);
		this.taskType = taskType;
	}
	
	public TaskieAction(TaskieEnum.Actions type, TaskieTask task, Object obj) {
		this(type, task);
		this.keyword = obj;
	}
	
	public TaskieEnum.Actions getType() {
		return type;
	}
	
	public TaskieEnum.TaskType getTaskType() {
		return taskType;
	}
	
	public void setTaskType(TaskieEnum.TaskType type) {
		this.taskType = type;
	}
	
	public int getIndex() {
		return index;
	}
	
	public TaskieTask getTask() {
		return task;
	}
	
	public Object getSearch() {
		return this.keyword;
	}
	
	
}