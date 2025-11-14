package com.studyhub.track.domain.model.session;

import java.util.List;
import java.util.UUID;

public interface SessionRepository {
	Session save(Session session);
	List<Session> findAllByUsername(String username);
    long deleteByFachId(UUID fachId);
	Session findSessionByFachId(UUID fachId);
}
