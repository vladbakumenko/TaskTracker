import com.google.gson.Gson;
import manager.*;
import servers.HttpTaskServer;
import servers.KVServer;
import servers.KVTaskClient;
import tasks.Task;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;

public class Main {
    public static void main(String[] args) throws IOException, InterruptedException {


//        HTTPTaskManager httpTaskManager = new HTTPTaskManager("http://localhost:8078");

//        Thread.sleep(15000);
//        httpTaskServer.stopServer();
        HTTPTaskManager httpTaskManager2 = HTTPTaskManager.load("http://localhost:8078");

        System.out.println(httpTaskManager2.getAllTasks());
        System.out.println(httpTaskManager2.getHistoryManager().getHistory());

//        TaskManager taskManager = new InMemoryTaskManager();
////
//        Task task = new Task("Title", "Desc", LocalDateTime.now(), Duration.ofMinutes(120));
//        taskManager.addTask(task);
//
//        System.out.println(task);
//
//        taskManager.addTask(task);
//
//        System.out.println(taskManager.getTasks());

//        servers.HttpTaskServer httpTaskServer = new servers.HttpTaskServer();
//        httpTaskServer.startServer();

//        KVServer kvServer = new KVServer();
//        kvServer.start();
//
//        KVTaskClient kvTaskClient = new KVTaskClient("http://localhost:8078");
//        System.out.println(kvTaskClient.getApiToken());
//
//        Gson gson = new Gson();
//        String value = "test value";
//        String json = gson.toJson(task);
//        kvTaskClient.put("test_key", json);
////        Thread.sleep(10000);
//
//        Task task1 = gson.fromJson(kvTaskClient.load("test_key"), Task.class);
//        System.out.println(task1);


//        FileBackedTasksManager fileBackedTasksManager = FileBackedTasksManager.loadFromFile(new File(".\\TaskTrackerData"));
//        System.out.println(fileBackedTasksManager.getAllTasks());

//        InMemoryTaskManager taskManager = (InMemoryTaskManager) Managers.getDefault();
//
//        Task task = taskManager.createTask("Title 1", "Description 1", LocalDateTime.now().plusDays(1), Duration.ofMinutes(120));
//        taskManager.createTask("Title 2", "Description 2", null, null);
//
//        taskManager.createEpic("EpicTitle", "EpicDescription");
//
//        System.out.println(taskManager.getPrioritizedTasks());
//
//        Subtask subtask = taskManager.createSubtask(3, "Subtask1", "SubtaskDescription", LocalDateTime.now().plusDays(3), Duration.ofMinutes(10));
//        taskManager.createSubtask(3, "Subtask2", "SubtaskDescription", LocalDateTime.now().plusDays(3), Duration.ofMinutes(10));
//
//        System.out.println(taskManager.getPrioritizedTasks());
//
//        Task task1 = new Task("Title 1 New", "Description 1 New", task.getId(), LocalDateTime.now().plusDays(1), Duration.ofMinutes(120));
//        taskManager.putTask(task1);
//
//        System.out.println(taskManager.getPrioritizedTasks());
//
//        taskManager.createTask("Title 3", "Description 3", LocalDateTime.now().plusDays(3), Duration.ofMinutes(10));
//
//        System.out.println(taskManager.getPrioritizedTasks());
//
//        Subtask subtaskTest = new Subtask("dfsdfsdfsfds", "adsadasdas", subtask.getId(),
//                subtask.getIdOfEpic(), LocalDateTime.now().plusDays(10), Duration.ofMinutes(10000));
//
//        System.out.println(taskManager.getPrioritizedTasks());
//
//        taskManager.putSubtask(subtaskTest);
//
//        System.out.println(taskManager.getPrioritizedTasks());



//        FileBackedTasksManager fileBackedTasksManager =
//                new FileBackedTasksManager(new File(".\\TaskTrackerData"));
//
//        fileBackedTasksManager.deleteAllTasks();
//
//        Task task1 = fileBackedTasksManager.createTask("Task1", "Task1 description",
//                LocalDateTime.now(), Duration.ofMinutes(120));
//        Task task2 = fileBackedTasksManager.createTask("Task2", "Task2 description",
//                LocalDateTime.now().plusDays(1), Duration.ofMinutes(90));
//        Epic epic = fileBackedTasksManager.createEpic("Epic", "Epic description");
//
//        Subtask subtask1 = fileBackedTasksManager.createSubtask(epic.getId(), "Subtask1",
//                "Subtask1 description", LocalDateTime.now().plusDays(2), Duration.ofMinutes(90));
//        Subtask subtask2 = fileBackedTasksManager.createSubtask(epic.getId(), "Subtask2",
//                "Subtask2 description", LocalDateTime.now().plusDays(3), Duration.ofMinutes(90));
//
//        fileBackedTasksManager.getTaskForId(1);
//
//        Epic testEpic = new Epic("TestEpic", "TestEpicDescription", epic.getId());
//        fileBackedTasksManager.putEpic(testEpic);
//        fileBackedTasksManager.getTaskForId(testEpic.getId());
//
//        fileBackedTasksManager.deleteAllTasks();
//
//        fileBackedTasksManager.putEpic(epic);
//
//        fileBackedTasksManager.getTaskForId(epic.getId());

    }
}