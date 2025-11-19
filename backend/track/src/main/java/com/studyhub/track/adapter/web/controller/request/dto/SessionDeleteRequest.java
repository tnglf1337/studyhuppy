package com.studyhub.track.adapter.web.controller.request.dto;

import java.util.UUID;

/**
 * Request dto for deleting a session.
 *
 * @param fachId The id of the session to be deleted.
 */
public record SessionDeleteRequest(
        UUID fachId
) {
}
