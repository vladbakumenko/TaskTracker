import adapters.DurationTypeAdapter;
import adapters.LocalDateTimeAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import manager.HTTPTaskManager;
import manager.Managers;
import manager.TaskManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import servers.HttpTaskServer;
import servers.KVServer;
import tasks.Task;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class HTTPTaskManagerTest extends TaskManagerTest<HTTPTaskManager> {

    private static Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .registerTypeAdapter(Duration.class, new DurationTypeAdapter())
            .create();
    private TaskManager httpTaskManager;
    private KVServer kvServer = new KVServer();

    HTTPTaskManagerTest() throws IOException {
    }

    void loadFromKVServer() {
        try {
            httpTaskManager = Managers.getDefault("http://localhost:8078");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @BeforeEach
    public void beforeEach() throws IOException {
        kvServer.start();
        taskManager = new HTTPTaskManager("http://localhost:8078");
        createTestTasks();
        taskManager.getTaskForId(1);
        taskManager.getTaskForId(5);
        taskManager.getTaskForId(3);
    }

    @AfterEach
    void stopServer() {
        kvServer.stop();
    }

    @Test
    void shouldSaveAndLoadCorrectInformation() {
        loadFromKVServer();

        assertEquals(taskManager.getAllTasks(), httpTaskManager.getAllTasks());

        assertEquals(taskManager.getHistoryManager().getHistory(),
                httpTaskManager.getHistoryManager().getHistory());
    }

    @Test
    void shouldEmptyStoragesWhenEmptyListOfTasks() {
        taskManager.deleteAllTasks();

        loadFromKVServer();

        assertEquals(taskManager.getAllTasks(), httpTaskManager.getAllTasks());

        assertEquals(taskManager.getHistoryManager().getHistory(),
                httpTaskManager.getHistoryManager().getHistory());
    }

    @Test
    void shouldLoadEmptyHistoryIfHistoryStorageEmpty() {
        taskManager.getHistoryManager().removeAll();
        taskManager.save();

        loadFromKVServer();

        assertTrue(httpTaskManager.getHistoryManager().getHistory().isEmpty());
    }

    @Test
    void shouldLoadCorrectInformationWhenSubtasksIsEmpty() {
        taskManager.removeTaskForId(subtask1.getId());
        taskManager.removeTaskForId(subtask2.getId());
        taskManager.putEpicStatus(epic1);

        loadFromKVServer();

        assertEquals(taskManager.getAllTasks(), httpTaskManager.getAllTasks());

        assertEquals(taskManager.getHistoryManager().getHistory(),
                httpTaskManager.getHistoryManager().getHistory());
    }
}