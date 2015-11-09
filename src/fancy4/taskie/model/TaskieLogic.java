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
	
	private static final String DATE_FORMAT_DAY_MON_YEAR = "dd-MM-YYYY";
	private static final String DATE_FORMAT_DAY_MONTH_HOUR_MIN = "E dd-MM HH:mm";
	private static final String DATE_FORMAT_HOUR_MIN = "HH:mm";
	private static final String DATE_FORMAT_MAIN = "dd, MMM, yyyy";
	private static final String EXCEPTION_INVALID_INDEX_NUMBER = "Invalid index number";
	private static final String EXCEPTION_UNRECOGNISED_UPDATE_CRITERION = "Unrecognised update criterion";
	private static final String EXCEPTION_WINDOW_PREFERENCE_NOT_INDICATED = "Window preference not indicated.";
	private static final String FEEDBACK_ADD = "\"%1$s\" is added";
	private static final String FEEDBACK_CANNOT_DELETE_FREESLOT = "You cannot update a slot.";
	private static final String FEEDBACK_DELETE = "\"%1$s\" is deleted";
	private static final String FEEDBACK_DELETEALL = "All deleted";
	private static final String FEEDBACK_EMPTY_PATH = "Empty path!";
	private static final String FEEDBACK_MARKDONE = "\"%1$s\" is marked done";
	private static final String FEEDBACK_NO_MORE_REDO = "No more action to redo";
	private static final String FEEDBACK_NO_MORE_UNDO = "No more action to undo";
	private static final String FEEDBACK_RESET = "Restored to factory settings";
	private static final String FEEDBACK_SEARCH = "Search finished in %1$s seconds";
	private static final String FEEDBACK_SETPATH = "File path is set to %1$s";
	private static final String FEEDBACK_UPDATE = "Updated successfully";
	private static final String FLOATING = " floating";
	private static final String FORMAT_COMPLETED = "Completed";
	private static final String FORMAT_COMPLETED_SPACING = "    ";
	private static final String FORMAT_EMPTY_STRING = "";
	private static final String FORMAT_INDEXPERIOD = ".  ";
	private static final String FORMAT_SAMEDAY_WAVE = " ~ ";
	private static final String FORMAT_SPACING = "  ";
	private static final String FORMAT_UI_TIME_INDICATOR = "-time ";
	private static final String HEADLINE_FLOATING = "You have %1$s%2$s.";
	private static final String HEADLINE_FREESLOTS = "You have %1$s in the next seven days.";
	private static final String HEADLINE_MANY_COMPLETED = "are %1$s completed tasks";
	private static final String HEADLINE_MANY_FREESLOTS = "are %1$s free slots";
	private static final String HEADLINE_MANY_TASKS = "%1$s%2$s tasks";
	private static final String HEADLINE_MANY_TASKS_SEARCH = "are %1$s tasks";
	private static final String HEADLINE_MANY_TASKS_VIEW = "%1$s tasks";
	private static final String HEADLINE_MARKDONE = "There %1$s.";
	private static final String HEADLINE_NO_FREESLOTS = "There is no free slots. Take some break.";
	private static final String HEADLINE_NO_TASK_GENERAL = "There is no task. Feed me some, or take a nap.";
	private static final String HEADLINE_NO_TASK_MARKDONE = "There is no completed task.";
	private static final String HEADLINE_NO_TASK_SEARCH = "Your search did not match any tasks.";
	private static final String HEADLINE_ONE_COMPLETED = "is one completed task";
	private static final String HEADLINE_ONE_FREESLOT = "is one free slot";
	private static final String HEADLINE_ONE_TASK = "one%1$s task";
	private static final String HEADLINE_ONE_TASK_SEARCH = "is one task";
	private static final String HEADLINE_ONE_TASK_VIEW = "one task";
	private static final String HEADLINE_SEARCH = "There %1$s found.";
	private static final String HEADLINE_VIEW = "You have %1$s in total.";
	private static final String ON_DAY = " on %1$s";
	private static final String WINDOW_LEFT = "left";
	private static final String WINDOW_RIGHT = "right";
	
    private static final int SEARCH_TIME_ONE_THOUSAND = 1000;

	private static TaskieLogic logic;
	
	private boolean isFreeSlots, isMarkdone, isSearch, isView, isUndoAction;
	private Stack<TaskieAction> commandSave, undoStack, redoStack;
	private ArrayList<IndexTaskPair> mainTasks, allTasks;
	private ArrayList<CalendarPair> freeSlots;
	private Calendar retrieveSave;
	private String feedback;

	private final Logger log = Logger.getLogger(TaskieLogic.class.getName() );
	
	public static TaskieLogic getInstance() {
		if (logic == null) {
			logic = new TaskieLogic();
		}
		return logic;
	}

	/* Constructor */
	protected TaskieLogic() {
		initialise();
	}
	
	

	/*****
	 * Below are "backbone" functions:
	 * initialise, execute, getMain, getAll, takeAction
	 * 
	 */
	private void initialise() {
		undoStack = new Stack<TaskieAction>();
		redoStack = new Stack<TaskieAction>();
		commandSave = new Stack<TaskieAction>();
		allTasks = new ArrayList<IndexTaskPair>();
		freeSlots = new ArrayList<CalendarPair>();
		mainTasks = new ArrayList<IndexTaskPair>();
		try {
			TaskieStorage.load(FORMAT_EMPTY_STRING);
			retrieve(retrieveSave);
			log.fine("Initialisation Completed.");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public LogicOutput execute(String str) throws UnrecognisedCommandException {
			if (str.equals(FORMAT_EMPTY_STRING)) {
				throw new UnrecognisedCommandException("Empty command.");
			}
			isView = false;
			isSearch = false;
			isMarkdone = false;
			isUndoAction = false;
			TaskieParser parser = TaskieParser.getInstance();
			TaskieAction action = parser.parse(str);
			// Save the undo- and re-do-able actions
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
			getFreeSlots(action);
			// Assemble the output: feedback, left screen output, right screen output
			return new LogicOutput(feedback, getMain(), getAll());
	}

	/*	This method generates the output to the left window	*/
	private ArrayList<String> getMain() {
		ArrayList<String> main = new ArrayList<String>();
		String headline, number, date, floating;
		if (isFreeSlots) {	// Customised headline for freeslots
			main = format(freeSlots);
			int size = main.size();
			if (size == 0) {
				headline = HEADLINE_NO_FREESLOTS;
			} else {
				number = size == 1 ? HEADLINE_ONE_FREESLOT : String.format(HEADLINE_MANY_FREESLOTS, size);
				headline = String.format(HEADLINE_FREESLOTS, number);
			}
		} else {
			int size = mainTasks.size();
			if (mainTasks.isEmpty()) {	// Handles the situation when no task is on display
				if (isMarkdone) {
					headline = HEADLINE_NO_TASK_MARKDONE;
				} else if (isSearch) {
					headline = HEADLINE_NO_TASK_SEARCH;
				} else {
					headline = HEADLINE_NO_TASK_GENERAL;
				}
			} else {  // Handles the various situation when one or multiply tasks are on display
				date = new String();
				floating = new String();
				if (!mainTasks.get(0).getTask().getType().equals(TaskieEnum.TaskType.FLOAT)) {
					Date day = mainTasks.get(0).getTask().getEndTime().getTime();
					SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_MAIN);
					date = String.format(ON_DAY, sdf.format(day));
				} else {
					floating = FLOATING;
				}
				if (isMarkdone) {
					number = size == 1 ? new String(HEADLINE_ONE_COMPLETED) : String.format(HEADLINE_MANY_COMPLETED, size);
					headline = String.format(HEADLINE_MARKDONE, number);
				} else if (isSearch) {
					number = size == 1 ? new String(HEADLINE_ONE_TASK_SEARCH) : String.format(HEADLINE_MANY_TASKS_SEARCH, size);
					headline = String.format(HEADLINE_SEARCH, number);
				} else if (isView) {
					number = size == 1 ? new String(HEADLINE_ONE_TASK_VIEW) : String.format(HEADLINE_MANY_TASKS_VIEW, size);
					headline = String.format(HEADLINE_VIEW, number);
				} else {
					number = size == 1 ? String.format(HEADLINE_ONE_TASK, floating) : String.format(HEADLINE_MANY_TASKS, size, floating);
					headline = String.format(HEADLINE_FLOATING, number, date);
				}
			}
			main = format(0, mainTasks); // index start from 0
		}
		main.add(0, headline);
		return main;
	}
	
	
	/*	This is the output to the right window
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
		
		// continuous indexing
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
				return;
			case SETPATH:
				setPath((String)action.getSearch());
				return;
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
	}

	
	
	/*****
	 * Below are the auxiliary methods:
	 *	  format, retrieveAllTasks, retrieve, getRightIndex, getLeftIndex, exit
	 * 
	 */
	/*	This method converts a IndexTaskPair list to a list of String which is suitable for displaying	*/
	private ArrayList<String> format(int index, ArrayList<IndexTaskPair> list) {
		ArrayList<String> formatted = new ArrayList<String>();
		
		SimpleDateFormat formatDayMonHourMin = new SimpleDateFormat(DATE_FORMAT_DAY_MONTH_HOUR_MIN);
		SimpleDateFormat formatHourMin = new SimpleDateFormat(DATE_FORMAT_HOUR_MIN);
		SimpleDateFormat formatDayMonYear = new SimpleDateFormat(DATE_FORMAT_DAY_MON_YEAR);
		for (IndexTaskPair pair : list) {
			index++;
			TaskieTask task = pair.getTask();
			Calendar start = task.getStartTime();
			Calendar end = task.getEndTime();
			boolean isSameDay = false;
			String startTimeFormatted, endTimeFormatted, taskDetail, status;
			// Check if start time and end time are on the same day.
			if (start != null && end != null && 
				formatDayMonYear.format(start.getTime()).equals(formatDayMonYear.format(end.getTime()))) {
				isSameDay = true;
			}
			
			if (start != null) {
				startTimeFormatted = formatDayMonHourMin.format(start.getTime());
			} else {
				startTimeFormatted = FORMAT_EMPTY_STRING;
			}
			if (end != null) {
				endTimeFormatted = formatDayMonHourMin.format(end.getTime());
			} else {
				endTimeFormatted = FORMAT_EMPTY_STRING;
			}
			
			status = pair.getTask().getStatus() ? FORMAT_COMPLETED : FORMAT_EMPTY_STRING;
			
			if (isSameDay) {
				taskDetail = new String(index + FORMAT_INDEXPERIOD + task.getTitle() + FORMAT_UI_TIME_INDICATOR + FORMAT_SPACING + startTimeFormatted + FORMAT_SAMEDAY_WAVE + formatHourMin.format(end.getTime()) + FORMAT_SPACING + (status = pair.getTask().getStatus() ? FORMAT_COMPLETED : FORMAT_EMPTY_STRING));
			} else {
				taskDetail = new String(index + FORMAT_INDEXPERIOD + task.getTitle() + FORMAT_UI_TIME_INDICATOR + startTimeFormatted + FORMAT_SPACING + endTimeFormatted);
			}
			formatted.add(new String(taskDetail + FORMAT_COMPLETED_SPACING + status));
		}
		return formatted;
	}
	
	/*	This method converts a CalendarPair list to a list of String which is suitable for displaying	*/
	private ArrayList<String> format(ArrayList<CalendarPair> list) {
		ArrayList<String> slots = new ArrayList<String>();
		for (int i = 1; i <= list.size(); i++) {
			slots.add(i + ". " + list.get(i - 1).toString());
		}
		return slots;
	}	
	
	/*	This method updates the field "allTasks", excluding the completed tasks  */
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

	/*	This method updates the field "mainTasks" to the list of all tasks on the "day" as specified  */
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
	
	/*	This method updates the field "mainTasks" to the list of completed tasks  */
	private void retrieve(boolean status) throws Exception {
		retrieveAllTasks();
		mainTasks = TaskieStorage.searchTask(true);
		Collections.sort(mainTasks);
	}
	
	/*  This method returns the displayed index in the right window of an IndexTaskPair in "allTasks"   */
	private int getRightIndex(IndexTaskPair pair) {
		return getRightIndex(pair.getIndex());
	}
	
	/*  This method returns the displayed index in the right window given the task's realIndex   */
	private int getRightIndex(int realIndex) {
		for (int i = 0; i < allTasks.size(); i++) {
			if (realIndex == allTasks.get(i).getIndex()) {
				return i + 1;
			}
		}
		return -1;
	}
	
	/*  This method returns the displayed index in the left window given the task's realIndex   */
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
	 * 
	 */
	/*  This method adds a new task and updates the fields "mainTasks" and "allTasks".
	 *  "mainTasks" will be updated to the list of tasks that has the same end date as the new task,
	 *  or to the list of all floating tasks if the new task is floating.  */
	private void add(TaskieTask task) throws Exception {
		IndexTaskPair added = TaskieStorage.addTask(task);
		assert task.getType().equals(added.getTask().getType());
		
		retrieve(added.getTask().getEndTime());
		
		if (!isUndoAction) {
			TaskieAction undoAction = new TaskieAction(TaskieEnum.Actions.DELETE, WINDOW_RIGHT, getRightIndex(added));
			undoAction.setTaskType(task.getType());
			undoStack.push(undoAction);
		}
		
		feedback = String.format(FEEDBACK_ADD, task.getTitle());
	}
	
	/*  This method deletes a specified task and updates the fields "mainTasks" and "allTasks" after the deletion.  */
	private void delete(String screen, int index) throws UnrecognisedCommandException {
		try {
			int realIndex;
			if (screen.equalsIgnoreCase(WINDOW_LEFT)) {
				if (isFreeSlots) {
					feedback = "You cannot delete a slot.";
					return;
				}
				realIndex = mainTasks.get(index).getIndex();
			} else if (screen.equalsIgnoreCase(WINDOW_RIGHT)) {
				if (allTasks.isEmpty()) {
					System.out.println("Empty allTasks");
				}
				realIndex = allTasks.get(index).getIndex();
			} else {
				throw new UnrecognisedCommandException(EXCEPTION_WINDOW_PREFERENCE_NOT_INDICATED);
			}

			TaskieTask deleted = TaskieStorage.deleteTask(realIndex);
			String title = deleted.getTitle();
			
			retrieve(retrieveSave);
			
			feedback = String.format(FEEDBACK_DELETE, title);
			
			// Construct undo action
			if (!isUndoAction) {
				TaskieAction action = new TaskieAction(TaskieEnum.Actions.ADD, deleted);
				undoStack.push(action);
			}
			
		} catch (IndexOutOfBoundsException e) {
			feedback = new String(EXCEPTION_INVALID_INDEX_NUMBER);
		} catch (Exception e) {
			feedback = e.getMessage();
		}
	}
	
	/*  This method marks a specified task as completed.
	 * 	It sets the flag "isMarkdone" to true, and removes the completed task from "allTasks".  */
	private void markdone(String screen, int index) throws Exception {
		isMarkdone = true;
		
		try {
			String title;
			int realIndex;
			if (screen.equalsIgnoreCase(WINDOW_LEFT)) {
				realIndex = mainTasks.get(index).getIndex();
				title = mainTasks.get(index).getTask().getTitle();
			} else if (screen.equalsIgnoreCase(WINDOW_RIGHT)) {
				realIndex = allTasks.get(index).getIndex();
				title = allTasks.get(index).getTask().getTitle();
			} else {
				throw new UnrecognisedCommandException("Screen preference not indicated.");
			}
			
			feedback = String.format(FEEDBACK_MARKDONE, title);
			
			TaskieStorage.changeStatus(realIndex);
			retrieve(true);
			
			// Construct undo action
			if (!isUndoAction) {
				TaskieAction action = new TaskieAction(TaskieEnum.Actions.MARKDONE, WINDOW_LEFT, getLeftIndex(realIndex));
				undoStack.push(action);
			}
		} catch (IndexOutOfBoundsException e) {
			feedback = new String(EXCEPTION_INVALID_INDEX_NUMBER);
		}
	}
	
	/*  This method searches for tasks that meets the criterion.  
	 *  Providing search for keyword, specific date and date interval, and combination of keyword and date.  */
	private void search(TaskieAction action) throws Exception {
		isSearch = true;
		ArrayList<IndexTaskPair> list;
		TaskieTask task = action.getTask();
		Object searchKey = action.getSearch();
		if (searchKey instanceof Boolean) { // Search for completed tasks
			isMarkdone = true;
			list = TaskieStorage.searchTask((Boolean) searchKey);
		} else if (task.getType() == TaskieEnum.TaskType.FLOAT) { // Search for keywords
			ArrayList<String> searchList = new ArrayList<String>();
			searchList.add(task.getTitle());
			list = TaskieStorage.searchTask(searchList);
			// Check if it is a View command
			isSearch = task.getTitle().equals(FORMAT_EMPTY_STRING) ? false : true;
			isView = !isSearch;
		} else { // Search by date
			Calendar taskStartTime = task.getStartTime(),
					   taskEndTime = task.getEndTime();
			if (task.getStartTime() != null) { // Search by a date interval
				list = TaskieStorage.searchTask(taskStartTime, taskEndTime);
			} else { // Search by exact date
				Calendar startOfDay = new Calendar.Builder().setDate(taskEndTime.get(Calendar.YEAR), 
						taskEndTime.get(Calendar.MONTH), taskEndTime.get(Calendar.DATE)).build();
				taskEndTime.add(Calendar.MINUTE, 1);
				list = TaskieStorage.searchTask(startOfDay, taskEndTime);
			}
			// Date + keywords composite searching, removing tasks which do not contain the keywords
			if (task.getTitle() != null) {
				for (int i = list.size() - 1; i >= 0; i--) {
					IndexTaskPair pair = list.get(i);
					if (!pair.getTask().getTitle().contains(task.getTitle())) {
						list.remove(i);
					}
				}
			}
		}
		mainTasks = list;
		// Below is for fun :D
		double time = Math.random() * Math.random() / SEARCH_TIME_ONE_THOUSAND;
		feedback = String.format(FEEDBACK_SEARCH, String.format("%.5f", time));
	}

	/*  This method updates the details of a task  */
	private void update(String screen, int index, TaskieTask task) throws Exception {
		TaskieTask undoTask = new TaskieTask((String)null);
		Calendar startTime, endTime;

		TaskieTask currTask;
		int realIndex;
		if (screen.equalsIgnoreCase(WINDOW_LEFT)) {
			if (isFreeSlots) {
				feedback = FEEDBACK_CANNOT_DELETE_FREESLOT;
				return;
			}
			currTask = mainTasks.get(index).getTask();
			realIndex = mainTasks.get(index).getIndex();
		} else if (screen.equalsIgnoreCase(WINDOW_RIGHT)) {
			currTask = allTasks.get(index).getTask();
			realIndex = allTasks.get(index).getIndex();
		} else {
			throw new Exception(EXCEPTION_WINDOW_PREFERENCE_NOT_INDICATED);
		}
		// For the section below, the name of the method called gives a good description of what the code does.
		if (task.getTitle() != null && task.getTitle().trim().length() != 0) {
			TaskieStorage.updateTaskTitle(realIndex, task.getTitle());
			// undoTask: task with old title
			undoTask.setTitle(currTask.getTitle());
		} else if (currTask.getType() == TaskieEnum.TaskType.FLOAT &&
				task.getType() == TaskieEnum.TaskType.DEADLINE) {
			TaskieStorage.updateFloatToDeadline(realIndex, task.getEndTime());
			// undoTask: Deadline/Event task with null start-time and null end-time
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
			throw new UnrecognisedCommandException(EXCEPTION_UNRECOGNISED_UPDATE_CRITERION);
		}
		
		retrieveSave = (Calendar) currTask.getEndTime().clone(); // Update the left display to the end date of the task that is being updated
		retrieve(retrieveSave);
		if (!isUndoAction) {
			TaskieAction action = new TaskieAction(TaskieEnum.Actions.UPDATE, WINDOW_RIGHT, getRightIndex(realIndex), undoTask);
			undoStack.push(action);
		}
		
		feedback = FEEDBACK_UPDATE;
	}
	
	/*  This method deletes all the tasks that are displayed in the left window  */
	private void deleteAll() throws UnrecognisedCommandException {
		for (int i = mainTasks.size() - 1; i >= 0; i--) {
			delete(WINDOW_LEFT, i);
		}
		feedback = FEEDBACK_DELETEALL;
		assert mainTasks.isEmpty();
	}
	
	/*  This method clears all the data and sets the software to its initial state  */
	private void reset() {
		allTasks.clear();
		mainTasks.clear();
		undoStack.clear();
		redoStack.clear();
		commandSave.clear();
		TaskieStorage.deleteAll();
		feedback = FEEDBACK_RESET;
	}
	
	/*  This method updates the field "freeSlots"  */
	private void getFreeSlots(TaskieAction action) {
		freeSlots = TaskieStorage.getFreeSlots();
		if (action.getType().equals(TaskieEnum.Actions.DELETE)) {
			return;
		}
		isFreeSlots = action.getType().equals(TaskieEnum.Actions.FREESLOT) ? true : false;
	}
	
	/*  This method undoes the latest action  */
	private void undo() {
		isUndoAction = true;
		if (undoStack.isEmpty()) {
			feedback = FEEDBACK_NO_MORE_UNDO;
			return;
		}
		TaskieAction action = undoStack.pop();
		redoStack.push(commandSave.pop());
		takeAction(action);
	}
	
	/*  This method redoes the last action which has been undone  */
	private void redo() {
		isUndoAction = false;
		if(redoStack.isEmpty()) {
			feedback = FEEDBACK_NO_MORE_REDO;
		} else {
			TaskieAction action = redoStack.pop();
			commandSave.push(action);
			takeAction(action);
		}
	}
	
	/*  This method changes the directory of the saved tasks file  */
	private void setPath(String path) {
		try {
			if (path == null || path.equals(FORMAT_EMPTY_STRING)) {
				feedback = FEEDBACK_EMPTY_PATH;
				return;
			}
			TaskieStorage.load(path);
			feedback = String.format(FEEDBACK_SETPATH, path);
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
