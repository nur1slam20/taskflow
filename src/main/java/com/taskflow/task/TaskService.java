package com.taskflow.task;

import com.taskflow.task.dto.CreateTaskRequest;
import com.taskflow.task.dto.UpdateTaskRequest;
import com.taskflow.task.exception.TaskNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class TaskService {
    private final TaskRepository repository;

    public TaskService(TaskRepository repository) { this.repository = repository; }

    public Task create(CreateTaskRequest request) {
        return repository.save(new Task(request.title().trim(), request.description(), request.dueDate()));
    }

    @Transactional(readOnly = true)
    public List<Task> findAll(TaskStatus status) {
        return status == null ? repository.findAllByOrderByCreatedAtDesc()
                : repository.findAllByStatusOrderByCreatedAtDesc(status);
    }

    @Transactional(readOnly = true)
    public Task findById(UUID id) { return getOrThrow(id); }

    public Task update(UUID id, UpdateTaskRequest request) {
        Task task = getOrThrow(id);
        task.update(request.title().trim(), request.description(), request.dueDate());
        return task;
    }

    public Task changeStatus(UUID id, TaskStatus status) {
        Task task = getOrThrow(id);
        task.changeStatus(status);
        return task;
    }

    public void delete(UUID id) { repository.delete(getOrThrow(id)); }

    private Task getOrThrow(UUID id) {
        return repository.findById(id).orElseThrow(() -> new TaskNotFoundException(id));
    }
}
