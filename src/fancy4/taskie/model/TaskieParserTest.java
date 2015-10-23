package fancy4.taskie.model;
import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TaskieParserTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testAdd() {
		TaskieAction action;
		// null and empty
		action = TaskieParser.parse(null);
		assertEquals(null, action.getTask());
		action = TaskieParser.parse("");
		assertEquals(null, action.getTask());
		
		// no time partition
		action = TaskieParser.parse("anything");
		assertEquals("anything", action.getTask().getTitle());
		action = TaskieParser.parse("buy 5 apples");
		assertEquals("buy 5 apples", action.getTask().getTitle());
		action = TaskieParser.parse("+ i want to float");
		assertEquals("i want to float", action.getTask().getTitle());
		
		// force time partition
		action = TaskieParser.parse("buy 5 apples -float");
		assertEquals("buy 5 apples", action.getTask().getTitle());
		
		// date deadline partition
		action = TaskieParser.parse("add test monday");
		assertEquals("test", action.getTask().getTitle());
		
		// time and date deadline partition
		action = TaskieParser.parse("buy pizza 7pm today");
		assertEquals("buy pizza", action.getTask().getTitle());
		
		// date range partition
		action = TaskieParser.parse("add test from today till mon");
		assertEquals("test", action.getTask().getTitle());
		
		// time range, specific date partition
		action = TaskieParser.parse("reflection 2 due 23.59 tmr");
		assertEquals("reflection 2", action.getTask().getTitle());
		
		action = TaskieParser.parse("reflection 2 deadline 23h59 tmr");
		assertEquals("reflection 2", action.getTask().getTitle());
		action = TaskieParser.parse("reflection 2 -deadline 23:59 tmr");
		assertEquals("reflection 2", action.getTask().getTitle());
		
		action = TaskieParser.parse("lab oct 15 from 14h30 to 16");
		assertEquals("lab", action.getTask().getTitle());
		action = TaskieParser.parse("class from 2 to 3");
		assertEquals("class", action.getTask().getTitle());
		action = TaskieParser.parse("conference 7 today till 9pm tomorrow");
		assertEquals("conference", action.getTask().getTitle());
		action = TaskieParser.parse("+ dinner with myself tmr from 5 to 6 pm");
		assertEquals("dinner with myself", action.getTask().getTitle());
		
		// date and time range partition
		action = TaskieParser.parse("conference event 7 today till 9pm tmr");
		assertEquals("conference", action.getTask().getTitle());
		
		// date and time in the middle
		action = TaskieParser.parse("dance 7.15pm till 9.30 tmr night at th");
		assertEquals("dance at th", action.getTask().getTitle());
		action = TaskieParser.parse("party today 7.15pm till 9.30 tmr night at th");
		assertEquals("party at th", action.getTask().getTitle());
	}
	
	@Test
	public void testDelete() {
		TaskieAction action;
		action = TaskieParser.parse("- 1");
		assertAction(action, TaskieEnum.Actions.DELETE, null, 1);
		action = TaskieParser.parse("- d2");
		assertAction(action, TaskieEnum.Actions.DELETE, TaskieEnum.TaskType.DEADLINE, 2);
		action = TaskieParser.parse("- d 0");
		assertAction(action, TaskieEnum.Actions.DELETE, TaskieEnum.TaskType.DEADLINE, 0);
		action = TaskieParser.parse("- f1");
		assertAction(action, TaskieEnum.Actions.DELETE, TaskieEnum.TaskType.FLOAT, 1);
		action = TaskieParser.parse("- -f9");
		assertAction(action, TaskieEnum.Actions.DELETE, TaskieEnum.TaskType.FLOAT, 9);
		action = TaskieParser.parse("- /f 1");
		assertAction(action, TaskieEnum.Actions.DELETE, TaskieEnum.TaskType.FLOAT, 1);
	}

	private void assertAction(TaskieAction action, TaskieEnum.Actions type, TaskieEnum.TaskType taskType,
			int index) {
		assertEquals(type, action.getType());
		assertEquals(taskType, action.getTaskType());
		assertEquals(index, action.getIndex());
	}

}
