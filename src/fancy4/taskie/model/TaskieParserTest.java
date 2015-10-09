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
		TaskieParser.parse("add test monday");
		TaskieParser.parse("buy 5 apples");
		TaskieParser.parse("add test from today till mon");
		TaskieParser.parse("buy pizza 7pm today");
		TaskieParser.parse("tmr 2 to 3"); // should I recognize this as time?
		TaskieParser.parse("class from 2 to 3");
		TaskieParser.parse("conference 7 today till 9pm tmr");
		TaskieParser.parse("conference tmr from 5 to 6 pm");
		TaskieParser.parse("conference 7.15pm till 9.30 tmr night at th");
		TaskieParser.parse("conference today 7.15pm till 9.30 tmr night at th");
		
	}

}
