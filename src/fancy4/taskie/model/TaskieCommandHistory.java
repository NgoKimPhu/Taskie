package fancy4.taskie.model;

import java.util.ArrayList;

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
	
	public String getCommand() {
		return history.get(pointer);
	}
	
	public boolean isEmpty() {
		return pointer == 0;
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
}
