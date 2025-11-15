package com.studyhub.track.service;

import com.studyhub.track.application.service.*;
import com.studyhub.track.domain.model.modul.Modul;
import com.studyhub.track.util.ModulMother;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.util.*;
import static com.studyhub.track.util.ModulMother.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

class ModulServiceTest {

	ModulRepository modulRepository;
	SessionService sessionService;
	ModulEventService modulEventService;
	ModulService modulService;

	@BeforeEach
	void init() {
		modulRepository = mock(ModulRepository.class);
		sessionService = mock(SessionService.class);
		modulEventService = mock(ModulEventService.class);
		modulService = new ModulService(modulRepository, sessionService, modulEventService);
	}

	@Test
	@DisplayName("repo.save() wird korrekt aufgerufen")
	void test_01() {
		modulService.saveNewModul(initModul());
		verify(modulRepository).save(any(Modul.class));
	}

	@Test
	@DisplayName("repo.findAll() wird korrekt aufgerufen")
	void test_02() {
		modulService.findAll();
		verify(modulRepository).findAll();
	}

	@Test
	@DisplayName("repo.updateSecondsByUuid() wird aufgerufen und Metode aktualisiert erfolgreich")
	void test_03() throws Exception {
		// TODO refactor
	}

	@Test
	@DisplayName("repo.deleteByUuid() wird korrekt aufgerufen")
	void test_04() {
		modulService.deleteModul(UUID.randomUUID(), "peter77");
		verify(modulRepository).deleteByUuid(any(UUID.class));
	}

	@Test
	@DisplayName("repo.findActiveModuleByUsername() wird korrekt aufgerufen")
	void test_05() {
		modulService.findActiveModuleByUsername(true, "user123");
		verify(modulRepository).findActiveModuleByUsername(true, "user123");
	}

	@Test
	@DisplayName("addTime funktioniert korrekt und addiert die Sekunden zum Modul")
	void test_08() throws Exception {
		// TODO refactor
	}

	@Test
	@DisplayName("Finde alle Module und transformiere die Liste in eine Map<Fach-Id, Modulname>")
	void test_09() {
		String username = "user123";
		List<Modul> modulList = new ArrayList<>();
		modulList.add(ModulMother.initModulWithName("Data Science"));
		modulList.add(ModulMother.initModulWithName("Aldat"));
		Map<UUID, String> desiredMap = new HashMap<>();
		desiredMap.put(modulList.get(0).getFachId(), modulList.get(0).getName());
		desiredMap.put(modulList.get(1).getFachId(), modulList.get(1).getName());
		when(modulRepository.findByUsername(username)).thenReturn(modulList);

		Map<UUID, String> returnedMap = modulService.getModuleMap(username);

		assertThat(returnedMap).isEqualTo(desiredMap);
	}

	@Test
	@DisplayName("Englisches Datums-Format wird korrekt ins deutsche Format geparsed")
	void test_10() {
		String eng = "2025-03-27 10:00:00";

		String ger = modulService.dateStringGer(eng);

		assertThat(ger).isEqualTo("27.03.2025, 10:00");
	}

	@Test
	@DisplayName("Für einen User wird die lernzeit pro Fachsemester als Map berechnet")
	void test_14() {
		List<Modul> modulList = ModulMother.modulListWithSemester();
		Map<Integer, Integer> expectedMap = new HashMap<>();
		expectedMap.put(3, 3000);
		expectedMap.put(4, 1000);
		expectedMap.put(5, 5000);
		when(modulRepository.findByUsername("peter")).thenReturn(modulList);

		Map<Integer, Integer> actual = modulService.getTotalStudyTimePerFachSemester("peter");

		assertThat(actual).isEqualTo(expectedMap);
	}

	@Test
	@DisplayName("Wenn ein User nur 10 Module erstellen darf und aktuelle 9 erstellt hat, kann er ein weiteres Modul erstellen")
	void test_15() {
		List<Modul> modulList = ModulMother.initListWithNEmptyModule(9);
		when(modulRepository.findByUsername("peter")).thenReturn(modulList);

		boolean allowed = modulService.modulCanBeCreated("peter", 10);

		assertThat(allowed).isTrue();
	}

	@Test
	@DisplayName("Wenn ein User nur 10 Module erstellen darf und aktuelle 10 erstellt hat, kann er kein weiteres Modul erstellen")
	void test_16() {
		List<Modul> modulList = ModulMother.initListWithNEmptyModule(10);
		when(modulRepository.findByUsername("peter")).thenReturn(modulList);

		boolean allowed = modulService.modulCanBeCreated("peter", 10);

		assertThat(allowed).isFalse();
	}

