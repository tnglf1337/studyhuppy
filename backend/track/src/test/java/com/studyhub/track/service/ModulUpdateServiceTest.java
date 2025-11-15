package com.studyhub.track.service;

import com.studyhub.track.adapter.db.modul.ModulDao;
import com.studyhub.track.adapter.db.modul.ModulRepositoryImpl;
import com.studyhub.track.application.service.ModulRepository;
import com.studyhub.track.application.service.ModulUpdateService;
import com.studyhub.track.domain.model.modul.Modul;
import com.studyhub.track.util.ModulMother;
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

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@Rollback(false)
@TestPropertySource(properties = {
		"spring.datasource.url=jdbc:postgresql://localhost:${container.port}/modultest",
		"spring.datasource.username=timo",
		"spring.datasource.password=1234"
})
@DataJdbcTest
public class ModulUpdateServiceTest {

	@Container
	static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15.2")
			.withDatabaseName("modultest")
			.withUsername("timo")
			.withPassword("1234");

	@DynamicPropertySource
	static void overrideProps(DynamicPropertyRegistry registry) {
		registry.add("spring.datasource.url", postgres::getJdbcUrl);
		registry.add("spring.datasource.username", postgres::getUsername);
		registry.add("spring.datasource.password", postgres::getPassword);
	}

	@Autowired
	ModulDao modulDao;

	ModulRepository modulRepository;

	ModulUpdateService modulUpdateService;

	@BeforeEach
	void init() {
		modulRepository = new ModulRepositoryImpl(modulDao);
		modulUpdateService = new ModulUpdateService(modulRepository);
	}

	@Test
	@DisplayName("Ein Modul wird erfolgreich geupdated und in der Datenbank gespeichert")
	void test1() {
		Modul modul = ModulMother.initModul();
		UUID modulId = modul.getFachId();
		modulRepository.save(modul);
		int secondsToAdd = 5000;

		modulUpdateService.updateSeconds(modulId, secondsToAdd);

		Modul updatedModul = modulRepository.findByUuid(modulId);
		assertThat(updatedModul.getSecondsLearned()).isEqualTo(modul.getSecondsLearned() + secondsToAdd);
	}



}
