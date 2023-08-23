package com.yazata.tar.util;

import java.util.Date;

public class DateTimeUtils {

	public static String printDifference(Date startDate) {
		Date endDate = new Date(System.currentTimeMillis());
		// milliseconds
		long different = endDate.getTime() - startDate.getTime();

		long secondsInMilli = 1000;
		long minutesInMilli = secondsInMilli * 60;
		long hoursInMilli = minutesInMilli * 60;
		long daysInMilli = hoursInMilli * 24;

		different = different % daysInMilli;

		long elapsedHours = different / hoursInMilli;
		different = different % hoursInMilli;

		long elapsedMinutes = different / minutesInMilli;
		different = different % minutesInMilli;

		long elapsedSeconds = different / secondsInMilli;

		return "Elapsed time: " + elapsedHours + ":" + elapsedMinutes + ":" + elapsedSeconds;

	}
}
