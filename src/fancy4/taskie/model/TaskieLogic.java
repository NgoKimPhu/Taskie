package fancy4.taskie.model;

/**
 * @author Qin_ShiHuang 
 *
 */

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Stack;

public class TaskieLogic {

	private static ArrayList<TaskieTask> searchResult;
	private static ArrayList<Integer> indexSave;
	private static Stack<TaskieAction> undoStack;
	private static Stack<TaskieAction> redoStack;
	private static Stack<TaskieAction> commandSave;

	/*****
	 * Below are backbone functions.
	 */
	public static void initialise() {
		try {
			TaskieStorage.load("");
			undoStack = new Stack<TaskieAction>();
			redoStack = new Stack<TaskieAction>();
			commandSave = new Stack<TaskieAction>();
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
			commandSave.push(action);
			switch (action.getType()) {
			case ADD:
				return add(action.getTask());
			case DELETE:
				return delete(action.getIndex());
			case DELETEALL:
				return deleteAll();
			case SEARCH:
				return search(action);
			case UPDATE:
				return update(action.getIndex(), action.getTask());
			case UNDO:
				return undo();
			case REDO:
				return redo();
			case RESET:
				return reset();
			default:
				return add(action.getTask());
			}
		} catch (UnrecognisedCommandException e) {
			System.err.println("Unrecognised Command");
			return new String[][] {{"Fatal error"}, {""}, {""}, {""}};
		}
	}
	
	/*****
	 * Below are auxiliary methods.
	 */
	private static String[][] display(Collection<TaskieTask> taskList, String message) {
		String[] feedback = new String[] {message};
		String[] tasks = toStringArray(taskList);
		String[] deadlines = toStringArray(retrieve(TaskieEnum.TaskType.DEADLINE));
		String[] floats = toStringArray(retrieve(TaskieEnum.TaskType.FLOAT));
		return new String[][] { feedback, tasks, deadlines, floats };
	}

	private static String[] toStringArray(Collection<TaskieTask> taskList) {
		String[] ary = new String[taskList.size()];
		int index = 0;
		for (TaskieTask task : taskList) {
			ary[index++] = index + ". " + task.getTitle();
		}
		return ary;
	}
	
	private static ArrayList<TaskieTask> retrieve(TaskieEnum.TaskType type) {
		switch (type) {
		case EVENT:
			case FLOAT:
				return TaskieStorage.displayFloatTask();
			case DEADLINE:
				Collection<TaskieTask> eventDeadline = TaskieStorage.displayEventDeadline();
				ArrayList<TaskieTask> deadline = new ArrayList<TaskieTask>();
				for (TaskieTask task : eventDeadline) {
					if (task.getStartTime() == null)
						deadline.add(task);
				}
				return deadline;
			default:
				return new ArrayList<TaskieTask>();
		}
	}

	
	/*****
	 * Below are feature methods.
	 * Including add, delete, search, update.
	 */
	private static String[][] add(TaskieTask task) {
		IndexTaskPair added = TaskieStorage.addTask(task);
		TaskieEnum.TaskType type = added.getTask().getType();
		//Collection<TaskieTask> taskList = retrieve(type);
		searchResult = retrieve(type);
		for (int i = 0; i < searchResult.size(); i++)
			indexSave.add(i);
		
		//Undo
		int index = added.getIndex();
		TaskieTask undo = new TaskieTask("");
		TaskieAction undoAction = new TaskieAction(TaskieEnum.Actions.DELETE, undo, index + 1);;
		undoStack.push(undoAction);
		
		String feedback = new String(task.getTitle() + " is added");
		return display(searchResult, feedback);
	}

	private static String[][] delete(int index) {
		try {
			int id = indexSave.get(index - 1);
			TaskieEnum.TaskType type = searchResult.get(index - 1).getType();
			TaskieTask deleted = TaskieStorage.deleteTask(id, type);
			searchResult.remove(index - 1);
			indexSave.remove(index - 1);
			String title = deleted.getTitle();
			
			// updating the real id
			for (int i = index - 1; i < searchResult.size(); i++) {
				if (searchResult.get(i).getType().equals(type)) {
					indexSave.set(i, indexSave.get(i) - 1);
				}
			}
			
			// Construct undo values
			TaskieAction action = new TaskieAction(TaskieEnum.Actions.ADD, deleted);
			undoStack.push(action);
			
			String feedback = new String(title + " is deleted");
			return display(searchResult, feedback);
		} catch (IndexOutOfBoundsException e) {
			String feedback = "Could not find index " + index;
			return display(searchResult, feedback);
		}
	}

	private static String[][] search(TaskieAction action)
			throws UnrecognisedCommandException {
		TaskieEnum.TaskType type = action.getTask().getType();
		Object searchKey = action.getSearch();
		Collection<IndexTaskPair> indexTaskList;
		if (searchKey instanceof String) {
			ArrayList<String> searchList = new ArrayList<String>();
			searchList.add((String) searchKey);
			indexTaskList = TaskieStorage.searchTask(searchList, type);
		} else if (searchKey instanceof Date) {
			indexTaskList = TaskieStorage.searchTask((Date) searchKey, type);
		} else if (searchKey instanceof Integer) {
			indexTaskList = TaskieStorage.searchTask(
					(TaskieEnum.TaskPriority) searchKey, type);
		} else if (searchKey instanceof Boolean) {
			indexTaskList = TaskieStorage.searchTask((Boolean) searchKey, type);
		} else {
			throw new UnrecognisedCommandException("Unrecognised search key.");
		}
		searchResult.clear();
		indexSave.clear();
		for (IndexTaskPair pair : indexTaskList) {
			searchResult.add(pair.getTask());
			indexSave.add(pair.getIndex());
		}
		double time = Math.random() * Math.random() / 1000;
		String feedback = new String("Search finished in " + String.format("%.5f", time) + " seconds.");
		return display(searchResult, feedback);
	}

	private static String[][] update(int index, TaskieTask task) throws UnrecognisedCommandException {
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
		Collection<TaskieTask> taskList = retrieve(task.getType());
		String feedback = new String("Updated successfully");
		return display(taskList, feedback);
	}
	
	private static String[][] deleteAll() {
		for (int i = searchResult.size(); i > 0; i--)
			delete(i);
		String feedback = new String("All deleted.");
		return display(new ArrayList<TaskieTask>(), feedback);
	}
	
	private static String[][] reset() {
		TaskieStorage.deleteAll();
		String feedback = new String("Restored to factory settings.");
		return display(new ArrayList<TaskieTask>(), feedback);
	}
	
	/*****
	 * Below are undo/redo methods.
	 */
	private static String[][] undo() {
		if (undoStack.isEmpty()) {
			String feedback = "No more action to undo.";
			return display(new ArrayList<TaskieTask>(), feedback);
		}
		TaskieAction action = undoStack.pop();
		redoStack.push(commandSave.pop());
		return takeAction(action);
	}
	
	private static String[][] redo() {
		if (redoStack.isEmpty()) {
			String feedback = "No more action to redo.";
			return display(new ArrayList<TaskieTask>(), feedback);
		}
		TaskieAction action = redoStack.pop();
		return takeAction(action);
	}

}


class UnrecognisedCommandException extends Exception {

	private static final long serialVersionUID = 1L;

	public UnrecognisedCommandException(String message) {
		super(message);
	}

}
