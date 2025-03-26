package com.krs.repository;

import com.krs.entity.GeneralTaskInfo;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface TaskRepository extends JpaRepository<GeneralTaskInfo, UUID> {

    @Query("SELECT t.status FROM GeneralTaskInfo t WHERE t.id = :uuid")
    Optional<String> findStatusById(UUID uuid);

    @Query("SELECT t.threads FROM GeneralTaskInfo t WHERE t.id = :uuid")
    Optional<Integer> findThreadsById(UUID uuid);

    @Query("SELECT t.taskType FROM GeneralTaskInfo t WHERE t.id = :uuid")
    Optional<String> findTaskTypeById(UUID uuid);

    @Modifying
    @Transactional
    @Query(value = "UPDATE tasks SET status = :status WHERE id = :uuid", nativeQuery = true)
    int updateTaskStatus(UUID uuid, String status);

}
