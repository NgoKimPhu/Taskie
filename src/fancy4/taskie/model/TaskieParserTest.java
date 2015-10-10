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
		TaskieParser.parse("add test monday");
		TaskieParser.parse("buy 5 apples");
		TaskieParser.parse("i want to float");
		TaskieParser.parse("buy 5 apples -float");
		TaskieParser.parse("add test from today till mon");
		TaskieParser.parse("reflection 2 due 23.59 tmr");
		TaskieParser.parse("reflection 2 deadline 23h59 tmr");
		TaskieParser.parse("reflection 2 -deadline 23:59 tmr");
		TaskieParser.parse("buy pizza 7pm today");
		TaskieParser.parse("lab oct 15 from 14h30 to 16");
		TaskieParser.parse("class from 2 to 3");
		TaskieParser.parse("conference 7 today till 9pm tomorrow");
		TaskieParser.parse("conference event 7 today till 9pm tmr");
		TaskieParser.parse("+ dinner with myself tmr from 5 to 6 pm");
		TaskieParser.parse("dance 7.15pm till 9.30 tmr night at th");
		TaskieParser.parse("party today 7.15pm till 9.30 tmr night at th");
	}
	
	@Test
	public void testDelete() {
		TaskieParser.parse("- 1");
		TaskieParser.parse("- d1");
		TaskieParser.parse("- d 1");
		TaskieParser.parse("- f1");
		TaskieParser.parse("- -f1");
		TaskieParser.parse("- /f 1");
	}

}
