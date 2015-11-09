// @@author A0126586W
package fancy4.taskie.model;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

public class IntegrationTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		try {
			TaskieStorage.load("test");
			TaskieStorage.deleteAll();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testAddFloat() throws UnrecognisedCommandException {
		TaskieLogic.getInstance().execute("finish tutorial");
		assertEquals(1, TaskieStorage.displayFloatTasks().size());
	}
	
	@Test
	public void testAddFloatAgain() throws UnrecognisedCommandException {
		TaskieLogic.getInstance().execute("finish ps");
		assertEquals(2, TaskieStorage.displayFloatTasks().size());
	}
	
	@Test
	public void testAddDeadline() throws UnrecognisedCommandException {
		TaskieLogic.getInstance().execute("\"finish\" hw by tmr 9pm");
		assertEquals(1, TaskieStorage.displayDeadlineTasks().size());
	}
	
	@Test
	public void testDelete() throws UnrecognisedCommandException {
		TaskieLogic.getInstance().execute("del 1");
		assertEquals(1, TaskieStorage.displayFloatTasks().size());
		TaskieLogic.getInstance().execute("undo");
		TaskieLogic.getInstance().execute("- r2");
		assertEquals(1, TaskieStorage.displayFloatTasks().size());
	}
	
	@Test
	// This is the boundary case search empty string, should display all result
	public void testSearch() throws UnrecognisedCommandException {
		LogicOutput out = TaskieLogic.getInstance().execute("view");
		assertEquals(2, out.getMain().size() - 1);
		int allSize = 0;
		for (ArrayList<String> subAll : out.getAll()) {
			allSize += subAll.size();
		}
		assertEquals(2, allSize);
	}

}
