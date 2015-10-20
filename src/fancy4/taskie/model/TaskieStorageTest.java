package fancy4.taskie.model;

import java.util.*;
import org.junit.*;
public class TaskieStorageTest {

	public static void main(String[] args){
		// TODO Auto-generated method stub
		try {
			TaskieStorage.load("test/");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println(e.getMessage());
		}
		TaskieStorage.deleteAll();
		TaskieTask float1 = new TaskieTask("finish tutorial");
		TaskieTask float2 = new TaskieTask("go to dinner with misaki");
		Date deadline = getDate(2015, 10, 6);
		TaskieTask deadline1 = new TaskieTask("finish assignment", deadline);
		Date start = getDate(2015, 10, 5, 14, 0);
		Date end = getDate(2015, 10, 5, 15, 0);
		TaskieTask event1 = new TaskieTask("meeting", start, end);
		TaskieStorage.addTask(float1);
		TaskieStorage.addTask(float2);
		TaskieStorage.addTask(deadline1);
		TaskieStorage.addTask(event1);
		ArrayList<String> keyWords = new ArrayList<String>();
		keyWords.add("");
		ArrayList<IndexTaskPair> fs = TaskieStorage.searchTask(keyWords, TaskieEnum.TaskType.FLOAT);
		ArrayList<IndexTaskPair> ds =  TaskieStorage.searchTask(keyWords, TaskieEnum.TaskType.DEADLINE);
		ArrayList<IndexTaskPair> es = TaskieStorage.searchTask(keyWords, TaskieEnum.TaskType.EVENT);
		System.out.println("float:");
		for(IndexTaskPair f: fs){
			System.out.println(f.getTask().toString());
		}
		System.out.println("event");
		for(IndexTaskPair e: es){
			System.out.println(e.getTask().toString());
		}
		System.out.println("deadline");
		for(IndexTaskPair d: ds){
			System.out.println(d.getTask().toString());
		}
		
		//TaskieStorage.deleteTask(2, TaskieEnum.TaskType.FLOAT);
		//TaskieStorage.deleteTask(2, TaskieEnum.TaskType.EVENT);

	}
	public static Date getDate(int year, int month, int day){
		Calendar cal = Calendar.getInstance();
		cal.clear();
		cal.set(year, month-1, day);
		Date date = cal.getTime();
		return date;
	}
	public static Date getDate(int year, int month, int day, int hour, int min){
		Calendar cal = Calendar.getInstance();
		cal.clear();
		cal.set(year, month-1, day, hour, min);
		Date date = cal.getTime();
		return date;
	}

}
