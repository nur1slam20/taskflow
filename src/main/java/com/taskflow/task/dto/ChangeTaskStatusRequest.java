package com.taskflow.task.dto;

import com.taskflow.task.TaskStatus;
import jakarta.validation.constraints.NotNull;

public record ChangeTaskStatusRequest(@NotNull TaskStatus status) {}
