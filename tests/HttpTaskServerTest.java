import adapters.DurationTypeAdapter;
import adapters.LocalDateTimeAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import manager.Managers;
import manager.TaskManager;
import manager.TaskStatus;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import servers.HttpTaskServer;
import servers.KVServer;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class HttpTaskServerTest {

    private TaskManager httpTaskManager;
    private HttpTaskServer httpTaskServer;
    private KVServer kvServer;

    private Task task1;
    private Epic epic1;
    private Subtask subtask1;
    private Subtask subtask2;
    private static Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .registerTypeAdapter(Duration.class, new DurationTypeAdapter())
            .create();

    HttpTaskServerTest() throws IOException {
    }

    @BeforeEach
    void initServerAndManager() throws IOException {
        kvServer = new KVServer();
        kvServer.start();

        httpTaskManager = Managers.getDefault("http://localhost:8078");

        httpTaskServer = new HttpTaskServer(httpTaskManager);
        httpTaskServer.startServer();
    }

    @AfterEach
    void stopServers() {
        kvServer.stop();
        httpTaskServer.stopServer();
    }

    @Test
    void shouldCreateTaskFromEndPoints() {
        //Создаём Task и проверяем, появился ли он в хранилище
        Task task = new Task("Task", "Task Desc", 1, LocalDateTime.now().plusDays(1),
                Duration.ofMinutes(120));
        addTask(task);
        Task testTask = httpTaskManager.getTaskForId(task.getId());
        assertEquals(task, testTask);
    }

    @Test
    void shouldCreateEpicFromEndPoints() {
        //Создаём Epic и проверяем, появился ли он в хранилище
        Epic epic = new Epic("Epic", "Epic Desc", 1);
        addEpic(epic);
        Epic testEpic = (Epic) httpTaskManager.getTaskForId(epic.getId());
        assertEquals(epic, testEpic);
    }

    @Test
    void shouldCreateSubtasksFromEndPoints() {
        Epic epic = new Epic("Epic", "Epic Desc", 1);
        addEpic(epic);

        //Создаём два Subtask и проверяем, появились ли они в хранилище
        Subtask subtask1 = new Subtask("Subtask1", "Subtask1 Desc", 2, 1,
                LocalDateTime.now().plusDays(2), Duration.ofMinutes(100));
        addSubtask(subtask1);

        Subtask subtask2 = new Subtask("Subtask2", "Subtask2 Desc", 3, 1,
                LocalDateTime.now().plusDays(3), Duration.ofMinutes(200));
        addSubtask(subtask2);

        Subtask testSubtask1 = (Subtask) httpTaskManager.getTaskForId(subtask1.getId());
        Subtask testSubtask2 = (Subtask) httpTaskManager.getTaskForId(subtask2.getId());

        assertEquals(subtask1, testSubtask1);
        assertEquals(subtask2, testSubtask2);
    }

    @Test
    void getAllTasks() {
        createAndAddTestTasks();

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = null;
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }

        List<JsonObject> tasks = gson.fromJson(response.body(), new TypeToken<List<JsonObject>>() {
        }.getType());
        List<Task> finalTasks = new ArrayList<>();
        for (JsonObject obj : tasks) {
            if (obj.has("subtasksInEpic")) {
                Epic epic = gson.fromJson(obj, Epic.class);
                finalTasks.add(epic);
            } else if (obj.has("idOfEpic")) {
                Subtask subtask = gson.fromJson(obj, Subtask.class);
                finalTasks.add(subtask);
            } else {
                Task task = gson.fromJson(obj, Task.class);
                finalTasks.add(task);
            }
        }

        assertEquals(finalTasks, new ArrayList<>(httpTaskManager.getPrioritizedTasks()));
    }

    @Test
    void shouldGetTasks() {
        //Получаем список Task
        createAndAddTestTasks();

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/task");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = null;
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }

        List<Task> tasks = gson.fromJson(response.body(), new TypeToken<List<Task>>() {
        }.getType());

        assertEquals(tasks, httpTaskManager.getTasks());
    }

    @Test
    void shouldGetEpics() {
        //Получаем список Task
        createAndAddTestTasks();

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/epic");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = null;
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }

        List<Epic> epics = gson.fromJson(response.body(), new TypeToken<List<Epic>>() {
        }.getType());

        assertEquals(epics, httpTaskManager.getEpics());
    }

    @Test
    void shouldGetSubtasks() {
        //Получаем список Task
        createAndAddTestTasks();

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/subtask");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = null;
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }

        List<Subtask> subtasks = gson.fromJson(response.body(), new TypeToken<List<Subtask>>() {
        }.getType());

        assertEquals(subtasks, httpTaskManager.getSubtasks());
    }

    @Test
    void shouldGetTaskForId() {
        //Получаем задачу по id
        createAndAddTestTasks();

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/task/?id=3");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = null;
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }

        JsonObject obj = gson.fromJson(response.body(), new TypeToken<JsonObject>() {
        }.getType());
        Task task;

        if (obj.has("subtasksInEpic")) {
            Epic epic = gson.fromJson(obj, Epic.class);
            task = epic;
        } else if (obj.has("idOfEpic")) {
            Subtask subtask = gson.fromJson(obj, Subtask.class);
            task = subtask;
        } else {
            task = gson.fromJson(obj, Task.class);
        }

        System.out.println(task);
        assertEquals(task, httpTaskManager.getTaskForId(3));
    }

    @Test
    void shouldNotGetTaskForNonExistentId() {
        //Проверим получение по несуществующему id
        createAndAddTestTasks();

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/task/?id=7");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = null;
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }

        assertEquals(404, response.statusCode());
    }

    @Test
    void shouldUpdateTask() {
        //Обновим у Subtask startTime, duration и status, у Epic так же должны измениться поля
        createAndAddTestTasks();

        Subtask newSubtask = new Subtask("NewSubtask1", "NewSubtask1 description",
                subtask1.getId(), 2, LocalDateTime.of(2022, 9, 20, 0, 0),
                Duration.ofMinutes(1200));
        newSubtask.setStatus(TaskStatus.DONE);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/subtask/?id=3");
        String json = gson.toJson(newSubtask);
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }

        assertEquals(newSubtask, httpTaskManager.getTaskForId(3));

        assertEquals(epic1.getStatus(), TaskStatus.IN_PROGRESS);

        assertEquals(epic1.getEndTime(), newSubtask.getEndTime());

        assertEquals(epic1.getDuration(), subtask2.getDuration().plusMinutes(newSubtask.getDuration().toMinutes()));
    }

    @Test
    void shouldDeleteAllTasks() {
        createAndAddTestTasks();

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/task");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response = null;
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }

        assertTrue(httpTaskManager.getAllTasks().isEmpty());
        assertTrue(httpTaskManager.getHistoryManager().getHistory().isEmpty());
        assertTrue(httpTaskManager.getPrioritizedTasks().isEmpty());
    }

    @Test
    void shouldDeleteTaskForId() {
        createAndAddTestTasks();

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/task/?id=2");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response = null;
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }

        //Удалим Epic, что бы остался только Task

        assertTrue(httpTaskManager.getEpics().isEmpty());
        assertTrue(httpTaskManager.getSubtasks().isEmpty());
        assertEquals(1, httpTaskManager.getAllTasks().size());
    }

    @Test
    void shouldDisplayHistory() {
        createAndAddTestTasks();

        httpTaskManager.getTaskForId(1);
        httpTaskManager.getTaskForId(4);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/history");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = null;
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }

        List<JsonObject> tasks = gson.fromJson(response.body(), new TypeToken<List<JsonObject>>() {
        }.getType());
        List<Task> history = new ArrayList<>();
        for (JsonObject obj : tasks) {
            if (obj.has("subtasksInEpic")) {
                Epic epic = gson.fromJson(obj, Epic.class);
                history.add(epic);
            } else if (obj.has("idOfEpic")) {
                Subtask subtask = gson.fromJson(obj, Subtask.class);
                history.add(subtask);
            } else {
                Task task = gson.fromJson(obj, Task.class);
                history.add(task);
            }
        }

        assertEquals(history, new ArrayList<>(httpTaskManager.getHistoryManager().getHistory()));
    }

    void addTask(Task task) {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/task/");
        String json = gson.toJson(task);
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    void addEpic(Epic epic) {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/epic/");
        String json = gson.toJson(epic);
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    void addSubtask(Subtask subtask) {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/subtask/");
        String json = gson.toJson(subtask);
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    void createAndAddTestTasks() {
        task1 = new Task("Task1", "Task1 description", LocalDateTime.now().plusDays(1),
                Duration.ofMinutes(120));
        httpTaskManager.addTask(task1);

        epic1 = new Epic("Epic1", "Epic1 description");
        httpTaskManager.addEpic(epic1);

        subtask1 = new Subtask("Subtask1", "Subtask1 description", 2,
                LocalDateTime.now().plusDays(2), Duration.ofMinutes(120));
        httpTaskManager.addSubtask(subtask1);

        subtask2 = new Subtask("Subtask2", "Subtask2 description", 2,
                LocalDateTime.now().plusDays(3), Duration.ofMinutes(120));
        httpTaskManager.addSubtask(subtask2);
    }
}