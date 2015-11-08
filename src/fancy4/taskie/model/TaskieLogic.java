package fancy4.taskie.model;

/**
 * @@author A0107360R 
 *
 */

import java.text.SimpleDateFormat;
import java.util.logging.Logger;
import java.util.Collections;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Stack;
import java.util.Date;

public class TaskieLogic {

	private static TaskieLogic logic;

	private boolean isFreeSlots, isMarkdone, isSearch, isView, isUndoAction;
	private Stack<TaskieAction> commandSave, undoStack, redoStack;
	private ArrayList<IndexTaskPair> mainTasks, allTasks;
	private ArrayList<CalendarPair> freeSlots;
	private Calendar retrieveSave;
	private String feedback;

	private final Logger log = Logger.getLogger(TaskieLogic.class.getName() );
	
	public static TaskieLogic logic() {
		if (logic == null) {
			logic = new TaskieLogic();
		}
		return logic;
	}

	/* Constructor */
	protected TaskieLogic() {
		undoStack = new Stack<TaskieAction>();
		redoStack = new Stack<TaskieAction>();
		commandSave = new Stack<TaskieAction>();
		allTasks = new ArrayList<IndexTaskPair>();
		freeSlots = new ArrayList<CalendarPair>();
		mainTasks = new ArrayList<IndexTaskPair>();
	}
	
	

