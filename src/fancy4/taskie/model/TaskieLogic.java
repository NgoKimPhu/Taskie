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

public class TaskieLogic {

	private static TaskieLogic logic;
	
	private Comparator<IndexTaskPair> comparator;
	private ArrayList<IndexTaskPair> mainTasks;
	private ArrayList<IndexTaskPair> allTasks;
	private ArrayList<CalendarPair> freeSlots;
	private Stack<TaskieAction> commandSave;
	private Stack<TaskieAction> undoStack;
	private Stack<TaskieAction> redoStack;
	private boolean isUndoAction;
	private boolean isFreeSlots;
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
			undoStack = new Stack<TaskieAction>();
			redoStack = new Stack<TaskieAction>();
			commandSave = new Stack<TaskieAction>();
			freeSlots = new ArrayList<CalendarPair>();
			allTasks = new ArrayList<IndexTaskPair>();
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
			isFreeSlots = false;
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
			takeAction(action);
			Collections.sort(mainTasks, comparator);
			Collections.sort(allTasks, comparator);
			return new LogicOutput(feedback, getMain(), getAll());
	}

	private ArrayList<String> getMain() {
		ArrayList<String> main = new ArrayList<String>();
		String headline, number, date;
		if (isFreeSlots) {
			main = format(freeSlots);
			int size = main.size();
			if (size == 0) {
				headline = "There is no free slots. Take some break.";
			} else {
				number = size == 1 ? "is one free slot" : "are " + size + " free slots";
				headline = new String("There " + number + ".");
			}
		} else {
			int size = mainTasks.size();
			if (mainTasks.isEmpty()) {
				headline = new String("There is no task. Feed me some, or take a nap.");
			} else {
				date = new String();
				if (!mainTasks.get(0).getTask().getType().equals(TaskieEnum.TaskType.FLOAT)) {
					Date day = mainTasks.get(0).getTask().getEndTime().getTime();
					SimpleDateFormat sdf = new SimpleDateFormat("dd, MMM, yyyy");
					date = " on " + sdf.format(day);
				}
				number = size == 1 ? new String("is one task") : new String("are " + size + " tasks");
				headline = new String("There " + number + date + ".");
			}
			main = format(0, mainTasks);
		}
		main.add(0, headline);
		return main;
	}
	
	
	/*  the "all" format:
	 *		all is an ArrayList<ArrayList<String>>. all.size() == 4
	 *		overdue, today, tmr, else
	 * 		
	 * 
	 * */
	private ArrayList<ArrayList<String>> getAll() {
		ArrayList<ArrayList<String>> all = new ArrayList<ArrayList<String>>();
		ArrayList<IndexTaskPair> ovd = new ArrayList<IndexTaskPair>(),
								 tod = new ArrayList<IndexTaskPair>(),
								 tmr = new ArrayList<IndexTaskPair>(),
								 els = new ArrayList<IndexTaskPair>();
		getAllTasks();
		Calendar now = Calendar.getInstance();
		for (IndexTaskPair pair : allTasks) {
			if (pair.getTask().getEndTime() == null) {
				els.add(pair);
			} else {
				if (pair.getTask().getEndTime().before(now)) {
					ovd.add(pair);
				}
				now = new Calendar.Builder().setDate(now.get(Calendar.YEAR), 
						now.get(Calendar.MONTH), now.get(Calendar.DATE) + 2).build();
				if (pair.getTask().getEndTime().after(now)) {
					els.add(pair);
				}
				now = Calendar.getInstance();
			}
		}
		
		Calendar endOfToday = (Calendar) now.clone();
		endOfToday.add(Calendar.DATE, 1);
		endOfToday.set(Calendar.HOUR_OF_DAY, 0);
		endOfToday.set(Calendar.MINUTE, 0);
		endOfToday.set(Calendar.SECOND, 0);
		endOfToday.set(Calendar.MILLISECOND, 0);
		try {
			tod = TaskieStorage.searchTask(now, endOfToday);
		} catch (Exception e) {
			assert 1 == 0;
		}
		
		Calendar endOfTmr = (Calendar) endOfToday.clone();
		endOfTmr.add(Calendar.DATE, 1);
		try {
			tmr = TaskieStorage.searchTask(endOfToday, endOfTmr);
		} catch (Exception e) {
			assert 1 == 0;
		}
		
		Collections.sort(ovd, comparator);
		Collections.sort(tod, comparator);
		Collections.sort(tmr, comparator);
		Collections.sort(els, comparator);
		
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
	 * Below are auxiliary methods.
	 * 
	 * 
	 */
	private ArrayList<IndexTaskPair> getAllTasks() {
		allTasks.clear();
		ArrayList<TaskieTask> complete = TaskieStorage.displayAllTasks();
		for (int i = 0; i < complete.size(); i++) {
			allTasks.add(new IndexTaskPair(i, complete.get(i)));
		}
		return allTasks;
	}
	
	private ArrayList<String> format(int index, ArrayList<IndexTaskPair> list) {
		ArrayList<String> formatted = new ArrayList<String>();
		
		SimpleDateFormat sdf = new SimpleDateFormat("E dd-MM HH:mm");
		SimpleDateFormat sdf2 = new SimpleDateFormat("HH:mm");
		SimpleDateFormat sdf3 = new SimpleDateFormat("dd-MM-YYYY");
		for (IndexTaskPair pair : list) {
			index++;
			TaskieTask task = pair.getTask();
			Calendar st = task.getStartTime();
			Calendar et = task.getEndTime();
			boolean isSameDay = false;
			String sst, set;
			
			if (st != null && et != null && sdf3.format(st.getTime()).equals(sdf3.format(et.getTime()))) {
				isSameDay = true;
			}
			
			if (st != null) {
				sst = sdf.format(st.getTime());
			} else {
				sst = "";
			}
			if (et != null) {
				set = sdf.format(et.getTime());
			} else {
				set = "";
			}
			
			if (isSameDay) {
				formatted.add(new String(index + ".  " + task.getTitle() + sst + " ~ " + sdf2.format(et.getTime())));
			} else {
				formatted.add(new String(index + ".  " + task.getTitle() + sst + "  " + set));
			}
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

	private void retrieveLeft(Calendar day) throws Exception {
		mainTasks.clear();
		getAllTasks();
		if (day == null) {
			for (IndexTaskPair pair : allTasks) {
				if (pair.getTask().getType().equals(TaskieEnum.TaskType.FLOAT)) {
					mainTasks.add(pair);
				}
			}
		} else {
			Calendar start = new Calendar.Builder().setDate(day.get(Calendar.YEAR), 
					day.get(Calendar.MONTH), day.get(Calendar.DATE)).build();
			Calendar end = (Calendar) start.clone();
			end.add(Calendar.DATE, 1);
			mainTasks = TaskieStorage.searchTask(start, end);
		}
	}
	
	private int getListIndex(IndexTaskPair pair) {
		for (int i = 0; i < allTasks.size(); i++) {
			if (pair.getIndex() == allTasks.get(i).getIndex()) {
				return i;
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
		try {
			retrieveLeft(added.getTask().getEndTime());
		} catch (Exception e) {
			feedback = e.getMessage();
			return;
		}
		
		if (!isUndoAction) {
			TaskieAction undoAction = new TaskieAction(TaskieEnum.Actions.DELETE, "right", getListIndex(added) + 1);
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
				realIndex = allTasks.get(index).getIndex();
			} else {
				throw new UnrecognisedCommandException("Window preference not indicated.");
			}
			
			for (IndexTaskPair pair : mainTasks) {
				if (pair.getIndex() == realIndex) {
					mainTasks.remove(pair);
					break;
				}
			}
			for (IndexTaskPair pair : allTasks) {
				if (pair.getIndex() == realIndex) {
					allTasks.remove(pair);
					break;
				}
			}
			
			TaskieTask deleted = TaskieStorage.deleteTask(realIndex);
			String title = deleted.getTitle();

			feedback = new String("\"" + title + "\"" + " is deleted");
			
			// Construct undo action
			if (!isUndoAction) {
				TaskieAction action = new TaskieAction(TaskieEnum.Actions.ADD, deleted);
				undoStack.push(action);
			}
			
		} catch (IndexOutOfBoundsException e) {
			feedback = new String("Invalid index number");
		}
	}
	
	private void markdone(String screen, int index) throws UnrecognisedCommandException {
		try {
			//TaskieEnum.TaskType type;
			String title;
			int realIndex;
			if (screen.equalsIgnoreCase("left")) {
				//type = mainTasks.get(index).getTask().getType();
				realIndex = mainTasks.get(index).getIndex();
				title = mainTasks.get(index).getTask().getTitle();
			} else if (screen.equalsIgnoreCase("right")) {
				//type = allTasks.get(index).getTask().getType();
				realIndex = allTasks.get(index).getIndex();
				title = allTasks.get(index).getTask().getTitle();
			} else {
				throw new UnrecognisedCommandException("Screen preference not indicated.");
			}
			
			feedback = new String("\"" + title + "\"" + " is marked done");
			TaskieStorage.markDone(realIndex);
			
			// Construct undo action
			if (!isUndoAction) {
				TaskieAction action = new TaskieAction(TaskieEnum.Actions.MARKDONE, screen, index + 1);
				undoStack.push(action);
			}
		} catch (IndexOutOfBoundsException e) {
			feedback = new String("Invalid index number");
		}
	}
	
	private void search(TaskieAction action) {
		try {
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
		} else if (searchKey instanceof Calendar) {
			indexTaskList = TaskieStorage.searchTask((Calendar) searchKey, (Calendar) searchKey);
		} else if (searchKey instanceof Integer) {
			indexTaskList = TaskieStorage.searchTask(
					(TaskieEnum.TaskPriority) searchKey);
		} else if (searchKey instanceof Boolean) {
			indexTaskList = TaskieStorage.searchTask((Boolean) searchKey);
		} else {
			throw new UnrecognisedCommandException("Unrecognised search key");
		}
		return indexTaskList;
	}

	private void update(String screen, int index, TaskieTask task) throws UnrecognisedCommandException {
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
			throw new UnrecognisedCommandException("Window preference not indicated.");
		}
		
		if (task.getTitle() != null && task.getTitle().trim().length() != 0) {
			ArrayList<TaskieTask> taskList = TaskieStorage.updateTaskTitle(realIndex, task.getTitle());
			// undoTask: new task with old title
			undoTask.setTitle(currTask.getTitle());
		}
		
		if (currTask.getType() == TaskieEnum.TaskType.FLOAT &&
				task.getType() == TaskieEnum.TaskType.DEADLINE) {
			TaskieStorage.updateFloatToDeadline(realIndex, task.getEndTime());
			// undoTask: Deadline/Event task with null starttime and null endtime
			undoTask.setToFloat(); // A little bit messy :-(
		} else if (task.getType() == TaskieEnum.TaskType.FLOAT &&
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
		// return update(action.getIndex() - 1, action.getTask());
		if (!isUndoAction) {
			TaskieAction action = new TaskieAction(TaskieEnum.Actions.UPDATE, screen, index, undoTask);
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
	
	
	/*****
	 * Below are undo/redo methods.
	 * @throws Exception 
	 */
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

}
