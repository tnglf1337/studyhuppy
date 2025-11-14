package com.studyhub.track.service;

import com.studyhub.track.domain.model.lernplan.LernplanRepository;
import com.studyhub.track.application.service.LernplanService;
import com.studyhub.track.domain.model.session.SessionRepository;
import com.studyhub.track.application.service.dto.LernplanWochenuebersicht;
import com.studyhub.track.domain.model.lernplan.Lernplan;
import com.studyhub.track.domain.model.session.Session;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.List;
import java.util.UUID;
import static com.studyhub.track.util.LernplanMother.*;
import static com.studyhub.track.util.SessionMother.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LernplanServiceTest {

	@Mock
	private LernplanRepository lernplanRepository;

	@Mock
	private SessionRepository sessionRepository;

	@InjectMocks
	private LernplanService lernplanService;

	@Test
	@DisplayName("Wenn ein Lernplan erfolgreich gespeichert wird, dann soll true zurueckgegeben werden.")
	void test_1() {
		Lernplan l = initFullLernplan();
		when(lernplanRepository.save(l)).thenReturn(l);

		boolean wasSaved = lernplanService.saveLernplan(l);

		assertThat(wasSaved).isTrue();
		verify(lernplanRepository, times(1)).save(l);
	}

	@Test
	@DisplayName("Wenn ein Lernplan erfolglos gespeichert wird, dann soll false zurueckgegeben werden.")
	void test_2() {
		Lernplan l = new Lernplan();
		when(lernplanRepository.save(any())).thenReturn(null);

		boolean wasSaved = lernplanService.saveLernplan(l);

		assertThat(wasSaved).isFalse();
		verify(lernplanRepository, times(1)).save(l);
	}

	@Test
	@DisplayName("Für einen User 'testuser' wird für seinen aktiv gesetzten Lernplan eine Wochenübersicht korrekt erstellt.")
	void test_3() {
		String username = "testuser";
		UUID sessionId = UUID.randomUUID();
		Lernplan aktiverLernplan = initFullLernplan(sessionId);
		Session s = createSessionWithTwoBlocks(sessionId);
		when(lernplanRepository.findActiveByUsername(username)).thenReturn(aktiverLernplan);
		when(sessionRepository.findSessionByFachId(sessionId)).thenReturn(s);
		LernplanWochenuebersicht expected = initFullLernplanWochenuebersicht(sessionId);

		LernplanWochenuebersicht actual = lernplanService.collectLernplanWochenuebersicht(username);

		assertThat(actual).isEqualTo(expected);
	}

	@Test
	@DisplayName("Alle erstellten Lernpläne für einen User 'testuser' werden zurückgegeben.")
	void test_4() {
		String username = "testuser";
		Lernplan l1 = initFullLernplan();
		Lernplan l2 = initFullLernplan();
		when(lernplanRepository.findAllByUsername(username)).thenReturn(List.of(l1, l2));

		java.util.List<Lernplan> allLernplaene = lernplanService.findAllLernplaeneByUsername(username);

		assertThat(allLernplaene)
				.hasSize(2)
				.containsExactlyInAnyOrder(l1, l2);
		verify(lernplanRepository, times(1)).findAllByUsername(username);
	}

	@Test
	@DisplayName("Wenn ein Lernplan anhand der FachId gelöscht wird, dann soll 1 zurückgegeben werden, was eine erfolgreiche Löschung signalisiert.")
	void test_5() {
		UUID fachId = UUID.randomUUID();
		when(lernplanRepository.deleteByFachId(fachId)).thenReturn(1);

		int result = lernplanService.deleteLernplanByFachId(fachId);

		assertThat(result).isEqualTo(1);
		verify(lernplanRepository, times(1)).deleteByFachId(fachId);
	}

	@Test
	@DisplayName("Wenn ein Lernplan anhand der FachId gelöscht werden soll, die nicht in der Datenbank existiert, soll 0 zurückgegeben werden, was eine erfolglose Löschung signalisiert.")
	void test_6() {
		UUID fachId = UUID.randomUUID();
		when(lernplanRepository.deleteByFachId(fachId)).thenReturn(0);

		int result = lernplanService.deleteLernplanByFachId(fachId);

		assertThat(result).isEqualTo(0);
		verify(lernplanRepository, times(1)).deleteByFachId(fachId);
	}
}
