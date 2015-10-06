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
			searchResult = new ArrayList<TaskieTask>();
			indexSave = new ArrayList<Integer>();
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
		try {
			switch (action.getType()) {
			case ADD:
				return add(action.getTask());
			case DELETE:
				return delete(action.getIndex(), action.getTask().getType());
			case SEARCH:
				return search(action);
			case UPDATE:
				return update(action.getIndex(), action.getTask());
			default:
				return add(action.getTask());
			}
		} catch (UnrecognisedCommandException e) {
			return new String[][] {};
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
		String[] feedback = new String[] { task.getTitle() + " is added" };
		return new String[][] { tasks, feedback };
	}

	private static String[][] delete(int index, TaskieEnum.TaskType type) {
		TaskieStorage.deleteTask(indexSave.get(index - 1), type);
		String title = searchResult.get(index - 1).getTitle();
		searchResult.remove(index - 1);
		String[] tasks = display(searchResult);
		String[] feedback = new String[] { title + " is deleted" };
		return new String[][] { tasks, feedback };
	}

	private static String[][] search(TaskieAction action)
			throws UnrecognisedCommandException {
		TaskieEnum.TaskType type = action.getTask().getType();
		Object searchKey = action.getSearch();
		Collection<IndexTaskPair> taskList;
		if (searchKey instanceof String) {
			taskList = TaskieStorage.searchTask((ArrayList<String>) searchKey,
					type);
		} else if (searchKey instanceof Date) {
			taskList = TaskieStorage.searchTask((Date) searchKey, type);
		} else if (searchKey instanceof Integer) {
			taskList = TaskieStorage.searchTask(
					(TaskieEnum.TaskPriority) searchKey, type);
		} else if (searchKey instanceof Boolean) {
			taskList = TaskieStorage.searchTask((Boolean) searchKey, type);
		} else {
			throw new UnrecognisedCommandException("Unrecognised search key.");
		}
		searchResult.clear();
		indexSave.clear();
		for (IndexTaskPair pair : taskList) {
			searchResult.add(pair.getTask());
			indexSave.add(pair.getIndex());
		}
		String[] tasks = display(searchResult);
		String[] feedback = new String[] { "Search finished in 0.00019 seconds." };
		return new String[][] { tasks, feedback };
	}

	private static String[][] update(int index, TaskieTask task) throws UnrecognisedCommandException {
		Collection<TaskieTask> taskList = new ArrayList<TaskieTask>();
		if (task.getTitle() != null)
			TaskieStorage.updateTaskTitle(index, task.getType(), task.getTitle());
		else if (task.getType() == TaskieEnum.TaskType.FLOAT &&
				 task.getStartTime() == null && task.getEndTime() != null)
			TaskieStorage.updateFloatToDeadline(index, task.getEndTime());
		else if (task.getType() == TaskieEnum.TaskType.FLOAT &&
				task.getStartTime() != null && task.getEndTime() != null)
			TaskieStorage.updateFloatToEvent(index, task.getStartTime(), task.getEndTime());
		else if ((task.getType() == TaskieEnum.TaskType.EVENT ||
				 task.getType() == TaskieEnum.TaskType.EVENT) && 
				 task.getStartTime() == null && task.getEndTime() == null)
			TaskieStorage.updateEventDeadlineToFloat(index);
		else if (task.getType() == TaskieEnum.TaskType.EVENT &&
				 task.getStartTime() == null && task.getEndTime() == null)
			TaskieStorage.updateEventToDeadline(index);
		else if (task.getType() == TaskieEnum.TaskType.DEADLINE &&
				 task.getStartTime() != null)
			TaskieStorage.updateDeadlineToEvent(index, task.getStartTime());
		else if (task.getEndTime() != null)
			TaskieStorage.updateEventDeadlineEnd(index, task.getEndTime());
		else if (task.getType() == TaskieEnum.TaskType.EVENT &&
				 task.getStartTime() != null && task.getEndTime() == null)
			TaskieStorage.updateEventStart(index, task.getStartTime());
		else if (task.getType() == TaskieEnum.TaskType.EVENT &&
				 task.getStartTime() != null && task.getEndTime() != null)
			TaskieStorage.updateEventStartEnd(index, task.getStartTime(), task.getEndTime());
		else 
			throw new UnrecognisedCommandException("Unrecognised update criterion.");
		String[] tasks = display(taskList);
		String[] feedback = new String[] { "Updated successfully" };
		return new String[][] { tasks, feedback };			
	}
	
//	private static String[][] undo() {
		
//	}

}

class UnrecognisedCommandException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public UnrecognisedCommandException(String message) {
		super(message);
	}

}
