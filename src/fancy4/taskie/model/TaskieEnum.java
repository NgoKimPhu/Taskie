package fancy4.taskie.model;
/**
 * @author Qin_ShiHuang
 *
 */
public class TaskieEnum {
	
	public static enum Actions {
		ADD, DELETE, DELETEALL, UNDO, REDO, CLEAR, SEARCH, SORT, MARKDONE, UPDATE, DISPLAYALL, RESET, EXIT, INVALID
	}
	
	public static enum TaskType {
		EVENT, DEADLINE, FLOAT, UNKNOWN
	}
	
	public static enum TaskPriority{
		//from 0 to 4
		VERY_HIGH, HIGH, MEDIUM, LOW, VERY_LOW;
	}

}