package com.studyhub.track.adapter.web.controller.request.dto;

import java.util.UUID;

/**
 * Request dto for fetching session rating statistics.
 *
 * @param sessionId The ID of the session.
 */
public record SessionBewertungStatistikRequest(
		UUID sessionId
) {
}
