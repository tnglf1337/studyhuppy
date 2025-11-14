package com.studyhub.track.adapter.db.session;

import com.studyhub.track.domain.model.session.SessionRepository;
import com.studyhub.track.domain.model.session.Session;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
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
		Long existingDbKey =
				sessionDao.findByFachId(session.getFachId()).map(SessionDto::id).orElse(null);
		SessionDto sessionDto = toDto(session, existingDbKey);
		SessionDto saved = sessionDao.save(sessionDto);
		return toEntity(saved);
	}

	@Override
	public List<Session> findAllByUsername(String username) {
		List<SessionDto> sessions = sessionDao.findAllByUsername(username);
		return sessions.stream().map(SessionMapper::toEntity).toList();
	}

	@Override
	public long deleteByFachId(UUID fachId) {
		return sessionDao.deleteByFachId(fachId);
	}

	@Override
	public Session findSessionByFachId(UUID fachId) {
		Optional<SessionDto> dto = sessionDao.findByFachId(fachId);
		return dto.map(SessionMapper::toEntity).orElse(null);
	}
}
