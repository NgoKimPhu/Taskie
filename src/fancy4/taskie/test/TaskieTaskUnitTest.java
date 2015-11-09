//@@author A0119390E
package fancy4.taskie.test;

import static org.junit.Assert.*;
import org.junit.Test;
import fancy4.taskie.model.*;
import java.util.*;

public class TaskieTaskUnitTest {

	@Test
	// failure case, start time after end time
	public void testCreateTaskStartAfterEndFail() {
		Calendar start = Calendar.getInstance();
		Calendar end = Calendar.getInstance();
		//start time one day after end time
		start.set(Calendar.DATE, start.get(Calendar.DATE)+1);
		try{
			TaskieTask task = new TaskieTask("test", start, end);
		}catch(Exception e){
			assertEquals("Start time should not after end time.", e.getMessage());
			System.out.println(e.getMessage());
		}
	}
	
	@Test
	// failure case, task type not match
	public void testCreateTaskTypeNotMatch() {
		try{
			TaskieTask task = new TaskieTask("test", TaskieEnum.TaskType.EVENT, TaskieEnum.TaskPriority.LOW, false, null);
		}catch(Exception e){
			assertEquals("Task type not match.", e.getMessage());
			System.out.println(e.getMessage());
		}
	}
	
	@Test
	// failure case, start time after end time
	public void testSetDateStartAfterEndFail() {
		Calendar start = Calendar.getInstance();
		Calendar end = Calendar.getInstance();
		// start time one day after end time
		start.set(Calendar.DATE, start.get(Calendar.DATE)+1);
		try{
			TaskieTask task = new TaskieTask("test");
			task.setToEvent(start, end);
		}catch(Exception e){
			assertEquals("Start time should not after end time.", e.getMessage());
			System.out.println(e.getMessage());
		}
	}
	
	@Test
	// failure case, set start time to deadline task fail
	public void testSetStartTimeToDeadlineFail() {
		Calendar start = Calendar.getInstance();
		Calendar end = Calendar.getInstance();
		// start time one day before end time
		start.set(Calendar.DATE, start.get(Calendar.DATE)-1);
		TaskieTask task = new TaskieTask("test", end);
		assertFalse(task.setStartTime(start));
	}
	
	@Test
	// failure case, set start time to floating task fail
	public void testSetStartTimeToFloatFail() {
		Calendar start = Calendar.getInstance();
		// start time one day before current time
		start.set(Calendar.DATE, start.get(Calendar.DATE)-1);
		TaskieTask task = new TaskieTask("test");
		assertFalse(task.setStartTime(start));
	}
	
	@Test
	// failure case, set end time to floating task fail
	public void testSetEndTimeToFloatFail() {
		Calendar end = Calendar.getInstance();
		TaskieTask task = new TaskieTask("test");
		assertFalse(task.setStartTime(end));
	}
	
	@Test 
	// test setting the status to a task
	public void testSetStatusToTask(){
		TaskieTask task = new TaskieTask("test");
		// by default, status is false - incomplete
		assertTrue(task.setStatus(true));	
	}

}
