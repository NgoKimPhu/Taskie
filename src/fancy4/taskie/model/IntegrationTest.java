package fancy4.taskie.model;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Date;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class IntegrationTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testAddFloat() throws UnrecognisedCommandException {
		TaskieLogic.logic().execute("reset");
		try {
			TaskieStorage.load("unit/");
			TaskieStorage.deleteAll();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		TaskieLogic.logic().execute("finish tutorial");
		assertEquals(TaskieStorage.displayFloatTask().size(), 1);
	}
	
	@Test
	public void testAddFloatAgain() throws UnrecognisedCommandException {
		try {
			TaskieStorage.load("unit/");
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		TaskieLogic.logic().execute("finish ps");
		assertEquals(TaskieStorage.displayFloatTask().size(), 2);
	}
	
	@Test
	public void testAddDeadline() throws UnrecognisedCommandException {
		try {
			TaskieStorage.load("unit/");
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		TaskieLogic.logic().execute("finish hw by tmr 9pm");
		assertEquals(TaskieStorage.displayEventDeadline().size(), 1);
	}
	
	@Test
	public void testDelete() throws UnrecognisedCommandException {
		try {
			TaskieStorage.load("unit/");
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		TaskieLogic.logic().execute("del 1");
		assertEquals(TaskieStorage.displayFloatTask().size(), 1);
	}
	
	@Test
	// This is the boundary case search empty string, should display all result
	public void testSearchFloat() throws UnrecognisedCommandException {
		try {
			TaskieStorage.load("unit/");
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		LogicOutput out = TaskieLogic.logic().execute("view");
		assertEquals(0, out.getMain().size());
		assertEquals(0, out.getAll().size());
	}

}
