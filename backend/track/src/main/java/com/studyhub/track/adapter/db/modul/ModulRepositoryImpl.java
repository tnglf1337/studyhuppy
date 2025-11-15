package com.studyhub.track.adapter.db.modul;

import com.studyhub.track.application.service.ModulRepository;
import com.studyhub.track.application.service.NoModulPresentException;
import com.studyhub.track.domain.model.modul.Modul;
import com.studyhub.track.domain.model.modul.Modultermin;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.studyhub.track.adapter.db.modul.ModulMapper.toModul;
import static com.studyhub.track.adapter.db.modul.ModulMapper.toModulDto;

@Repository
public class ModulRepositoryImpl implements ModulRepository {

	private final ModulDao modulDao;

	public ModulRepositoryImpl(ModulDao modulDao) {
		this.modulDao = modulDao;
	}

	@Override
	public int findSecondsById(UUID fachId) {
		Optional<Integer> oldSeconds = modulDao.findSecondsById(fachId);
		return oldSeconds.orElse(0);
	}

	@Override
	public Modul save(Modul modul) {
		Integer existingDbKey =
				modulDao.findByFachId(modul.getFachId()).map(ModulDto::id).orElse(null);
		return toModul(modulDao.save(ModulMapper.toModulDto(modul, existingDbKey)));
	}

	@Override
	public List<Modul> findAll() {
		return modulDao.findAll().stream().map(e -> toModul(e)).toList();
	}

	@Override
	public int deleteByUuid(UUID fachId) {

		return modulDao.deleteByFachId(fachId);
	}

	@Override
	public Modul findByUuid(UUID fachId) {
		Optional<ModulDto> dto = modulDao.findByFachId(fachId).stream().findAny();
		if(dto.isEmpty()) throw new NoModulPresentException("no modul present with id: %s".formatted(fachId.toString()));
		return toModul(dto.get());
	}

	@Override
	public String findByMaxSeconds(String username) {
		Optional<String> modulName = modulDao.findMaxSeconds(username).stream().findFirst();
		return modulName.orElse(null);
	}

	@Override
	public String findByMinSeconds(String username) {
		Optional<String> modulName = modulDao.findMinSeconds(username).stream().findFirst();
		return modulName.orElse(null);
	}

	@Override
	public boolean isModulDbHealthy() {
		Integer result = modulDao.isModulDbHealthy();
		return result != null;
	}

	@Override
	public void saveAll(List<Modul> modulList) {
		List<ModulDto> mapped = modulList.stream().map(ModulMapper::toModulDto).toList();
		modulDao.saveAll(mapped);
	}

	@Override
	public List<Modul> findByActiveIsTrue() {
		return modulDao.findByActiveIsTrue().stream().map(ModulMapper::toModul).toList();
	}

	@Override
	public List<Modul> findByActiveIsFalse() {
		return modulDao.findByActiveIsFalse().stream().map(ModulMapper::toModul).toList();
	}

	@Override
	public List<Modul> findByUsername(String username) {
		return modulDao.findByUsername(username).stream().map(ModulMapper::toModul).toList();
	}

	@Override
	public Integer getTotalStudyTime(String username) {
		return modulDao.sumAllSeconds(username);
	}

	@Override
	public Integer countActiveModules(String username) {
		return modulDao.countByActiveIsTrue(username);
	}

	@Override
	public Integer countNotActiveModules(String username) {
		return modulDao.countByActiveIsFalse(username);
	}

	@Override
	public void setActive(UUID fachId, boolean active) {
		modulDao.setActive(fachId, active);
	}

	@Override
	public boolean addModultermin(UUID fachId, Modultermin modultermin) {
		if (modultermin == null || fachId == null) return false;

		Optional<ModulDto> dto = modulDao.findByFachId(fachId);

		if (dto.isPresent()) {
			Modul modul = toModul(dto.get());
			modul.putNewModulTermin(modultermin);
			save(modul);
			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean deleteModultermin(UUID fachId, Modultermin modultermin) {
		if (modultermin == null || fachId == null) return false;
		Optional<ModulDto> dto = modulDao.findByFachId(fachId);

		if (dto.isPresent()) {
			Modul modul = toModul(dto.get());
			boolean succes = modul.removeModulTermin(modultermin);
			save(modul);
			return succes;
		} else {
			return false;
		}
	}

	@Override
	public List<Modul> findActiveModuleByUsername(boolean active, String username) {
		return modulDao.findAll().stream()
				.filter(m -> m.active() == active)
				.filter(m -> m.username().equals(username))
				.sorted(Comparator.comparing(ModulDto::secondsLearned).reversed())
				.map(ModulMapper::toModul)
				.toList();
	}
}