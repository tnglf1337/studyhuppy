package com.studyhub.track.application.service;

import com.studyhub.track.application.service.dto.SessionInfoDto;
import com.studyhub.track.application.service.dto.SessionRequest;
import com.studyhub.track.domain.model.session.Block;
import com.studyhub.track.domain.model.session.Session;
import com.studyhub.track.domain.model.session.SessionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.UUID;

@Service
public class SessionService {

	private final SessionRepository sessionRepository;

	public SessionService(SessionRepository sessionRepository) {
		this.sessionRepository = sessionRepository;
	}

	/**
	 * Saves a session to the repository.
	 * @param session The session to be saved
	 * @return True if the session was saved successfully, false otherwise
	 */
	public boolean save(Session session) {
		Session saved = sessionRepository.save(session);
		return saved != null;
	}

	/**
	 * Gets all sessions of a user by username.
	 * @param username The username of the user
	 * @return A list of sessions belonging to the user
	 */
	public List<Session> getSessionsByUsername(String username) {
		return sessionRepository.findAllByUsername(username);
	}

	/**
	 * Gets a list of <code>SessionInfoDto</code> needed for ui rendering.
	 * @param username The username of the user
	 * @return A list of SessionInfoDto containing the necessary data
	 */
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

	/**
	 * Gets a session by its sessionId.
	 * @param sessionId The id of the session
	 * @return The wanted session
	 */
	public Session getSessionByFachId(UUID sessionId) {
		return sessionRepository.findSessionByFachId(sessionId);
	}

	/**
	 * Deletes all blocks containing the moduleId from all sessions of the user.
	 * @param modulId The moduleId that needs to be deleted from the session blocks
	 * @param username The username of the user
	 */
	@Transactional
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

	/**
	 * Sets the new session atreributes and saves it to the repository.
	 * @param sessionRequest The request with the new session attributes
	 */
	@Transactional
	public void saveEditedSession(SessionRequest sessionRequest) {
		Session oldSession = sessionRepository.findSessionByFachId(UUID.fromString(sessionRequest.fachId()));
		oldSession.setTitel(sessionRequest.titel());
		oldSession.setBeschreibung(sessionRequest.beschreibung());
		oldSession.setBlocks(sessionRequest.blocks());
		sessionRepository.save(oldSession);
	}
}
