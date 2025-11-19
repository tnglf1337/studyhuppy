package com.studyhub.track.adapter.web.controller.request.dto;

import java.time.LocalTime;
import java.util.UUID;

/**
 * The request dto to add seconds to a module's secondsLearned.
 * @param modulId The id of the module
 * @param time The time to add
 */
public record AddSecondsRequest(
        UUID modulId,
        LocalTime time
) {
	/**
	 * Converts the LocalTime to seconds.
	 * @return The total seconds represented by the LocalTime
	 */
	public Integer localTimeToSeconds() {
		return time.getHour() * 3600 + time.getMinute() * 60;
	}
}
