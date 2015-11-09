package fancy4.taskie.model;

/**
 * 	@@author A0107360R
 * 
 */

import static org.junit.Assert.*;

import java.util.ArrayList;

import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class LogicTest {
	
	@Test
	public void testReset() throws UnrecognisedCommandException {
		TaskieLogic logic = TaskieLogic.getInstance();
		LogicOutput output = logic.execute("reset");
		Assert.assertTrue(output.getAll().size() == 4);
		for (int i = 0; i < 4; i++) {
			Assert.assertTrue(output.getAll().get(i).isEmpty());
		}
		
	}
	
	@Test
	public void testGetAll() throws UnrecognisedCommandException {
		TaskieLogic logic = TaskieLogic.getInstance();
		logic.execute("reset");
		logic.execute("test1 0:00");
		logic.execute("test2 tmr");
		logic.execute("test3");
		logic.execute("test4 today 23:58 to 23:59");
	}
	
	@Test
	public void testAdd() throws Exception {
		TaskieLogic logic = TaskieLogic.getInstance();
		logic.execute("reset");
		logic.execute("pizza");
		LogicOutput output = logic.execute("presentation today");
		String feedbackExpected = new String("\"presentation\" is added");
		String task1 = new String("1. presentation 23:59");
		ArrayList<String> allExpected = new ArrayList<String>();
		Assert.assertTrue(output.getFeedback().equals(feedbackExpected));
		Assert.assertTrue(output.getAll().size() == 2);
		Assert.assertTrue(output.getAll().equals(allExpected));
	}
	
	@Test
	public void testUndo() throws Exception {
		TaskieLogic.getInstance().execute("reset");
		TaskieLogic.getInstance().execute("pizza");
		TaskieLogic.getInstance().execute("coffee");
		Assert.assertTrue(true);
	}
	
	@Test
	public void testRedo() throws Exception {
		TaskieLogic.getInstance().execute("reset");
		TaskieLogic.getInstance().execute("pizza");
		TaskieLogic.getInstance().execute("coffee");
		Assert.assertTrue(true);
		
	}

}