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
import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

abstract class TaskManagerTest<T extends TaskManager> {

    T taskManager;
    Task task1;
    Task task2;
    Epic epic1;
    Subtask subtask1;
    Subtask subtask2;

    public void createTestTasks() {
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

        taskManager.setNewTaskStatus(task1, TaskStatus.IN_PROGRESS);
        taskManager.setNewSubtaskStatus(subtask1, TaskStatus.DONE);
        taskManager.putEpicStatus(epic1);
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
        Task taskTest = new Task("TaskTest", "TaskTest Description",
                LocalDateTime.now(), Duration.ofMinutes(120));
        taskManager.addTask(taskTest);

        Task taskTest2 = taskManager.getTaskForId(taskTest.getId());

        assertEquals(taskTest2, taskTest);

        //создание таск с пересекающимся временем и без
        DefaultTasksException exception = assertThrows(
                DefaultTasksException.class, () -> {
                    Task newTask = new Task("Task", "Task", LocalDateTime.now(), Duration.ofMinutes(120));
                    taskManager.addTask(newTask);
                }
        );

        assertEquals(exception.getMessage(), "Время выполнения задачи пересекается с" +
                " выполнением другой задачи, задача не создана.");

        taskTest = new Task("TaskTest", "TaskTest Description",
                LocalDateTime.now().plusDays(10), Duration.ofMinutes(120));
        taskManager.addTask(taskTest);
        taskTest2 = taskManager.getTaskForId(taskTest.getId());
        assertEquals(taskTest2, taskTest);

        //создание таск при пустом списке задач
        deleteAllTestTasks();

        taskManager.addTask(new Task("TaskTest", "TaskTest Description",
                LocalDateTime.now(), Duration.ofMinutes(120)));

        assertEquals(1, taskManager.getTasks().size());
    }

    @Test
    void createEpic() {
        //стандартное поведение
        Epic epicTest = new Epic("EpicTest", "EpicTest Description");
        taskManager.addEpic(epicTest);

        Epic epicTest2 = (Epic) taskManager.getTaskForId(epicTest.getId());

        assertEquals(epicTest2, epicTest);

        /*так как эпик всегда создаётся сначала без сабтаск, то и пересекаться по времени он никак не будет, так как
        изначально его startTime null, поэтому проверим создание еще одного эпика */

        Epic newEpic = new Epic ("New Epic", "Description");
        taskManager.addEpic(newEpic);
        assertNotNull(taskManager.getTaskForId(newEpic.getId()));

        //создание эпик при пустом списке задач
        deleteAllTestTasks();

        taskManager.addEpic(new Epic("EpicTest", "EpicTest Description"));

        assertEquals(1, taskManager.getEpics().size());
    }

