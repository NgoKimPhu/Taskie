package fancy4.taskie.test;

import static org.junit.Assert.*;
import org.junit.Test;
import fancy4.taskie.model.*;
import java.util.*;

public class TaskieTaskUnitTest {

	@Test
	//boundary case, start time after end time, raise exception
	public void testCreateTaskStartAfterEnd() {
		Calendar start = Calendar.getInstance();
		Calendar end = Calendar.getInstance();
		start.set(Calendar.DATE, start.get(Calendar.DATE)+1);
		try{
			TaskieTask task = new TaskieTask("test", start, end);
		}catch(Exception e){
			assertEquals("start time should not after end time.", e.getMessage());
			System.out.println(e.getMessage());
		}
	}
	@Test
	//boundary case, task type not match
	public void testCreateTaskTypeNotMatch() {
		try{
			TaskieTask task = new TaskieTask("test", TaskieEnum.TaskType.EVENT, TaskieEnum.TaskPriority.LOW, false, null);
		}catch(Exception e){
			assertEquals("Task type not match.", e.getMessage());
			System.out.println(e.getMessage());
		}
	}

}
