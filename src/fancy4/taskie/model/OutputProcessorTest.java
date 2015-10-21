package fancy4.taskie.model;

import static org.junit.Assert.*;

import java.util.Arrays;

import org.junit.Test;

public class OutputProcessorTest {

	@Test
	
	public void test() throws UnrecognisedCommandException {
		String[][] executed0 = TaskieLogic.logic().execute("reset");
		String[][] executed1 = TaskieLogic.logic().execute("add finish homework");
		String[][] executed2 = TaskieLogic.logic().execute("delete 1");
		String[][] executed3 = TaskieLogic.logic().execute("add go home");
		String processedFeedback0 = ResponseProcessor.process(executed0);
		String processedFeedback1 = ResponseProcessor.process(executed1);
		String processedFeedback2 = ResponseProcessor.process(executed2);
		String processedFeedback3 = ResponseProcessor.process(executed3);
		assertEquals(processedFeedback0, "Restored to factory settings!");
		assertEquals(processedFeedback1, "\"finish homework\" is added!");
		assertEquals(processedFeedback2, "\"finish homework\" is deleted!");
		assertEquals(processedFeedback3, "\"go home\" is added!");
	}

}
