import java.util.Collection;

public class TaskieLogic {
	
	public TaskieLogic() {
	}
	
	public static String[] execute(String str) {
		TaskieAction action = TaskieParser.parse(str);
		Collection<TaskieTask> taskList = takeAction(action);
		String[] screen = display(taskList);
		return screen;
	}
	
	public static String[] display(Collection<TaskieTask> taskList) {
		String[] screen = new String[taskList.size()];
		int index = 0;
		for (TaskieTask task : taskList) {
			screen[index] = task.getTitle();
		}
		return screen;
	}
	
	

	private static Collection<TaskieTask> takeAction(TaskieAction action) {
		switch (action.getType()) {
			case ADD:
				return add(action.getTask());
			default:
				return add(action.getTask());
		}
	}
	
	private static Collection<TaskieTask> add(TaskieTask task) {
		return TaskieStorage.addTask(task);
	}
	
}
