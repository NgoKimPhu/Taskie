package fancy4.taskie.test;

import static org.junit.Assert.*;
import org.junit.Test;
import fancy4.taskie.model.TaskieCommandHistory;

//@@author: A0130221H

/**
 * Junit test class for TaskieCommandHistory class.
 * Tests isEmpty, add, get methods, pointer behavior and exceptions.
 */
public class TaskieCommandHistoryTest {

	@Test
	public void testIsEmpty() {
		TaskieCommandHistory cmdHistory = new TaskieCommandHistory();
		assertTrue(cmdHistory.isEmpty());
		
		cmdHistory.addCommand("add 1");
		assertFalse(cmdHistory.isEmpty());
	}
	
	@Test
	public void testAdd() {
		TaskieCommandHistory cmdHistory = new TaskieCommandHistory();
		assertTrue(cmdHistory.isEmpty());
		
		//decrementPointer() needs to be used together with add.
		
		//test default getCommand().
		cmdHistory.addCommand("add 1");
		cmdHistory.decrementPointer();
		assertTrue(cmdHistory.getCommand().equals("add 1"));
		
		cmdHistory.addCommand("add 2");
		cmdHistory.decrementPointer();
		assertTrue(cmdHistory.getCommand().equals("add 2"));
		
		//test getCommand(int index)
		cmdHistory.addCommand("add 3");
		cmdHistory.decrementPointer();
		try {
			assertTrue(cmdHistory.getCommand(2).equals("add 3"));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	@Test
	public void testPointer() {
		TaskieCommandHistory cmdHistory = new TaskieCommandHistory();
		assertEquals(cmdHistory.getPointer(), 0);
		
		cmdHistory.addCommand("add 1");
		assertEquals(cmdHistory.getPointer(), 1);
		
		cmdHistory.addCommand("add 2");
		assertEquals(cmdHistory.getPointer(), 2);
		
		cmdHistory.decrementPointer();
		assertEquals(cmdHistory.getPointer(), 1);
		
		cmdHistory.incrementPointer();
		assertEquals(cmdHistory.getPointer(), 2);
		
		cmdHistory.incrementPointer();
		assertEquals(cmdHistory.getPointer(), 2);
		
		cmdHistory.setPointer(10);
		assertEquals(cmdHistory.getPointer(), 2);
		
		cmdHistory.setPointer(-1);
		assertEquals(cmdHistory.getPointer(), 2);
		
		cmdHistory.setPointer(1);
		assertEquals(cmdHistory.getPointer(), 1);
		
		cmdHistory.setPointer(0);
		assertEquals(cmdHistory.getPointer(), 0);
	}
	
	@Test
	public void testGet() {
		TaskieCommandHistory cmdHistory = new TaskieCommandHistory();
		
		cmdHistory.addCommand("add 1");
		cmdHistory.decrementPointer();
		assertTrue(cmdHistory.getCommand().equals("add 1"));
		
		cmdHistory.addCommand("add 2");
		cmdHistory.decrementPointer();
		assertTrue(cmdHistory.getCommand().equals("add 2"));
		
		cmdHistory.addCommand("add 3");
		cmdHistory.decrementPointer();
		assertTrue(cmdHistory.getCommand().equals("add 3"));
		
		cmdHistory.addCommand("add 4");
		cmdHistory.decrementPointer();
		assertTrue(cmdHistory.getCommand().equals("add 4"));
		
		cmdHistory.decrementPointer();
		assertTrue(cmdHistory.getCommand().equals("add 3"));
		
		cmdHistory.decrementPointer();
		assertTrue(cmdHistory.getCommand().equals("add 2"));
		
		cmdHistory.decrementPointer();
		assertTrue(cmdHistory.getCommand().equals("add 1"));
		
		cmdHistory.decrementPointer();
		assertTrue(cmdHistory.getCommand().equals("add 1"));
		
		try {
			assertTrue(cmdHistory.getCommand(0).equals("add 1"));
			assertTrue(cmdHistory.getCommand(1).equals("add 2"));
			assertTrue(cmdHistory.getCommand(2).equals("add 3"));
			assertTrue(cmdHistory.getCommand(3).equals("add 4"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	@Test
	public void testException() {
		TaskieCommandHistory cmdHistory = new TaskieCommandHistory();
		try {
			cmdHistory.getCommand(-1);
		} catch (Exception e) {
			assertEquals(e.getMessage(), "IndexOutOfBound");
		}
		
		try {
			cmdHistory.getCommand(1);
		} catch (Exception e) {
			assertEquals(e.getMessage(), "IndexOutOfBound");
		}
		
		try {
			cmdHistory.getCommand(0);
		} catch (Exception e) {
			assertNotEquals(e.getMessage(), "IndexOutOfBound");
		}
		
		cmdHistory.addCommand("add 1");
		try {
			cmdHistory.getCommand(1);
		} catch (Exception e) {
			assertNotEquals(e.getMessage(), "IndexOutOfBound");
		}
		
	}

}
