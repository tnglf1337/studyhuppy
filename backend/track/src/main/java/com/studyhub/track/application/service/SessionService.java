package com.studyhub.track.application.service;

import com.studyhub.track.application.service.dto.SessionInfoDto;
import com.studyhub.track.application.service.dto.SessionRequest;
import com.studyhub.track.domain.model.session.Block;
import com.studyhub.track.domain.model.session.Session;
import com.studyhub.track.domain.model.session.SessionRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

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

	public List<Session> getSessionsByUsername(String username) {
		return sessionRepository.findAllByUsername(username);
	}

	public List<SessionInfoDto> getLernplanSessionDataOfUser(String username) {
		List<Session> sessions = sessionRepository.findAllByUsername(username);
		return sessions.stream()
				.map(session -> new SessionInfoDto(
						session.getFachId().toString(),
						session.getTitel(),
						session.getTotalZeit()
				))
				.toList();
	}

	public Session getSessionByFachId(UUID sessionId) {
		return sessionRepository.findSessionByFachId(sessionId);
	}

	public void deleteModuleFromBlocks(UUID modulId, String username) {
		List<Session> userSessions = sessionRepository.findAllByUsername(username);
		for(Session session : userSessions) {
			List<Block> filteredBlocks = session.getBlocks()
					.stream().filter(e -> !e.getModulId().equals(modulId))
					.toList();
			session.setBlocks(filteredBlocks);
			sessionRepository.save(session);
		}
	}

	public void saveEditedSession(SessionRequest sessionRequest) {
		Session oldSession = sessionRepository.findSessionByFachId(UUID.fromString(sessionRequest.fachId()));
		oldSession.setTitel(sessionRequest.titel());
		oldSession.setBeschreibung(sessionRequest.beschreibung());
		oldSession.setBlocks(sessionRequest.blocks());
		sessionRepository.save(oldSession);
	}
}
