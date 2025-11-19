package com.studyhub.track.application.service;

import lombok.NoArgsConstructor;
import java.time.LocalTime;

@Deprecated(forRemoval = true)
@NoArgsConstructor
public class TimeConverter {
	public int timeToSeconds(String time) {

		if (!isValidTimeFormat(time)) throw new IllegalArgumentException("hh and mm out of range");

		String[] parts = time.split(":");

		int hours = Integer.parseInt(parts[0]);
		int minutes = Integer.parseInt(parts[1]);
		return (hours * 3600) + (minutes * 60);
	}

	public LocalTime getLocalTimeFromString(String time) {
		if (!isValidTimeFormat(time)) throw new IllegalArgumentException("hh and mm out of range");
		String[] times = time.split(":");
		int hour = Integer.parseInt(times[0]);
		int minute = Integer.parseInt(times[1]);

		return LocalTime.of(hour, minute);
	}

	public boolean isValidTimeFormat(String time) {
		if (time == null || time.isEmpty()) return false;
		String regex = "^(?:[01]\\d|2[0-3]):[0-5]\\d$";
		return time.matches(regex);
	}
}