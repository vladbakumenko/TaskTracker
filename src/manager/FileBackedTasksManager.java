package manager;

import exceptions.ManagerSaveException;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.io.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class FileBackedTasksManager extends InMemoryTaskManager {

    private final File file;

    public FileBackedTasksManager(File file) {
        super();
        this.file = file;
    }

    public FileBackedTasksManager() {
        super();
        this.file = null;
    }

    @Override
    public void addTask(Task task) {
        super.addTask(task);
        save();
    }

    @Override
    public void addEpic(Epic epic) {
        super.addEpic(epic);
        save();
    }

    @Override
    public void addSubtask(Subtask subtask) {
        super.addSubtask(subtask);
        save();
    }

    @Override
    public void deleteAllTasks() {
        super.deleteAllTasks();
        save();
    }

    @Override
    public Task getTaskForId(int id) {
        Task task = super.getTaskForId(id);
        save();
        return task;
    }

    @Override
    public void putTask(Task task) {
        super.putTask(task);
        save();
    }

    public void loadTask(Task task) {
        TaskStatus status = TaskStatus.NEW;

        if (task.getStatus().equals(TaskStatus.IN_PROGRESS) || task.getStatus().equals(TaskStatus.DONE)) {
            status = task.getStatus();
        }
        super.addTask(task);
        task.setStatus(status);
    }

    @Override
    public void putEpic(Epic epic) {
        super.putEpic(epic);
        save();
    }

    public void loadEpic(Epic epic) {
        TaskStatus status = TaskStatus.NEW;
        if (epic.getStatus().equals(TaskStatus.IN_PROGRESS) || epic.getStatus().equals(TaskStatus.DONE)) {
            status = epic.getStatus();
        }
        super.addEpic(epic);
        epic.setStatus(status);
    }

    @Override
    public void putSubtask(Subtask subtask) {
        super.putSubtask(subtask);
        save();
    }

    public void loadSubtask(Subtask subtask) {
        TaskStatus status = TaskStatus.NEW;
        if (subtask.getStatus().equals(TaskStatus.IN_PROGRESS) || subtask.getStatus().equals(TaskStatus.DONE)) {
            status = subtask.getStatus();
        }
        super.addSubtask(subtask);
        subtask.setStatus(status);
    }

    @Override
    public void setNewTaskStatus(Task task, TaskStatus newStatus) {
        super.setNewTaskStatus(task, newStatus);
        save();
    }

    @Override
    public void setNewSubtaskStatus(Subtask subtask, TaskStatus newStatus) {
        super.setNewSubtaskStatus(subtask, newStatus);
        save();
    }

    @Override
    public void putEpicStatus(Epic epic) {
        super.putEpicStatus(epic);
        save();
    }

    @Override
    public void removeTaskForId(int id) {
        super.removeTaskForId(id);
        save();
    }

    public void save() {
        String info = "id,type,name,status,description,start-time,duration,epic";

        try (BufferedWriter wr = new BufferedWriter(new FileWriter(file))) {
            wr.write(info + System.lineSeparator());

            for (Task task : super.getTasks()) {
                wr.write(task.toString() + System.lineSeparator());
            }

            for (Epic epic : super.getEpics()) {
                wr.write(epic.toString() + System.lineSeparator());
            }

            for (Subtask subtask : super.getSubtasks()) {
                wr.write(subtask.toString() + System.lineSeparator());
            }

            if (!super.getHistoryManager().getHistory().isEmpty()) {
                wr.write(System.lineSeparator());

                wr.write(toStringHistory(super.getHistoryManager()));
            }
        } catch (IOException e) {
            throw new ManagerSaveException(e);
        }
    }

    public static String toStringHistory(HistoryManager manager) {
        String history = "";

        if (manager.getHistory().isEmpty()) {
            return history;
        }

        for (Task task : manager.getHistory()) {
            int id = task.getId();
            if (manager.getHistory().size() == 1) {
                history = "" + id;
                continue;
            }
            if (task.equals(manager.getHistory().get(0))) {
                history = id + ",";
                continue;
            }
            if (task.equals(manager.getHistory().get(manager.getHistory().size() - 1))) {
                history = history + id;
                continue;
            }
            history = history + id + ",";
        }

        return history;
    }

    public static List<Integer> historyFromString(String value) {
        List<Integer> tasksId = new ArrayList<>();

        if (!value.isEmpty()) {
            String[] integers = value.split(",");

            for (String integer : integers) {
                tasksId.add(Integer.parseInt(integer));
            }
        } else {
            System.out.println("История просмотров пуста.");
        }

        return tasksId;
    }

    public Task fromString(String value) {
        String[] arr = value.split(",");

        Task task = null;

        TaskStatus status = switch (arr[3]) {
            case "NEW" -> TaskStatus.NEW;
            case "IN_PROGRESS" -> TaskStatus.IN_PROGRESS;
            case "DONE" -> TaskStatus.DONE;
            default -> null;
        };

        int id = Integer.parseInt(arr[0]);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy|HH:mm");
        LocalDateTime startTime = null;
        if (!"null".equals(arr[5])) {
            startTime = LocalDateTime.parse(arr[5], formatter);
        }

        Duration duration = null;
        if (!"null".equals(arr[6])) {
            duration = Duration.ofMinutes(Integer.parseInt(arr[6]));
        }

        switch (arr[1]) {
            case "TASK" -> {
                task = new Task(arr[2], arr[4], status, startTime, duration);
                task.setId(id);
            }
            case "EPIC" -> {
                task = new Epic(arr[2], arr[4]);
                task.setId(id);
                task.setStatus(status);
                task.setStartTime(startTime);
                task.setDuration(duration);
            }
            case "SUBTASK" -> {
                task = new Subtask(arr[2], arr[4], Integer.parseInt(arr[7]), status, startTime, duration);
                task.setId(id);
            }
        }

        return task;
    }

    public static FileBackedTasksManager loadFromFile(File file) {
        FileBackedTasksManager fileBackedTasksManager = new FileBackedTasksManager(file);

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {

            int mode = 0;

            while (br.ready()) {
                String line = br.readLine();

                if (mode == 0) {
                    mode = 1;
                    continue;
                }

                if (line.isEmpty()) {
                    mode = 2;
                    continue;
                }

                if (mode == 1) {

                    Task obj = fileBackedTasksManager.fromString(line);

                    if (obj instanceof Epic) {
                        Epic epic = (Epic) obj;
                        fileBackedTasksManager.putEpic(epic);
                        continue;
                    }
                    if (obj instanceof Subtask) {
                        Subtask subtask = (Subtask) obj;
                        fileBackedTasksManager.putSubtask(subtask);
                        continue;
                    }
                    if (obj instanceof Task) {
                        Task task = (Task) obj;
                        fileBackedTasksManager.putTask(task);
                        continue;
                    }

                }

                if (mode == 2) {
                    List<Integer> tasksId = historyFromString(line);

                    for (int id : tasksId) {
                        fileBackedTasksManager.getTaskForId(id);
                    }
                }
            }

            int max = 0;

            for (Task task : fileBackedTasksManager.getAllTasks()) {
                if (task.getId() > max) {
                    max = task.getId();
                }
            }

            fileBackedTasksManager.idOfNewTask = max;

        } catch (IOException e) {
            e.printStackTrace();
        }

        return fileBackedTasksManager;
    }

}
