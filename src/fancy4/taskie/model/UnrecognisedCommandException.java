package fancy4.taskie.model;

/**
 * 
 * @@author A0107360R
 *
 */

public class UnrecognisedCommandException extends Exception {

	private static final long serialVersionUID = 1L;

	public UnrecognisedCommandException(String message) {
		super(message);
	}

}