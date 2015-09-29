/**
 * @author Qin_ShiHuang
 *
 */
public class TaskieAction {
	
	private TaskieEnum.Actions type;
	private TaskieTask task;
	private	int index;
	
	public TaskieAction(TaskieEnum.Actions type, TaskieTask task) {
		this.type = type;
		this.task = task;
	}
	
	public TaskieAction(TaskieEnum.Actions type, TaskieTask task, int index) {
		this(type, task);
		this.index = index;
	}
	
	public TaskieEnum.Actions getType() {
		return type;
	}
	
	public int getIndex() {
		return index;
	}
	
	public TaskieTask getTask() {
		return task;
	}

}