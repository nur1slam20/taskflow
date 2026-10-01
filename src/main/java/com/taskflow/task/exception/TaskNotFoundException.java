package com.taskflow.task.exception;

import java.util.UUID;

public class TaskNotFoundException extends RuntimeException {
    public TaskNotFoundException(UUID id) { super("Задача с id %s не найдена".formatted(id)); }
}
