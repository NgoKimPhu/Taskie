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
		TaskieParser parser = TaskieParser.getInstance();
		// null and empty
		action = parser.parse(null);
		assertEquals(null, action.getTask());
		action = parser.parse("");
		assertEquals(null, action.getTask());
		
		// no time partition
		action = parser.parse("anything");
		assertEquals("anything", action.getTask().getTitle());
		action = parser.parse("buy 5 apples");
		assertEquals("buy 5 apples", action.getTask().getTitle());
		action = parser.parse("+ i want to float");
		assertEquals("i want to float", action.getTask().getTitle());
		
		// force time partition
		action = parser.parse("buy 5 apples -float");
		assertEquals("buy 5 apples", action.getTask().getTitle());
		
		// date deadline partition
		action = parser.parse("add test monday");
		assertEquals("test", action.getTask().getTitle());
		
		// time and date deadline partition
		action = parser.parse("buy pizza 7pm today");
		assertEquals("buy pizza", action.getTask().getTitle());
		
		// date range partition
		action = parser.parse("add test from today till mon");
		assertEquals("test", action.getTask().getTitle());
		
		// time range, specific date partition
		action = parser.parse("reflection 2 due 23.59 tmr");
		assertEquals("reflection 2", action.getTask().getTitle());
		
		action = parser.parse("reflection 2 deadline 23h59 tmr");
		assertEquals("reflection 2", action.getTask().getTitle());
		action = parser.parse("reflection 2 -deadline 23:59 tmr");
		assertEquals("reflection 2", action.getTask().getTitle());
		
		action = parser.parse("lab oct 15 from 14h30 to 16");
		assertEquals("lab", action.getTask().getTitle());
		action = parser.parse("class from 2 to 3");
		assertEquals("class", action.getTask().getTitle());
		action = parser.parse("conference 7 today till 9pm tomorrow");
		assertEquals("conference", action.getTask().getTitle());
		action = parser.parse("+ dinner with myself tmr from 5 to 6 pm");
		assertEquals("dinner with myself", action.getTask().getTitle());
		
		// date and time range partition
		action = parser.parse("conference event 7 today till 9pm tmr");
		assertEquals("conference", action.getTask().getTitle());
		
		// date and time in the middle
		action = parser.parse("dance 7.15pm till 9.30 tmr night at th");
		assertEquals("dance at th", action.getTask().getTitle());
		action = parser.parse("party today 7.15pm till 9.30 tmr night at th");
		assertEquals("party at th", action.getTask().getTitle());
	}
	
	@Test
	public void testDelete() {
		TaskieAction action;
		TaskieParser parser = TaskieParser.getInstance();
		
		action = parser.parse("- 1");
		assertAction(action, TaskieEnum.Actions.DELETE, "left", 1);
		action = parser.parse("- l2");
		assertAction(action, TaskieEnum.Actions.DELETE, "left", 2);
		action = parser.parse("- /left 0");
		assertAction(action, TaskieEnum.Actions.DELETE, "left", 0);
		action = parser.parse("- -r 1");
		assertAction(action, TaskieEnum.Actions.DELETE, "right", 1);
		action = parser.parse("- r 9");
		assertAction(action, TaskieEnum.Actions.DELETE, "right", 9);
		action = parser.parse("- right 1");
		assertAction(action, TaskieEnum.Actions.DELETE, "right", 1);
	}

	private void assertAction(TaskieAction action, TaskieEnum.Actions type, String scr,
			int index) {
		assertEquals(type, action.getType());
		assertEquals(scr, action.getScreen());
		assertEquals(index, action.getIndex());
	}

}
