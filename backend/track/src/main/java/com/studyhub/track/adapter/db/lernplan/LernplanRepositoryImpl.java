package com.studyhub.track.adapter.db.lernplan;

import com.studyhub.track.domain.model.lernplan.LernplanRepository;
import com.studyhub.track.domain.model.lernplan.Lernplan;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.studyhub.track.adapter.db.lernplan.LernplanMapper.toDomain;
import static com.studyhub.track.adapter.db.lernplan.LernplanMapper.toDto;

@Repository
public class LernplanRepositoryImpl implements LernplanRepository {

	private final LernplanDao lernplanDao;

	public LernplanRepositoryImpl(LernplanDao lernplanDao) {
		this.lernplanDao = lernplanDao;
	}

	@Override
	public Lernplan save(Lernplan lernplan) {
		Long existringId = lernplanDao.findByFachId(lernplan.getFachId()).map(LernplanDto::id).orElse(null);
		LernplanDto dto = toDto(lernplan, existringId);
		return  toDomain(lernplanDao.save(dto));
	}

	@Override
	public Lernplan findByFachId(UUID fachId) {
		Optional<LernplanDto> dto = lernplanDao.findByFachId(fachId);
		return dto.map(LernplanMapper::toDomain).orElse(null);
	}

	@Override
	public Lernplan findActiveByUsername(String username) {
		LernplanDto dto = lernplanDao.findActiveByUsername(username);
		if (dto == null) {
			return null;
		}
		return toDomain(dto);
	}

	@Override
	public List<Lernplan> findAllByUsername(String username) {
		List<LernplanDto> dtos = lernplanDao.findAllByUsername(username);
		return dtos.stream().map(LernplanMapper::toDomain).toList();
	}

	@Override
	public int deleteByFachId(UUID fachId) {
		return lernplanDao.deleteByFachId(fachId);
	}

	@Override
	public void setIsActiveOfLernplan(UUID fachId, boolean isActive) {
		lernplanDao.setIsActiveOfLernplan(fachId, isActive);
	}

	@Override
	public void deactivateAllByUsername(String username) {
		lernplanDao.deactivateAllByUsername(username);
	}


}
