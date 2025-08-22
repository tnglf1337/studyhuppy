package com.studyhub.track.adapter.db.session;

import org.springframework.data.repository.CrudRepository;

public interface SessionDao extends CrudRepository<SessionDto, Long> {
}
