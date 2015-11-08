package fancy4.taskie.model;

/**
 *	@@author A0107360R
 */

import java.util.*;

public class LogicOutput {

	private String feedback;
	private ArrayList<ArrayList<String>> all;
	private ArrayList<String> main;
	
	public LogicOutput(String feedback, ArrayList<String> main, ArrayList<ArrayList<String>> all) {
		this.feedback = feedback;
		this.main = main;
		this.all = all;
	}
	
	public void setFeedback(String str) {
		this.feedback = str;
	}
	
	public void setAll(ArrayList<ArrayList<String>> list) {
		this.all = list;
	}
	
	public void setMain(ArrayList<String> list) {
		this.main = list;
	}
	
	public String getFeedback() {
		return feedback;
	}
	
	public ArrayList<ArrayList<String>> getAll() {
		return all;
	}
	
	public ArrayList<String> getMain() {
		return main;
	}

}
