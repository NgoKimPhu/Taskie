//@@author A0119390E
package fancy4.taskie.model;

import static org.junit.Assert.*;
import java.util.*;
import org.junit.Test;

public class TaskieStorageUnitTest {
	@Test
	// test delete all
	public void testDeleteAll() {
		try {
			TaskieStorage.load("unit/");
			TaskieStorage.deleteAll();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		assertEquals(TaskieStorage.displayAllTasks().size(), 0);
	}
	@Test	
	// test add a float task partition 
	public void testAddFloat() {
		try {
			TaskieStorage.load("unit/");
			TaskieStorage.deleteAll();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		TaskieTask float1 = new TaskieTask("finish tutorial");
		TaskieStorage.addTask(float1);
		assertEquals(TaskieStorage.displayFloatTasks().size(), 1);
		assertEquals(TaskieStorage.displayAllTasks().size(), 1);
	}
	
	@Test
	// test add a deadline task partition
	public void testAddDeadline() {
		try {
			TaskieStorage.load("unit/");
			TaskieStorage.deleteAll();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		TaskieTask deadline = new TaskieTask("study", Calendar.getInstance());
		TaskieStorage.addTask(deadline);
		assertEquals(TaskieStorage.displayDeadlineTasks().size(), 1);
		assertEquals(TaskieStorage.displayAllTasks().size(), 1);
	}
	@Test
	// test add a event task partition
	public void testAddEvent() throws Exception {
		try {
			TaskieStorage.load("unit/");
			TaskieStorage.deleteAll();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		Calendar start = Calendar.getInstance();
		Calendar end = Calendar.getInstance();
		start.set(Calendar.DATE, start.get(Calendar.DATE)-1);
		TaskieTask event = new TaskieTask("study", start, end);
		TaskieStorage.addTask(event);
		assertEquals(TaskieStorage.displayEventTasks().size(), 1);
		assertEquals(TaskieStorage.displayAllTasks().size(), 1);
	}
	@Test
	public void testDelete() {
		try {
			TaskieStorage.load("unit/");
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		TaskieTask float1 = new TaskieTask("finish tutorial");
		TaskieStorage.addTask(float1);
		TaskieStorage.deleteTask(0);
		assertEquals(TaskieStorage.displayAllTasks().size(), 0);
	}

}
