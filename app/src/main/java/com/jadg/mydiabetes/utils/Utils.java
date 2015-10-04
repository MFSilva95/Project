package com.jadg.mydiabetes.utils;

import java.util.Date;
import java.util.GregorianCalendar;

public class Utils
{
	public static Date getDateFromTimestamp(long timeInMilliseconds)
	{
		GregorianCalendar calendar = new GregorianCalendar();
		calendar.setTimeInMillis(timeInMilliseconds);
		return calendar.getTime();
	}
}
