import exceptions.DefaultTasksException;
import manager.HistoryManager;
import manager.TaskManager;
import manager.TaskStatus;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

abstract class TaskManagerTest<T extends TaskManager> {

    T taskManager;
    Task task1;
    Task task2;
    Epic epic1;
    Subtask subtask1;
    Subtask subtask2;

    public void createTestTasks() {
        task1 = taskManager.createTask("Task1", "Task1 description", LocalDateTime.now(), Duration.ofMinutes(120));
        task2 = taskManager.createTask("Task2", "Task2 description", LocalDateTime.now(), Duration.ofMinutes(120));
        epic1 = taskManager.createEpic("Epic1", "Epic1 description");
        subtask1 = taskManager.createSubtask(epic1.getId(), "Subtask1", "Subtask1 description", LocalDateTime.now(), Duration.ofMinutes(120));
        subtask2 = taskManager.createSubtask(epic1.getId(), "Subtask2", "Subtask2 description", LocalDateTime.now(), Duration.ofMinutes(120));

        taskManager.setNewTaskStatus(task1, TaskStatus.IN_PROGRESS);
        taskManager.setNewSubtaskStatus(subtask1, TaskStatus.DONE);
        taskManager.updateEpicStatus(epic1);
    }

    public void deleteAllTestTasks() {
        taskManager.deleteAllTasks();
    }

    @Test
    void getHistoryManager() {
        //стандартное поведение
        assertNotNull(taskManager.getHistoryManager());
        HistoryManager historyManager = taskManager.getHistoryManager();
        assertEquals(historyManager, taskManager.getHistoryManager());
    }

    @Test
    void createTask() {
        //стандартное поведение
        Task taskTest = taskManager.createTask("TaskTest", "TaskTest Description", LocalDateTime.now(), Duration.ofMinutes(120));

        Task taskTest2 = taskManager.getTaskForId(taskTest.getId());

        assertEquals(taskTest2, taskTest);

        //создание таск при пустом списке задач
        deleteAllTestTasks();

        taskManager.createTask("TaskTest", "TaskTest Description", LocalDateTime.now(), Duration.ofMinutes(120));

        assertEquals(1, taskManager.getTasks().size());
    }

    @Test
    void createEpic() {
        //стандартное поведение
        Epic epicTest = taskManager.createEpic("EpicTest", "EpicTest Description");

        Epic epicTest2 = (Epic) taskManager.getTaskForId(epicTest.getId());

        assertEquals(epicTest2, epicTest);

        //создание эпик при пустом списке задач
        deleteAllTestTasks();

        taskManager.createEpic("EpicTest", "EpicTest Description");

        assertEquals(1, taskManager.getEpics().size());
    }

    @Test
    void createSubtask() {
        //стандартное поведение
        Subtask subtaskTest = taskManager.createSubtask(
                epic1.getId(), "SubtaskTest", "SubtaskTest description", LocalDateTime.now(), Duration.ofMinutes(120));

        Subtask subtaskTest2 = (Subtask) taskManager.getTaskForId(subtaskTest.getId());

        assertEquals(subtaskTest, subtaskTest2);

        //создание сабтаск эпика с неправильным id
        DefaultTasksException exception = assertThrows(
                DefaultTasksException.class, () -> taskManager.createSubtask(
                        0, "SubtaskTest", "SubtaskTest description", LocalDateTime.now(), Duration.ofMinutes(120))
        );

        assertEquals("Эпика с таким id не существует.", exception.getMessage());


        //создание сабтаск при пустых списках задач
        deleteAllTestTasks();

        DefaultTasksException exception2 = assertThrows(
                DefaultTasksException.class, () -> taskManager.createSubtask(
                        epic1.getId(), "SubtaskTest", "SubtaskTest description", LocalDateTime.now(), Duration.ofMinutes(120))
        );

        assertEquals("Эпика с таким id не существует.", exception2.getMessage());
    }

    @Test
    void getTasks() {
        //стандартное поведение
        assertEquals(2, taskManager.getTasks().size());

        //при пустых списках задач
        deleteAllTestTasks();

        assertEquals(0, taskManager.getTasks().size());
    }

    @Test
    void getEpics() {
        //стандартное поведение
        assertEquals(1, taskManager.getEpics().size());

        //при пустых списках задач
        deleteAllTestTasks();

        assertEquals(0, taskManager.getEpics().size());
    }

    @Test
    void getSubtasks() {
        //стандартное поведение
        assertEquals(2, taskManager.getSubtasks().size());

        //при пустых списках задач
        deleteAllTestTasks();

        assertEquals(0, taskManager.getSubtasks().size());
    }

    @Test
    void getAllTasks() {
        //стандартное поведение
        assertEquals(5, taskManager.getAllTasks().size());

        //при пустых списках задач
        deleteAllTestTasks();

        assertEquals(0, taskManager.getAllTasks().size());
    }

