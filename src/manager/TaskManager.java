package manager;

import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;

public interface TaskManager {

    public HistoryManager getHistoryManager();

    int getIdOfNewTask();

    void createSubtaskOfEpic(String epicTitle, String title, String description);

    void createTask(String title, String description);

    void createEpic(String title, String description);

    ArrayList getAllTasks();

    void deleteAllTasks();

    Task getTaskForId(int id);

    void updateTask(Task task);

    void updateEpic(Epic epic);

    void updateSubtask(Subtask subtask);

    void updateEpicStatus(Epic epic);

    void removeTaskForId(int id);

    HashMap getSubtaskOfEpic(Epic epic);
}
