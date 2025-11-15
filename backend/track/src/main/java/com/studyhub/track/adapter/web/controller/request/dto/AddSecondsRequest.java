package com.studyhub.track.adapter.web.controller.request.dto;

import java.time.LocalTime;
import java.util.UUID;

public record AddSecondsRequest(
        UUID modulId,
        LocalTime time
) {
	public Integer localTimeToSeconds() {
		return time.getHour() * 3600 + time.getMinute() * 60;
	}
}
