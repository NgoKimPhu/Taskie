import java.util.Collection;


public class TaskieLogic {
	
	public TaskieLogic() {
	}
	
	public static String[] execute(String str) {
		TaskieAction action = TaskieParser.parse(str);
		Collection<TaskieTask> taskList = takeAction(action);
		// To read the taskList and return a String array
	}
	
	

	private static Collection<TaskieTask> takeAction(TaskieAction action) {
		switch (action.getType()) {
			case ADD:
				return add(action.getTask());
			default:
				return add(action.getTask()); // Will be changed to final message
		}
	}
	
	private static Collection<TaskieTask> add(TaskieTask task) {
		return TaskieStorage.addTask(task);
	}
	
}
