package com.studyhub.track.adapter.web;

import com.studyhub.track.domain.model.modul.Kreditpunkte;
import com.studyhub.track.domain.model.modul.Modul;
import com.studyhub.track.domain.model.semester.Semester;
import com.studyhub.track.domain.model.semester.SemesterTyp;
import java.time.LocalDate;
import java.util.UUID;

public record ModulForm(
		String name,
		Integer creditPoints,
		Integer kontaktzeitStunden,
		Integer selbststudiumStunden,
		LocalDate klausurDatum,
		String time
)
{
	public Modul newModulFromFormData(ModulForm modulForm, String username, int semester) {
		Kreditpunkte kreditpunkte = new Kreditpunkte(modulForm.creditPoints(), modulForm.kontaktzeitStunden(), modulForm.selbststudiumStunden());
		return new Modul(UUID.randomUUID(), name, 0, kreditpunkte, username, true, semester, null, null);
	}
}