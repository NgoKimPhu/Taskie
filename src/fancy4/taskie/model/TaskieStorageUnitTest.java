package fancy4.taskie.model;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Date;

import org.junit.Test;

public class TaskieStorageUnitTest {

	@Test	
	public void testAddFloat() {
		try {
			TaskieStorage.load("unit/");
			TaskieStorage.deleteAll();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		TaskieTask float1 = new TaskieTask("finish tutorial");
		TaskieStorage.addTask(float1);
		assertEquals(TaskieStorage.displayFloatTask().size(), 1);
	}
	
	@Test
	public void testAddFloatAgain() {
		try {
			TaskieStorage.load("unit/");
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		TaskieTask float2 = new TaskieTask("finish tutorial");
		TaskieStorage.addTask(float2);
		assertEquals(TaskieStorage.displayFloatTask().size(), 2);
	}
	
	@Test
	public void testAddDeadline() {
		try {
			TaskieStorage.load("unit/");
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		TaskieTask deadline = new TaskieTask("study", new Date());
		TaskieStorage.addTask(deadline);
		assertEquals(TaskieStorage.displayEventDeadline().size(), 1);
	}
	
	@Test
	public void testDeleteFloat() {
		try {
			TaskieStorage.load("unit/");
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		TaskieStorage.deleteTask(1, TaskieEnum.TaskType.FLOAT);
		assertEquals(TaskieStorage.displayFloatTask().size(), 1);
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
		ArrayList<IndexTaskPair> fs = TaskieStorage.searchTask(keyWords, TaskieEnum.TaskType.FLOAT);
		assertEquals(fs.size(), 1);
	}
	@Test
	// This is the boundary case for upper case partition 
	public void testSearchDeadline() {
		try {
			TaskieStorage.load("unit/");
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		ArrayList<String> keyWords = new ArrayList<String>();
		keyWords.add("");
		ArrayList<IndexTaskPair> ds = TaskieStorage.searchTask(keyWords, TaskieEnum.TaskType.DEADLINE);
		assertEquals(ds.size(), 0);
	}
	
	

}
