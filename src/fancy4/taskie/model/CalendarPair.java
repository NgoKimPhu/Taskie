// @@author A0119390E
package fancy4.taskie.model;

import java.text.SimpleDateFormat;
import java.util.*;

class CalendarPair {
	private Calendar start;
	private Calendar end;
	private static final SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm");

	public CalendarPair(Calendar start, Calendar end) {
		this.start = (Calendar) start.clone();
		this.end = (Calendar) end.clone();
	}

	public Calendar getStart() {
		return this.start;
	}

	public Calendar getEnd() {
		return this.end;
	}

	public void setStart(Calendar start) {
		this.start = start;
	}

	public void setEnd(Calendar end) {
		this.end = end;
	}

	public String toString() {
		String pair;
		pair = "(" + sdf.format(this.start.getTime()) + " - " + sdf.format(this.end.getTime()) + ")";
		return pair;
	}
}