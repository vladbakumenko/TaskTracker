package manager;

import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

public interface TaskManager {

    HistoryManager getHistoryManager();

    Subtask createSubtask(int epicId, String title, String description, LocalDateTime startTime, Duration duration);

    Task createTask(String title, String description, LocalDateTime startTime, Duration duration);

    Epic createEpic(String title, String description);

    List<Task> getTasks();

    List<Epic> getEpics();

    List<Subtask> getSubtasks();

    List<Task> getAllTasks();

    void deleteAllTasks();

    Task getTaskForId(int id);

    void putTask(Task task);

    void putEpic(Epic epic);

    void putSubtask(Subtask subtask);

    void setNewTaskStatus(Task task, TaskStatus newStatus);

    void setNewSubtaskStatus(Subtask subtask, TaskStatus newStatus);

    void updateEpicStatus(Epic epic);

    void removeTaskForId(int id);
}
