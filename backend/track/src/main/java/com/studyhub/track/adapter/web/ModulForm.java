package com.studyhub.track.adapter.web;

import com.studyhub.track.domain.model.modul.Kreditpunkte;
import com.studyhub.track.domain.model.modul.Modul;
import com.studyhub.track.domain.model.semester.Semester;
import com.studyhub.track.domain.model.semester.SemesterTyp;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Form-class to create a Modul entity of the data sent from the frontend.
 */
public record ModulForm(
		String name,
		Integer creditPoints,
		Integer kontaktzeitStunden,
		Integer selbststudiumStunden,
		LocalDate klausurDatum,
		String time
)
{
	/**
	 * Creates a Modul entity from the form data.
	 * @param username Username of the user
	 * @param semester Fachsemester of the user
	 * @return The Modul-Entity
	 */
	public Modul newModulFromFormData(String username, int semester) {
		Kreditpunkte kreditpunkte = new Kreditpunkte(creditPoints, kontaktzeitStunden, selbststudiumStunden);
		return new Modul(UUID.randomUUID(), name, 0, kreditpunkte, username, true, semester, null, null);
	}
}