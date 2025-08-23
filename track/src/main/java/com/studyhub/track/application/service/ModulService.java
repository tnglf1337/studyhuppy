package com.studyhub.track.application.service;


import com.studyhub.track.adapter.web.controller.api.ModulSelectDto;
import com.studyhub.track.application.JWTService;
import com.studyhub.track.application.service.dto.NeuerModulterminRequest;
import com.studyhub.track.domain.model.modul.Modul;
import com.studyhub.track.domain.model.modul.Modultermin;
import com.studyhub.track.domain.model.modul.Terminart;
import com.studyhub.track.domain.model.modul.Terminfrequenz;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.IntStream;

@Service
public class ModulService {
	private final Logger log = LoggerFactory.getLogger(ModulService.class);
	private final ModulRepository repo;
	private final JWTService jwtService;

	public ModulService(ModulRepository modulRepository, JWTService jwtService) {
		this.repo = modulRepository;
		this.jwtService = jwtService;
	}

	public void saveNewModul(Modul modul) {
		repo.save(modul);
		log.info("Saved new modul '%s'".formatted(modul.getName()));
	}

	public void saveAllNewModule(List<Modul> modulList) {
		repo.saveAll(modulList);
	}

	public List<Modul> findAll() {
		return repo.findAll();
	}

	public void updateSeconds(UUID fachId, int seconds) throws Exception {
		int res = repo.updateSecondsByUuid(fachId, seconds);
		if (res == 0) throw new Exception();
		log.info("updated modul with id:%s to seconds=%s".formatted(fachId.toString(), String.valueOf(seconds)));
	}

	public void deleteModul(UUID fachId) {
		repo.deleteByUuid(fachId);
		log.warn("deleted modul with id:%s".formatted(fachId.toString()));
	}

	public void resetModulTime(UUID fachId) throws Exception {
		int res = repo.updateSecondsByUuid(fachId, 0);
		if (res == 0) throw new Exception();
		log.info("reseted modul time to 0 with id:%s".formatted(fachId.toString()));
	}

	public List<Modul> findActiveModuleByUsername(boolean active, String username) {
		return repo.findActiveModuleByUsername(active, username);
	}

	public void changeActivity(UUID fachId) {
		Modul m = repo.findByUuid(fachId);
		boolean isActive = m.isActive();

		if (isActive) {
			repo.setActive(fachId, false);
			log.info("deactivated modul id:%s".formatted(fachId.toString()));
		} else {
			repo.setActive(fachId, true);
			log.info("activated modul id:%s".formatted(fachId.toString()));
		}
	}

	public void deactivateModul(UUID fachId) {
		repo.setActive(fachId, false);
		log.info("deactivated modul id:%s".formatted(fachId.toString()));
	}

	public void addTime(UUID fachId, String time) throws Exception {
		TimeConverter tc = new TimeConverter();
		int addSeconds = tc.timeToSeconds(time);
		int alreadyLearned = repo.findSecondsById(fachId);
		int newSeconds = addSeconds + alreadyLearned;
		updateSeconds(fachId, newSeconds);
	}

	public int getSecondsForId(UUID fachId) {
		return repo.findSecondsById(fachId);
	}

	public String findModulNameByFachid(UUID fachId) {
		return repo.findByUuid(fachId).getName();
	}

	public Integer getTotalStudyTimeForUser(String username) {
		Integer totalStudyTime = repo.getTotalStudyTime(username);

		if(totalStudyTime == null) {
			return null;
		}
		return totalStudyTime;
	}

