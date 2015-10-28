package fancy4.taskie;
/**
 * @author Qin_ShiHuang 
 *
 */
import java.util.*;

import fancy4.taskie.model.LogicOutput;
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
		//while(true) {
			String command = "pizza";
			LogicOutput screen;
			try {
				screen = TaskieLogic.logic().execute(command);
				String output = screen.getMain().get(0).toString();
				//System.out.println(output);
				System.out.println(output.equals("1.   --    --   pizza"));
			} catch (UnrecognisedCommandException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		//}
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
