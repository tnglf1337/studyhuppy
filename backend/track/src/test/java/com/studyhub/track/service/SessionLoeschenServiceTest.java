package com.studyhub.track.service;

import com.studyhub.track.domain.model.lernplan.LernplanRepository;
import com.studyhub.track.domain.model.session.SessionRepository;
import com.studyhub.track.domain.model.lernplan.Lernplan;
import com.studyhub.track.domain.service.SessionLoeschenService;
import com.studyhub.track.util.LernplanMother;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SessionLoeschenServiceTest {

	@Mock
	SessionRepository sessionRepository;

	@Mock
	LernplanRepository lernplanRepository;

	@InjectMocks
	SessionLoeschenService sessionLoeschenService;

	@Test
	@DisplayName("Wenn eine Session gelöscht werden soll, wird sie aus der Datenbank entfernt und aus den Tag-Objekten aller Lernpläne des Benutzers.")
	void test1() {
		String username = "testuser";
		UUID sessionToRemove = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
		Lernplan plan1 = LernplanMother.initFullLernplanWithRandomSessions(sessionToRemove);
		Lernplan plan2 = LernplanMother.initFullLernplan();
		Lernplan plan3 = LernplanMother.initFullLernplanWithRandomSessions(sessionToRemove);
		List<Lernplan> plaene = new ArrayList<>(List.of(plan1, plan2, plan3));
		when(lernplanRepository.findAllByUsername(username)).thenReturn(plaene);

		sessionLoeschenService.sessionLoeschen(sessionToRemove, username);

		assertThat(plaene.get(0).getTagesListe()).hasSize(6);
		assertThat(plaene.get(1).getTagesListe()).hasSize(7);
		assertThat(plaene.get(2).getTagesListe()).hasSize(6);
		verify(sessionRepository, times(1)).deleteByFachId(sessionToRemove);
		verify(lernplanRepository, times(2)).save(any(Lernplan.class));
	}

}
