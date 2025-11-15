package com.studyhub.track.adapter.db.modul;

import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ModulDao extends CrudRepository<ModulDto, Integer> {
	Optional<ModulDto> findByFachId(UUID fachId);
	List<ModulDto> findAll();
	List<ModulDto> findByActiveIsTrue();
	List<ModulDto> findByActiveIsFalse();

	@Query("select count(*) from modul where active = true and username = :username")
	Integer countByActiveIsTrue(@Param("username") String username);

	@Query("select count(*) from modul where active = false and username = :username")
	Integer countByActiveIsFalse(@Param("username") String username);

	@Query("select sum(seconds_learned) from modul where username = :username")
	Integer sumAllSeconds(@Param("username") String username);

	@Modifying
	@Query("delete from modul where fach_id = :fachId")
	int deleteByFachId(@Param("fachId") UUID fachId);

	@Modifying
	@Transactional
	@Query("update modul set active = :active where fach_id = :fachId")
	void setActive(@Param("fachId") UUID fachId,
	               @Param("active") boolean active);

	@Query("select seconds_learned from modul where fach_id = :fachId")
	Optional<Integer> findSecondsById(UUID fachId);

	@Query("SELECT m.name FROM Modul m WHERE m.username = :username AND m.seconds_learned = (" +
			"SELECT MAX(m2.seconds_learned) FROM Modul m2 WHERE m2.username = :username" +
			") ORDER BY m.name ASC")
	List<String> findMaxSeconds(@Param("username") String username);


	@Query("SELECT m.name FROM Modul m WHERE m.username = :username AND m.seconds_learned = (" +
			"SELECT MIN(m2.seconds_learned) FROM Modul m2 WHERE m2.username = :username" +
			") ORDER BY m.name ASC")
	List<String> findMinSeconds(@Param("username") String username);


	@Query("select 1")
	Integer isModulDbHealthy();

	List<ModulDto> findByUsername(String username);

	List<ModulDto> findActiveModuleByUsername(boolean active, String username);
}
