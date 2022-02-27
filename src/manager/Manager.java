package manager;

import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;

public class Manager {
    private HashMap<Integer, Task> tasks = new HashMap<>();
    private HashMap<Integer, Epic> epics = new HashMap<>();
    private HashMap<Integer, Subtask> subtasks = new HashMap<>();

    protected int idOfNewTask = 0;

    public int getIdOfNewTask() {
        return ++idOfNewTask;
    }

    public Subtask createSubtaskOfEpic(Epic epic, String title, String description) {
        Subtask subtask = new Subtask(title, description, this.getIdOfNewTask(), epic.getId());
        epic.getSubtasksInEpic().put(subtask.getId(), subtask);
        subtasks.put(subtask.getId(), subtask);
        return subtask;
    }

    public void addTaskInMap(Task task) {
        tasks.put(task.getId(), task);
    }

    public void addEpicInMap(Epic epic) {
        epics.put(epic.getId(), epic);
    }

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


    public void deleteAllTasks() {
        tasks.clear();
        epics.clear();
        subtasks.clear();
        System.out.println("Список задач пуст");
    }

    public Task getTaskForId(int id) {
        if (tasks.containsKey(id)) {
            return tasks.get(id);
        }
        if (epics.containsKey(id)) {
            return epics.get(id);
        }
        if (subtasks.containsKey(id)) {
            return subtasks.get(id);
        }
        System.out.println("Такого id не существует.");
        return null;
    }

    public void updateTask(Task task) {
        tasks.put(task.getId(), task);
    }

    public void updateEpic(Epic epic) {
        epics.put(epic.getId(), epic);
    }

    public void updateSubtask(Subtask subtask) {
        Epic epic = epics.get(subtask.getIdOfEpic());
        epic.getSubtasksInEpic().put(subtask.getId(), subtask);

        subtasks.put(subtask.getId(), subtask);
    }

    public void updateEpicStatus(Epic epic) {
        int numberOfInProgress = 0;
        int numberOfDone = 0;
        for (Subtask subtask : epic.getSubtasksInEpic().values()) {
            if (subtask.getStatus().equals("IN_PROGRESS")) {
                numberOfInProgress++;
            }
            if (subtask.getStatus().equals("DONE")) {
                numberOfDone++;
            }
        }
        if (numberOfDone == epic.getSubtasksInEpic().size()) {
            epic.setStatus("DONE");
            System.out.println("Статус эпика " + epic.getTitle() + " " + epic.getStatus());
        } else if (numberOfDone < epic.getSubtasksInEpic().size() && numberOfInProgress > 0) {
            epic.setStatus("IN_PROGRESS");
            System.out.println("Статус эпика " + epic.getTitle() + " " + epic.getStatus());
        } else {
            System.out.println("Статус эпика " + epic.getTitle() + " всё ещё " + epic.getStatus());
        }
    }

    public void removeTaskForId(int id) {
        subtasks.remove(id);
        tasks.remove(id);
        epics.remove(id);
    }

    public HashMap getSubtaskOfEpic(Epic epic) {
        return epic.getSubtasksInEpic();
    }
}
