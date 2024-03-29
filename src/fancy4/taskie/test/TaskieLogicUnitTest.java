package fancy4.taskie.test;

/**
 * 	@@author A0107360R
 * 
 */


import org.junit.Assert;
import org.junit.Test;

import fancy4.taskie.model.LogicOutput;
import fancy4.taskie.model.TaskieLogic;
import fancy4.taskie.model.UnrecognisedCommandException;

public class TaskieLogicUnitTest {
	
	private TaskieLogic logic;
	
	@Test
	public void testReset() throws UnrecognisedCommandException {
		logic = TaskieLogic.getInstance();
		LogicOutput output = logic.execute("reset");
		Assert.assertTrue(output.getAll().size() == 4);
		for (int i = 0; i < 4; i++) {
			Assert.assertTrue(output.getAll().get(i).isEmpty());
		}
	}
	
	@Test
	public void testAdd() throws UnrecognisedCommandException {
		logic = TaskieLogic.getInstance();
		logic.execute("reset");
		logic.execute("test1 0am");
		logic.execute("test2 tmr");
		logic.execute("test3");
		LogicOutput output = logic.execute("test4 today 23:57 to 23:58");
		
		String feedbackExpected = new String("\"test4\" is added");
		String ovd = new String("1.  test1-time   Mon 09-11 00:00");
		String tod = new String("2.  test4-time   Mon 09-11 23:57 ~ 23:58");
		String tmr = new String("3.  test2-time   Tue 10-11 23:59");
		String els = new String("4.  test3-time");
		
		String headline = new String("You have 2 tasks on 09, Nov, 2015.");
		String main1 = new String("1.  test1-time   Mon 09-11 00:00");
		String main2 = new String("2.  test4-time   Mon 09-11 23:57 ~ 23:58");
		// Test for the feedback of addition
		Assert.assertTrue(output.getFeedback().equals(feedbackExpected));
		// Test for the result of overdue list of allTasks
		Assert.assertTrue(output.getAll().get(0).size() == 1);
		Assert.assertTrue(output.getAll().get(0).get(0).trim().equals(ovd));
		// Test for the result of today list of allTasks
		Assert.assertTrue(output.getAll().get(1).size() == 1);
		Assert.assertTrue(output.getAll().get(1).get(0).trim().equals(tod));
		// Test for the result of tomorrow list of allTasks
		Assert.assertTrue(output.getAll().get(2).size() == 1);
		Assert.assertTrue(output.getAll().get(2).get(0).trim().equals(tmr));
		// Test for the result of else list of allTasks
		Assert.assertTrue(output.getAll().get(3).size() == 1);
		Assert.assertTrue(output.getAll().get(3).get(0).trim().equals(els));
		// Test for the result of mainTasks
		Assert.assertTrue(output.getMain().size() == 3);
		Assert.assertTrue(output.getMain().get(0).trim().equals(headline));
		Assert.assertTrue(output.getMain().get(1).trim().equals(main1));
		Assert.assertTrue(output.getMain().get(2).trim().equals(main2));
	}
	
	@Test
	public void testDelete() throws UnrecognisedCommandException {
		logic = TaskieLogic.getInstance();
		logic.execute("reset");
		logic.execute("test1 0am");
		logic.execute("test2 tmr");
		logic.execute("test3");
		logic.execute("test4 today 23:57 to 23:58");
		LogicOutput output = logic.execute("del r2");
		
		String feedbackExpected = new String("\"test4\" is deleted");
		String headline = new String("You have one task on 09, Nov, 2015.");
		String main1 = new String("1.  test1-time   Mon 09-11 00:00");
		// Test for the feedback of deletion
		Assert.assertTrue(output.getFeedback().equals(feedbackExpected));
		// Test for the result of allTasks after deletion
		Assert.assertTrue(output.getAll().get(1).size() == 0);
		// Test for the result of mainTasks after deletion
		Assert.assertTrue(output.getMain().size() == 2);
		Assert.assertTrue(output.getMain().get(0).trim().equals(headline));
		Assert.assertTrue(output.getMain().get(1).trim().equals(main1));
	}
	
	@Test
	public void testUpdate() throws UnrecognisedCommandException {
		logic = TaskieLogic.getInstance();
		logic.execute("reset");
		logic.execute("test1 11pm");
		
		String titleUpdated = new String("1.  test2-time   Mon 09-11 23:00");
		String timeUpdated = new String("1.  test2-time   Tue 10-11 08:00");
		
		LogicOutput output1 = logic.execute("update r1 test2");
		Assert.assertTrue(output1.getAll().get(1).get(0).trim().equals(titleUpdated));
		
		LogicOutput output2 = logic.execute("update r1 tmr 8am");
		Assert.assertTrue(output2.getAll().get(2).get(0).trim().equals(timeUpdated));
	}
	
	@Test
	public void testMarkdone() throws UnrecognisedCommandException {
		logic = TaskieLogic.getInstance();
		logic.execute("reset");
		logic.execute("test1");
		logic.execute("test2");
		logic.execute("markdone r1");
		LogicOutput output = logic.execute("markdone r1");
		
		String feedbackExpected = new String("\"test2\" is marked done");
		String headline = new String("There are 2 completed tasks.");
		String main = new String("1.  test1-time       Completed");
		
		Assert.assertTrue(output.getFeedback().equals(feedbackExpected));
		Assert.assertTrue(output.getMain().get(0).equals(headline));
		Assert.assertTrue(output.getMain().get(1).equals(main));
		Assert.assertTrue(output.getAll().get(3).isEmpty());
	}
	
	@Test
	public void testSearch() throws UnrecognisedCommandException {
		logic = TaskieLogic.getInstance();
		logic.execute("reset");
		logic.execute("test1");
		logic.execute("test2 28 Nov");

		String headline1 = new String("There are 2 tasks found.");
		String headline2 = new String("There is one task found.");

		LogicOutput output1 = logic.execute("search test");
		Assert.assertTrue(output1.getMain().get(0).equals(headline1));
		Assert.assertTrue(output1.getMain().size() == 3);
		LogicOutput output2 = logic.execute("search 28 Nov");
		Assert.assertTrue(output2.getMain().get(0).equals(headline2));
		Assert.assertTrue(output2.getMain().size() == 2);
	}
	
	@Test
	public void testUndo() throws Exception {
		logic = TaskieLogic.getInstance();
		logic.execute("reset");
		logic.execute("pizza");
		LogicOutput output = logic.execute("undo");
		
		String feedbackExpected = new String("\"pizza\" is deleted");
		String headline = new String("There is no task. Feed me some, or take a nap.");
		
		Assert.assertTrue(output.getFeedback().equals(feedbackExpected));
		Assert.assertTrue(output.getMain().get(0).equals(headline));
		Assert.assertTrue(output.getMain().size() == 1);
		Assert.assertTrue(output.getAll().get(3).isEmpty());
	}
	
	@Test
	public void testRedo() throws Exception {
		logic = TaskieLogic.getInstance();
		logic.execute("reset");
		logic.execute("pizza");
		logic.execute("undo");
		LogicOutput output = logic.execute("redo");
		
		String feedbackExpected = new String("\"pizza\" is added");
		String headline = new String("You have one floating task.");
		
		Assert.assertTrue(output.getFeedback().equals(feedbackExpected));
		Assert.assertTrue(output.getMain().get(0).equals(headline));
		Assert.assertTrue(output.getMain().size() == 2);
		Assert.assertTrue(output.getAll().get(3).size() == 1);
	}

}