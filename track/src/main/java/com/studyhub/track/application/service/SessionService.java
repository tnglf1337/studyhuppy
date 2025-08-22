package com.studyhub.track.application.service;

import com.studyhub.track.domain.model.session.Session;
import org.springframework.stereotype.Service;

@Service
public class SessionService {

	private final SessionRepository sessionRepository;

	public SessionService(SessionRepository sessionRepository) {
		this.sessionRepository = sessionRepository;
	}

	public boolean save(Session session) {
		Session saved = sessionRepository.save(session);
		return saved != null;
	}
}
