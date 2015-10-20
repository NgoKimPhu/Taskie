package fancy4.taskie.model;

import static org.junit.Assert.*;

import java.util.Arrays;

import org.junit.Test;

public class LogicUnitTest {
	
	private static String[][] test;

	@Test
	public void testAdd() throws Exception {
		TaskieLogic.logic().execute("reset");
		TaskieLogic.logic().execute("pizza");
		test = TaskieLogic.logic().execute("coffee");
		assertTrue(Arrays.equals(test[1], new String[]{"1. coffee", "2. pizza"}));
	}
	
	@Test
	public void testUndo() throws Exception {
		test = TaskieLogic.logic().execute("undo");
		assertTrue(Arrays.equals(test[1], new String[]{"1. pizza"}));
		test = TaskieLogic.logic().execute("undo");
		assertTrue(Arrays.equals(test[1], new String[]{}));
		test = TaskieLogic.logic().execute("undo");
		assertTrue(Arrays.equals(test[0], new String[]{"No more action to undo."}));
		
		test = TaskieLogic.logic().execute("redo");
		assertTrue(Arrays.equals(test[1], new String[]{"1. pizza"}));
	}
	
	@Test
	public void testRedo() throws Exception {
		test = TaskieLogic.logic().execute("redo");
		assertTrue(Arrays.equals(test[0], new String[]{"No more action to redo."}));
	}

}
