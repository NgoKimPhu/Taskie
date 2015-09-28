import java.util.Collection;


public class TaskieLogic {
	
	public TaskieLogic() {
	}
	
	public static String[] execute(String str) {
		TaskieAction action = TaskieParser.parse(str);
		Collection<TaskieTask> taskList = dispatch(action);
		// To read the taskList and return a String array
	}
	
	

	private static Collection<TaskieTask> dispatch(TaskieAction action) {
		switch (action.getType()) {
			case ADD:
				add(action.getTask());
				break;
			default:
				System.out.println("What are you looking for?");
				break;
		}
	}
	
	private static Collection<TaskieTask> add(TaskieTask task) {
		return TaskieStorage.add(task);
	}
	
}
