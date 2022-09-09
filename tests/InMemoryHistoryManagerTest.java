import exceptions.DefaultTasksException;
import manager.HistoryManager;
import manager.InMemoryTaskManager;
import manager.TaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {
    TaskManager taskManager;

    HistoryManager historyManager;
    Task task1;
    Task task2;
    Epic epic1;
    Subtask subtask1;
    Subtask subtask2;

    @BeforeEach
    public void beforeEach() {
        taskManager = new InMemoryTaskManager();
        historyManager = taskManager.getHistoryManager();

        task1 = new Task("Task1", "Task1 description", LocalDateTime.now().plusDays(1),
                Duration.ofMinutes(120));
        taskManager.addTask(task1);

        task2 = new Task("Task2", "Task2 description", LocalDateTime.now().plusDays(2),
                Duration.ofMinutes(120));
        taskManager.addTask(task2);

        epic1 = new Epic("Epic1", "Epic1 description");
        taskManager.addEpic(epic1);

        subtask1 = new Subtask("Subtask1", "Subtask1 description", epic1.getId(),
                LocalDateTime.now().plusDays(3), Duration.ofMinutes(120));
        taskManager.addSubtask(subtask1);

        subtask2 = new Subtask("Subtask2", "Subtask2 description", epic1.getId(),
                LocalDateTime.now().plusDays(4), Duration.ofMinutes(120));
        taskManager.addSubtask(subtask2);

        addTasksToHistory();
    }


    void addTasksToHistory() {
        historyManager.add(subtask2);
        historyManager.add(task1);
        historyManager.add(epic1);
    }

    @Test
    void addAndGetHistory() {
        //стандартное поведение

        List<Task> list = List.of(subtask2, task1, epic1);

        assertEquals(list, historyManager.getHistory());

        //дублирование добавления задач
        historyManager.add(subtask2);

        list = List.of(task1, epic1, subtask2);

        assertEquals(list, historyManager.getHistory());
    }

    @Test
    void remove() {
        //стандартное поведение при удалении из середины
        historyManager.add(task2);
        historyManager.add(subtask1);

        historyManager.remove(epic1.getId());

        List<Task> list = List.of(subtask2, task1, task2, subtask1);

        assertEquals(list, historyManager.getHistory());

        //стандартное поведение при удалении с начала
        historyManager.remove(subtask2.getId());

        list = List.of(task1, task2, subtask1);

        assertEquals(list, historyManager.getHistory());

        //стандартное поведение при удалении с конца
        historyManager.remove(subtask1.getId());

        list = List.of(task1, task2);

        assertEquals(list, historyManager.getHistory());

        //при отсутствии задачи в истории
        DefaultTasksException exception = assertThrows(
                DefaultTasksException.class, () -> {
                    historyManager.remove(subtask1.getId());
                }
        );

        assertEquals("Такой задачи нет.", exception.getMessage());

        list = List.of(task1, task2);

        assertEquals(list, historyManager.getHistory());
    }

    @Test
    void removeAll() {
        historyManager.removeAll();

        assertTrue(historyManager.getHistory().isEmpty());
    }
}