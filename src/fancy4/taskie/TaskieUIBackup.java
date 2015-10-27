package fancy4.taskie;
/**
 * @author Qin_ShiHuang 
 *
 */
import java.util.*;

import fancy4.taskie.model.TaskieLogic;
import fancy4.taskie.model.UnrecognisedCommandException;

public class TaskieUIBackup {
	
	private static Scanner sc;

	public static void main(String[] args) {
		initialise();
		run();
	}
	
	public static void initialise() {
		TaskieLogic.logic().initialise();
		sc = new Scanner(System.in);
	}
	
	public static void run() {
		while(true) {
			String command = read();
			String[][] screen;
			try {
				screen = TaskieLogic.logic().execute(command);
				display(screen[0]);
				display(screen[1]);
			} catch (UnrecognisedCommandException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
	}
	
	public static String read() {
		return sc.nextLine();
	}
	
	public static void display(String[] screen) {
		for (String s : screen) {
			System.out.println(s);
		}
	}
	
	public static void display(String str) {
		System.out.println(str);
	}

}