package com.studyhub.track.domain.model.session;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.UUID;

@Data
@AllArgsConstructor
public class Block {
	private UUID fachId;
	private UUID modulId;
	private int lernzeitSeconds;
	private int pausezeitSeconds;
}
