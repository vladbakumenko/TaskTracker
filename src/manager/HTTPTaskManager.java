package manager;


import adapters.DurationTypeAdapter;
import adapters.LocalDateTimeAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import servers.KVTaskClient;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class HTTPTaskManager extends FileBackedTasksManager {

    private static Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .registerTypeAdapter(Duration.class, new DurationTypeAdapter())
            .create();
    private static KVTaskClient kvTaskClient;

    public HTTPTaskManager(String url) throws IOException {
        super();
        kvTaskClient = new KVTaskClient(url);
    }

    @Override
    public void save() {
        kvTaskClient.put("tasks", gson.toJson(this.getTasks()));
        kvTaskClient.put("epics", gson.toJson(this.getEpics()));
        kvTaskClient.put("subtasks", gson.toJson(this.getSubtasks()));

        List<Integer> history = this.getHistoryManager().getHistory().stream().map(Task::getId)
                .collect(Collectors.toList());
        kvTaskClient.put("history", gson.toJson(history));
    }

    public static HTTPTaskManager load(String url) throws IOException {
        HTTPTaskManager httpTaskManager = new HTTPTaskManager(url);

        List<Task> tasks = gson.fromJson(kvTaskClient.load("tasks"), new TypeToken<List<Task>>() {}.getType());

        if (tasks != null) {
            tasks.forEach(httpTaskManager::loadTask);
        }

        List<Epic> epics = gson.fromJson(kvTaskClient.load("epics"), new TypeToken<List<Epic>>() {}.getType());

        if (epics != null) {
            epics.forEach(httpTaskManager::loadEpic);
        }

        List<Subtask> subtasks = gson.fromJson(kvTaskClient.load("subtasks"),
                new TypeToken<List<Subtask>>() {}.getType());

        if (subtasks != null) {
            subtasks.forEach(httpTaskManager::loadSubtask);
        }

        List<Integer> history = gson.fromJson(kvTaskClient.load("history"),
                new TypeToken<List<Integer>>() {}.getType());

        if (history != null) {
            history.forEach(httpTaskManager::getTaskForId);
        }

        if (!httpTaskManager.getAllTasks().isEmpty()) {
            int currentId = 0;
            for (Task task : httpTaskManager.getAllTasks()) {
                if (task.getId() > currentId) {
                    currentId = task.getId();
                }
            }

            httpTaskManager.idOfNewTask = currentId;
        }

        return httpTaskManager;
    }
}