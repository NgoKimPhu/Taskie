import java.util.Collection;


public class TaskieLogic {
	
	//private static TaskieAction action;
	
	public static String[] execute(String str) {
		TaskieAction action = TaskieParser.parse(str);
		Collection<Task> taskList = dispatch(action);
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
