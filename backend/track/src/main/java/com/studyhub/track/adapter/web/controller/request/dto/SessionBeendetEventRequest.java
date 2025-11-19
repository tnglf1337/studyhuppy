package com.studyhub.track.adapter.web.controller.request.dto;

import com.studyhub.track.domain.model.session.SessionBeendetEvent;
import com.studyhub.track.domain.model.session.SessionBewertung;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Request dto when a learning session has ended.
 * @param sessionId The id of the learn session
 * @param bewertung The rating of the session
 * @param abgebrochen Whether the session was aborted
 */
public record SessionBeendetEventRequest(
		UUID sessionId,
		SessionBewertung bewertung,
		Boolean abgebrochen
) {
	/**
	 * Converts the request dto to a <code>SessionBeendetEvent</code> entity.
	 * @param username The username of the user
	 * @return The created <code>SessionBeendetEvent</code> entity
	 */
	public SessionBeendetEvent toEntity(String username) {
		UUID eventId = UUID.randomUUID();
		LocalDateTime beendetDatum = LocalDateTime.now();
		return new SessionBeendetEvent(eventId, sessionId, username, beendetDatum, bewertung, abgebrochen);
	}
}
