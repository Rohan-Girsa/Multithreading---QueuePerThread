package com.example.repo;

import java.util.List;
import java.util.Optional;

import jakarta.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.dto.CallFile;
import com.example.entities.TblBase;


@Repository
@Transactional
public interface TblBaseRepo extends JpaRepository<TblBase, Long>{
	@Query(value = "SELECT \r\n"
			+ "    id AS id,\r\n"
			+ "    REPLACE(REPLACE(msisdn, '\\r', ''), '\\n', '') AS ani,\r\n"
			+ "    REPLACE(REPLACE(context, '\\r', ''), '\\n', '') AS context,\r\n"
			+ "    dnis,\r\n"
			+ "    queue_code AS queuecode,\r\n"
			+ "    template_id AS templateid,\r\n"
			+ "    account_id AS accountid,\r\n"
			+ "    user_id AS userid,\r\n"
			+ "    'idle' AS status,\r\n"
			+ "    voice_log_id AS voicelogid \r\n"
			+ "FROM \r\n"
			+ "    tbl_base_test\r\n"
			+ "LIMIT 10;",nativeQuery = true)
	Optional<List<CallFile>> findData();
	@Query(value = "select * from tbl_base limit 1",nativeQuery = true)
	TblBase findFirst();
	void deleteById(Long id);
	int countByContext(String context);
	
	@Modifying
	@Query(value = "delete from tbl_base_test where id in :ids",nativeQuery = true)
	void deleteFromList(List<Long> ids);
}
