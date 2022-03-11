package manager;

import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;

public class InMemoryTaskManager implements TaskManager {
    private HashMap<Integer, Task> tasks = new HashMap<>();
    private HashMap<Integer, Epic> epics = new HashMap<>();
    private HashMap<Integer, Subtask> subtasks = new HashMap<>();

    private HistoryManager historyManager = Managers.getDefaultHistory();

    protected int idOfNewTask = 0;

    @Override
    public HistoryManager getHistoryManager() {
        return historyManager;
    }

    @Override
    public int getIdOfNewTask() {
        return ++idOfNewTask;
    }

    @Override
    public void createTask(String title, String description) {
        Task task = new Task(title, description, this.getIdOfNewTask());
        tasks.put(task.getId(), task);
    }

    @Override
    public void createEpic(String title, String description) {
        Epic epic = new Epic(title, description, this.getIdOfNewTask());
        epics.put(epic.getId(), epic);
    }

    @Override
    public void createSubtaskOfEpic(String epicTitle, String title, String description) {
        boolean isEpicExists = false;
        for (Integer i : epics.keySet()) {
            if (epics.get(i).getTitle().equals(epicTitle)) {
                Subtask subtask = new Subtask(title, description, this.getIdOfNewTask(), i);
                epics.get(i).getSubtasksInEpic().put(subtask.getId(), subtask);
                subtasks.put(subtask.getId(), subtask);
                isEpicExists = true;
            }
        }
        if (!isEpicExists) {
            System.out.println("Эпика с таким названием не существует");
        }
    }

    @Override
    public ArrayList getAllTasks() {
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
        System.out.println("Список задач пуст");
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
        System.out.println("Такого id не существует.");
        return null;
    }

    @Override
    public void updateTask(Task task) {
        tasks.put(task.getId(), task);
    }

    @Override
    public void updateEpic(Epic epic) {
        epics.put(epic.getId(), epic);
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        Epic epic = epics.get(subtask.getIdOfEpic());
        epic.getSubtasksInEpic().put(subtask.getId(), subtask);

        subtasks.put(subtask.getId(), subtask);
    }

    @Override
    public void updateEpicStatus(Epic epic) {
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
        } else if (numberOfDone < epic.getSubtasksInEpic().size() && numberOfInProgress > 0) {
            epic.setStatus(TaskStatus.IN_PROGRESS);
            System.out.println("Статус эпика " + epic.getTitle() + " " + epic.getStatus());
        } else {
            System.out.println("Статус эпика " + epic.getTitle() + " всё ещё " + epic.getStatus());
        }
    }

    @Override
    public void removeTaskForId(int id) {
        subtasks.remove(id);
        tasks.remove(id);
        epics.remove(id);
    }

    @Override
    public HashMap getSubtaskOfEpic(Epic epic) {
        return epic.getSubtasksInEpic();
    }
}
