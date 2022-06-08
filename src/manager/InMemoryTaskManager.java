package manager;

import exceptions.DefaultTasksException;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class InMemoryTaskManager implements TaskManager {

    private Map<Integer, Task> tasks = new HashMap<>();
    private Map<Integer, Epic> epics = new HashMap<>();
    private Map<Integer, Subtask> subtasks = new HashMap<>();

    private HistoryManager historyManager = Managers.getDefaultHistory();

    protected int idOfNewTask = 0;

    @Override
    public HistoryManager getHistoryManager() {
        return historyManager;
    }

    public int getIdOfNewTask() {
        return ++idOfNewTask;
    }

    @Override
    public Task createTask(String title, String description, LocalDateTime startTime, Duration duration) {
        Task task = new Task(title, description, this.getIdOfNewTask(), startTime, duration);
        tasks.put(task.getId(), task);
        return task;
    }

    @Override
    public Epic createEpic(String title, String description) {
        Epic epic = new Epic(title, description, this.getIdOfNewTask());
        epics.put(epic.getId(), epic);
        return epic;
    }

    @Override
    public Subtask createSubtask(int epicId, String title, String description, LocalDateTime startTime, Duration duration) {

        if (epics.get(epicId) != null) {
            Subtask subtask = new Subtask(title, description, this.getIdOfNewTask(), epicId, startTime, duration);
            epics.get(epicId).getSubtasksInEpic().put(subtask.getId(), subtask);
            subtasks.put(subtask.getId(), subtask);
            return subtask;
        }

        throw new DefaultTasksException("Эпика с таким id не существует.");
    }

    @Override
    public List<Task> getTasks() {
        ArrayList<Task> list = new ArrayList<>();
        for (int id : tasks.keySet()) {
            list.add(tasks.get(id));
        }
        return list;
    }

    @Override
    public List<Epic> getEpics() {
        ArrayList<Epic> list = new ArrayList<>();
        for (int id : epics.keySet()) {
            list.add(epics.get(id));
        }
        return list;
    }

    @Override
    public List<Subtask> getSubtasks() {
        ArrayList<Subtask> list = new ArrayList<>();
        for (int id : subtasks.keySet()) {
            list.add(subtasks.get(id));
        }
        return list;
    }

    @Override
    public List<Task> getAllTasks() {
        ArrayList<Task> listOfTasks = new ArrayList<>();
        for (int id : tasks.keySet()) {
            listOfTasks.add(tasks.get(id));
        }
        for (int id : epics.keySet()) {
            listOfTasks.add(epics.get(id));
            for (int idOfSubtask : epics.get(id).getSubtasksInEpic().keySet()) {
                listOfTasks.add(epics.get(id).getSubtasksInEpic().get(idOfSubtask));
            }
        }
        return listOfTasks;
    }

    @Override
    public void deleteAllTasks() {
        tasks.clear();
        epics.clear();
        subtasks.clear();
        historyManager.removeAll();
//        System.out.println("Список задач и истории просмотров пуст.");
    }

    @Override
    public Task getTaskForId(int id) {
        if (tasks.containsKey(id)) {
            historyManager.add(tasks.get(id));
            return tasks.get(id);
        }
        if (epics.containsKey(id)) {
            historyManager.add(epics.get(id));
            return epics.get(id);
        }
        if (subtasks.containsKey(id)) {
            historyManager.add(subtasks.get(id));
            return subtasks.get(id);
        }
        throw new DefaultTasksException("Такого id не существует.");
    }

    @Override
    public void putTask(Task task) {
        tasks.put(task.getId(), task);
    }

    @Override
    public void putEpic(Epic epic) {
        epics.put(epic.getId(), epic);
    }

    @Override
    public void putSubtask(Subtask subtask) {
        Epic epic = epics.get(subtask.getIdOfEpic());
        if (epic != null) {
            epic.getSubtasksInEpic().put(subtask.getId(), subtask);

            subtasks.put(subtask.getId(), subtask);
        } else {
            throw new DefaultTasksException("У сабтаск не существует эпика.");
        }
    }

    @Override
    public void setNewTaskStatus(Task task, TaskStatus newStatus) {
        task.setStatus(newStatus);
    }

    @Override
    public void setNewSubtaskStatus(Subtask subtask, TaskStatus newStatus) {
        subtask.setStatus(newStatus);
    }


    @Override
    public void updateEpicStatus(Epic epic) {
        if (epic.getSubtasksInEpic().isEmpty()) {
            epic.setStatus(TaskStatus.NEW);
            return;
        }

        int numberOfInProgress = 0;
        int numberOfDone = 0;
        for (Subtask subtask : epic.getSubtasksInEpic().values()) {
            if (TaskStatus.IN_PROGRESS.equals(subtask.getStatus())) {
                numberOfInProgress++;
            }
            if (TaskStatus.DONE.equals(subtask.getStatus())) {
                numberOfDone++;
            }
        }
        if (numberOfDone == epic.getSubtasksInEpic().size()) {
            epic.setStatus(TaskStatus.DONE);
            System.out.println("Статус эпика " + epic.getTitle() + " " + epic.getStatus());
        } else if (numberOfDone > 0 || numberOfInProgress > 0) {
            epic.setStatus(TaskStatus.IN_PROGRESS);
            System.out.println("Статус эпика " + epic.getTitle() + " " + epic.getStatus());
        } else {
            System.out.println("Статус эпика " + epic.getTitle() + " всё ещё " + epic.getStatus());
        }

//        epic.getStartTime();
//        epic.getDuration();
    }

    @Override
    public void removeTaskForId(int id) {
        Map<Integer, Task> collect = historyManager.getHistory().stream().collect(Collectors.toMap(task -> task.getId(), Function.identity()));
        tasks.remove(id);
        if (collect.containsKey(id)) {
            historyManager.remove(id);
        }

        if (subtasks.containsKey(id)) {
            int idOFEpic = subtasks.get(id).getIdOfEpic();
            Epic epic = epics.get(idOFEpic);
            epic.getSubtasksInEpic().remove(id);

            subtasks.remove(id);
        }

        if (epics.containsKey(id)) {
            Epic epic = epics.get(id);
//            epic.getSubtasksInEpic().clear();

            Map<Integer, Subtask> subtaskMap = epic.getSubtasksInEpic();
            for (int idSubtask : subtaskMap.keySet()) {
                subtasks.remove(idSubtask);
                if (collect.containsKey(idSubtask)) {
                    historyManager.remove(idSubtask);
                }
            }
            epics.remove(id);
        }
    }
}
