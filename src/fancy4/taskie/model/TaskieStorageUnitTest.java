package fancy4.taskie.model;

import static org.junit.Assert.*;
import java.util.*;
import org.junit.Test;

public class TaskieStorageUnitTest {

	@Test	
	// add a float task partition 
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
	}
	
	@Test
	public void testAddFloatAgain() {
		try {
			TaskieStorage.load("unit/");
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		TaskieTask float2 = new TaskieTask("finish tutorial again");
		TaskieStorage.addTask(float2);
		assertEquals(TaskieStorage.displayFloatTasks().size(), 2);
	}
	
	@Test
	// add a deadline task partition
	public void testAddDeadline() {
		try {
			TaskieStorage.load("unit/");
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		TaskieTask deadline = new TaskieTask("study", Calendar.getInstance());
		TaskieStorage.addTask(deadline);
		assertEquals(TaskieStorage.displayDeadlineTasks().size(), 1);
	}
	
	@Test
	public void testDelete() {
		try {
			TaskieStorage.load("unit/");
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		TaskieStorage.deleteTask(0);
		assertEquals(TaskieStorage.displayAllTasks().size(), 1);
	}
	
	@Test
	// This is the boundary case search empty string, should display all result
	public void testSearchFloat() {
		try {
			TaskieStorage.load("unit/");
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		ArrayList<String> keyWords = new ArrayList<String>();
		keyWords.add("");
		ArrayList<IndexTaskPair> fs = TaskieStorage.searchTask(keyWords);
		assertEquals(fs.size(), 1);
	}
}
