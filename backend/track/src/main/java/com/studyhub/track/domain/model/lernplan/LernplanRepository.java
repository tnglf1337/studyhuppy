package com.studyhub.track.domain.model.lernplan;

import java.util.List;
import java.util.UUID;

public interface LernplanRepository {
	Lernplan save(Lernplan lernplan);
	Lernplan findByFachId(UUID fachId);
	Lernplan findActiveByUsername(String username);
	List<Lernplan> findAllByUsername(String username);
	int deleteByFachId(UUID fachId);
	void setIsActiveOfLernplan(UUID fachId, boolean isActive);
	void deactivateAllByUsername(String username);
}
