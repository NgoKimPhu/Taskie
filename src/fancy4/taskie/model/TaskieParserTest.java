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
	public void test() {
		TaskieAction action = TaskieParser.parse("add test monday");
		System.err.println("^ " + action.getTask().getTitle());
		action = TaskieParser.parse("add test from today till mon");
		System.err.println("^ " + action.getTask().getTitle());
		action = TaskieParser.parse("buy pizza 7pm today");
		System.err.println("^ " + action.getTask().getTitle());
		action = TaskieParser.parse("class from 2 to 3");
		System.err.println("^ " + action.getTask().getTitle());
		action = TaskieParser.parse("conference 7 today till 9pm tmr");
		System.err.println("^ " + action.getTask().getTitle());
		action = TaskieParser.parse("conference tmr from 5 to 6 pm"); //TODO
		System.err.println("^ " + action.getTask().getTitle());
		action = TaskieParser.parse("conference 7.15pm till 9.30 tmr night at th");
		System.err.println("^ " + action.getTask().getTitle());
		action = TaskieParser.parse("conference today 7.15pm till 9.30 tmr night at th");
		System.err.println("^ " + action.getTask().getTitle());
		
	}

}
