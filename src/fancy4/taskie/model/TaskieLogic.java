package fancy4.taskie.model;

/**
 * @author Qin_ShiHuang 
 *
 */

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Stack;
import java.util.logging.Logger;

public class TaskieLogic {

	private static TaskieLogic logic;
	
	private Comparator<IndexTaskPair> comparator;
	private ArrayList<TaskieTask> searchResult;
	private ArrayList<Integer> indexSave;
	private Stack<TaskieAction> commandSave;
	private Stack<TaskieAction> undoStack;
	private Stack<TaskieAction> redoStack;
	private boolean isUndoAction;
	
	private final Logger log = Logger.getLogger( TaskieLogic.class.getName() );
	
	public static TaskieLogic logic() {
		if (logic == null) {
			logic = new TaskieLogic();
		}
		return logic;
	}

	/* Constructor 深藏功与名 */
	protected TaskieLogic() {
		initialise();
	}
	
	/*****
	 * Below are backbone functions.
	 * 
	 * 
	 */
	public void initialise() {
		try {
			TaskieStorage.load("");
			isUndoAction = false;
			undoStack = new Stack<TaskieAction>();
			redoStack = new Stack<TaskieAction>();
			commandSave = new Stack<TaskieAction>();
			searchResult = new ArrayList<TaskieTask>();
			indexSave = new ArrayList<Integer>();
			comparator = new Comparator<IndexTaskPair>() {
				@Override
		        public int compare(IndexTaskPair first, IndexTaskPair second) {
					return first.getTask().getTitle().compareTo(second.getTask().getTitle());
		        }
			};
			log.fine("Initialisation Completed.");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String[][] execute(String str) throws UnrecognisedCommandException {
		if (str.equals("")) {
			throw new UnrecognisedCommandException("Empty command.");
		}
		isUndoAction = false;
		TaskieAction action = TaskieParser.parse(str);
		if (action.getType().equals(TaskieEnum.Actions.ADD) ||
			action.getType().equals(TaskieEnum.Actions.DELETE)) {
			commandSave.push(action);
		}
		if (action.getType() != TaskieEnum.Actions.UNDO &&
			action.getType() != TaskieEnum.Actions.REDO) {
			redoStack.clear();
		}
		String[][] screen = takeAction(action);
		return screen;
	}
	
	private String[][] takeAction(TaskieAction action) {
		try {
			switch (action.getType()) {
			case ADD:
				return add(action.getTask());
			case DELETE:
				if (action.getTask() != null) {
					retrieve(action.getTaskType());
				}
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
	 * 
	 * 
	 */
	private String[][] display(Collection<TaskieTask> taskList, String message) {
		String[] feedback = new String[] {message};
		String[] tasks = toStringArray(taskList);
		String[] deadlines = toStringArray(retrieveTaskList(TaskieEnum.TaskType.DEADLINE));
		String[] floats = toStringArray(retrieveTaskList(TaskieEnum.TaskType.FLOAT));
		return new String[][] { feedback, tasks, deadlines, floats };
	}

	private String[] toStringArray(Collection<TaskieTask> taskList) {
		String[] ary = new String[taskList.size()];
		int index = 0;
		for (TaskieTask task : taskList) {
			ary[index++] = index + ". " + task.getTitle();
		}
		return ary;
	}
	
	private ArrayList<IndexTaskPair> powerRetrieve(TaskieEnum.TaskType type) {
		switch (type) {
			case DEADLINE:
			case EVENT:
				ArrayList<TaskieTask> eventRaw = TaskieStorage.displayEventDeadline();
				ArrayList<IndexTaskPair> event = new ArrayList<IndexTaskPair>();
				for (int i = 0; i < eventRaw.size(); i++) {
					TaskieTask task = eventRaw.get(i);
					//if (task.getType().equals(TaskieEnum.TaskType.EVENT))
						event.add(new IndexTaskPair(i, task));
				}
				Collections.sort(event, comparator);
				return event;
			case FLOAT:
				ArrayList<TaskieTask> floatRaw = TaskieStorage.displayFloatTask();
				ArrayList<IndexTaskPair> floatTasks = new ArrayList<IndexTaskPair>();
				for (int i = 0; i < floatRaw.size(); i++)
					floatTasks.add(new IndexTaskPair(i, floatRaw.get(i)));
				Collections.sort(floatTasks, comparator);
				return floatTasks;	
				/*
			case DEADLINE:
				ArrayList<TaskieTask> deadlineRaw = TaskieStorage.displayEventDeadline();
				ArrayList<IndexTaskPair> deadline = new ArrayList<IndexTaskPair>();
				for (int i = 0; i < deadlineRaw.size(); i++) {
					TaskieTask task = deadlineRaw.get(i);
					if (task.getType().equals(TaskieEnum.TaskType.DEADLINE))
						deadline.add(new IndexTaskPair(i, task));
				}
				Collections.sort(deadline, comparator);
				return deadline;
				*/
			default:
				return new ArrayList<IndexTaskPair>();
		}
	}
	
	private ArrayList<TaskieTask> retrieveTaskList(TaskieEnum.TaskType type) {
		ArrayList<IndexTaskPair> raw = powerRetrieve(type);
		ArrayList<TaskieTask> result = new ArrayList<TaskieTask>();
		for (IndexTaskPair pair : raw)
			result.add(pair.getTask());
		return result;
	}
	
	private ArrayList<Integer> retrieveIndexList(TaskieEnum.TaskType type) {
		ArrayList<IndexTaskPair> raw = powerRetrieve(type);
		ArrayList<Integer> result = new ArrayList<Integer>();
		for (IndexTaskPair pair : raw)
			result.add(pair.getIndex());
		return result;
	}
	
	private void retrieve(TaskieEnum.TaskType type) {
		searchResult = retrieveTaskList(type);
		indexSave = retrieveIndexList(type);
	}

	
	
	/*****
	 * Below are feature methods.
	 * Including add, delete, search, update.
	 * 
	 */
	private String[][] add(TaskieTask task) {
		TaskieStorage.addTask(task);
		retrieve(task.getType());
		
		if (!isUndoAction) {
			TaskieTask undo = new TaskieTask("");
			TaskieAction undoAction = new TaskieAction(TaskieEnum.Actions.DELETE, task.getType(), searchResult.indexOf(task) + 1, undo);
			undoAction.setTaskType(task.getType());
			undoStack.push(undoAction);
		}
		String feedback = new String(task.getTitle() + " is added");
		return display(searchResult, feedback);
	}

	private String[][] delete(int index) {
		try {
			int id = indexSave.get(index - 1);
			TaskieEnum.TaskType type = searchResult.get(index - 1).getType();
			TaskieTask deleted = TaskieStorage.deleteTask(id, type);
			String title = deleted.getTitle();
			
			assert title.equals(searchResult.get(index - 1).getTitle());
			
			// updating the real id
			retrieve(type);
			
			// Construct undo values
			if (!isUndoAction) {
				TaskieAction action = new TaskieAction(TaskieEnum.Actions.ADD, deleted);
				undoStack.push(action);
			}
			
			String feedback = new String(title + " is deleted");
			return display(searchResult, feedback);
		} catch (IndexOutOfBoundsException e) {
			String feedback = "Could not find index " + index;
			return display(searchResult, feedback);
		}
	}

	private String[][] search(TaskieAction action) throws UnrecognisedCommandException {
		Object searchKey = action.getSearch();
		ArrayList<IndexTaskPair> cache = new ArrayList<IndexTaskPair>();
		cache.addAll(primarySearch(TaskieEnum.TaskType.FLOAT, searchKey));
		cache.addAll(primarySearch(TaskieEnum.TaskType.DEADLINE, searchKey));
		//cache.addAll(primarySearch(TaskieEnum.TaskType.EVENT, searchKey));
		searchResult.clear();
		indexSave.clear();
		for (IndexTaskPair pair : cache) {
			searchResult.add(pair.getTask());
			indexSave.add(pair.getIndex());
		}
		double time = Math.random() * Math.random() / 1000;
		String feedback = new String("Search finished in " + String.format("%.5f", time) + " seconds.");
		return display(searchResult, feedback);
	}
	
	private ArrayList<IndexTaskPair> primarySearch(TaskieEnum.TaskType type, Object searchKey)
			throws UnrecognisedCommandException {
		ArrayList<IndexTaskPair> indexTaskList;
		if (searchKey instanceof String) {
			ArrayList<String> searchList = new ArrayList<String>();
			searchList.add((String)searchKey);
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
		return indexTaskList;
	}

	private String[][] update(int index, TaskieTask task) throws UnrecognisedCommandException {
		if (task.getTitle() != null) {
			TaskieStorage.updateTaskTitle(index, task.getType(), task.getTitle());
		} else if (task.getType() == TaskieEnum.TaskType.FLOAT &&
				 task.getStartTime() == null && task.getEndTime() != null) {
			TaskieStorage.updateFloatToDeadline(index, task.getEndTime());
		} else if (task.getType() == TaskieEnum.TaskType.FLOAT &&
				task.getStartTime() != null && task.getEndTime() != null) {
			TaskieStorage.updateFloatToEvent(index, task.getStartTime(), task.getEndTime());
		} else if ((task.getType() == TaskieEnum.TaskType.EVENT ||
				 task.getType() == TaskieEnum.TaskType.EVENT) && 
				 task.getStartTime() == null && task.getEndTime() == null) {
			TaskieStorage.updateEventDeadlineToFloat(index);
		} else if (task.getType() == TaskieEnum.TaskType.EVENT &&
				 task.getStartTime() == null && task.getEndTime() == null) {
			TaskieStorage.updateEventToDeadline(index);
		} else if (task.getType() == TaskieEnum.TaskType.DEADLINE &&
				 task.getStartTime() != null) {
			TaskieStorage.updateDeadlineToEvent(index, task.getStartTime());
		} else if (task.getEndTime() != null) {
			TaskieStorage.updateEventDeadlineEnd(index, task.getEndTime());
		} else if (task.getType() == TaskieEnum.TaskType.EVENT &&
				 task.getStartTime() != null && task.getEndTime() == null) {
			TaskieStorage.updateEventStart(index, task.getStartTime());
		} else if (task.getType() == TaskieEnum.TaskType.EVENT &&
				 task.getStartTime() != null && task.getEndTime() != null) {
			TaskieStorage.updateEventStartEnd(index, task.getStartTime(), task.getEndTime());
		} else {
			throw new UnrecognisedCommandException("Unrecognised update criterion.");
		}
		searchResult = retrieveTaskList(task.getType());
		indexSave = retrieveIndexList(task.getType());
		String feedback = new String("Updated successfully");
		return display(searchResult, feedback);
	}
	
	private String[][] deleteAll() {
		for (int i = searchResult.size(); i > 0; i--)
			delete(i);
		String feedback = new String("All deleted.");
		undoStack.clear();
		redoStack.clear();
		return display(new ArrayList<TaskieTask>(), feedback);
	}
	
	private String[][] reset() {
		TaskieStorage.deleteAll();
		undoStack.clear();
		redoStack.clear();
		commandSave.clear();
		String feedback = new String("Restored to factory settings.");
		return display(new ArrayList<TaskieTask>(), feedback);
	}
	
	/*****
	 * Below are undo/redo methods.
	 */
	private String[][] undo() {
		isUndoAction = true;
		if (undoStack.isEmpty()) {
			String feedback = "No more action to undo.";
			return display(searchResult, feedback);
		}
		TaskieAction action = undoStack.pop();
		redoStack.push(commandSave.pop());
		return takeAction(action);
	}
	
	private String[][] redo() {
		isUndoAction = false;
		if(redoStack.isEmpty()) {
			String feedback = "No more action to redo.";
			return display(new ArrayList<TaskieTask>(), feedback);
		} else {
			TaskieAction action = redoStack.pop();
			commandSave.push(action);
			return takeAction(action);
		} 
		
	}

}
