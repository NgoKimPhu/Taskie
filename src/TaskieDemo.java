/** 
 * This is a interactive demo of Taskie.
 * @author Qin Xueying
 */
import java.util.*;

public class TaskieDemo {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Scanner sc = new Scanner(System.in);
		System.out.println("Welcome to Taskie. ");
		System.out.println("New user? (YES/NO)");
		String op = sc.next();
		if(op.equalsIgnoreCase("YES")){
			System.out.println("Please enter a user name: ");
			String username = sc.nextLine();
			System.out.println("Please enter your data storage path: \n(If you do not want to cutomize your data storage path,\njust press enter. you data will be saved with the same path of Taskie.)");
			String path = sc.nextLine();
			System.out.println();
		}
		else if(op.equalsIgnoreCase("NO")){
			System.out.println("Please enter your user name: ");
			String username = sc.nextLine();
		}
		
		

	}

}
