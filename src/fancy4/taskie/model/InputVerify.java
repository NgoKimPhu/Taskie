package fancy4.taskie.model;

public class InputVerify {
	public static boolean process(String input) {
		if (input == null || input.length() < 3) {
			return false;
		} if (input.length() >= 3) {
			if (input.substring(0, 3).equals("add")) {
				return true;
			}
		}  if (input.length() >= 4) {
			if (input.substring(0, 4).equals("undo") || input.substring(0, 4).equals("redo")) {
				return true;
			}
		}  if (input.length() >= 5) {
			if (input.substring(0, 5).equals("reset")) {
				return true;
			}
		}  if (input.length() >= 6) {
			if (input.substring(0, 6).equals("delete")) {
				return true;
			}
		} else { 
		return false; 
		}
		return false;
	}
	public static void main(String[] args) {
		System.out.println("undo".length());
		System.out.println(process("undo"));
	}

}
