package com.studyhub.track.domain.model.lernplan;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

/**
 * A Lernplan is a study plan that contains a list of days (Tag) for which a specific Session may exist
 * that the user wants to study at that day.
 * <ul>
 *     <li>{@code fachId} - Unique ID of the Lernplan</li>
 *     <li>{@code username} – Username of the user</li>
 *     <li>{@code titel} – Title of the Lernplan</li>
 *     <li>{@code tagesListe} – List of the Tag value objects.</li>
 *     <li>{@code isActive}</li> - Value tells wether this Lernplan is active or not. If a Lernplan is active, it is rendered in the UI. Only one Lernplan can be active at the same time for a user.
 *
 * </ul>
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Lernplan {
	private UUID fachId;
	private String username;
	private String titel;
	private List<Tag> tagesListe;
	private boolean isActive;

	/**
	 * Updates the list of Tag objects in the Lernplan.
	 * @param neueTagesListe The new list of Tag objects to set.
	 */
	public void aktualisiereTagesliste(List<Tag> neueTagesListe) {
		this.tagesListe = neueTagesListe;
	}
}