    @Test
    void deleteAllTasks() {
        //стандартное поведение
        taskManager.deleteAllTasks();

        assertTrue(taskManager.getAllTasks().isEmpty());
        assertTrue(taskManager.getHistoryManager().getHistory().isEmpty());
    }

    @Test
    void getTaskForId() {
        //стандартное поведение
        Task taskTest = taskManager.getTaskForId(task1.getId());
        assertEquals(taskTest, task1);

        Subtask subtaskTest = (Subtask) taskManager.getTaskForId(subtask1.getId());
        assertEquals(subtaskTest, subtask1);

        Epic epicTest = (Epic) taskManager.getTaskForId(epic1.getId());
        assertEquals(epicTest, epic1);

        //при пустых списках задач
        deleteAllTestTasks();

        DefaultTasksException exception1 = assertThrows(
                DefaultTasksException.class, () -> {
                    taskManager.getTaskForId(1);
                }
        );

        assertEquals(exception1.getMessage(), "Такого id не существует.");

        //при несуществующем id
        DefaultTasksException exception2 = assertThrows(
                DefaultTasksException.class, () -> {
                    taskManager.getTaskForId(0);
                }
        );

        assertEquals(exception2.getMessage(), "Такого id не существует.");

    }

    @Test
    void putTask() {
        //стандартное поведение при изменении данных таск
        Task testTask = new Task("TestTask", "TestTask Description", task1.getId(),
                LocalDateTime.now(), Duration.ofMinutes(120));
        taskManager.putTask(testTask);
        Task testTask2 = taskManager.getTaskForId(1);

        assertEquals(testTask2, testTask);

        //при пустых списках задач
        deleteAllTestTasks();
        taskManager.putTask(task1);
        testTask = taskManager.getTaskForId(task1.getId());

        assertEquals(task1, testTask);
    }

    @Test
    void putEpic() {
        //стандартное поведение при изменении данных эпика
        Epic testEpic = new Epic("TestEpic", "TestEpicDescription", epic1.getId());
        taskManager.putEpic(testEpic);
        Epic testEpic2 = (Epic) taskManager.getTaskForId(testEpic.getId());

        assertEquals(testEpic2, testEpic);

        //при пустых списках задач
        deleteAllTestTasks();
        taskManager.putEpic(epic1);
        testEpic = (Epic) taskManager.getTaskForId(epic1.getId());

        assertEquals(epic1, testEpic);
    }

    @Test
    void putSubtask() {
        //стандартное поведение при изменении данных сабтаск
        Subtask testSubtask = new Subtask("TestSubtask", "TestSubtaskDescription", subtask1.getId(), epic1.getId(), LocalDateTime.now(), Duration.ofMinutes(120));
        taskManager.putSubtask(testSubtask);
        Subtask testSubtask2 = (Subtask) taskManager.getTaskForId(testSubtask.getId());

        assertEquals(testSubtask2, testSubtask);

        //при пустых списках задач
        deleteAllTestTasks();

        DefaultTasksException exception = assertThrows(
                DefaultTasksException.class, () -> {
                    taskManager.putSubtask(subtask1);
                });
        assertEquals(exception.getMessage(), "У сабтаск не существует эпика.");
    }

    @Test
    void setNewTaskStatus() {
        //стандартное поведение
        taskManager.setNewTaskStatus(task1, TaskStatus.IN_PROGRESS);

        assertEquals(task1.getStatus(), TaskStatus.IN_PROGRESS);
    }

    @Test
    void setNewSubtaskStatus() {
        //стандартное поведение
        taskManager.setNewSubtaskStatus(subtask1, TaskStatus.IN_PROGRESS);

        assertEquals(subtask1.getStatus(), TaskStatus.IN_PROGRESS);
    }

    //тесты, проверяющие работу метода обновления статуса эпика находятся в классе EpicTest

    @Test
    void removeTaskForId() {
        //стандартное поведение, вызываем метод getTaskForId(), что бы добавить задачу в историю просмотров
        taskManager.getTaskForId(1);
        taskManager.removeTaskForId(1);

        DefaultTasksException exception1 = assertThrows(
                DefaultTasksException.class, () -> {
                    taskManager.getTaskForId(1);
                }
        );

        assertEquals(exception1.getMessage(), "Такого id не существует.");
        assertFalse(taskManager.getHistoryManager().getHistory().contains(task1));

        //проверяем удаление эпика, тогда и должны удалиться его сабтаски
        taskManager.getTaskForId(3);
        taskManager.getTaskForId(4);
        taskManager.removeTaskForId(3);

        DefaultTasksException exception2 = assertThrows(
                DefaultTasksException.class, () -> {
                    taskManager.getTaskForId(4);
                }
        );

        assertEquals(exception2.getMessage(), "Такого id не существует.");
        assertFalse(taskManager.getHistoryManager().getHistory().contains(epic1));
        assertFalse(taskManager.getHistoryManager().getHistory().contains(subtask1));
    }
}