	/*****
	 * Below are "backbone" functions:
	 * initialise, execute, getMain, getAll, takeAction
	 * 
	 */
	public void initialise() {
		try {
			TaskieStorage.load("");
			retrieve(retrieveSave);
			log.fine("Initialisation Completed.");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public LogicOutput execute(String str) throws UnrecognisedCommandException {
			if (str.equals("")) {
				throw new UnrecognisedCommandException("Empty command.");
			}
			isView = false;
			isSearch = false;
			isMarkdone = false;
			isUndoAction = false;
			TaskieParser parser = TaskieParser.getInstance();
			TaskieAction action = parser.parse(str);
			// Save the undo- and redo-able actions
			if (action.getType().equals(TaskieEnum.Actions.ADD) ||
				action.getType().equals(TaskieEnum.Actions.DELETE) ||
				action.getType().equals(TaskieEnum.Actions.UPDATE) ||
				action.getType().equals(TaskieEnum.Actions.MARKDONE)) {
				commandSave.push(action);
			}
			if (action.getType() != TaskieEnum.Actions.UNDO &&
				action.getType() != TaskieEnum.Actions.REDO) {
				redoStack.clear();
			}
			takeAction(action);
			// Assemble the output: feedback, left screen output, right screen output
			return new LogicOutput(feedback, getMain(), getAll());
	}

	/*	The output to left window	*/
	private ArrayList<String> getMain() {
		ArrayList<String> main = new ArrayList<String>();
		String headline, number, date, floating;
		if (isFreeSlots) {
			main = format(freeSlots);
			int size = main.size();
			if (size == 0) {
				headline = "There is no free slots. Take some break.";
			} else {
				number = size == 1 ? "is one free slot" : "are " + size + " free slots";
				headline = new String("You have " + number + " in the next seven days.");
			}
		} else {
			int size = mainTasks.size();
			if (mainTasks.isEmpty()) {
				if (isMarkdone) {
					headline = new String("There is no completed task.");
				} else if (isSearch) {
					headline = new String("Your search did not match any tasks.");
				} else {
					headline = new String("There is no task. Feed me some, or take a nap.");
				}
			} else {
				date = new String();
				floating = new String();
				if (!mainTasks.get(0).getTask().getType().equals(TaskieEnum.TaskType.FLOAT)) {
					Date day = mainTasks.get(0).getTask().getEndTime().getTime();
					SimpleDateFormat sdf = new SimpleDateFormat("dd, MMM, yyyy");
					date = " on " + sdf.format(day);
				} else {
					floating = " floating";
				}
				if (isMarkdone) {
					number = size == 1 ? new String("is one completed task") : new String("are " + size + " completed tasks");
					headline = new String("There " + number + ".");
				} else if (isSearch) {
					number = size == 1 ? new String("is one task") : new String("are " + size + " tasks");
					headline = new String("There " + number + " found.");
				} else if (isView) {
					number = size == 1 ? new String("one task") : new String(size + " tasks");
					headline = new String("You have " + number + " in total.");
				} else {
					number = size == 1 ? new String(" one" + floating + " task") : new String(" " + size + floating + " tasks");
					headline = new String("You have" + number + date + ".");
				}
			}
			main = format(0, mainTasks);
		}
		main.add(0, headline);
		return main;
	}
	
	
	/*	The output to right window
	 *    the "all" format:
	 *		all is an ArrayList<ArrayList<String>>. all.size() == 4
	 *		The four elements are:
	 *		a. overdue tasks, ovd;
	 *		b. today's tasks, tod;
	 *		c. tomorrow's tasks, tmr; and
	 *		d. everything else, els, which includes floating tasks and other tasks with deadlines more than two days away.
	 * 
	 * */
	private ArrayList<ArrayList<String>> getAll() {
		ArrayList<ArrayList<String>> all = new ArrayList<ArrayList<String>>();
		ArrayList<IndexTaskPair> ovd = new ArrayList<IndexTaskPair>(),
								 tod = new ArrayList<IndexTaskPair>(),
								 tmr = new ArrayList<IndexTaskPair>(),
								 els = new ArrayList<IndexTaskPair>();
		
		// Get the current time
		Calendar now = Calendar.getInstance();
		
		// Get the time of the end of today
		Calendar endOfToday = new Calendar.Builder().setDate(now.get(Calendar.YEAR), 
				now.get(Calendar.MONTH), now.get(Calendar.DATE) + 1).build();
		
		// Get the time of the end of tomorrow
		Calendar endOfTmr = (Calendar) endOfToday.clone();
		endOfTmr.add(Calendar.DATE, 1);
		
		// Add the pairs in allTasks to their respective list
		for (IndexTaskPair pair : allTasks) {
			Calendar time = pair.getTask().getEndTime();
			if (time == null) {
				els.add(pair);
			} else {
				if (time.before(now)) {
					ovd.add(pair);
				} else if (time.before(endOfToday) && time.after(now)) {
					tod.add(pair);
				} else if (time.before(endOfTmr) && time.after(endOfToday)) {
					tmr.add(pair);
				} else if (pair.getTask().getEndTime().after(endOfTmr)) {
					els.add(pair);
				} else {
					assert 1 == 0;
				}
			}
		}
		
		Collections.sort(ovd);
		Collections.sort(tod);
		Collections.sort(tmr);
		Collections.sort(els);
		
		all.add(format(0, ovd));
		all.add(format(ovd.size(), tod));
		all.add(format(ovd.size() + tod.size(), tmr));
		all.add(format(ovd.size() + tod.size() + tmr.size(), els));
		
		return all;
	}
	
	private void takeAction(TaskieAction action) {
		try {
			switch (action.getType()) {
			case ADD:
				add(action.getTask());
				return;
			case DELETE:
				delete(action.getScreen(), action.getIndex() - 1);
				return;
			case DELETEALL:
				deleteAll();
				return;
			case SEARCH:
				search(action);
				return;
			case UPDATE:
				update(action.getScreen(), action.getIndex() - 1, action.getTask());
				return;
			case MARKDONE:
				markdone(action.getScreen(), action.getIndex() - 1);
				return;
			case UNDO:
				undo();
				return;
			case REDO:
				redo();
				return;
			case FREESLOT:
				getFreeSlots();
				return;
			case SETPATH:
				setPath((String)action.getSearch());
			case RESET:
				reset();
				return;
			case EXIT:
				exit();
				return;
			default:
				add(action.getTask());
				return;
			}
		} catch (Exception e) {
			feedback = e.getMessage();
		}
		if (!action.getType().equals(TaskieEnum.Actions.FREESLOT)) {
			isFreeSlots = false;
		}
	}

	
	
	/*****
	 * Below are auxiliary methods.
	 * 
	 * 
	 */
	private ArrayList<String> format(int index, ArrayList<IndexTaskPair> list) {
		ArrayList<String> formatted = new ArrayList<String>();
		
		SimpleDateFormat sdf1 = new SimpleDateFormat("E dd-MM HH:mm");
		SimpleDateFormat sdf2 = new SimpleDateFormat("HH:mm");
		SimpleDateFormat sdf3 = new SimpleDateFormat("dd-MM-YYYY");
		for (IndexTaskPair pair : list) {
			index++;
			TaskieTask task = pair.getTask();
			Calendar st = task.getStartTime();
			Calendar et = task.getEndTime();
			boolean isSameDay = false;
			String sst, set, taskDetail, status;
			
			if (st != null && et != null && sdf3.format(st.getTime()).equals(sdf3.format(et.getTime()))) {
				isSameDay = true;
			}
			
			if (st != null) {
				sst = sdf1.format(st.getTime());
			} else {
				sst = "";
			}
			if (et != null) {
				set = sdf1.format(et.getTime());
			} else {
				set = "";
			}
			
			status = pair.getTask().getStatus() ? "Completed" : "";
			
			if (isSameDay) {
				taskDetail = new String(index + ".  " + task.getTitle() + "-time " + sst + " ~ " + sdf2.format(et.getTime()) + "  " + (status = pair.getTask().getStatus() ? "Completed" : ""));
			} else {
				taskDetail = new String(index + ".  " + task.getTitle() + "-time " + sst + "  " + set);
			}
			formatted.add(new String(taskDetail + "    " + status));
		}
		return formatted;
	}
	
	private ArrayList<String> format(ArrayList<CalendarPair> list) {
		ArrayList<String> slots = new ArrayList<String>();
		for (int i = 1; i <= list.size(); i++) {
			slots.add(i + ". " + list.get(i - 1).toString());
		}
		return slots;
	}	
	
	private void retrieveAllTasks() {
		allTasks.clear();
		ArrayList<TaskieTask> complete = TaskieStorage.displayAllTasks();
		for (int i = 0; i < complete.size(); i++) {
			if (!complete.get(i).getStatus()) {
				allTasks.add(new IndexTaskPair(i, complete.get(i)));
			}
		}
		Collections.sort(allTasks);
	}

	private void retrieve(Calendar day) throws Exception {
		mainTasks.clear();
		retrieveAllTasks();
		
		if (day == null) {
			retrieveSave = null;
			for (IndexTaskPair pair : allTasks) {
				if (pair.getTask().getType().equals(TaskieEnum.TaskType.FLOAT)) {
					mainTasks.add(pair);
				}
			}
		} else {
			retrieveSave = (Calendar) day.clone();
			Calendar start = new Calendar.Builder().setDate(day.get(Calendar.YEAR), 
					day.get(Calendar.MONTH), day.get(Calendar.DATE)).build();
			Calendar end = (Calendar) start.clone();
			end.add(Calendar.DATE, 1);
			mainTasks = TaskieStorage.searchTask(start, end);
		}
		
		Collections.sort(mainTasks);
	}
	
	private void retrieve(boolean status) throws Exception {
		retrieveAllTasks();
		mainTasks = primarySearch(true);
		Collections.sort(mainTasks);
	}
	
	private int getRightIndex(IndexTaskPair pair) {
		return getRightIndex(pair.getIndex());
	}
	
	private int getRightIndex(int realIndex) {
		for (int i = 0; i < allTasks.size(); i++) {
			if (realIndex == allTasks.get(i).getIndex()) {
				return i + 1;
			}
		}
		return -1;
	}
	
	private int getLeftIndex(int realIndex) {
		for (int i = 0; i < mainTasks.size(); i++) {
			if (realIndex == mainTasks.get(i).getIndex()) {
				return i + 1;
			}
		}
		return -1;
	}

	private void exit() {
		System.exit(0);
	}
	
	
	
	/*****
	 * Below are feature methods.
	 * Including add, delete, search, update.
	 * @throws Exception 
	 * 
	 */
	private void add(TaskieTask task) throws Exception {
		IndexTaskPair added = TaskieStorage.addTask(task);
		assert task.getType().equals(added.getTask().getType());
		
		retrieve(added.getTask().getEndTime());
		
		if (!isUndoAction) {
			TaskieAction undoAction = new TaskieAction(TaskieEnum.Actions.DELETE, "right", getRightIndex(added));
			undoAction.setTaskType(task.getType());
			undoStack.push(undoAction);
		}
		
		feedback = new String("\"" + task.getTitle() + "\"" + " is added");
	}
	
	private void delete(String screen, int index) throws UnrecognisedCommandException {
		try {
			int realIndex;
			if (screen.equalsIgnoreCase("left")) {
				if (isFreeSlots) {
					feedback = "You cannot delete a slot.";
					return;
				}
				realIndex = mainTasks.get(index).getIndex();
			} else if (screen.equalsIgnoreCase("right")) {
				if (allTasks.isEmpty()) {
					System.out.println("Empty allTasks");
				}
				realIndex = allTasks.get(index).getIndex();
			} else {
				throw new UnrecognisedCommandException("Window preference not indicated.");
			}

			TaskieTask deleted = TaskieStorage.deleteTask(realIndex);
			String title = deleted.getTitle();
			
			retrieve(retrieveSave);
			
			feedback = new String("\"" + title + "\"" + " is deleted");
			
			// Construct undo action
			if (!isUndoAction) {
				TaskieAction action = new TaskieAction(TaskieEnum.Actions.ADD, deleted);
				undoStack.push(action);
			}
			
		} catch (IndexOutOfBoundsException e) {
			feedback = new String("Invalid index number");
		} catch (Exception e) {
			feedback = e.getMessage();
		}
	}
	
	private void markdone(String screen, int index) throws Exception {
		isMarkdone = true;
		
		try {
			String title;
			int realIndex;
			if (screen.equalsIgnoreCase("left")) {
				realIndex = mainTasks.get(index).getIndex();
				title = mainTasks.get(index).getTask().getTitle();
			} else if (screen.equalsIgnoreCase("right")) {
				realIndex = allTasks.get(index).getIndex();
				title = allTasks.get(index).getTask().getTitle();
			} else {
				throw new UnrecognisedCommandException("Screen preference not indicated.");
			}
			
			feedback = new String("\"" + title + "\"" + " is marked done");
			
			TaskieStorage.changeStatus(realIndex);
			retrieve(true);
			
			// Construct undo action
			if (!isUndoAction) {
				TaskieAction action = new TaskieAction(TaskieEnum.Actions.MARKDONE, "left", getLeftIndex(realIndex));
				undoStack.push(action);
			}
		} catch (IndexOutOfBoundsException e) {
			feedback = new String("Invalid index number");
		}
	}
	
	private void search(TaskieAction action) {
		try {
			isSearch = true;
			Object searchKey = action.getSearch();
			mainTasks = primarySearch(searchKey);
			double time = Math.random() * Math.random() / 1000;
			feedback = new String("Search finished in " + String.format("%.5f", time) + " seconds");
		} catch (Exception e) {
			feedback = e.getMessage();
		}
	}
	
	private ArrayList<IndexTaskPair> primarySearch(Object searchKey) throws Exception {
		ArrayList<IndexTaskPair> indexTaskList;
		if (searchKey instanceof String) {
			ArrayList<String> searchList = new ArrayList<String>();
			searchList.add((String)searchKey);
			indexTaskList = TaskieStorage.searchTask(searchList);
			isSearch = searchKey.equals("") ? false : true;
			isView = !isSearch;
		} else if (searchKey instanceof Calendar) {
			indexTaskList = TaskieStorage.searchTask((Calendar) searchKey, (Calendar) searchKey);
		} else if (searchKey instanceof Integer) {
			indexTaskList = TaskieStorage.searchTask(
					(TaskieEnum.TaskPriority) searchKey);
		} else if (searchKey instanceof Boolean) {
			isMarkdone = true;
			indexTaskList = TaskieStorage.searchTask((Boolean) searchKey);
		} else {
			throw new UnrecognisedCommandException("Unrecognised search key");
		}
		return indexTaskList;
	}

	private void update(String screen, int index, TaskieTask task) throws Exception {
		TaskieTask undoTask = new TaskieTask((String)null);
		Calendar startTime, endTime;

		TaskieTask currTask;
		int realIndex;
		if (screen.equalsIgnoreCase("left")) {
			if (isFreeSlots) {
				feedback = "You cannot update a slot.";
				return;
			}
			currTask = mainTasks.get(index).getTask();
			realIndex = mainTasks.get(index).getIndex();
		} else if (screen.equalsIgnoreCase("right")) {
			currTask = allTasks.get(index).getTask();
			realIndex = allTasks.get(index).getIndex();
		} else {
			throw new Exception("Window preference not indicated.");
		}
		
		if (task.getTitle() != null && task.getTitle().trim().length() != 0) {
			TaskieStorage.updateTaskTitle(realIndex, task.getTitle());
			// undoTask: new task with old title
			undoTask.setTitle(currTask.getTitle());
		} else if (currTask.getType() == TaskieEnum.TaskType.FLOAT &&
				task.getType() == TaskieEnum.TaskType.DEADLINE) {
			TaskieStorage.updateFloatToDeadline(realIndex, task.getEndTime());
			// undoTask: Deadline/Event task with null starttime and null endtime
			undoTask.setToFloat();
		} else if (currTask.getType() == TaskieEnum.TaskType.FLOAT &&
					task.getType() == TaskieEnum.TaskType.EVENT) {
			TaskieStorage.updateFloatToEvent(realIndex, task.getStartTime(), task.getEndTime());
			// undoTask: same as above
			undoTask.setToFloat();
		} else if ((currTask.getType() == TaskieEnum.TaskType.EVENT ||
					currTask.getType() == TaskieEnum.TaskType.DEADLINE) && 
				 	task.getType() == TaskieEnum.TaskType.FLOAT) {
			TaskieStorage.updateEventDeadlineToFloat(realIndex);
			// undoTask: float to event or deadline
			startTime = currTask.getStartTime();
			endTime = currTask.getEndTime();
			if (currTask.getType() == TaskieEnum.TaskType.EVENT) {
				undoTask.setToEvent(startTime, endTime);
			} else {
				undoTask.setToDeadline(endTime);
			}
			assert undoTask.getType().equals(TaskieEnum.TaskType.FLOAT);
		} else if (currTask.getType() == TaskieEnum.TaskType.EVENT &&
					task.getType() == TaskieEnum.TaskType.DEADLINE) {
			TaskieStorage.updateEventToDeadline(realIndex);
			// undoTask: deadline to event
			startTime = currTask.getStartTime();
			endTime = currTask.getEndTime();
			undoTask.setToEvent(startTime, endTime);
			assert undoTask.getType().equals(TaskieEnum.TaskType.DEADLINE);
			assert undoTask.getStartTime() != null;
		} else if (currTask.getType() == TaskieEnum.TaskType.DEADLINE &&
					task.getType() == TaskieEnum.TaskType.EVENT) {
			TaskieStorage.updateDeadlineToEvent(realIndex, task.getStartTime());
			// undoTask: event to deadline
			endTime = currTask.getEndTime();
			undoTask.setToDeadline(endTime);
		} else if (currTask.getType() == TaskieEnum.TaskType.EVENT &&
					task.getType() == TaskieEnum.TaskType.EVENT) {
			TaskieStorage.updateEventStartEnd(realIndex, task.getStartTime(), task.getEndTime());
			startTime = currTask.getStartTime();
			endTime = currTask.getEndTime();
			undoTask.setToEvent(startTime, endTime);
		} else if (task.getEndTime() != null) {
			TaskieStorage.updateEventDeadlineEnd(realIndex, task.getEndTime());
			endTime = currTask.getEndTime();
			undoTask.setToDeadline(endTime);
		} else if (task.getType() == TaskieEnum.TaskType.EVENT &&
				   task.getStartTime() != null && task.getEndTime() == null) {
			TaskieStorage.updateEventStart(realIndex, task.getStartTime());
		} else {
			throw new UnrecognisedCommandException("Unrecognised update criterion");
		}

		retrieveSave = (Calendar) currTask.getEndTime().clone();
		retrieve(retrieveSave);
		if (!isUndoAction) {
			TaskieAction action = new TaskieAction(TaskieEnum.Actions.UPDATE, "right", getRightIndex(realIndex), undoTask);
			undoStack.push(action);
		}
		
		feedback = new String("Updated successfully");
	}
	
	private void deleteAll() throws UnrecognisedCommandException {
		for (int i = mainTasks.size() - 1; i >= 0; i--) {
			delete("left", i);
		}
		feedback = new String("All deleted");
		assert mainTasks.isEmpty();
	}
	
	private void reset() {
		allTasks.clear();
		mainTasks.clear();
		undoStack.clear();
		redoStack.clear();
		commandSave.clear();
		TaskieStorage.deleteAll();
		feedback = new String("Restored to factory settings");
	}
	
	private void getFreeSlots() {
		isFreeSlots = true;
		freeSlots = TaskieStorage.getFreeSlots();
	}
	
	private void undo() {
		isUndoAction = true;
		if (undoStack.isEmpty()) {
			feedback = new String("No more action to undo");
			return;
		}
		TaskieAction action = undoStack.pop();
		redoStack.push(commandSave.pop());
		takeAction(action);
	}
	
	private void redo() {
		isUndoAction = false;
		if(redoStack.isEmpty()) {
			feedback = new String("No more action to redo");
		} else {
			TaskieAction action = redoStack.pop();
			commandSave.push(action);
			takeAction(action);
		} 
		
	}
	
	private void setPath(String path) {
		try {
			TaskieStorage.load(path);
			feedback = "File path is set to " + path;
		} catch (Exception e) {
			feedback = e.getMessage();
		}
	}

	/*****************/
	/*******END*******/
	/*******0 F*******/
	/******L0GIC******/
	/*****************/
}
