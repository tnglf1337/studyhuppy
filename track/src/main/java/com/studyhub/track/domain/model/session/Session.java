package com.studyhub.track.domain.model.session;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
public class Session {
	private UUID fachId;
	private String username;
	private String titel;
	private String beschreibung;
	private List<Block> blocks;
}
