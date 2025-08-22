package com.studyhub.track.service;

import com.studyhub.track.adapter.db.modul.ModulDao;
import com.studyhub.track.adapter.db.modul.ModulRepositoryImpl;
import com.studyhub.track.adapter.db.session.SessionDao;
import com.studyhub.track.adapter.db.session.SessionRepositoryImpl;
import com.studyhub.track.application.service.ModulRepository;
import com.studyhub.track.application.service.SessionRepository;
import com.studyhub.track.domain.model.session.Session;
import com.studyhub.track.util.SessionMother;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@DataJdbcTest
@Rollback(false)
@Sql(scripts = "drop_session_table.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
//@Sql(scripts = "init_session_db_data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@TestPropertySource(properties = {
		"spring.datasource.url=jdbc:postgresql://localhost:${container.port}/sessiontest",
		"spring.datasource.username=timo",
		"spring.datasource.password=1234"
})
public class SessionRepositoryTest {
	@Container
	static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15.2")
			.withDatabaseName("sessiontest")
			.withUsername("timo")
			.withPassword("1234");

	@DynamicPropertySource
	static void overrideProps(DynamicPropertyRegistry registry) {
		registry.add("spring.datasource.url", postgres::getJdbcUrl);
		registry.add("spring.datasource.username", postgres::getUsername);
		registry.add("spring.datasource.password", postgres::getPassword);
	}

	@Autowired
	SessionDao sessionRepository;

	SessionRepository repository;

	@BeforeEach
	void setUp() {
		repository = new SessionRepositoryImpl(sessionRepository);
	}

	@Test
	@DisplayName("Eine Session kann gespeichert werden")
	void saveSession() {
		Session session = SessionMother.createSessionWithNRandomBlocks(5);

		Session savedSession = repository.save(session);

		assertThat(savedSession).isNotNull();
		assertThat(savedSession.getFachId()).isNotNull();
		assertThat(savedSession.getBlocks()).hasSize(5);
	}
}
