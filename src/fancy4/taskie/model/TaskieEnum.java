package fancy4.taskie.model;
/**
 * @author Qin_ShiHuang
 *
 */
public class TaskieEnum {
	
	public static enum Actions {
		ADD, SEARCH, DELETE, DELETEALL, UPDATE, MARKDONE, 
		UNDO, REDO, FREESLOT, RESET, EXIT, INVALID;
	}
	
	public static enum TaskType {
		EVENT, DEADLINE, FLOAT;
	}
	
	public static enum TaskPriority{
		//from 0--low, 1--high
		LOW, HIGH;
	}
	
	// Right window rules
	

}