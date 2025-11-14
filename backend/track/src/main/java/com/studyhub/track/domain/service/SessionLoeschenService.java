package com.studyhub.track.domain.service;

import com.studyhub.track.domain.model.lernplan.LernplanRepository;
import com.studyhub.track.domain.model.session.SessionRepository;
import com.studyhub.track.domain.model.lernplan.Lernplan;
import com.studyhub.track.domain.model.lernplan.Tag;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Class deletes a session from database and all the references of it in all Lernplaene created by a user.
 */
@Service
public class SessionLoeschenService {

	private SessionRepository sessionRepository;
	private LernplanRepository lernplanRepository;

	public SessionLoeschenService(SessionRepository sessionRepository, LernplanRepository lernplanRepository) {
		this.sessionRepository = sessionRepository;
		this.lernplanRepository = lernplanRepository;
	}

	/**
	 * Deletes the session from the database and associated Lernplan-Entities.
	 * @param sessionId The identity of the Session
	 * @param username The username of the user
	 */
	@Transactional
	public void sessionLoeschen(UUID sessionId, String username) {
		sessionRepository.deleteByFachId(sessionId);
		List<Lernplan> lernplaene = lernplanRepository.findAllByUsername(username);

		for (Lernplan lernplan : lernplaene) {
			List<Tag> tage = lernplan.getTagesListe();

			boolean removed = tage.removeIf(tag -> sessionId.equals(tag.getSessionId()));

			if(removed) {
				lernplanRepository.save(lernplan);
				removed = false;
			}
		}
	}
}
