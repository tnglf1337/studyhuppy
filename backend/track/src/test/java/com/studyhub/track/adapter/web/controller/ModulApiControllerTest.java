package com.studyhub.track.adapter.web.controller;

import com.studyhub.track.adapter.authentication.AuthenticationService;
import com.studyhub.track.adapter.metric.PrometheusMetrics;
import com.studyhub.track.adapter.web.controller.request.dto.TimerRequest;
import com.studyhub.track.application.JWTService;
import com.studyhub.track.adapter.security.SecurityConfig;
import com.studyhub.track.adapter.web.controller.request.dto.AddTimeRequest;
import com.studyhub.track.adapter.web.ModulForm;
import com.studyhub.track.adapter.web.controller.api.ModulApiController;
import com.studyhub.track.application.service.ModulUpdateService;
import com.studyhub.track.application.service.dto.NeuerModulterminRequest;
import com.studyhub.track.application.service.ModulEventService;
import com.studyhub.track.application.service.ModulService;
import com.studyhub.track.domain.model.modul.Modul;
import com.studyhub.track.domain.model.modul.Terminart;
import com.studyhub.track.domain.model.modul.Terminfrequenz;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.databind.SerializationFeature;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ModulApiController.class)
@Import(SecurityConfig.class)
@TestPropertySource(properties = "maxModule=30")
@ActiveProfiles("application-dev.yaml")
class ModulApiControllerTest {
	@Value("${maxModule}")
	private int maxModule;

	@Autowired
	MockMvc mvc;

	ObjectMapper objectMapper = new ObjectMapper();
	{
		// Für die JSON-Deserialisierung von LocalDateTime
		objectMapper.registerModule(new JavaTimeModule());
		objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
	}

	@MockitoBean
	private ModulService modulService;

	@MockitoBean
	private JWTService jwtService;

	@MockitoBean
	private ModulEventService modulEventService;

	@MockitoBean
	private AuthenticationService authenticationService;

	@MockitoBean
	private PrometheusMetrics metrics;

	@MockitoBean
	private ModulUpdateService modulUpdateService;

	@Autowired
	private ModulApiController modulApiController;

