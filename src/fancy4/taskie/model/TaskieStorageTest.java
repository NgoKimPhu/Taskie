
package fancy4.taskie.model;

import java.util.*;
public class TaskieStorageTest {

	public static void main(String[] args) throws Exception{
		try {
			TaskieStorage.load("/Users/misakiyuki/Desktop");
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		TaskieStorage.deleteAll();
		TaskieTask float1 = new TaskieTask("finish tutorial");
		TaskieTask float2 = new TaskieTask("go to dinner with misaki");
		Calendar deadline = getDate(2015, 10, 5);
		TaskieTask deadline1 = new TaskieTask("finish assignment", deadline);
		Calendar start = getDate(2015, 11, 5, 14, 0);
		Calendar end = getDate(2015, 11, 5, 15, 0);
		TaskieTask event1 = new TaskieTask("meeting", start, end);
		TaskieTask event2 = new TaskieTask("meeting2", start, end);
		TaskieStorage.addTask(float1);
		TaskieStorage.addTask(float2);
		TaskieStorage.addTask(deadline1);
		TaskieStorage.addTask(event1);
		TaskieStorage.addTask(event2);
		
		ArrayList<String> keyWords = new ArrayList<String>();
		keyWords.add("finish");
		ArrayList<IndexTaskPair> fs = TaskieStorage.searchTask(keyWords);
		System.out.println("all:");
		for(IndexTaskPair f: fs){
			System.out.println(f.getTask().toString());
		}
		Calendar startKey = getDate(2015, 10, 5, 0, 0);
		Calendar endKey = getDate(2015, 10, 5,16,0);
		ArrayList<IndexTaskPair> searchStart = TaskieStorage.searchTask(startKey, endKey);
		System.out.println("search date:");
		for(IndexTaskPair f: searchStart){
			System.out.println(f.getTask().toString());
		}
		//TaskieStorage.deleteTask(2, TaskieEnum.TaskType.FLOAT);
		//TaskieStorage.deleteTask(2, TaskieEnum.TaskType.EVENT);
		ArrayList<CalendarPair> freeSlots = TaskieStorage.getFreeSlots();
		System.out.println("Free Slot:");
		for(CalendarPair slot: freeSlots){
			System.out.println(slot);
		}

	}
	public static Calendar getDate(int year, int month, int day){
		Calendar cal = Calendar.getInstance();
		cal.clear();
		cal.set(year, month-1, day);
		return cal;
	}
	public static Calendar getDate(int year, int month, int day, int hour, int min){
		Calendar cal = Calendar.getInstance();
		cal.clear();
		cal.set(year, month-1, day, hour, min);
		return cal;
	}

}
