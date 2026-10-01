package com.taskflow.task;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface TaskRepository extends JpaRepository<Task, UUID> {
    List<Task> findAllByStatusOrderByCreatedAtDesc(TaskStatus status);
    List<Task> findAllByOrderByCreatedAtDesc();
}
