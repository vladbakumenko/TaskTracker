import manager.HTTPTaskManager;
import manager.Managers;
import manager.TaskManager;
import servers.HttpTaskServer;
import servers.KVServer;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;

public class Main {
    public static void main(String[] args) throws IOException {

        KVServer kvServer = new KVServer();
        kvServer.start();

        TaskManager taskManager = Managers.getDefault("http://localhost:8078");

        HttpTaskServer httpTaskServer = new HttpTaskServer(taskManager);
        httpTaskServer.startServer();

        Task task1 = new Task("Title 2", "Description 2", null, null);
        taskManager.addTask(task1);

        Epic epic = new Epic("EpicTitle", "EpicDescription");
        taskManager.addEpic(epic);

        Subtask subtask = new Subtask("Subtask1", "SubtaskDescription", epic.getId(), LocalDateTime.now().plusDays(3), Duration.ofMinutes(10));
        taskManager.addSubtask(subtask);

        Task task2 = new Task("Title 1 New", "Description 1 New", task1.getId(), LocalDateTime.now().plusDays(1), Duration.ofMinutes(120));
        taskManager.putTask(task2);


        Task task3 = new Task("Title 3", "Description 3", LocalDateTime.now().plusDays(5), Duration.ofMinutes(10));
        taskManager.addTask(task3);

        Subtask subtaskTest = new Subtask("dfsdfsdfsfds", "adsadasdas", subtask.getId(),
                subtask.getIdOfEpic(), LocalDateTime.now().plusDays(10), Duration.ofMinutes(10000));

        taskManager.putSubtask(subtaskTest);

        System.out.println("Get getPrioritizedTasks:");
        System.out.println(taskManager.getPrioritizedTasks());

        HTTPTaskManager httpTaskManager = HTTPTaskManager.load("http://localhost:8078");

        System.out.println("Loading task1 from http:");

        System.out.println(httpTaskManager.getTaskForId(task1.getId()));
    }
}