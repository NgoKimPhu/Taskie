package fancy4.taskie.model;

import static org.junit.Assert.*;

import org.junit.Test;

public class InputVerifierTest {

	@Test
	
	public void test() throws UnrecognisedCommandException {
		/*executed0 2 4 6 8 belongs in an Equivalence partition - correct input
		 * executed1  3 5 7 9, each belongs in an Equivalence partition - wrong letter case
		 * executed 10 belongs in an Equivalence partition - null input
		 * executed 11 and afterwards belong in an Equivalence partition - other string
		 */
		boolean executed0 = InputVerify.process("reset");
		boolean executed1 = InputVerify.process("RESET");
		boolean executed2 = InputVerify.process("add finish homework");
		boolean executed3 = InputVerify.process("ADD finish homework");
		boolean executed4 = InputVerify.process("delete 1");
		boolean executed5 = InputVerify.process("DELETE 1");
		boolean executed6 = InputVerify.process("undo");
		boolean executed7 = InputVerify.process("UNDO");
		boolean executed8 = InputVerify.process("redo");
		boolean executed9 = InputVerify.process("REDO");
		boolean executed10 = InputVerify.process(null);
		boolean executed11 = InputVerify.process("randomstring");
		boolean executed12 = InputVerify.process("dassasfndomstring");
		boolean executed13 = InputVerify.process("r");

		assertTrue(executed0);
		assertEquals(executed1, false);
		assertEquals(executed2, true);
		assertEquals(executed3, false);
		assertEquals(executed4, true);
		assertEquals(executed5, false);
		assertEquals(executed6, true);
		assertEquals(executed7, false);
		assertEquals(executed8, true);
		assertEquals(executed9, false);
		assertEquals(executed10, false);
		assertEquals(executed11, false);
		assertEquals(executed12, false);
		assertEquals(executed13, false);
		
	}

}
