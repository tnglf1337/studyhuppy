package com.studyhub.track.adapter.web.controller.request.dto;

import java.util.UUID;

public record AddSecondsRequest(
        UUID modulId,
        int secondsToAdd
) {
}
