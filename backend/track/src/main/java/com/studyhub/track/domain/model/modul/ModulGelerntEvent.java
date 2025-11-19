package com.studyhub.track.domain.model.modul;

import java.time.LocalDate;
import java.util.UUID;

public record ModulGelerntEvent(
		UUID eventId,
		UUID modulId,
		String username,
		int secondsLearned,
		LocalDate dateGelernt)
{
	public static ModulGelerntEvent initEvent(UUID modulId, int secondsLearned, String username) {
		return new ModulGelerntEvent(UUID.randomUUID(), modulId, username, secondsLearned, LocalDate.now());
	}
}
