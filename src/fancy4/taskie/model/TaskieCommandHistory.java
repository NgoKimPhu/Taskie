package fancy4.taskie.model;

import java.util.ArrayList;
//@@author: A0130221H
/**
 * TaskieCommandHistory is a class to keep track of commands input in the GUI.
 * It enables the user to use UP and DOWN arrow key to browse through the command history
 * by keeping track of a pointer.
 */

public class TaskieCommandHistory {
	private ArrayList<String> history;
	private int pointer;
	
	public TaskieCommandHistory() {
		history = new ArrayList<>();
		pointer = 0;
	}
	
	public void incrementPointer() {
		pointer ++;
	}
	
	public void decrementPointer() {
		pointer --;
	}
	
	public void addCommand(String cmd) {
		history.add(cmd);
	}
	
	/*
	 * Retrieves the command that the current pointer points to.
	 */
	public String getCommand() {
		return history.get(pointer);
	}
	
	public String getCommand(int index) {
		return history.get(index);
	}
	
	public int getSize() {
		return history.size();
	}
	
	public int getPointer() {
		return pointer;
	}
	
	public void setPointer(int i) {
		pointer = i;
	}

	public boolean isEmpty() {
		return history.isEmpty();
	}
}
