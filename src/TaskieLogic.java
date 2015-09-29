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
//				return delete(action.getIndex());
			default:
				return add(action.getTask());
		}
	}
	
	public static String[] display(Collection<TaskieTask> taskList) {
		String[] screen = new String[taskList.size()];
		int index = 0;
		for (TaskieTask task : taskList) {
			screen[index] = index + ". " + task.getTitle();
		}
		return screen;
	}
	
	private static String[][] add(TaskieTask task) {
		Collection<TaskieTask> taskList = TaskieStorage.addTask(task);
		String[] tasks = display(taskList);
		String[] feedback = new String[] {task.getTitle() + " is added"};
		return new String[][] {tasks, feedback};
	}
/*	
	private static Collection<TaskieTask> delete(int index) {
		return TaskieStorage.deleteTask(index, );
	}
*/	
}
