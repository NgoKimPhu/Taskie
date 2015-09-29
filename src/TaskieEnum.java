/**
 * @author Qin_ShiHuang
 *
 */
public class TaskieEnum {
	
	public static enum Actions {
		ADD, DELETE, UNDO, CLEAR, SEARCH, SORT, INVALID
	}
	
	public static enum TaskType {
		EVENT, DEADLINE, FLOAT, UNKNOWN
	}
	
	public static enum TaskPriority{
		//from 0 to 4
		VERY_HIGH, HIGH, MEDIUM, LOW, VERY_LOW;
	}

}