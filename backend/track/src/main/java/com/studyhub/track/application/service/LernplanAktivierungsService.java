package com.studyhub.track.application.service;

import com.studyhub.track.domain.model.lernplan.LernplanRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Class maintains invariant that only one Lernplan of a user is marked as active.
 */
@Service
public class LernplanAktivierungsService {

	private final LernplanRepository lernplanRepository;

	public LernplanAktivierungsService(LernplanRepository lernplanRepository) {
		this.lernplanRepository = lernplanRepository;
	}

	/**
	 * Deactivates all Lernplan-Entities of a user and then activates the desired Lernplan.
	 * @param lernplanId ID of the Lernplan to activate
	 * @param username Username of the user
	 */
	@Transactional
	public void setActiveLernplan(UUID lernplanId, String username) {
		lernplanRepository.deactivateAllByUsername(username);
		lernplanRepository.setIsActiveOfLernplan(lernplanId, true);
	}
}
