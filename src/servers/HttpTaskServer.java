package servers;

import adapters.DurationTypeAdapter;
import adapters.LocalDateTimeAdapter;
import com.google.gson.*;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import exceptions.DefaultTasksException;
import manager.Managers;
import manager.TaskManager;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;

public class HttpTaskServer {

    public TaskManager getHttpTasksManager() {
        return httpTasksManager;
    }

    private TaskManager httpTasksManager;

    private static final int PORT = 8080;

    private static Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .registerTypeAdapter(Duration.class, new DurationTypeAdapter())
            .create();

    private HttpServer httpServer;

    public HttpTaskServer(TaskManager taskManager) throws IOException {
        httpTasksManager = taskManager;
        httpServer = HttpServer.create();
        httpServer.bind(new InetSocketAddress(PORT), 0);
    }

    public void startServer() {
        httpServer.start();
        httpServer.createContext("/tasks", new TaskTrackerHandler());
        System.out.println("HTTP-сервер запущен на " + PORT + " порту!");
    }

    public void stopServer() {
        httpServer.stop(1);
    }

    public static void main(String[] args) throws IOException {
        TaskManager taskManager = Managers.getDefault("http://localhost:8078");
        HttpTaskServer httpTaskServer = new HttpTaskServer(taskManager);
        httpTaskServer.startServer();
    }

    private class TaskTrackerHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String method = exchange.getRequestMethod();
            String response = "";

            switch (method) {
                case "GET":
                    response = getHandler(exchange);
                    if (!response.isBlank()) {
                        exchange.sendResponseHeaders(200, 0);
                    } else {
                        exchange.sendResponseHeaders(404, 0);
                    }
                    break;
                case "POST":
                    postHandler(exchange);
                    break;
                case "DELETE":
                    deleteHandler(exchange);
                    break;
                default:
                    exchange.sendResponseHeaders(405, 0);

            }

            try (OutputStream outputStream = exchange.getResponseBody()) {
                outputStream.write(response.getBytes());
            } catch (Exception exc) {
                exc.printStackTrace();
            } finally {
                exchange.close();
            }
        }
    }

    private void deleteHandler(HttpExchange exchange) throws IOException {
        String[] pathParts = exchange.getRequestURI().getPath().split("/");

        if ("task".equals(pathParts[2])) {
            if (exchange.getRequestURI().getQuery() != null) {
                if (checkIdContains(exchange)) {
                    int id = Integer.parseInt(exchange.getRequestURI().getQuery().split("=")[1]);
                    httpTasksManager.removeTaskForId(id);
                    exchange.sendResponseHeaders(204, 0);
                } else {
                    exchange.sendResponseHeaders(400, 0);
                }
            } else {
                httpTasksManager.deleteAllTasks();
                exchange.sendResponseHeaders(204, 0);
            }
        } else {
            exchange.sendResponseHeaders(400, 0);
        }
    }

    private String getHandler(HttpExchange exchange) throws IOException {
        String response = "";
        String[] pathParts = exchange.getRequestURI().getPath().split("/");
        if ("tasks".equals(pathParts[pathParts.length - 1])) {
            try {
                response = gson.toJson(httpTasksManager.getPrioritizedTasks());
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        } else if ("task".equals(pathParts[2])) {
            if (exchange.getRequestURI().getQuery() != null) {
                if (checkIdContains(exchange)) {
                    int id = Integer.parseInt(exchange.getRequestURI().getQuery().split("=")[1]);
                    response = gson.toJson(httpTasksManager.getTaskForId(id));
                }
            } else {
                response = gson.toJson(httpTasksManager.getTasks());
            }
        } else if ("epic".equals(pathParts[2])) {
            response = gson.toJson(httpTasksManager.getEpics());
        } else if ("subtask".equals(pathParts[2])) {
            response = gson.toJson(httpTasksManager.getSubtasks());
        } else if ("history".equals(pathParts[2])) {
            response = gson.toJson(httpTasksManager.getHistoryManager().getHistory());
        } else {
            exchange.sendResponseHeaders(400, 0);
        }
        return response;
    }

    private void postHandler(HttpExchange exchange) throws IOException {

        String[] pathParts = exchange.getRequestURI().getPath().split("/");
        String pathEnds = pathParts[pathParts.length - 1];

        InputStream inputStream = exchange.getRequestBody();
        String body = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);

        JsonElement jsonElement = JsonParser.parseString(body);

        if (!jsonElement.isJsonObject()) {
            exchange.sendResponseHeaders(400, 0);
            return;
        }

        JsonObject jsonObject = jsonElement.getAsJsonObject();

        if (exchange.getRequestURI().getQuery() != null) {
            if (checkIdContains(exchange)) {
                int id = Integer.parseInt(exchange.getRequestURI().getQuery().split("=")[1]);
                boolean idOfTaskIsExists = httpTasksManager.getTasks().stream().anyMatch(task -> task.getId() == id);
                boolean idOfEpicIsExists = httpTasksManager.getEpics().stream().anyMatch(task -> task.getId() == id);
                boolean idOfSubtaskIsExists = httpTasksManager.getSubtasks().stream()
                        .anyMatch(task -> task.getId() == id);

                if (jsonObject.has("idOfEpic") && "subtask".equals(pathEnds) && idOfSubtaskIsExists) {
                    Subtask subtask = gson.fromJson(body, Subtask.class);
                    subtask.setId(id);
                    httpTasksManager.putSubtask(subtask);
                    exchange.sendResponseHeaders(201, 0);
                } else if (!jsonObject.has("idOfEpic") && "epic".equals(pathEnds) && idOfEpicIsExists) {
                    Epic epic = gson.fromJson(body, Epic.class);
                    epic.setId(id);
                    httpTasksManager.putEpic(epic);
                    exchange.sendResponseHeaders(201, 0);
                } else if (!jsonObject.has("idOfEpic") && "task".equals(pathEnds) && idOfTaskIsExists) {
                    Task task = gson.fromJson(body, Task.class);
                    task.setId(id);
                    httpTasksManager.putTask(task);
                    exchange.sendResponseHeaders(201, 0);
                } else {
                    exchange.sendResponseHeaders(400, 0);
                }
            } else {
                exchange.sendResponseHeaders(400, 0);
            }
        } else {
            if (jsonObject.has("idOfEpic") && "subtask".equals(pathEnds)) {
                Subtask subtask = gson.fromJson(body, Subtask.class);
                try {
                    httpTasksManager.addSubtask(subtask);
                    exchange.sendResponseHeaders(201, 0);
                } catch (DefaultTasksException exception) {
                    exchange.sendResponseHeaders(400, 0);
                    exception.printStackTrace();
                }
            } else if (!jsonObject.has("idOfEpic") && "epic".equals(pathEnds)) {
                Epic epic = gson.fromJson(body, Epic.class);
                httpTasksManager.addEpic(epic);
                exchange.sendResponseHeaders(201, 0);
            } else if (!jsonObject.has("idOfEpic") && "task".equals(pathEnds)) {
                Task task = gson.fromJson(body, Task.class);
                httpTasksManager.addTask(task);
                exchange.sendResponseHeaders(201, 0);
            } else {
                exchange.sendResponseHeaders(400, 0);
            }
        }
    }

    private boolean checkIdContains(HttpExchange exchange) {
        String[] queryParts = exchange.getRequestURI().getQuery().split("=");
        int id = Integer.parseInt(queryParts[1]);
        boolean checkId = true;
        try {
            checkId = httpTasksManager.getAllTasks()
                    .stream()
                    .anyMatch(task -> task.getId() == id);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return checkId;
    }
}