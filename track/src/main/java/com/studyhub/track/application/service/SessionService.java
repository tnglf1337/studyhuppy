package com.studyhub.track.application.service;

import com.studyhub.track.domain.model.session.Session;
import org.springframework.stereotype.Service;

@Service
public class SessionService {

	private final SessionRepository sessionRepository;

	public SessionService(SessionRepository sessionRepository) {
		this.sessionRepository = sessionRepository;
	}

	public void save(Session session) {
		// implement db connection and save logic here
		sessionRepository.save(session);
	}

}
