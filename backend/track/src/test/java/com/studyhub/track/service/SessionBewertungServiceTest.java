package com.studyhub.track.service;

import com.studyhub.track.application.service.SessionBeendetEventRepository;
import com.studyhub.track.application.service.SessionBewertungService;
import com.studyhub.track.application.service.SessionBewertungGeneralStatistikDto;
import com.studyhub.track.application.service.dto.SessionBewertungAveragesDto;
import com.studyhub.track.domain.model.session.SessionBeendetEvent;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.studyhub.track.util.SessionBeendetEventMother.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SessionBewertungServiceTest {

	private static final String USER = "peter93";

	@Mock
	private SessionBeendetEventRepository sessionBeendetEventRepository;

	@InjectMocks
	private SessionBewertungService sessionBewertungService;

	@Test
	@DisplayName("Für einen User 'peter93' wird der Durchschnitt der Konzentrationsbewertung korrekt berechnet")
	void testGetAverageKonzentrationBewertungByUsername() {
		List<SessionBeendetEvent> events = initEventsOfUser(USER);
		when(sessionBeendetEventRepository.findAllByUsername(USER))
			.thenReturn(events);

		Double average = sessionBewertungService.getAverageKonzentrationBewertungByUsername(USER);

		assertThat(average).isEqualTo(5.0);
	}

	@Test
	@DisplayName("Für einen User 'peter93' wird der Durchschnitt der Produktivitätsbewertung korrekt berechnet")
	void testGetAverageProduktivitaetBewertungByUsername() {
		List<SessionBeendetEvent> events = initEventsOfUser(USER);
		when(sessionBeendetEventRepository.findAllByUsername(USER))
				.thenReturn(events);

		Double average = sessionBewertungService.getAverageProduktivitaetBewertungByUsername(USER);

		assertThat(average).isEqualTo(6.0);
	}

	@Test
	@DisplayName("Für einen User 'peter93' wird der Durchschnitt der Schwierigkeitsbewertung korrekt berechnet")
	void testGetAverageSchwierigkeitBewertungByUsername() {
		List<SessionBeendetEvent> events = initEventsOfUser(USER);
		when(sessionBeendetEventRepository.findAllByUsername(USER))
				.thenReturn(events);

		Double average = sessionBewertungService.getAverageSchwierigkeitBewertungByUsername(USER);

		assertThat(average).isEqualTo(7.0);
	}

	@Test
	@DisplayName("Ein SessionBewertungStatistikDto wird korrekt für einen User wiedergegeben")
	void test_04() {
		List<SessionBeendetEvent> events = initEventsOfUser(USER);
		when(sessionBeendetEventRepository.findAllByUsername(USER))
				.thenReturn(events);

		SessionBewertungGeneralStatistikDto statistikDto = sessionBewertungService.getSessionBewertungStatistikByUsername(USER);

		assertThat(statistikDto.konzentrationBewertung()).isEqualTo(5.0);
		assertThat(statistikDto.produktivitaetBewertung()).isEqualTo(6.0);
		assertThat(statistikDto.schwierigkeitBewertung()).isEqualTo(7.0);
	}

	@Test
	@DisplayName("Für einen User 'peter93' wird ein Histogram der Konzentrationsbewertung korrekt berechnet")
	void test_05() {
		List<SessionBeendetEvent> events = initEventsOfUserHisto(USER);
		when(sessionBeendetEventRepository.findAllByUsername(USER)).thenReturn(events);

		Map<Integer, Integer> actual = sessionBewertungService.getKonzentrationHisto(USER);

		assertThat(actual.get(0)).isZero();
		assertThat(actual.get(1)).isZero();
		assertThat(actual.get(3)).isZero();
		assertThat(actual.get(4)).isZero();
		assertThat(actual.get(8)).isZero();
		assertThat(actual.get(10)).isZero();
		assertThat(actual)
				.containsEntry(2,2)
				.containsEntry(5,2)
				.containsEntry(6,2)
				.containsEntry(7,1)
				.containsEntry(9,1);
	}

	@Test
	@DisplayName("Für einen User 'peter93' wird ein Histogram der Produktivitätsbewertung korrekt berechnet")
	void test_06() {
		List<SessionBeendetEvent> events = initEventsOfUserHisto(USER);
		when(sessionBeendetEventRepository.findAllByUsername(USER)).thenReturn(events);

		Map<Integer, Integer> actual = sessionBewertungService.getProduktivitaetHisto(USER);
		assertThat(actual.get(2)).isZero();
		assertThat(actual.get(4)).isZero();
		assertThat(actual.get(5)).isZero();
		assertThat(actual.get(6)).isZero();
		assertThat(actual.get(7)).isZero();
		assertThat(actual.get(8)).isZero();
		assertThat(actual.get(9)).isZero();
		assertThat(actual)
				.containsEntry(0,1)
				.containsEntry(1,3)
				.containsEntry(3,3)
				.containsEntry(10,1);
	}

	@Test
	@DisplayName("Für einen User 'peter93' wird ein Histogram der Schwierigkeitsbewertung korrekt berechnet")
	void test_07() {
		List<SessionBeendetEvent> events = initEventsOfUserHisto(USER);
		when(sessionBeendetEventRepository.findAllByUsername(USER)).thenReturn(events);

		Map<Integer, Integer> actual = sessionBewertungService.getSchwierigkeitHisto(USER);

		assertThat(actual.get(0)).isZero();
		assertThat(actual.get(1)).isZero();
		assertThat(actual.get(2)).isZero();
		assertThat(actual.get(3)).isZero();
		assertThat(actual.get(4)).isZero();
		assertThat(actual.get(5)).isZero();
		assertThat(actual.get(6)).isZero();
		assertThat(actual.get(9)).isZero();
		assertThat(actual.get(10)).isZero();
		assertThat(actual)
				.containsEntry(7,6)
				.containsEntry(8,2);
	}

	@Test
	@DisplayName("Für einen User wird für eine bestimmte Session die Statistik-Map korrekt berechnet")
	void test_08() {
		UUID sessionId = UUID.randomUUID();
		List<SessionBeendetEvent> events = initSessionBewertungStatistikEvents(sessionId);
		when(sessionBeendetEventRepository.findAllBySessionId(sessionId)).thenReturn(events);
		Map<LocalDate, SessionBewertungAveragesDto> expected = Map.of(
				LocalDate.of(2025,10,21),
				new SessionBewertungAveragesDto(6.0, 5.0, 4.0),
				LocalDate.of(2025,10,20),
				new SessionBewertungAveragesDto(6.0, 6.5, 8.5),
				LocalDate.of(2025,10,19),
				new SessionBewertungAveragesDto(4.5, 6.0, 6.5)

				);

		Map<LocalDate, SessionBewertungAveragesDto> actual = sessionBewertungService.getMonthlySessionBewertungStatistik(sessionId);

		//assertThat(actual).isEqualTo(expected);
	}
}