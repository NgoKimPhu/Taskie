package fancy4.taskie.model;

/**
 * @author Qin_ShiHuang 
 *
 */

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Stack;
import java.util.logging.Logger;

public class LogicDeprecated {

	private static TaskieLogic logic;
	
	private Comparator<IndexTaskPair> comparator;
	private ArrayList<TaskieTask> searchResult;
	private ArrayList<Integer> indexSave;
	private ArrayList<IndexTaskPair> mainTasks;
	private ArrayList<IndexTaskPair> allTasks;
	private Stack<TaskieAction> commandSave;
	private Stack<TaskieAction> undoStack;
	private Stack<TaskieAction> redoStack;
	private ArrayList<String> main;
	private ArrayList<ArrayList<String>> all;
	private boolean isUndoAction;
	private TaskieAction searchSave;

	private DateFormat df = DateFormat.getDateInstance();
	
	private final Logger log = Logger.getLogger( TaskieLogic.class.getName() );
	
	public static TaskieLogic logic() {
		if (logic == null) {
			logic = new TaskieLogic();
		}
		return logic;
	}

	/* Constructor */
	protected LogicDeprecated() {
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
			main = new ArrayList<String>();
			indexSave = new ArrayList<Integer>();
			undoStack = new Stack<TaskieAction>();
			redoStack = new Stack<TaskieAction>();
			commandSave = new Stack<TaskieAction>();
			all = new ArrayList<ArrayList<String>>();
			allTasks = new ArrayList<IndexTaskPair>();
			searchResult = new ArrayList<TaskieTask>();
			mainTasks = new ArrayList<IndexTaskPair>(); 
			comparator = new Comparator<IndexTaskPair>() {
				@Override
		        public int compare(IndexTaskPair first, IndexTaskPair second) {
					if (first.getTask().getEndTime() == null || second.getTask().getEndTime() == null)
						return first.getTask().getTitle().compareTo(second.getTask().getTitle());
					return first.getTask().getEndTime().compareTo(second.getTask().getEndTime());
		        }
			};
			log.fine("Initialisation Completed.");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public LogicOutput execute(String str) throws UnrecognisedCommandException {
		if (str.equals("")) {
			throw new UnrecognisedCommandException("Empty command.");
		}
		isUndoAction = false;
		TaskieParser parser = TaskieParser.getInstance();
		TaskieAction action = parser.parse(str);
		if (action.getType().equals(TaskieEnum.Actions.ADD) ||
			action.getType().equals(TaskieEnum.Actions.DELETE)) {
			commandSave.push(action);
		}
		if (action.getType() != TaskieEnum.Actions.UNDO &&
			action.getType() != TaskieEnum.Actions.REDO) {
			redoStack.clear();
		}
		String[][] screen = takeAction(action);	
		return output(screen);
	}
/*
	private LogicOutput output(String[][] screen) throws UnrecognisedCommandException {
		all.clear();
		main.clear();
		allTasks.clear();
		mainTasks.clear();
		String feedback = screen[0][0];
		for (String task : screen[1]) {
			main.add(task);
		}
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, -1);
		
		/*
=======
		cal.add(Calendar.DATE, -1);
>>>>>>> ef3f3bf0b5af09cf337208a9b19a9ba1418d7b0d
		int index = 0, save = 0;
		for (int i = 0; i < 3; i++) {
			cal.add(Calendar.DATE, 1);
			Date date = cal.getTime();
			ArrayList<IndexTaskPair> todayTasks = new ArrayList<IndexTaskPair>();
			todayTasks.addAll(primarySearch(TaskieEnum.TaskType.EVENT, date));
			todayTasks.addAll(primarySearch(TaskieEnum.TaskType.DEADLINE, date));
			Collections.sort(todayTasks, comparator);
			//if (todayTasks.size() != 0) {
				all.add(df.format(date));
			//}
			for (; index < todayTasks.size() + save; index++) {
				TaskieTask task = todayTasks.get(index).getTask();
				all.add(index+1 + "  " + task.getStartTime() + "  " + task.getEndTime() + "  " + task.getTitle());
			}
			save = index;
			allTasks.addAll(todayTasks);
		}
		//---
		all.add("Everything else:");
		ArrayList<IndexTaskPair> todayTasks = new ArrayList<IndexTaskPair>();
		todayTasks.addAll(primarySearch(TaskieEnum.TaskType.FLOAT, new String()));
		for (; index < todayTasks.size() + save; index++) {
			TaskieTask task = todayTasks.get(index).getTask();
			all.add(index+1 + ".  " + task.getTitle());
		}
		allTasks.addAll(todayTasks);
		//---
		for (int i = 0; i < searchResult.size(); i++) {
			mainTasks.add(new IndexTaskPair(indexSave.get(i), searchResult.get(i)));
		}
		//Collections.sort(allTasks, comparator);
		 */
		return new LogicOutput(feedback, main, all);
	}

