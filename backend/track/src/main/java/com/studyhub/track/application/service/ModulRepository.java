package com.studyhub.track.application.service;

import com.studyhub.track.domain.model.modul.Modul;
import com.studyhub.track.domain.model.modul.Modultermin;
import java.util.List;
import java.util.UUID;

public interface ModulRepository {
	Modul save(Modul modul);
	Modul findByUuid(UUID fachId);
	List<Modul> findAll();
	List<Modul> findByActiveIsTrue();
	List<Modul> findByActiveIsFalse();
	List<Modul> findByUsername(String username);
	void saveAll(List<Modul> modulList);
	Integer getTotalStudyTime(String username);
	Integer countActiveModules(String username);
	int findSecondsById(UUID fachId);
	Integer countNotActiveModules(String username);
	String findByMinSeconds(String username);
	String findByMaxSeconds(String username);
	boolean isModulDbHealthy();
	int deleteByUuid(UUID fachId);
	void setActive(UUID fachId, boolean active);
	boolean addModultermin(UUID fachId, Modultermin modultermin);
	boolean deleteModultermin(UUID fachId, Modultermin modultermin);

	List<Modul> findActiveModuleByUsername(boolean active, String username);
}
