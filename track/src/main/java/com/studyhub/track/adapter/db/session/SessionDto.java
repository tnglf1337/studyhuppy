package com.studyhub.track.adapter.db.session;

import com.studyhub.track.domain.model.session.Block;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.util.List;
import java.util.UUID;

@Table("session")
public record SessionDto(
		@Id Long id,
		UUID fachId,
		String username,
		String titel,
		String beschreibung,
		List<Block> blocks
) {
}
