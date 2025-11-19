package com.studyhub.track.adapter.web.controller.request.dto;

@Deprecated
public record ModulRequest(
		String modulName,
		Integer creditPoints,
		Integer kontaktzeitStunden,
		Integer selbststudiumStunden
) {
}
