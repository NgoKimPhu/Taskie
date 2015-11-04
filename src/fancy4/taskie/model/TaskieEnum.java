package fancy4.taskie.model;
/**
 * @author Qin_ShiHuang
 *
 */
public class TaskieEnum {
	
	public static enum Actions {
		ADD, SEARCH, DISPLAYALL, CLEAR, DELETE, DELETEALL, UPDATE, MARKDONE, 
		SORT, UNDO, REDO, FREESLOT, RESET, EXIT, INVALID
	}
	
	public static enum TaskType {
		EVENT, DEADLINE, FLOAT, UNKNOWN
	}
	
	public static enum TaskPriority{
		//from 0 to 4
		VERY_HIGH, HIGH, MEDIUM, LOW, VERY_LOW;
	}
	
	// Right window rules
	

}