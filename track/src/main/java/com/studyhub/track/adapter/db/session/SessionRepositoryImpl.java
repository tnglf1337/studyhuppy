package com.studyhub.track.adapter.db.session;

import com.studyhub.track.application.service.SessionRepository;
import com.studyhub.track.domain.model.session.Session;
import org.springframework.stereotype.Repository;
import static com.studyhub.track.adapter.db.session.SessionMapper.toDto;
import static com.studyhub.track.adapter.db.session.SessionMapper.toEntity;

@Repository
public class SessionRepositoryImpl implements SessionRepository {

	private final SessionDao sessionDao;

	public SessionRepositoryImpl(SessionDao sessionDao) {
		this.sessionDao = sessionDao;
	}

	@Override
	public Session save(Session session) {
		SessionDto sessionDto = toDto(session);
		SessionDto saved = sessionDao.save(sessionDto);
		return toEntity(saved);
	}
}
