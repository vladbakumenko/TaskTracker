import manager.FileBackedTasksManager;
import manager.HistoryManager;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.io.File;
import java.time.Duration;
import java.time.LocalDateTime;

public class Main {
    public static void main(String[] args) {

        FileBackedTasksManager fileBackedTasksManager =
                new FileBackedTasksManager(new File(".\\TaskTrackerData"));

        fileBackedTasksManager.deleteAllTasks();

        Task task1 = fileBackedTasksManager.createTask("Task1", "Task1 description",
                LocalDateTime.now(), Duration.ofMinutes(120));
        Task task2 = fileBackedTasksManager.createTask("Task2", "Task2 description",
                LocalDateTime.now().plusDays(1), Duration.ofMinutes(90));
        Epic epic = fileBackedTasksManager.createEpic("Epic", "Epic description");

        Subtask subtask1 = fileBackedTasksManager.createSubtask(epic.getId(), "Subtask1",
                "Subtask1 description", LocalDateTime.now().plusDays(2), Duration.ofMinutes(90));
        Subtask subtask2 = fileBackedTasksManager.createSubtask(epic.getId(), "Subtask2",
                "Subtask2 description", LocalDateTime.now().plusDays(3), Duration.ofMinutes(90));

        fileBackedTasksManager.getTaskForId(1);

        Epic testEpic = new Epic("TestEpic", "TestEpicDescription", epic.getId());
        fileBackedTasksManager.putEpic(testEpic);
        fileBackedTasksManager.getTaskForId(testEpic.getId());

        fileBackedTasksManager.deleteAllTasks();

        fileBackedTasksManager.putEpic(epic);

        fileBackedTasksManager.getTaskForId(epic.getId());

    }
}