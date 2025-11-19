package com.studyhub.track.adapter.web;

import com.studyhub.track.adapter.web.controller.request.dto.ModulRequest;
import com.studyhub.track.application.JWTService;
import com.studyhub.track.domain.model.modul.Kreditpunkte;
import com.studyhub.track.domain.model.modul.Modul;
import com.studyhub.track.domain.model.semester.Semester;
import com.studyhub.track.domain.model.semester.SemesterTyp;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

@Deprecated
@Data
@AllArgsConstructor
public class SemesterForm {

	@Min(value = 1, message = "Semester Stufe muss positiv zwischen 1 und 16 liegen")
	@Max(value = 16, message = "Semester Stufe muss positiv zwischen 1 und 16 liegen")
	private Integer semesterstufe;
	private LocalDate semesterBeginn;
	private LocalDate semesterEnde;
	private LocalDate vlBeginn;
	private LocalDate vlEnde;
	private List<ModulRequest> module;
	private String moduleData;

	private final JWTService jwtService = new JWTService();

	public List<Modul> toModulList(String token) {
		List<Modul> m = new LinkedList<>();
		for(ModulRequest modulRequest : module) {
			if(modulRequest == null) continue;
			// TODO: muss alles noch in dem request mitgeschickt werden
			Kreditpunkte kreditpunkte = new Kreditpunkte(0,0,0);
			Semester semester = new Semester(null, 1, SemesterTyp.WINTERSEMESTER, vlBeginn, vlEnde, semesterBeginn, semesterEnde);
			Modul modul = new Modul(UUID.randomUUID(), modulRequest.modulName(), 0, kreditpunkte, jwtService.extractUsername(token), true, semesterstufe, semester, null);
			m.add(modul);
		}
		return m;
	}
}
