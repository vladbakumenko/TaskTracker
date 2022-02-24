import java.util.HashMap;

public class Manager {
    HashMap<Integer, Task> mapOfTask = new HashMap<>();
    HashMap<Integer, Epic> mapOfEpic = new HashMap<>();

    protected int idOfNewTask = 0;

    public void setNewTaskInMap(Task task) {
        mapOfTask.put(task.getId(), task);
    }

    public void setNewEpicInMap(Epic epic) {
        mapOfEpic.put(epic.getId(), epic);
    }

    public void setNewSubtaskInMap(Epic epic, Subtask subtask) {
        epic.mapOfSubtask.put(subtask.getId(), subtask);
    }

    public void printAllTasks() {
        for (int id : mapOfTask.keySet()) {
            System.out.println("Задача:");
            System.out.println(mapOfTask.get(id));
        }
        System.out.println("");
        for (int id : mapOfEpic.keySet()) {
            System.out.println("Эпик:");
            System.out.println(mapOfEpic.get(id));
            System.out.println("Подзадачи:");
            for (int idOfSubtask : mapOfEpic.get(id).mapOfSubtask.keySet()) {
                System.out.println(mapOfEpic.get(id).mapOfSubtask.get(idOfSubtask));
            }
            System.out.println("");
        }
    }

    public void deleteAllTasks() {
        mapOfTask.clear();
        mapOfEpic.clear();
        System.out.println("Список задач пуст");
    }

    public Object getTaskForId(int id) {
        if (mapOfTask.containsKey(id)) {
            return mapOfTask.get(id);
        }
        if (mapOfEpic.containsKey(id)) {
            return mapOfEpic.get(id);
        } else {
            for (int i : mapOfEpic.keySet()) {
                if (mapOfEpic.get(i).mapOfSubtask.containsKey(id)) {
                    return mapOfEpic.get(i).mapOfSubtask.get(id);
                }
            }
        }
        return "Такого id не существует";
    }

    public void updateTask(Task task) {
        mapOfTask.put(task.getId(), task);
    }

    public void updateEpic(Epic epic) {
        mapOfEpic.put(epic.getId(), epic);
    }

    public void updateSubtask(Epic epic, Subtask subtask) {
        epic.mapOfSubtask.put(subtask.getId(), subtask);
        for (int i : mapOfEpic.keySet()) {
            Epic epic2 = mapOfEpic.get(i);
            int numberOfInProgress = 0;
            int numberOfDone = 0;
            for (int id : epic2.mapOfSubtask.keySet()) {
                if (epic2.mapOfSubtask.get(id).getStatus() == "IN_PROGRESS") {
                    numberOfInProgress++;
                }
                if (epic2.mapOfSubtask.get(id).getStatus() == "DONE") {
                    numberOfDone++;
                }
            }
            if (numberOfDone == epic2.mapOfSubtask.size()) {
                epic2.setStatus("DONE");
                System.out.println("Статус эпика " + epic2.getTitle() + " " + epic2.getStatus());
            } else if (numberOfDone < epic2.mapOfSubtask.size() && numberOfInProgress > 0) {
                epic2.setStatus("IN_PROGRESS");
                System.out.println("Статус эпика " + epic2.getTitle() + " " + epic2.getStatus());
            } else {
                System.out.println("Статус эпика " + epic2.getTitle() + " всё ещё " + epic2.getStatus());
            }
        }
    }

    public void removeTaskForId(int id) {
        if (mapOfTask.containsKey(id)) {
            mapOfTask.remove(id);
        }
        if (mapOfEpic.containsKey(id)) {
            mapOfEpic.remove(id);
        }
        for (int i : mapOfEpic.keySet()) {
            if (mapOfEpic.get(i).mapOfSubtask.containsKey(id)) {
                mapOfEpic.get(i).mapOfSubtask.remove(id);
            }
        }
    }

    public HashMap getSubtaskOfEpic(Epic epic) {
        return epic.mapOfSubtask;
    }

    public int getIdOfNewTask() {
        idOfNewTask++;
        return idOfNewTask;
    }
}
