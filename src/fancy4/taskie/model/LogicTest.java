package fancy4.taskie.model;

/**
 * 	@@author A0107360R
 * 
 */

import static org.junit.Assert.*;

import org.junit.Test;

public class LogicTest {
	
	private static LogicOutput test;

	@Test
	public void testAdd() throws Exception {
		TaskieLogic.logic().execute("reset");
		TaskieLogic.logic().execute("pizza");
		test = TaskieLogic.logic().execute("coffee");
		assertArrayEquals(new String[]{"1.   --    --   coffee", "2.   --    --   pizza"},
				test.getMain().toArray());
	}
	
	@Test
	public void testUndo() throws Exception {
		TaskieLogic.logic().execute("reset");
		TaskieLogic.logic().execute("pizza");
		TaskieLogic.logic().execute("coffee");
		
		test = TaskieLogic.logic().execute("undo");
		assertArrayEquals(new String[]{"1.   --    --   pizza"}, test.getMain().toArray());
		test = TaskieLogic.logic().execute("undo");
		assertArrayEquals(new String[0], test.getMain().toArray());
		// Boundary testing
		TaskieLogic.logic().execute("undo");
		test = TaskieLogic.logic().execute("undo");
		assertEquals("No more action to undo", test.getFeedback());
	}
	
	@Test
	public void testRedo() throws Exception {
		TaskieLogic.logic().execute("reset");
		TaskieLogic.logic().execute("pizza");
		TaskieLogic.logic().execute("coffee");
		
		test = TaskieLogic.logic().execute("redo");

		// Boundary testing
		test = TaskieLogic.logic().execute("redo");
		assertEquals("No more action to redo", test.getFeedback());
	}

}