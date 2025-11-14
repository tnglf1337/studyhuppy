package com.studyhub.track.application.service;

import com.studyhub.track.application.service.dto.LernplanWochenuebersicht;
import com.studyhub.track.application.service.dto.LernplanTagesuebersicht;
import com.studyhub.track.domain.model.lernplan.Lernplan;
import com.studyhub.track.domain.model.lernplan.LernplanRepository;
import com.studyhub.track.domain.model.lernplan.Tag;
import com.studyhub.track.domain.model.session.Session;
import com.studyhub.track.domain.model.session.SessionRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class LernplanService {

	private final LernplanRepository lernplanRepository;
	private final SessionRepository sessionRepository;

	public LernplanService(LernplanRepository lernplanRepository, SessionRepository sessionRepository) {
		this.lernplanRepository = lernplanRepository;
		this.sessionRepository = sessionRepository;
	}

	/**
	 * Persists a {@link Lernplan} entity in the database.
	 *
	 * @param lernplan the Lernplan entity to be saved
	 * @return {@code true} if the entity was successfully persisted, {@code false} otherwise
	 */
	public boolean saveLernplan(Lernplan lernplan) {
		Lernplan saved = lernplanRepository.save(lernplan);
		return saved != null;
	}

	/**
	 * Builds a {@link LernplanWochenuebersicht} (weekly overview) for the specified user.
	 * <p>
	 * Each day of the week is represented by a {@link LernplanTagesuebersicht},
	 * which includes the session details for that day.
	 * </p>
	 *
	 * @param username the username of the user
	 * @return the generated {@link LernplanWochenuebersicht} object, or {@code null} if no active Lernplan exists
	 */
	public LernplanWochenuebersicht collectLernplanWochenuebersicht(String username) {
		Lernplan entityLerntag = lernplanRepository.findActiveByUsername(username);

		if (entityLerntag == null) return null;

		List<LernplanTagesuebersicht> sessions = new ArrayList<>();
		for (Tag tag : entityLerntag.getTagesListe()) {
			UUID sessionId = tag.getSessionId();
			Session session = sessionRepository.findSessionByFachId(sessionId);
			String dayString = "";
			switch (tag.getTag()) {
				case MONDAY -> dayString = "Montags";
				case TUESDAY -> dayString = "Dienstags";
				case WEDNESDAY -> dayString = "Mittwochs";
				case THURSDAY -> dayString = "Donnerstags";
				case FRIDAY -> dayString = "Freitags";
				case SATURDAY -> dayString = "Samstags";
				case SUNDAY -> dayString = "Sonntags";
			}

			sessions.add(new LernplanTagesuebersicht(
					dayString,
					tag.getBeginn().toString(),
					session.getFachId().toString(),
					session.getBlocks()
			));
		}

		return new LernplanWochenuebersicht(entityLerntag.getTitel(), sessions);
	}

	/**
	 * Retrieves all {@link Lernplan} entities associated with a given username.
	 *
	 * @param username the username of the user
	 * @return a list of all Lernplaene belonging to the user
	 */
	public List<Lernplan> findAllLernplaeneByUsername(String username) {
		return lernplanRepository.findAllByUsername(username);
	}

	/**
	 * Deletes a {@link Lernplan} identified by its fachId.
	 *
	 * @param fachId the unique identifier of the Lernplan
	 * @return the number of affected rows (0 if none were deleted)
	 */
	public int deleteLernplanByFachId(UUID fachId) {
		return lernplanRepository.deleteByFachId(fachId);
	}

	/**
	 * Retrieves a {@link Lernplan} by its fachId.
	 *
	 * @param fachId the unique identifier of the Lernplan
	 * @return the found Lernplan, or {@code null} if not found
	 */
	public Lernplan findByFachId(UUID fachId) {
		return lernplanRepository.findByFachId(fachId);
	}

	/**
	 * Updates an existing {@link Lernplan} with new data and persists the changes in the database.
	 *
	 * @param bearbeiteterPlan the edited Lernplan provided by the user
	 * @return {@code true} if the updated Lernplan was successfully saved, {@code false} otherwise
	 */
	public boolean saveBearbeitetenLernplan(Lernplan bearbeiteterPlan) {
		UUID lernplanId = bearbeiteterPlan.getFachId();
		Lernplan alterPlan = lernplanRepository.findByFachId(lernplanId);
		alterPlan.aktualisiereTagesliste(bearbeiteterPlan.getTagesListe());
		Lernplan saved = lernplanRepository.save(alterPlan);
		return saved != null;
	}
}
