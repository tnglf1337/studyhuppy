package com.studyhub.track.model;

import com.studyhub.track.domain.model.modul.*;
import com.studyhub.track.domain.model.semester.Semester;
import com.studyhub.track.domain.model.semester.SemesterTyp;
import com.studyhub.track.util.ModulMother;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class ModulTest {

	@Test
	@DisplayName("Modul mit 10CP , 90 Kontaktzeitstunden und 210 Selbststudiumstunden hat einen Arbeitsauffwand von 300")
	void test_1() {
		Kreditpunkte kreditpunkte = new Kreditpunkte(10, 90, 210);
		Modul m = ModulMother.initModulWithCp(kreditpunkte);

		assertEquals("300", m.getGesamtArbeitsaufwand());
	}

	@Test
	@DisplayName("Ein Modul, dass den voraussichtlichen Gesamtarbeitsaufwand überschreitet wird korrekt als true gewertet")
	void test_2() {
		Kreditpunkte kreditpunkte = new Kreditpunkte(10, 90, 210);
		Modul m = ModulMother.initModulWithCpAndSecondsLearned(kreditpunkte, 1083600);

		assertTrue(m.ueberschreitetGesamtarbeitsaufwand());
	}

	@Test
	@DisplayName("Ein Modul, dass den voraussichtlichen Gesamtarbeitsaufwand noch nicht erreicht hat, wird als false gewertet")
	void test_3() {
		Kreditpunkte kreditpunkte = new Kreditpunkte(10, 90, 210);
		Modul m = ModulMother.initModulWithCpAndSecondsLearned(kreditpunkte,  1076400);

		assertFalse(m.ueberschreitetGesamtarbeitsaufwand());
	}

	@Test
	@DisplayName("Ein Modul, dass den voraussichtlichen Selbststudiumaufwand überschreitet, wird korrekt als true gewertet")
	void test_4() {
		Kreditpunkte kreditpunkte = new Kreditpunkte(10, 90, 210);
		Modul m = ModulMother.initModulWithCpAndSecondsLearned(kreditpunkte,  757000);

		assertTrue(m.ueberschreitetSelbststudiumaufwand());
	}

	@Test
	@DisplayName("Ein Modul, dass den voraussichtlichen Selbststudiumaufwand noch nicht erreicht hat, wird als false gewertet")
	void test_5() {
		Kreditpunkte kreditpunkte = new Kreditpunkte(10, 90, 210);
		Modul m = ModulMother.initModulWithCpAndSecondsLearned(kreditpunkte,  755000);

		assertFalse(m.ueberschreitetSelbststudiumaufwand());
	}

	@Test
	@DisplayName("Wenn das Semester, in dem das Modul stattfindet keine Datum-Angaben für den Vorlesungsbeginn und -ende hat wird false zurückgegeben")
	void test_8() {
		Semester s = new Semester(1, 1, SemesterTyp.WINTERSEMESTER, null, null, null, null);
		Modul m = new Modul(UUID.randomUUID(), "modul1",
				10000,
				null,
				"peter44",
				true,
				2,
				s,
				null);

		boolean vorlesungEingetragen = m.vorlesungDatumangabenEingetragen();

		assertFalse(vorlesungEingetragen);
	}

	@Test
	@DisplayName("Wenn das Semester, in dem das Modul stattfindet keine Datum-Angaben für den Vorlesungsbeginn hat wird false zurückgegeben")
	void test_9() {
		Semester s = new Semester(1, 1, SemesterTyp.WINTERSEMESTER, LocalDate.now(), null, null, null);
		Modul m = new Modul(UUID.randomUUID(), "modul1",
				10000,
				null,
				"peter44",
				true,
				2,
				s,
				null);

		boolean vorlesungEingetragen = m.vorlesungDatumangabenEingetragen();

		assertFalse(vorlesungEingetragen);
	}

	@Test
	@DisplayName("Wenn das Semester, in dem das Modul stattfindet keine Datum-Angaben für das Vorlesungsende hat wird false zurückgegeben")
	void test_10() {
		Semester s = new Semester(1, 1, SemesterTyp.WINTERSEMESTER, null, LocalDate.now(), null, null);
		Modul m = new Modul(UUID.randomUUID(), "modul1",
				10000,
				null,
				"peter44",
				true,
				2,
				s,
				null);

		boolean vorlesungEingetragen = m.vorlesungDatumangabenEingetragen();

		assertFalse(vorlesungEingetragen);
	}

	@Test
	@DisplayName("Wenn das Semester, in dem das Modul stattfindet eine Datum-Angaben für den Vorlesungsbeginn und das Vorlesungende hat wird true zurückgegeben")
	void test_11() {
		Semester s = new Semester(1, 1, SemesterTyp.WINTERSEMESTER, LocalDate.now(), LocalDate.now(), null, null);
		Modul m = new Modul(UUID.randomUUID(), "modul1",
				10000,
				null,
				"peter44",
				true,
				2,
				s,
				null);

		boolean vorlesungEingetragen = m.vorlesungDatumangabenEingetragen();

		assertTrue(vorlesungEingetragen);
	}

	@Test
	@DisplayName("Das übrige benötigt Selbststudium-Zeit wird korrekt berechnet")
	void test_12() {
		Kreditpunkte kp = new Kreditpunkte(10, 10, 10);
		Modul m = new Modul(UUID.randomUUID(), "modul1",
				10000,
				kp,
				"peter44",
				true,
				2,
				null,
				null);

		int remaining = m.getRemainingSelbststudiumZeit();

		assertThat(remaining).isEqualTo(26000);
	}

	@Test
	@DisplayName("Ein Modultermin kann erfolgreich hinzugefügt werden")
	void test_13() {
		Modul m = ModulMother.initModulWithoutTermine();
		Modultermin modultermin = new Modultermin("T1",LocalDateTime.now(), null, "Testtermin", Terminart.SONSTIGES,Terminfrequenz.EINMALIG);

		boolean added = m.putNewModulTermin(modultermin);
		assertTrue(added);
		assertTrue(m.getModultermine().contains(modultermin));
	}

	@Test
	@DisplayName("Wenn ein Modul ein Modultermin der Art KLAUSUR hat, wird true returned")
	void test_14() {
		Modul m = ModulMother.initModulWithKlausurtermin();
		assertTrue(m.hasKlausurtermin());
	}

	@Test
	@DisplayName("Wenn ein Modul kein Modultermin der Art KLAUSUR hat, wird false returned")
	void test_15() {
		Modul m = ModulMother.initModul();
		assertFalse(m.hasKlausurtermin());
	}

	@Test
	@DisplayName("Wenn ein Modul Klausurtermine enthält, können diese Termine aus der Modulliste geholt werden")
	void test_16() {
		Modul m = ModulMother.initModulWithKlausurtermin();

		Modultermin[] klausurTermine = m.getKlausurtermine();

		assertThat(klausurTermine).hasSize(1);
		assertThat(klausurTermine[0].getTerminart()).isEqualTo(Terminart.KLAUSUR);
	}

	@Test
	@DisplayName("Wenn ein Modul keine Klausurtermine enthält, wird ein leeres Array mit length=0 zurückgegeben")
	void test_17() {
		Modul m = ModulMother.initModul();

		Modultermin[] klausurTermine = m.getKlausurtermine();

		assertThat(klausurTermine).isEmpty();
	}

	@Test
	@DisplayName("Wenn versucht wird, ein ModulTermin aus einem Modul zu entfernen, das nicht im Modul enthalten ist, wird false returned")
	void test_18() {
		Modul m = ModulMother.initModulWithoutTermine();
		Modultermin modultermin = new Modultermin("T1",LocalDateTime.now(), null, "Testtermin", Terminart.SONSTIGES,Terminfrequenz.EINMALIG);

		boolean removed = m.removeModulTermin(modultermin);
		assertFalse(removed);
	}

	@Test
	@DisplayName("Ein aktives Modul kann deaktiviert werden")
	void test_19() {
		Modul m = ModulMother.initActiveModul();

		m.toggleActivity();

		assertFalse(m.isActive());
	}

	@Test
	@DisplayName("Ein nicht aktives Modul kann aktiviert werden")
	void test_20() {
		Modul m = ModulMother.initNotActiveModul();

		m.toggleActivity();

		assertTrue(m.isActive());
	}

	@Test
	@DisplayName("Die gelernten Sekunden eines Moduls können auf 0 zurückgesetzt werden")
	void test_21() {
		Modul m = ModulMother.initModul();

		m.resetSecondsLearned();

		assertThat(m.getSecondsLearned()).isZero();
	}

	@Test
	@DisplayName("Sekunden werden einem Modul hinzugefügt")
	void test_22() {
		Modul m = ModulMother.initModul();
		int secondsToAdd = 5000;

		m.addSeconds(secondsToAdd);

		assertThat(m.getSecondsLearned()).isEqualTo(6000);
	}

	@Test
	@DisplayName("Negative Sekunden werden einem Modul nicht hinzugefügt und es wird eine Exception geworfen")
	void test_23() {
		Modul m = ModulMother.initModul();
		int secondsToAdd = -5000;

		assertThrows(IllegalArgumentException.class, () -> m.addSeconds(secondsToAdd));
		assertThat(m.getSecondsLearned()).isEqualTo(1000);
	}
}
