package fancy4.taskie.model;
import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TaskieParserTest {

	@Before
	public void setUp() throws Exception {
	}

	
	public void tearDown() throws Exception {
	}

	@Test
	public void test() {
		TaskieAction action = TaskieParser.parse("add test");
		System.err.println(action.getTask().getTitle());
	}

}
