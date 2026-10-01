package com.taskflow.task;

import com.taskflow.task.dto.CreateTaskRequest;
import com.taskflow.task.dto.UpdateTaskRequest;
import com.taskflow.task.exception.TaskNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {
    @Mock
    private TaskRepository repository;

    @InjectMocks
    private TaskService service;

    @Test
    void createTrimsTitleAndSavesTaskWithProvidedFields() {
        var request = new CreateTaskRequest("  Learn Spring  ", "Build the first endpoint", LocalDate.of(2026, 10, 15));
        when(repository.save(any(Task.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Task created = service.create(request);

        ArgumentCaptor<Task> captor = ArgumentCaptor.forClass(Task.class);
        verify(repository).save(captor.capture());
        assertEquals("Learn Spring", captor.getValue().getTitle());
        assertEquals("Build the first endpoint", created.getDescription());
        assertEquals(LocalDate.of(2026, 10, 15), created.getDueDate());
        assertEquals(TaskStatus.TODO, created.getStatus());
    }

    @Test
    void findAllUsesStatusFilterWhenProvided() {
        var expected = List.of(mock(Task.class));
        when(repository.findAllByStatusOrderByCreatedAtDesc(TaskStatus.IN_PROGRESS)).thenReturn(expected);

        assertSame(expected, service.findAll(TaskStatus.IN_PROGRESS));
        verify(repository).findAllByStatusOrderByCreatedAtDesc(TaskStatus.IN_PROGRESS);
        verify(repository, never()).findAllByOrderByCreatedAtDesc();
    }

    @Test
    void findAllReturnsAllTasksWhenStatusIsMissing() {
        var expected = List.of(mock(Task.class));
        when(repository.findAllByOrderByCreatedAtDesc()).thenReturn(expected);

        assertSame(expected, service.findAll(null));
        verify(repository).findAllByOrderByCreatedAtDesc();
    }

    @Test
    void updateChangesEditableFields() {
        UUID id = UUID.randomUUID();
        Task task = new Task("Old title", "Old description", null);
        when(repository.findById(id)).thenReturn(Optional.of(task));

        Task updated = service.update(id, new UpdateTaskRequest("  New title ", "New description", LocalDate.of(2026, 11, 1)));

        assertEquals("New title", updated.getTitle());
        assertEquals("New description", updated.getDescription());
        assertEquals(LocalDate.of(2026, 11, 1), updated.getDueDate());
    }

    @Test
    void changeStatusUpdatesTask() {
        UUID id = UUID.randomUUID();
        Task task = new Task("Title", null, null);
        when(repository.findById(id)).thenReturn(Optional.of(task));

        Task updated = service.changeStatus(id, TaskStatus.DONE);

        assertEquals(TaskStatus.DONE, updated.getStatus());
    }

    @Test
    void deleteRemovesFoundTask() {
        UUID id = UUID.randomUUID();
        Task task = mock(Task.class);
        when(repository.findById(id)).thenReturn(Optional.of(task));

        service.delete(id);

        verify(repository).delete(task);
    }

    @Test
    void missingTaskThrowsNotFound() {
        UUID id = UUID.randomUUID();
        when(repository.findById(id)).thenReturn(Optional.empty());

        assertThrows(TaskNotFoundException.class, () -> service.findById(id));
    }
}
