package fancy4.taskie.model;

import java.util.*;

public class TaskieLogicOutput {
	
	private static TaskieLogicOutput output;

	private String feedback;
	private ArrayList<TaskieTask> all;
	private ArrayList<TaskieTask> main;
	
	public static TaskieLogicOutput output() {
		if (output == null) {
			output = new TaskieLogicOutput();
		}
		return output;
	}
	
	protected TaskieLogicOutput() {
		feedback = new String();
		all = new ArrayList<TaskieTask>();
		main = new ArrayList<TaskieTask>();
	}
	
	public void setFeedback(String str) {
		this.feedback = str;
	}
	
	public void setAll(ArrayList<TaskieTask> list) {
		this.all = list;
	}
	
	public void setMain(ArrayList<TaskieTask> list) {
		this.main = list;
	}
	
	public String getFeedback() {
		return feedback;
	}
	
	public ArrayList<TaskieTask> getAll() {
		return all;
	}
	
	public ArrayList<TaskieTask> getMain() {
		return main;
	}

}
