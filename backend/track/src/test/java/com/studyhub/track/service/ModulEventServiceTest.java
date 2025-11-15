package com.studyhub.track.service;

import com.studyhub.track.application.service.*;
import com.studyhub.track.domain.model.modul.Modul;
import com.studyhub.track.domain.model.modul.ModulGelerntEvent;
import com.studyhub.track.util.ModulMother;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ModulEventServiceTest {
	static ModulRepository modulRepo;
	static ModulGelerntEventRepository eventRepo;
	static ModulEventService service;
	static DateProvider dateProvider;

	@BeforeAll
	static void init() {
		modulRepo = mock(ModulRepository.class);
		eventRepo = mock(ModulGelerntEventRepository.class);
		dateProvider = mock(DateProvider.class);
		service = new ModulEventService(eventRepo, modulRepo, dateProvider);
	}

	@Test
	@DisplayName("Die Lerndaten der letzten 7 Tage werden für einen User und den in dieser Zeit gelernten Module korrekt zurückgegeben.")
	void test_01() {
		List<Modul> modules = List.of(
				ModulMother.initModulWithNameAndUsername("Mathe", "timo123"),
				ModulMother.initModulWithNameAndUsername("AlDat", "timo123"));

		when(modulRepo.findByUsername("timo123")).thenReturn(modules);
		when(dateProvider.getTodayDate()).thenReturn(LocalDate.of(2025, 1, 10));
		when(eventRepo.getSumSecondsLearned(any(), any(), any())).thenReturn(20);

		Map<LocalDate, List<ModulStat>> res = service.getStatisticsForRecentDays(7, "timo123");

		assertThat(res).hasSize(7);
		List<ModulStat> stats = res.get(LocalDate.of(2025, 1, 4));
		assertThat(stats).hasSize(2);
		assertThat(stats.get(0).secondsLearned()).isEqualTo("20");
		assertThat(stats.get(1).secondsLearned()).isEqualTo("20");
		assertThat(stats.get(1).modulName()).isEqualTo("AlDat");
	}

	@Test
	@DisplayName("Wenn in den letzten x Tagen nicht gelernt wurde, gibt es auch keine Statistiken")
	void test_02() {
		List<Modul> modules = List.of(
				ModulMother.initModulWithNameAndUsername("Mathe", "timo123"),
				ModulMother.initModulWithNameAndUsername("AlDat", "timo123"));

		when(modulRepo.findByUsername("timo123")).thenReturn(modules);
		when(dateProvider.getTodayDate()).thenReturn(LocalDate.of(2025, 1, 10));
		when(eventRepo.getSumSecondsLearned(any(), any(), any())).thenReturn(0);

		Map<LocalDate, List<ModulStat>> res = service.getStatisticsForRecentDays(7, "token");

		assertThat(res).isEmpty();
	}

	@Test
	@DisplayName("Die durchschnittliche Lernzeit eines Users an 6 gelernten Tagen wird korrekt berechnet.")
	void test_03() {
		String username = "timo123";
		UUID modulAId = UUID.randomUUID();
		UUID modulBId = UUID.randomUUID();
		List<ModulGelerntEvent> events = List.of(
				new ModulGelerntEvent(UUID.randomUUID(), modulAId, "timo123",  20, LocalDate.of(2025, 1, 10)),
				new ModulGelerntEvent(UUID.randomUUID(),modulBId, "timo123",  20, LocalDate.of(2025, 1, 10)),
				new ModulGelerntEvent(UUID.randomUUID(),modulAId,"timo123",  30, LocalDate.of(2025, 1, 11)),
				new ModulGelerntEvent(UUID.randomUUID(),modulBId, "timo123",  30, LocalDate.of(2025, 1, 12)),
				new ModulGelerntEvent(UUID.randomUUID(),modulAId, "timo123",  30, LocalDate.of(2025, 1, 12)),
				new ModulGelerntEvent(UUID.randomUUID(),modulBId, "timo123",  30, LocalDate.of(2025, 1, 12)),
				new ModulGelerntEvent(UUID.randomUUID(),modulAId, "timo123",  30, LocalDate.of(2025, 1, 13)),
				new ModulGelerntEvent(UUID.randomUUID(),modulBId, "timo123",  30, LocalDate.of(2025, 1, 14)),
				new ModulGelerntEvent(UUID.randomUUID(),modulAId, "timo123",  30, LocalDate.of(2025, 1, 15)),
				new ModulGelerntEvent(UUID.randomUUID(),modulBId, "timo123", 30, LocalDate.of(2025, 1, 15)));
		when(eventRepo.getAllByUsername(username)).thenReturn(events);

		int res = service.computeAverageStudyTimePerDay(username);

		assertThat(res).isEqualTo(47);
	}

	@Test
	@DisplayName("Wenn keine Events für eine User gefunden werden, wird 0 als durchschnittliche Lernzeit am Tag berechnet")
	void test_04() {
		String username = "timo123";
		List<ModulGelerntEvent> events = List.of();
		when(eventRepo.getAllByUsername(username)).thenReturn(events);

		int res = service.computeAverageStudyTimePerDay(username);

		assertThat(res).isZero();
	}

	@DisplayName("Ein ModulEvent mit {4, 0, -1000} Sekunden wird nicht gespeichert.")
	@ParameterizedTest
	@ValueSource(ints = {4, 0, -1000})
	void test_05(int secondsLearned) {
		assertThrows(IllegalStateException.class, () -> {
			service.saveEvent(secondsLearned, UUID.randomUUID(), "timo123");
		});
	}

	@Test
	@DisplayName("Ein ModulEvent mit 5 Sekunden wird gespeichert.")
	void test_06() {
		int secondsLearned = 5;

		service.saveEvent(secondsLearned, UUID.randomUUID(), "timo123");

		verify(eventRepo, times(1)).save(any(ModulGelerntEvent.class));
	}
}
