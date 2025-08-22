package com.studyhub.track.application.service;

import com.studyhub.track.domain.model.session.Session;

import java.util.List;

public interface SessionRepository {
	Session save(Session session);
	List<Session> findAllByUsername(String username);
}
