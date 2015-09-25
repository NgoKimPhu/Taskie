/** 
 * This the the Task class for this project.
 * @author Qin Xueying
 *
 */

import java.util.*;
public class Task {
	private TaskType type;
	private String name;
	private long id;
	private String description;
	private Date startTime;
	private Date endTime;
	private int priority;
	private static long count = 0;
	public Task(){
		this.count+=1;
		this.type = TaskType.FLOAT;
	}
	
	
	
}
enum TaskType{
	EVENT, DEADLINE, FLOAT;
}