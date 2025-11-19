package com.studyhub.track.adapter.web.controller.request.dto;

import com.studyhub.track.domain.model.lernplan.Lernplan;
import com.studyhub.track.domain.model.lernplan.Tag;
import com.studyhub.track.domain.model.lernplan.TagDto;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

/**
 * The request dto with edited data of an existing Lernplan entity.
 * @param lernplanId The id of the Lernplan to be edited
 * @param tage The list of TagDto representing the days and sessions of the Lernplan
 */
public record LernplanBearbeitetRequest(
		UUID lernplanId,
		List<TagDto> tage
) {

	// TODO duplicated code with LernplanCreateRequest.toEntity()
	/**
	 * Converts the LernplanCreateRequest to a Lernplan entity.
	 * @param username The username of the user creating the Lernplan
	 * @return The edited Lernplan entity
	 */
	public Lernplan toEntity(String username) {
		List<Tag> tagesListe = tage.stream()
				.filter(e -> !e.sessionId().equals("none"))
				.map(dtoObj -> {

					DayOfWeek tag;

					switch(dtoObj.tag()) {
						case "Montags" -> tag = DayOfWeek.MONDAY;
						case "Dienstags" -> tag = DayOfWeek.TUESDAY;
						case "Mittwochs" -> tag = DayOfWeek.WEDNESDAY;
						case "Donnerstags" -> tag = DayOfWeek.THURSDAY;
						case "Freitags" -> tag = DayOfWeek.FRIDAY;
						case "Samstags" -> tag = DayOfWeek.SATURDAY;
						case "Sonntags" -> tag = DayOfWeek.SUNDAY;
						default -> tag = null;
					}

					LocalTime beginn = LocalTime.parse(dtoObj.beginn());


					return new Tag(tag, beginn, UUID.fromString(dtoObj.sessionId()));
				})
				.toList();

		return Lernplan.builder()
				.fachId(lernplanId)
				.username(username)
				.titel("")
				.tagesListe(tagesListe)
				.isActive(false)
				.build();
	}
}
