package com.studyhub.track.adapter.db.session;

import com.studyhub.track.application.service.SessionRepository;
import com.studyhub.track.domain.model.session.Session;
import org.springframework.stereotype.Repository;

@Repository
public class SessionRepositoryImpl implements SessionRepository {

	private final SessionDao sessionDao;

	public SessionRepositoryImpl(SessionDao sessionDao) {
		this.sessionDao = sessionDao;
	}

	@Override
	public void save(Session session) {

	}
}
