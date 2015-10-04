package fancy4.taskie.model;
/**
 * @author Qin_ShiHuang 
 *
 */

import java.util.ArrayList;
import java.util.Collection;

public class TaskieLogic {
	
	public static void initialise() {
		try {
			TaskieStorage.load("");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static String[][] execute(String str) {
		TaskieAction action = TaskieParser.parse(str);
		String[][] screen = takeAction(action);
		return screen;
	}
	
	private static String[][] takeAction(TaskieAction action) {
		switch (action.getType()) {
			case ADD:
				return add(action.getTask());
			case DELETE:
				return delete(action.getIndex(), action.getTask().getType());
			case UPDATE:
				return update();
			default:
				return add(action.getTask());
		}
	}
	
	public static String[] display(Collection<TaskieTask> taskList) {
		String[] screen = new String[taskList.size()];
		int index = 0;
		for (TaskieTask task : taskList) {
			screen[index++] = index + ". " + task.getTitle();
		}
		return screen;
	}
	
	private static String[][] add(TaskieTask task) {
		Collection<TaskieTask> taskList = TaskieStorage.addTask(task);
		String[] tasks = display(taskList);
		String[] feedback = new String[] {task.getTitle() + " is added"};
		return new String[][] {tasks, feedback};
	}
	
	private static String[][] delete(int index, TaskieEnum.TaskType type) {
		Collection<TaskieTask> taskList = TaskieStorage.deleteTask(index, type);
		String[] tasks = display(taskList);
		String[] feedback = new String[] {task.getTitle() + " is added"};
		return new String[][] {tasks, feedback};
	}
	
	private static String[][] update(int index, TaskieEnum.TaskType type) {
		
	}
	
}
