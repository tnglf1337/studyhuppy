package com.studyhub.track.domain.model.modul;

import com.studyhub.track.domain.model.AggregateRoot;
import com.studyhub.track.domain.model.semester.Semester;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.*;

@AllArgsConstructor
@NoArgsConstructor
@AggregateRoot
@Data
public class Modul {
	private UUID fachId;
	private String name;
	private Integer secondsLearned;
	private Kreditpunkte kreditpunkte;
	private String username;
	private boolean active;
	private int semesterstufe;
	private Semester semester; //TODO remove field from project
	private List<Modultermin> modultermine;

	public String getGesamtArbeitsaufwand() {
		return String.valueOf(kreditpunkte.getSelbststudiumStunden() + kreditpunkte.getKontaktzeitStunden());
	}

	public boolean ueberschreitetGesamtarbeitsaufwand() {
		return secondsLearned >= Integer.parseInt(getGesamtArbeitsaufwand())*60*60;
	}

	public boolean ueberschreitetSelbststudiumaufwand() {
		return secondsLearned >= kreditpunkte.getSelbststudiumStunden()*60*60;
	}

	public boolean vorlesungDatumangabenEingetragen() {
		return semester.getVorlesungBeginn() != null && semester.getVorlesungEnde() != null;
	}

	public int getRemainingSelbststudiumZeit() {
		return (kreditpunkte.getSelbststudiumStunden()*60*60) - secondsLearned;
	}

	public boolean putNewModulTermin(Modultermin modultermin) {
		if (!this.modultermine.contains(modultermin)) {
			return this.modultermine.add(modultermin);
		}
		return false;
	}

	public boolean removeModulTermin(Modultermin modultermin) {
		if (modultermine.contains(modultermin)) {
			modultermine.remove(modultermin);
			return true;
		}
		return false;
	}

	public boolean hasKlausurtermin() {
		int c = 0;
		for (Modultermin modultermin : modultermine) {
			if(modultermin.getTerminart() == Terminart.KLAUSUR)  c++;
		}

		return c > 0;
	}

	public Modultermin[] getKlausurtermine() {
		if (hasKlausurtermin()) {
			List<Modultermin> l = new ArrayList<>();
			for (Modultermin modultermin : modultermine) {
				if (modultermin.getTerminart() == Terminart.KLAUSUR) {
					l.add(modultermin);
				}
			}
			return l.toArray(new Modultermin[0]);
		}
		return new Modultermin[0];
	}

	public void toggleActivity() {
		this.active = !this.active;
	}

	public void resetSecondsLearned() {
		this.secondsLearned = 0;
	}

	public void addSeconds(int secondsToAdd) {
		if(secondsToAdd < 0) throw new IllegalArgumentException("seconds to add must be non-negative");
		this.secondsLearned += secondsToAdd;
	}
}