    @Test
    void createSubtask() {
        //стандартное поведение
        Subtask subtaskTest = new Subtask("SubtaskTest", "SubtaskTest description", epic1.getId(),
                LocalDateTime.now(), Duration.ofMinutes(120));
        taskManager.addSubtask(subtaskTest);

        Subtask subtaskTest2 = (Subtask) taskManager.getTaskForId(subtaskTest.getId());

        assertEquals(subtaskTest, subtaskTest2);

        //создание сабтаск с пересекающимся временем и без
        DefaultTasksException exception3 = assertThrows(
                DefaultTasksException.class, () -> {
                    Subtask newSubtask = new Subtask("New Subtask", "Description", epic1.getId(),
                            LocalDateTime.now().plusDays(1), Duration.ofMinutes(10));
                    taskManager.addSubtask(newSubtask);
                }
        );

        assertEquals(exception3.getMessage(), "Время выполнения задачи пересекается с выполнением другой задачи, задача не создана.");

        subtaskTest = new Subtask("New Subtask", "Description", epic1.getId(),
                LocalDateTime.now().plusDays(10), Duration.ofMinutes(10));
        taskManager.addSubtask(subtaskTest);

        subtaskTest2 = (Subtask) taskManager.getTaskForId(subtaskTest.getId());

        assertEquals(subtaskTest, subtaskTest2);

        //создание сабтаск эпика с неправильным id
        DefaultTasksException exception = assertThrows(
                DefaultTasksException.class, () -> {
                    Subtask wrongSubtask = new Subtask("SubtaskTest", "SubtaskTest description",
                            0, LocalDateTime.now(),
                            Duration.ofMinutes(120));
                    taskManager.addSubtask(wrongSubtask);
                }
        );

        assertEquals("Эпика с таким id не существует.", exception.getMessage());


        //создание сабтаск при пустых списках задач
        deleteAllTestTasks();

        DefaultTasksException exception2 = assertThrows(
                DefaultTasksException.class, () -> {
                    Subtask wrongSubtask = new Subtask("SubtaskTest", "SubtaskTest description",
                            epic1.getId(), LocalDateTime.now(), Duration.ofMinutes(120));
                    taskManager.addSubtask(wrongSubtask);
                }
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
        assertTrue(taskManager.getPrioritizedTasks().isEmpty());
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

        //обновляем таск с пересечением времени, тогда новый таск должен не создаться, а старый остаться в хранилищах
        Task newTask = new Task("New Task", "Description", task1.getId(),
                LocalDateTime.now().plusDays(2), Duration.ofMinutes(120));
        taskManager.putTask(newTask);

        testTask2 = taskManager.getTaskForId(newTask.getId());

        assertEquals(testTask, testTask2);

        //обновляем таск без пересечения времени, тогда новый таск заменит старый и появится в хранилищах
        newTask = new Task("New Task", "Description", task1.getId(),
                LocalDateTime.now(), Duration.ofMinutes(120));
        taskManager.putTask(newTask);

        testTask2 = taskManager.getTaskForId(newTask.getId());

        assertEquals(newTask, testTask2);

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

        //обновляем эпик, но так как его startTime рассчётный, то пересечения не будет и он обновится в хранилищах
        testEpic = new Epic("New Epic", "Description", epic1.getId());
        taskManager.putEpic(testEpic);
        testEpic2 = (Epic) taskManager.getTaskForId(testEpic.getId());

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
        Subtask testSubtask = new Subtask("TestSubtask", "TestSubtaskDescription", subtask1.getId(),
                epic1.getId(), LocalDateTime.now(), Duration.ofMinutes(120));
        taskManager.putSubtask(testSubtask);
        Subtask testSubtask2 = (Subtask) taskManager.getTaskForId(testSubtask.getId());

        assertEquals(testSubtask2, testSubtask);

        /* обновляем сабтаск с пересечением времени, тогда новый таск должен не создаться, а старый остаться в
        хранилищах, эпик так же не должен измениться */
        Subtask newSubtask = new Subtask("New Subtask", "Description", subtask1.getId(),
                epic1.getId(), LocalDateTime.now().plusDays(2), Duration.ofMinutes(120));
        taskManager.putSubtask(newSubtask);

        testSubtask2 = (Subtask) taskManager.getTaskForId(newSubtask.getId());

        assertEquals(testSubtask, testSubtask2);

        assertEquals(epic1, taskManager.getTaskForId(epic1.getId()));

        /* обновляем сабтаск без пересечения времени, тогда новый сабтаск должен заменить старый в хранилищах,
        эпик так же должен измениться, если startTime меняется у его самой ранней сабтаск */
        newSubtask = new Subtask("New Subtask", "Description", subtask1.getId(),
                epic1.getId(), LocalDateTime.now(), Duration.ofMinutes(120));
        taskManager.putSubtask(newSubtask);

        testSubtask2 = (Subtask) taskManager.getTaskForId(newSubtask.getId());

        assertEquals(newSubtask, testSubtask2);

        assertEquals(epic1.getStartTime(), newSubtask.getStartTime());

        /* обновляем сабтаск без пересечения времени, тогда новый сабтаск должен заменить старый в хранилищах,
        эпик не должен измениться, если startTime меняется не у самой ранней сабтаск */
        newSubtask = new Subtask("New Subtask", "Description", subtask2.getId(),
                epic1.getId(), LocalDateTime.now().plusDays(4), Duration.ofMinutes(120));
        taskManager.putSubtask(newSubtask);

        testSubtask2 = (Subtask) taskManager.getTaskForId(newSubtask.getId());

        assertEquals(newSubtask, testSubtask2);

        assertNotEquals(epic1.getStartTime(), newSubtask.getStartTime());

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

    @Test
    void getPrioritizedTasks() {
//        Создаём LinkedList с нужным порядком элементов для сравнения с сортировкой TreeSet
        List<Task> testList = new LinkedList<>(List.of(task1, task2, epic1, subtask1, subtask2));
        List<Task> testList2 = new LinkedList<>(taskManager.getPrioritizedTasks());
        assertEquals(testList, testList2);

//        Меняем время startTime у всех задач и проверяем соответствие сортировки TreeSet с ожидаемой

        task1 = new Task("New Task 1", "Description", task1.getId(), LocalDateTime.now().plusDays(10),
                Duration.ofMinutes(120));
        task2 = new Task("New Task 2", "Description", task2.getId(), null,
                Duration.ofMinutes(120));
        subtask1 = new Subtask("New Subtask 1", "Description", subtask1.getId(), epic1.getId(),
                LocalDateTime.now().plusDays(20), Duration.ofMinutes(90));
        subtask2 = new Subtask("New Subtask 2", "Description", subtask2.getId(), epic1.getId(),
                null, Duration.ofMinutes(90));

        taskManager.putTask(task1);
        taskManager.putTask(task2);
        taskManager.putSubtask(subtask1);
        taskManager.putSubtask(subtask2);

        testList = new LinkedList<>(List.of(task1, epic1, subtask1, task2, subtask2));
        testList2 = new LinkedList<>(taskManager.getPrioritizedTasks());
        assertEquals(testList, testList2);

        System.out.println(taskManager.getPrioritizedTasks());
    }
}
