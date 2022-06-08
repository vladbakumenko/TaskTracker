import manager.InMemoryTaskManager;
import manager.TaskStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Subtask;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class EpicTest {

    Epic epic;
    InMemoryTaskManager inMemoryTaskManager;
    Subtask subtask1;
    Subtask subtask2;
    Subtask subtask3;

    @BeforeEach
    public void beforeEach() {
        inMemoryTaskManager = new InMemoryTaskManager();
        epic = inMemoryTaskManager.createEpic("Эпик", "Описание эпика");
        subtask1 = inMemoryTaskManager.createSubtask(epic.getId(), "Сабтаск1", "Описание сабтаск1",
                LocalDateTime.now().plusDays(1), Duration.ofMinutes(120));
        subtask2 = inMemoryTaskManager.createSubtask(epic.getId(), "Сабтаск2", "Описание сабтаск2",
                LocalDateTime.now().plusDays(2), Duration.ofMinutes(120));
        subtask3 = inMemoryTaskManager.createSubtask(epic.getId(), "Сабтаск3", "Описание сабтаск3",
                LocalDateTime.now().plusDays(3), Duration.ofMinutes(120));
    }

    @Test
    public void epicShouldBeWithStatusNewIfThereAreNoSubtasks() {
        epic.getSubtasksInEpic().clear();

        inMemoryTaskManager.updateEpicStatus(epic);

        assertEquals(TaskStatus.NEW, epic.getStatus());
    }

    @Test
    public void epicShouldBeWithStatusNewIfAllSubtasksWithStatusNew() {

        inMemoryTaskManager.updateEpicStatus(epic);

        assertEquals(TaskStatus.NEW, epic.getStatus());
    }

    @Test
    public void epicShouldBeWithStatusDoneIfAllSubtasksWithStatusDone() {
        for (Subtask subtask : epic.getSubtasksInEpic().values()) {
            subtask.setStatus(TaskStatus.DONE);
        }

        inMemoryTaskManager.updateEpicStatus(epic);

        assertEquals(TaskStatus.DONE, epic.getStatus());
    }

    @Test
    public void epicShouldBeWithStatusInProgressIfSubtasksWithStatusDoneAndNew() {
        for (Subtask subtask : epic.getSubtasksInEpic().values()) {
            subtask.setStatus(TaskStatus.DONE);
        }

        subtask3.setStatus(TaskStatus.NEW);

        inMemoryTaskManager.updateEpicStatus(epic);

        assertEquals(TaskStatus.IN_PROGRESS, epic.getStatus());
    }

    @Test
    public void epicShouldBeWithStatusInProgressIfSubtasksWithStatusDoneAndNewAndInProgress() {
        for (Subtask subtask : epic.getSubtasksInEpic().values()) {
            subtask.setStatus(TaskStatus.DONE);
        }

        subtask3.setStatus(TaskStatus.NEW);

        subtask2.setStatus(TaskStatus.IN_PROGRESS);

        inMemoryTaskManager.updateEpicStatus(epic);

        assertEquals(TaskStatus.IN_PROGRESS, epic.getStatus());
    }

    @Test
    public void epicShouldBeWithStatusInProgressIfSubtasksWithStatusInProgress() {
        for (Subtask subtask : epic.getSubtasksInEpic().values()) {
            subtask.setStatus(TaskStatus.IN_PROGRESS);
        }

        inMemoryTaskManager.updateEpicStatus(epic);

        assertEquals(TaskStatus.IN_PROGRESS, epic.getStatus());
    }

    @Test
    public void dateAndTimeShouldBeCorrectWithDifferentSubtasks() {
        Subtask subtaskTest = inMemoryTaskManager.createSubtask(epic.getId(), "Test", "Subtask",
                LocalDateTime.now(), Duration.ofMinutes(10));

        Subtask subtaskTest2 = inMemoryTaskManager.createSubtask(epic.getId(), "Test", "Subtask",
                LocalDateTime.now().plusDays(10), Duration.ofMinutes(10));

        assertEquals(epic.getStartTime(), subtaskTest.getStartTime());

        assertEquals(epic.getEndTime(), subtaskTest2.getEndTime());

        Duration duration = Duration.ofMinutes(0);
        for (Subtask subtask : epic.getSubtasksInEpic().values()) {
            duration = duration.plusMinutes(subtask.getDuration().get().toMinutes());
        }

        assertEquals(epic.getDuration().get(), duration);

        //при пустом списке подзадач
        epic.getSubtasksInEpic().clear();

        assertFalse(epic.getStartTime().isPresent());

        assertEquals(epic.getEndTime(), LocalDateTime.now());

        assertEquals(epic.getDuration(), Duration.ofMinutes(0));
    }

}