	private String[][] takeAction(TaskieAction action) {
		try {
			switch (action.getType()) {
			case ADD:
				return add(action.getTask());
			case DELETE:
				/*if (action.getTask() != null) {
					retrieve(action.getTaskType());
				}
				return delete(action.getTaskType(), action.getIndex());*/
				return delete(action.getScreen(), action.getIndex() - 1);
			case DELETEALL:
				return deleteAll();
			case SEARCH:
				searchSave = action;
				return search(action);
			case UPDATE:
				return update(action.getIndex() - 1, action.getTask());
			case MARKDONE:
				return markdone(action.getScreen(), action.getIndex() - 1);
			case UNDO:
				return undo();
			case REDO:
				return redo();
			case RESET:
				return reset();
			case EXIT:
				exit();
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
		SimpleDateFormat sdf = new SimpleDateFormat("E dd-MM HH:mm");
		SimpleDateFormat sdf2 = new SimpleDateFormat("HH:mm");
		SimpleDateFormat sdf3 = new SimpleDateFormat("dd-MM-YYYY");
<<<<<<< HEAD
		Calendar c1 = Calendar.getInstance();
		Calendar c2 = Calendar.getInstance();
=======
		boolean isSameDay = false;
>>>>>>> ef3f3bf0b5af09cf337208a9b19a9ba1418d7b0d
		int index = 0;
		for (TaskieTask task : taskList) {
			Date st = task.getStartTime();
			Date et = task.getEndTime();
			boolean isSameDay = false;
			String sst, set;
			
			if (st != null && et != null && sdf3.format(st).equals(sdf3.format(et))) {
				isSameDay = true;
			}
			
			if (st != null) {
				sst = sdf.format(st);
			} else {
				sst = " -- ";
			}
			if (et != null) {
				set = sdf.format(et);
			} else {
				set = " -- ";
			}
			if (isSameDay) {
				ary[index++] = index + ".  " + sst + " ~ " + sdf2.format(et) + "  " + task.getTitle();
			} else {
				ary[index++] = index + ".  " + sst + "  " + set + "  " + task.getTitle();
			}
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

	private void exit() {
		System.exit(0);
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
			// TaskieTask undo = new TaskieTask("");
			// TODO: replace searchResult with allTasksInRightWindow_List
			TaskieAction undoAction = new TaskieAction(TaskieEnum.Actions.DELETE, "left", searchResult.indexOf(task) + 1);
			undoAction.setTaskType(task.getType());
			undoStack.push(undoAction);
		}
		
		String feedback = new String("\"" + task.getTitle() + "\"" + " is added");
		return display(searchResult, feedback);
	}
/*
	private String[][] delete(TaskieEnum.TaskType type, int index) {
		try {
			if (type != null) {
				retrieve(type);
			}
			int id = indexSave.get(index - 1);
			type = searchResult.get(index - 1).getType();
			TaskieTask deleted = TaskieStorage.deleteTask(id, type);
			String title = deleted.getTitle();
			
			assert title.equals(searchResult.get(index - 1).getTitle());
			
			// updating the real id
			retrieve(type);
			
			// Construct undo action
			if (!isUndoAction) {
				TaskieAction action = new TaskieAction(TaskieEnum.Actions.ADD, deleted);
				undoStack.push(action);
			}
			
			String feedback = new String("\"" + title + "\"" + " is deleted");
			return display(searchResult, feedback);
		} catch (IndexOutOfBoundsException e) {
			String feedback = "Could not find index " + index;
			return display(searchResult, feedback);
		}
	}
*/
	private String[][] delete(String screen, int index) throws UnrecognisedCommandException {
		try {
		TaskieEnum.TaskType type;
		int realIndex;
		if (screen.equalsIgnoreCase("left")) {
			type = mainTasks.get(index).getTask().getType();
			realIndex = mainTasks.get(index).getIndex();
			mainTasks.remove(index);
			searchResult.remove(index);
			indexSave.remove(index);
		} else if (screen.equalsIgnoreCase("right")) {
			type = allTasks.get(index).getTask().getType();
			realIndex = allTasks.get(index).getIndex();
			allTasks.remove(index);
		} else {
			throw new UnrecognisedCommandException("Screen preference not indicated.");
		}
		
		TaskieTask deleted = TaskieStorage.deleteTask(realIndex, type);
		String title = deleted.getTitle();
		//retrieve(type);
		
		String feedback = new String("\"" + title + "\"" + " is deleted");
		
		// Construct undo action
		if (!isUndoAction) {
			TaskieAction action = new TaskieAction(TaskieEnum.Actions.ADD, deleted);
			undoStack.push(action);
		}
					
		return display(searchResult, feedback);
		} catch (IndexOutOfBoundsException e) {
			return display(searchResult, "Invalid index number");
		}
	}
	
	private String[][] markdone(String screen, int index) throws UnrecognisedCommandException {
		try {
			TaskieEnum.TaskType type;
			String title;
			int realIndex;
			if (screen.equalsIgnoreCase("left")) {
				type = mainTasks.get(index).getTask().getType();
				realIndex = mainTasks.get(index).getIndex();
				title = mainTasks.get(index).getTask().getTitle();
			} else if (screen.equalsIgnoreCase("right")) {
				type = allTasks.get(index).getTask().getType();
				realIndex = allTasks.get(index).getIndex();
				title = allTasks.get(index).getTask().getTitle();
			} else {
				throw new UnrecognisedCommandException("Screen preference not indicated.");
			}
			
			String feedback = new String("\"" + title + "\"" + " is marked done");
			TaskieStorage.markDone(realIndex, type);
			
			// Construct undo action
			if (!isUndoAction) {
				TaskieAction action = new TaskieAction(TaskieEnum.Actions.MARKDONE, screen, index + 1);
				undoStack.push(action);
			}

			return display(searchResult, feedback);
			
		} catch (IndexOutOfBoundsException e) {
			return display(searchResult, "Invalid index number");
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
		String feedback = new String("Search finished in " + String.format("%.5f", time) + " seconds");
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
			throw new UnrecognisedCommandException("Unrecognised search key");
		}
		return indexTaskList;
	}

	private String[][] update(int index, TaskieTask task) throws UnrecognisedCommandException {
		TaskieTask undoTask = new TaskieTask((String)null);
		if (task.getTitle() != null) {
			TaskieStorage.updateTaskTitle(index, task.getType(), task.getTitle());
			// undoTask: new task with old title
			undoTask.setTitle(searchResult.get(index).getTitle());
		} else if (task.getType() == TaskieEnum.TaskType.FLOAT &&
				   task.getStartTime() == null && task.getEndTime() != null) {
			TaskieStorage.updateFloatToDeadline(index, task.getEndTime());
			// undoTask: Deadline/Event task with null starttime and null endtime
			undoTask.setToDeadline(null); // A little bit messy :-(
		} else if (task.getType() == TaskieEnum.TaskType.FLOAT &&
				   task.getStartTime() != null && task.getEndTime() != null) {
			TaskieStorage.updateFloatToEvent(index, task.getStartTime(), task.getEndTime());
			// undoTask: same as above
			undoTask.setToEvent(null, null);
		} else if ((task.getType() == TaskieEnum.TaskType.EVENT ||
				 	task.getType() == TaskieEnum.TaskType.DEADLINE) && 
				 	task.getStartTime() == null && task.getEndTime() == null) {
			TaskieStorage.updateEventDeadlineToFloat(index);
			// undoTask: float to event or deadline
			Date startTime = searchResult.get(index).getStartTime();
			Date endTime = searchResult.get(index).getEndTime();
			undoTask.setStartTime(startTime);
			undoTask.setEndTime(endTime);
			assert undoTask.getType().equals(TaskieEnum.TaskType.FLOAT);
		} else if (task.getType() == TaskieEnum.TaskType.EVENT &&
				   task.getStartTime() == null && task.getEndTime() == null) {
			TaskieStorage.updateEventToDeadline(index);
			// undoTask: deadline to event
			Date startTime = searchResult.get(index).getStartTime();
			Date endTime = searchResult.get(index).getEndTime();
			undoTask.setToDeadline(endTime);
			undoTask.setStartTime(startTime);
			assert undoTask.getType().equals(TaskieEnum.TaskType.DEADLINE);
			assert undoTask.getStartTime() != null;
		} else if (task.getType() == TaskieEnum.TaskType.DEADLINE &&
				   task.getStartTime() != null) {
			TaskieStorage.updateDeadlineToEvent(index, task.getStartTime());
			// undoTask: event to deadline
			Date startTime = searchResult.get(index).getStartTime();
			Date endTime = searchResult.get(index).getEndTime();
			//undoTask.setToEvent();
		} else if (task.getEndTime() != null) {
			TaskieStorage.updateEventDeadlineEnd(index, task.getEndTime());
		} else if (task.getType() == TaskieEnum.TaskType.EVENT &&
				   task.getStartTime() != null && task.getEndTime() == null) {
			TaskieStorage.updateEventStart(index, task.getStartTime());
		} else if (task.getType() == TaskieEnum.TaskType.EVENT &&
				   task.getStartTime() != null && task.getEndTime() != null) {
			TaskieStorage.updateEventStartEnd(index, task.getStartTime(), task.getEndTime());
		} else {
			throw new UnrecognisedCommandException("Unrecognised update criterion");
		}
		// return update(action.getIndex() - 1, action.getTask());
		if (!isUndoAction) {
			TaskieAction action = new TaskieAction(TaskieEnum.Actions.UPDATE, index + 1, undoTask);
			undoStack.push(action);
		}
		
		searchResult = retrieveTaskList(task.getType());
		indexSave = retrieveIndexList(task.getType());
		String feedback = new String("Updated successfully");
		return display(searchResult, feedback);
	}
	
	private String[][] deleteAll() throws UnrecognisedCommandException {
		for (int i = searchResult.size(); i > 0; i--)
			delete("left", i);
		String feedback = new String("All deleted");
		undoStack.clear();
		redoStack.clear();
		return display(new ArrayList<TaskieTask>(), feedback);
	}
	
	private String[][] reset() {
		undoStack.clear();
		redoStack.clear();
		commandSave.clear();
		searchResult.clear();
		indexSave.clear();
		mainTasks.clear();
		allTasks.clear();
		TaskieStorage.deleteAll();
		String feedback = new String("Restored to factory settings");
		return display(new ArrayList<TaskieTask>(), feedback);
	}
	
	
	/*****
	 * Below are undo/redo methods.
	 */
	private String[][] undo() {
		isUndoAction = true;
		if (undoStack.isEmpty()) {
			String feedback = "No more action to undo";
			return display(searchResult, feedback);
		}
		TaskieAction action = undoStack.pop();
		redoStack.push(commandSave.pop());
		return takeAction(action);
	}
	
	private String[][] redo() {
		isUndoAction = false;
		if(redoStack.isEmpty()) {
			String feedback = "No more action to redo";
			return display(new ArrayList<TaskieTask>(), feedback);
		} else {
			TaskieAction action = redoStack.pop();
			commandSave.push(action);
			return takeAction(action);
		} 
		
	}

}
