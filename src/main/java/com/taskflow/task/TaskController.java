package com.taskflow.task;

import com.taskflow.task.dto.ChangeTaskStatusRequest;
import com.taskflow.task.dto.CreateTaskRequest;
import com.taskflow.task.dto.TaskResponse;
import com.taskflow.task.dto.UpdateTaskRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {
    private final TaskService service;

    public TaskController(TaskService service) { this.service = service; }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TaskResponse create(@Valid @RequestBody CreateTaskRequest request) {
        return TaskResponse.from(service.create(request));
    }

    @GetMapping
    public List<TaskResponse> findAll(@RequestParam(required = false) TaskStatus status) {
        return service.findAll(status).stream().map(TaskResponse::from).toList();
    }

    @GetMapping("/{id}")
    public TaskResponse findById(@PathVariable UUID id) {
        return TaskResponse.from(service.findById(id));
    }

    @PutMapping("/{id}")
    public TaskResponse update(@PathVariable UUID id, @Valid @RequestBody UpdateTaskRequest request) {
        return TaskResponse.from(service.update(id, request));
    }

    @PatchMapping("/{id}/status")
    public TaskResponse changeStatus(@PathVariable UUID id, @Valid @RequestBody ChangeTaskStatusRequest request) {
        return TaskResponse.from(service.changeStatus(id, request.status()));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) { service.delete(id); }
}
