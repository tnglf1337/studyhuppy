package com.studyhub.track.adapter.db.session;

import org.springframework.data.repository.CrudRepository;
import java.util.List;

public interface SessionDao extends CrudRepository<SessionDto, Long> {
	List<SessionDto> findAllByUsername(String username);
}
