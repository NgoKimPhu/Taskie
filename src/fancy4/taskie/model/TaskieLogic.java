package fancy4.taskie.model;
/**
 * @author Qin_ShiHuang 
 *
 */

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

public class TaskieLogic {
	
	private static ArrayList<TaskieTask> searchResult;
	private static ArrayList<Integer> indexSave;
	
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
			case SEARCH:
				Object searchKey = action.getSearch(); 
				if (searchKey instanceof String)
					return search((ArrayList<String>) action.getSearch(), action.getTask().getType());
				//else if 
			case UPDATE:
				//return update();
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
		TaskieStorage.deleteTask(indexSave.get(index - 1), type);
		String title = searchResult.get(index - 1).getTitle();
		searchResult.remove(index - 1);
		String[] tasks = display(searchResult);
		String[] feedback = new String[] {title + " is deleted"};
		return new String[][] {tasks, feedback};
	}
	/*******
	 * Important remark:
	 * returned
	 * @param keyword
	 * @param type
	 * @return
	 */
	
	//private static String[][] update(int index, TaskieEnum.TaskType type) {
	//}
	
	private static String[][] search(ArrayList<String> keyword, TaskieEnum.TaskType type) {
		Collection<IndexTaskPair> taskList = TaskieStorage.searchTask(keyword, type);
		return null;
	}
/*
	private static String[][] search(Date date, TaskieEnum.TaskType type) {
		Collection<IndexTaskPair> taskList = TaskieStorage.searchTask(date, type);
	}
	
	private static String[][] search(int priority, TaskieEnum.TaskType type) {
		Collection<IndexTaskPair> taskList = TaskieStorage.searchTask(priority, type);
	}
	
	private static String[][] search(boolean isDone, TaskieEnum.TaskType type) {
		Collection<IndexTaskPair> taskList = TaskieStorage.searchTask(isDone, type);
	}
*/
	
	
}
