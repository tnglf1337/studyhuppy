package com.studyhub.track.application.service;

import com.studyhub.track.domain.model.session.SessionBeendetEvent;
import org.springframework.stereotype.Service;

@Service
public class SessionEventsService {

	private SessionBeendetEventRepository sessionBeendetEventRepository;

	public SessionEventsService(SessionBeendetEventRepository sessionBeendetEventRepository) {
		this.sessionBeendetEventRepository = sessionBeendetEventRepository;
	}

	/**
	 * Saves a SessionBeendetEvent to the repository.
	 * @param event The SessionBeendetEvent to be saved
	 * @return true if the event was saved successfully, false otherwise
	 */
	public boolean save(SessionBeendetEvent event) {
		return sessionBeendetEventRepository.save(event) != null;
	}
}
