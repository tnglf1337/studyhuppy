package com.studyhub.track.application.service;

import com.studyhub.track.domain.model.session.Session;

public interface SessionRepository {
	Session save(Session session);
}
