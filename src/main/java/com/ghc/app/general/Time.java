package com.ghc.app.general;

/**
 * Simple time representation
 */
public class Time {
	public int days;
	public int hours;
	public int minutes;
	public int seconds;

	public Time() {
		days = 0;
		hours = 0;
		minutes = 0;
		seconds = 0;
	}

	public String toString() {
		return "";
	}

	public static Time convertHours(double hoursOld) {
		Time time = new Time();

		time.days = (int) (hoursOld / 24.0);
		time.hours = (int) (hoursOld - (24.0 * time.days));
		time.minutes = (int) ((hoursOld - (24.0 * time.days + time.hours)) * 60.0);
		time.seconds = (int) ((60.0 * (hoursOld - (24.0 * time.days + time.hours)) - (double) time.minutes) * 60.0);

		return time;
	}
}

