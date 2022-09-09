package manager;

import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public interface TaskManager {

    HistoryManager getHistoryManager();

    void addSubtask(Subtask subtask);

    void addTask(Task task);

    void addEpic(Epic epic);

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

    void putEpicStatus(Epic epic);

    void removeTaskForId(int id);

    Set<Task> getPrioritizedTasks();
}
