package fancy4.taskie.model;
/**
 * @@author A0107360R
 *
 */
public class TaskieAction {
	
	private TaskieEnum.Actions type;
	private TaskieEnum.TaskType taskType;
	private TaskieTask task;
	private String window; // To indicate which window: left or right
	private	int index = -1;
	private Object keyword;
	
	public TaskieAction(TaskieEnum.Actions type, TaskieTask task) {
		this.type = type;
		this.task = task;
	}
	
	public TaskieAction(TaskieEnum.Actions type, int index) {
		this(type, index, null);
	}
	
	public TaskieAction(TaskieEnum.Actions type, String scr, int index) {
		this(type, index);
		this.window = scr;
	}
	
	public TaskieAction(TaskieEnum.Actions type, int index, TaskieTask task) {
		this(type, task);
		this.index = index;
	}
	
	public TaskieAction(TaskieEnum.Actions type, String scr, int index, TaskieTask task) {
		this(type, index, task);
		this.window = scr;
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
	
	public String getScreen() {
		return window;
	}
	
	public TaskieTask getTask() {
		return task;
	}
	
	public Object getSearch() {
		return this.keyword;
	}
	
	
}