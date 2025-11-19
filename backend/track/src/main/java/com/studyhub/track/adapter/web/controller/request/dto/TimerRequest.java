package com.studyhub.track.adapter.web.controller.request.dto;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

+/**
 * Request DTO for timer updates.
 *
 * @param modulId          The id of the module.
 * @param startDateMillis The start date in milliseconds as a string.
 */
public record TimerRequest(
		String modulId,
		String startDateMillis
) {

	/**
	 * Converts the start date in milliseconds to seconds elapsed since that time.
	 * @return The number of seconds that have elapsed since the start date.
	 */
	public int toSeconds() {
		long timestampMillis = Long.parseLong(startDateMillis);
		Instant instant = Instant.ofEpochMilli(timestampMillis);
		LocalDateTime clientTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
		LocalDateTime serverTime = LocalDateTime.now();
		Duration duration = Duration.between(clientTime, serverTime);
		return (int) (duration.toMillis() / 1000.0);
	}
}
