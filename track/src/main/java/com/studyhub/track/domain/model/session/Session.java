package com.studyhub.track.domain.model.session;

import lombok.Data;
import java.util.List;
import java.util.UUID;

@Data
public class Session {
	private UUID id;
	private String username;
	private String titel;
	private String beschreibung;
	private List<Block> blocks;
}