	public Map<Integer, Integer> getTotalStudyTimePerFachSemester(String username) {
		List<Modul> allModule = repo.findByUsername(username);
		Set<Integer> fachsemesterMap = new HashSet<>();
		Map<Integer, Integer> res = new HashMap<>();

		for (Modul modul : allModule) {
			int fachsemester = modul.getSemesterstufe();
			fachsemesterMap.add(fachsemester);
		}

		for (Integer thisFachsemester : fachsemesterMap) {
			List<Modul> moduleWithThisFachsemester = allModule.stream()
					.filter(e -> Objects.equals(e.getSemesterstufe(), thisFachsemester))
					.toList();

			int sumSecondsLearned = moduleWithThisFachsemester.stream()
					.flatMapToInt(e -> IntStream.of(e.getSecondsLearned()))
					.sum();

			res.put(thisFachsemester, sumSecondsLearned);
		}

		return res;
	}

	public Integer countActiveModules(String username) {
		return repo.countActiveModules(username);
	}

	public Object countNotActiveModules(String username) {
		return repo.countNotActiveModules(username);
	}

	public String findModulWithMaxSeconds(String username) {
		return repo.findByMaxSeconds(username);
	}

	public String findModulWithMinSeconds(String username) {
		return repo.findByMinSeconds(username);
	}

	public Map<UUID, String> getModuleMap(String username) {
		List<Modul> all = repo.findByUsername(username);
		Map<UUID, String> map = new HashMap<>();

		for (Modul m: all) {
			map.put(m.getFachId(), m.getName());
		}

		return map;
	}

	public boolean isModulDbHealthy() {
		log.info("fetching database health status");
		return repo.isModulDbHealthy();
	}


	public String dateStringGer(String zeitString) {
		try {
			DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
			LocalDateTime dateTime = LocalDateTime.parse(zeitString, inputFormatter);

			DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy, HH:mm")
					.withLocale(Locale.GERMANY);
			return dateTime.format(outputFormatter);
		} catch (Exception e) {
			return "Ungültiges Datums-/Zeitformat";
		}
	}

	public Modul findByFachId(UUID uuid) {
		return repo.findByUuid(uuid);
	}

	public List<Modul> findAllByUsername(String username) {
		return repo.findByUsername(username);
	}

	public boolean modulCanBeCreated(String username, int limit) {
		List<Modul> module = repo.findByUsername(username);
        return module.size() < limit;
    }

	public List<Modultermin> getModultermineByModulId(UUID modulId) {
		Modul m = findByFachId(modulId);
		if (m == null) {
			return Collections.emptyList();
		}
		return m.getModultermine();
	}

	public void saveNewModultermin(NeuerModulterminRequest req) {
		Modultermin termin = req.toModultermin();
		UUID modulId = req.getModulId();
		Modul modul = findByFachId(modulId);
		modul.putNewModulTermin(termin);
		repo.save(modul);
	}

	public Map<Integer, List<Modul>> getFachsemesterModuleMap(String username) {
		List<Modul> module = repo.findActiveModuleByUsername(true, username);
		Map<Integer, List<Modul>> moduleMap = new TreeMap<>(Comparator.reverseOrder());
		Set<Integer> fachsemester = new HashSet<>();

		for (Modul m : module) {
			fachsemester.add(m.getSemesterstufe());
		}

		for (Integer fachsem : fachsemester) {
			List<Modul> listForMap = module.stream().filter( e -> e.getSemesterstufe() == fachsem).toList();
			moduleMap.put(fachsem, listForMap);
		}

		return moduleMap;
	}

	public List<ModulSelectDto> getModulSelectData(String username) {
		List<ModulSelectDto> res = new LinkedList<>();
		List<Modul> module = repo.findByUsername(username);
		for (Modul m : module) {
			res.add(new ModulSelectDto(m.getFachId(), m.getName()));
		}
		return res;
	}

	public void addSecondsToModul(UUID uuid, int secondsToAdd) {
		int oldSeconds = repo.findSecondsById(uuid);
		oldSeconds += secondsToAdd;
		try {
			updateSeconds(uuid, oldSeconds);
		} catch(Exception e) {
			System.out.println("Error while updating seconds");
			System.out.println(e.getMessage());
		}

	}
}