	@Test
	@DisplayName("getFachsemesterModuleMap baut die Datenstruktur Map<Integer, List<Modul>> korrekt zusammen")
	void test_17() {
		List<Modul> modulList = ModulMother.modulListWithSemester();
		when(modulRepository.findActiveModuleByUsername(true, "peter")).thenReturn(modulList);
		Map<Integer, List<Modul>> expectedMap = new TreeMap<>();
		List<Modul> l1 = List.of(
				new Modul(UUID.randomUUID(), "m1", 1000, DEFAULT_KREDITPUNKTE, "peter", true, 3, DEFAULT_SEMESTER, DEFAULT_MODULTERMINE) ,
				new Modul(UUID.randomUUID(), "m2", 2000, DEFAULT_KREDITPUNKTE, "peter", true, 3, DEFAULT_SEMESTER, DEFAULT_MODULTERMINE));
		expectedMap.put(3, l1);
		List<Modul> l2 = List.of(new Modul(UUID.randomUUID(), "m3", 1000, DEFAULT_KREDITPUNKTE, "peter", true, 4, DEFAULT_SEMESTER, DEFAULT_MODULTERMINE));
		expectedMap.put(4, l2);
		List<Modul> l3 = List.of(
				new Modul(UUID.randomUUID(), "m4", 500, DEFAULT_KREDITPUNKTE, "peter", true, 5, DEFAULT_SEMESTER, DEFAULT_MODULTERMINE),
				new Modul(UUID.randomUUID(), "m5", 500, DEFAULT_KREDITPUNKTE, "peter", true, 5, DEFAULT_SEMESTER, DEFAULT_MODULTERMINE),
				new Modul(UUID.randomUUID(), "m6", 4000, DEFAULT_KREDITPUNKTE, "peter", true, 5, DEFAULT_SEMESTER, DEFAULT_MODULTERMINE));
		expectedMap.put(5, l3);

		Map<Integer, List<Modul>> actualMap = modulService.getFachsemesterModuleMap("peter");

		assertThat(actualMap.get(3)).hasSize(2);
		assertThat(actualMap.get(3).get(0).getName()).isEqualTo("m1");
		assertThat(actualMap.get(3).get(1).getName()).isEqualTo("m2");
		assertThat(actualMap.get(4)).hasSize(1);
		assertThat(actualMap.get(4).get(0).getName()).isEqualTo("m3");
		assertThat(actualMap.get(5)).hasSize(3);
		assertThat(actualMap.get(5).get(0).getName()).isEqualTo("m4");
		assertThat(actualMap.get(5).get(1).getName()).isEqualTo("m5");
		assertThat(actualMap.get(5).get(2).getName()).isEqualTo("m6");
	}

	@Test
	@DisplayName("deleteModul(...) gibt Löschanweisung korrekt an alle betroffenen Microservices weiter und alle Daten zum Modul werden gelöscht")
	void test_18() {
		UUID modulid = UUID.randomUUID();
		String username = "peter77";
		when(modulRepository.deleteByUuid(modulid)).thenReturn(1);
		doNothing().when(sessionService).deleteModuleFromBlocks(modulid, username);
		doNothing().when(modulEventService).deleteAllModulEvents(modulid);

		boolean success = modulService.deleteModul(modulid, username);

		assertThat(success).isTrue();
		verify(modulRepository, times(1)).deleteByUuid(modulid);
		verify(sessionService, times(1)).deleteModuleFromBlocks(modulid, username);
		verify(modulEventService, times(1)).deleteAllModulEvents(modulid);
	}

	@Test
	@DisplayName("changeActivity() ändert den Aktivitätsstatus eines Moduls und speichert es im Repository")
	void test_19() {
		UUID modulid = UUID.randomUUID();
		Modul modul = initNotActiveModul();
		when(modulRepository.findByUuid(modulid)).thenReturn(modul);
		when(modulRepository.save(any(Modul.class))).thenReturn(modul);

		modulService.changeActivity(modulid);

		assertThat(modul.isActive()).isTrue();
		verify(modulRepository, times(1)).findByUuid(modulid);
		verify(modulRepository, times(1)).save(modul);
	}

	@Test
	@DisplayName("Die secondsLearned eines Moduls werden auf 0 zurückgesetzt.")
	void test_20() {
		Modul modulToReset = initModul();
		when(modulRepository.findByUuid(modulToReset.getFachId())).thenReturn(modulToReset);

		modulService.resetSecondsLearnedOfModul(modulToReset.getFachId());

		assertThat(modulToReset.getSecondsLearned()).isZero();
		verify(modulRepository, times(1)).save(modulToReset);
	}

	@Test
	@DisplayName("Wenn beim Zurücksetzen der Lernzeit das Modul nicht gefunden wird, passiert nichts.")
	void test_21() {
		Modul mock = mock(Modul.class);
		UUID nonExistingModulId = UUID.randomUUID();
		when(modulRepository.findByUuid(nonExistingModulId)).thenReturn(null);

		modulService.resetSecondsLearnedOfModul(nonExistingModulId);

		verify(mock, times(0)).resetSecondsLearned();
		verify(modulRepository, times(0)).save(any(Modul.class));
	}

	@Test
	@DisplayName("Für ein Modul mit secondsLearned = 2000 wird dieser Wert über den Service korrekt zurückgegeben")
	void test_22() {
		UUID desiredSecondsOfModul = UUID.randomUUID();
		when(modulRepository.findSecondsById(desiredSecondsOfModul)).thenReturn(2000);

		int actualSeconds = modulService.getSecondsForId(desiredSecondsOfModul);

		assertThat(actualSeconds).isEqualTo(2000);
	}

	@Test
	@DisplayName("Wenn ein Modul, dass nicht existiert die secondsLearned abfragt werden, wird 0 zurückgegeben")
	void test_23() {
		UUID notExistingModulId = UUID.randomUUID();
		when(modulRepository.findSecondsById(notExistingModulId)).thenReturn(0);

		int actualSeconds = modulService.getSecondsForId(notExistingModulId);

		assertThat(actualSeconds).isEqualTo(0);
	}
}