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

        epic = new Epic("Эпик", "Описание эпика");
        inMemoryTaskManager.addEpic(epic = new Epic("Эпик", "Описание эпика"));

        subtask1 = new Subtask("Сабтаск1", "Описание сабтаск1", epic.getId(),
                LocalDateTime.now().plusDays(1), Duration.ofMinutes(120));
        inMemoryTaskManager.addSubtask(subtask1);

        subtask2 = new Subtask("Сабтаск2", "Описание сабтаск2", epic.getId(),
                LocalDateTime.now().plusDays(2), Duration.ofMinutes(120));
        inMemoryTaskManager.addSubtask(subtask2);

        subtask3 = new Subtask("Сабтаск3", "Описание сабтаск3", epic.getId(),
                LocalDateTime.now().plusDays(3), Duration.ofMinutes(120));
        inMemoryTaskManager.addSubtask(subtask3);
    }

    @Test
    public void epicShouldBeWithStatusNewIfThereAreNoSubtasks() {
        epic.getSubtasksInEpic().clear();

        inMemoryTaskManager.putEpicStatus(epic);

        assertEquals(TaskStatus.NEW, epic.getStatus());
    }

    @Test
    public void epicShouldBeWithStatusNewIfAllSubtasksWithStatusNew() {

        inMemoryTaskManager.putEpicStatus(epic);

        assertEquals(TaskStatus.NEW, epic.getStatus());
    }

    @Test
    public void epicShouldBeWithStatusDoneIfAllSubtasksWithStatusDone() {
        for (Subtask subtask : epic.getSubtasksInEpic().values()) {
            subtask.setStatus(TaskStatus.DONE);
        }

        inMemoryTaskManager.putEpicStatus(epic);

        assertEquals(TaskStatus.DONE, epic.getStatus());
    }

    @Test
    public void epicShouldBeWithStatusInProgressIfSubtasksWithStatusDoneAndNew() {
        for (Subtask subtask : epic.getSubtasksInEpic().values()) {
            subtask.setStatus(TaskStatus.DONE);
        }

        subtask3.setStatus(TaskStatus.NEW);

        inMemoryTaskManager.putEpicStatus(epic);

        assertEquals(TaskStatus.IN_PROGRESS, epic.getStatus());
    }

    @Test
    public void epicShouldBeWithStatusInProgressIfSubtasksWithStatusDoneAndNewAndInProgress() {
        for (Subtask subtask : epic.getSubtasksInEpic().values()) {
            subtask.setStatus(TaskStatus.DONE);
        }

        subtask3.setStatus(TaskStatus.NEW);

        subtask2.setStatus(TaskStatus.IN_PROGRESS);

        inMemoryTaskManager.putEpicStatus(epic);

        assertEquals(TaskStatus.IN_PROGRESS, epic.getStatus());
    }

    @Test
    public void epicShouldBeWithStatusInProgressIfSubtasksWithStatusInProgress() {
        for (Subtask subtask : epic.getSubtasksInEpic().values()) {
            subtask.setStatus(TaskStatus.IN_PROGRESS);
        }

        inMemoryTaskManager.putEpicStatus(epic);

        assertEquals(TaskStatus.IN_PROGRESS, epic.getStatus());
    }

    @Test
    public void dateAndTimeShouldBeCorrectWithDifferentSubtasks() {
        Subtask subtaskTest = new Subtask("Test", "Subtask", epic.getId(),
                LocalDateTime.now(), Duration.ofMinutes(10));

        inMemoryTaskManager.addSubtask(subtaskTest);

        Subtask subtaskTest2 = new Subtask("Test", "Subtask", epic.getId(),
                LocalDateTime.now().plusDays(10), Duration.ofMinutes(10));
        inMemoryTaskManager.addSubtask(subtaskTest2);

        assertEquals(epic.getStartTime(), subtaskTest.getStartTime());

        assertEquals(epic.getEndTime(), subtaskTest2.getEndTime());

        Duration duration = Duration.ofMinutes(0);
        for (Subtask subtask : epic.getSubtasksInEpic().values()) {
            duration = duration.plusMinutes(subtask.getDuration().toMinutes());
        }

        assertEquals(epic.getDuration(), duration);

        //при пустом списке подзадач
        epic.getSubtasksInEpic().clear();

        assertNull(epic.getStartTime());

        assertNull(epic.getEndTime());

        assertEquals(epic.getDuration(), Duration.ofMinutes(0));

        subtaskTest = new Subtask("Test", "Subtask", epic.getId(),
                null, null);
        inMemoryTaskManager.addSubtask(subtaskTest);

        assertNull(epic.getStartTime());

        assertEquals(epic.getDuration(), Duration.ofMinutes(0));
    }
}