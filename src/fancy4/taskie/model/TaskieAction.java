package fancy4.taskie.model;

import fancy4.taskie.model.TaskieEnum.Actions;

/**
 * @author Qin_ShiHuang
 *
 */
public class TaskieAction {
	
	private Actions type;
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
	
	public Actions getType() {
		return type;
	}
	
	public int getIndex() {
		return index;
	}
	
	public TaskieTask getTask() {
		return task;
	}

}