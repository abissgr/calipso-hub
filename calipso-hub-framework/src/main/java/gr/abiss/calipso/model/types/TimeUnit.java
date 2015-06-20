package gr.abiss.calipso.model.types;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public enum TimeUnit {
	MILLISECOND,
	SECOND,
	MINUTE,
	HOUR,
	DAY,
	WEEK,
	MONTH,
	YEAR;
	
	private static Map<TimeUnit, Integer> toCalendarUnit = new HashMap<TimeUnit, Integer>();
	static{
		toCalendarUnit.put(YEAR, Calendar.YEAR);
		toCalendarUnit.put(MONTH, Calendar.MONTH);
		toCalendarUnit.put(DAY, Calendar.DATE);
	}
	
	public int toCalendarUnit(){
		return toCalendarUnit.get(this);
	}
}