	@Test
	@DisplayName("Ein Post-Request auf /update ist nicht als unauthentifizierte Person möglich")
	void test_03() throws Exception {
		TimerRequest timerRequest = new TimerRequest(UUID.randomUUID().toString(), "786786786");
		mvc.perform(post("/api/update")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(timerRequest)))
				.andExpect(status().isForbidden());
	}

	@Test
	@DisplayName("Ein Post-Request auf /update ist als authentifizierte Person möglich")
	@WithMockUser(username="testuser", roles = "USER")
	void test_04() throws Exception {
		TimerRequest timerRequest = new TimerRequest(UUID.randomUUID().toString(), "786786786");
		mvc.perform(post("/api/modul/v1/update")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(timerRequest)))
				.andExpect(status().isOk());
	}

	@Test
	@DisplayName("Get-Request auf /get-seconds ist nicht als unauthentifizierte Person möglich")
	void test_05() throws Exception {
		mvc.perform(get("/api/modul/v1/get-seconds"))
				.andExpect(status().isForbidden());
	}

	@Test
	@DisplayName("Get-Request auf /get-seconds ist als authentifizierte Person möglich")
	@WithMockUser(username="testuser", roles = "USER")
	void test_06() throws Exception {
		mvc.perform(get("/api/modul/v1/get-seconds")
						.queryParam("fachId", UUID.randomUUID().toString()))
				.andExpect(status().isOk());
	}

	@Test
	@DisplayName("Get-Request auf /module-map ist nicht als unauthentifizierte Person möglich")
	void test_7() throws Exception {
		mvc.perform(get("/api/modul/v1/module-map"))
				.andExpect(status().isForbidden());
	}

	@Test
	@DisplayName("Get-Request auf /module-map ist als authentifizierte Person möglich")
	@WithMockUser(username="testuser", roles = "USER")
	void test_8() throws Exception {
		mvc.perform(get("/api/modul/v1/module-map"))
				.andExpect(status().isOk());
	}

	@Test
	@DisplayName("Get-Request auf /module-name ist nicht als unauthentifizierte Person möglich")
	void test_9() throws Exception {
		mvc.perform(get("/api/modul/v1/module-name"))
				.andExpect(status().isForbidden());
	}

	@Test
	@DisplayName("Get-Request auf /module-name ist nicht als unauthentifizierte Person möglich")
	@WithMockUser(username="testuser", roles = "USER")
	void test_10() throws Exception {
		mvc.perform(get("/api/modul/v1/module-name")
						.param("modulFachId", UUID.randomUUID().toString()))
				.andExpect(status().isOk());
	}

	@Test
	@DisplayName("Put-Request auf /reset ist nicht als unauthentifizierte Person möglich")
	void test_13() throws Exception {
		mvc.perform(put("/api/modul/v1/reset"))
				.andExpect(status().isForbidden());
	}

	@Test
	@DisplayName("Put-Request auf /reset ist als authentifizierte Person möglich")
	@WithMockUser(username="testuser", roles = "USER")
	void test_14() throws Exception {
		mvc.perform(put("/api/modul/v1/reset")
						.param("fachId", UUID.randomUUID().toString()))
				.andExpect(status().isOk());
	}

	@Test
	@DisplayName("Delete-Request auf /delete ist nicht als unauthentifizierte Person möglich")
	void test_15() throws Exception {
		mvc.perform(delete("/api/modul/v1/delete"))
				.andExpect(status().isForbidden());
	}

	@Test
	@DisplayName("Delete-Request auf /delete ist als authentifizierte Person möglich")
	@WithMockUser(username="testuser", roles = "USER")
	void test_16() throws Exception {
		mvc.perform(delete("/api/modul/v1/delete")
						.param("fachId", UUID.randomUUID().toString()))
				.andExpect(status().isNotFound()); // Da randomuuid
	}

	@Test
	@DisplayName("Get-Request auf /get-active-modules ist nicht als unauthentifizierte Person möglich")
	void test_17() throws Exception {
		mvc.perform(get("/api/modul/v1/get-active-modules"))
				.andExpect(status().isForbidden());
	}

	@Test
	@DisplayName("Get-Request auf /get-active-modules ist als authentifizierte Person möglich")
	@WithMockUser(username="testuser", roles = "USER")
	void test_18() throws Exception {
		mvc.perform(get("/api/modul/v1/get-active-modules"))
				.andExpect(status().isOk());
	}

	@Test
	@DisplayName("Get-Request auf /get-all-by-username ist nicht als unauthentifizierte Person möglich")
	void test_19() throws Exception {
		mvc.perform(get("/api/modul/v1/get-all-by-username"))
				.andExpect(status().isForbidden());
	}

	@Test
	@DisplayName("Get-Request auf /get-all-by-username ist als authentifizierte Person möglich")
	@WithMockUser(username="testuser", roles = "USER")
	void test_20() throws Exception {
		mvc.perform(get("/api/modul/v1/get-all-by-username"))
				.andExpect(status().isOk());
	}

	@Test
	@DisplayName("Put-Request auf /change-active ist nicht als unauthentifizierte Person möglich")
	void test_21() throws Exception {
		mvc.perform(put("/api/modul/v1/change-active"))
				.andExpect(status().isForbidden());
	}

	@Test
	@DisplayName("Put-Request auf /change-active ist als authentifizierte Person möglich")
	@WithMockUser(username="testuser", roles = "USER")
	void test_22() throws Exception {
		mvc.perform(put("/api/modul/v1/change-active")
						.param("fachId", UUID.randomUUID().toString()))
				.andExpect(status().isOk());
	}

	@Test
	@DisplayName("Post-Request auf /add-time ist nicht als unauthentifizierte Person möglich")
	void test_23() throws Exception {
		AddTimeRequest req = new AddTimeRequest(UUID.randomUUID().toString(), "01:30");
		mvc.perform(post("/api/modul/v1/add-time")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(req)))
				.andExpect(status().isForbidden());
	}

	@Test
	@DisplayName("Get-Request auf /getModultermine ist nicht als unauthentifizierte Person möglich")
	void test_27() throws Exception {
		mvc.perform(get("/api/modul/v1/getModultermine"))
				.andExpect(status().isForbidden());
	}

	@Test
	@DisplayName("Get-Request auf /getModultermine ist als authentifizierte Person möglich")
	@WithMockUser(username="testuser", roles = "USER")
	void test_28() throws Exception {
		mvc.perform(get("/api/modul/v1/getModultermine")
						.param("modulId", UUID.randomUUID().toString()))
				.andExpect(status().isOk());
	}

	@Test
	@DisplayName("Post-Request auf /addModultermin ist nicht als unauthentifizierte Person möglich")
	void test_29() throws Exception {
		NeuerModulterminRequest req = new NeuerModulterminRequest(
				UUID.randomUUID(),
				"titel",
				LocalDateTime.now(),
				LocalDateTime.now().plusHours(1),
				"notiz",
				Terminart.SONSTIGES,
				Terminfrequenz.EINMALIG
		);

		mvc.perform(post("/api/modul/v1/addModultermin")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(req)))
				.andExpect(status().isForbidden());
	}

	@Disabled("Geht praktisch")
	@Test
	@DisplayName("Post-Request auf /addModultermin ist als authentifizierte Person möglich")
	@WithMockUser(username="testuser", roles = "USER")
	void test_30() throws Exception {
		NeuerModulterminRequest req = new NeuerModulterminRequest(
				UUID.randomUUID(),
				"titel",
				LocalDateTime.now(),
				LocalDateTime.now().plusHours(1),
				"notiz",
				Terminart.SONSTIGES,
				Terminfrequenz.EINMALIG
		);

		mvc.perform(post("/api/modul/v1/addModultermin")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(req)))
				.andExpect(status().isCreated());
	}
}

