package manager;

import exceptions.DefaultTasksException;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class InMemoryTaskManager implements TaskManager {

    protected int idOfNewTask = 0;
    private Map<Integer, Task> tasks = new HashMap<>();
    private Map<Integer, Epic> epics = new HashMap<>();
    private Map<Integer, Subtask> subtasks = new HashMap<>();

    private Comparator<Task> comparator1 = (o1, o2) -> {
        int i = 0;
        if (o1.getStartTime() == null && o2.getStartTime() == null) {
            i = 0;
        } else if (o1.getStartTime() != null && o2.getStartTime() == null) {
            i = -1;
        } else if (o1.getStartTime() == null && o2.getStartTime() != null) {
            i = 1;
        } else if (o1.getStartTime().equals(o2.getStartTime())) {
            i = 0;
        } else if (o1.getStartTime().isBefore(o2.getStartTime())) {
            i = -1;
        } else if (o1.getStartTime().isAfter(o2.getStartTime())) {
            i = 1;
        }
        return i;
    };

    private Comparator<Task> comparator2 = Comparator.comparingInt(Task::getId);

    private Set<Task> prioritizedList = new TreeSet<>(comparator1.thenComparing(comparator2));

    private HistoryManager historyManager = Managers.getDefaultHistory();

    @Override
    public Set<Task> getPrioritizedTasks() {
        return prioritizedList;
    }

    @Override
    public HistoryManager getHistoryManager() {
        return historyManager;
    }

    public int getIdOfNewTask() {
        return ++idOfNewTask;
    }

    public boolean isValidForDuration(Task task) {
        boolean isValid = true;
        LocalDateTime startTime = task.getStartTime();
        LocalDateTime endTime = task.getEndTime();

        if (endTime != null) {
            for (Task currentTask : prioritizedList) {
                if (currentTask.getStartTime() != null && currentTask.getEndTime() != null) {
                    if (endTime.isAfter(currentTask.getStartTime()) &&
                            startTime.isBefore(currentTask.getEndTime())) {
                        isValid = false;
                    }
                }
            }
        }
        return isValid;
    }

    @Override
    public void addTask(Task task) {
        task.setStatus(TaskStatus.NEW);

        if (isValidForDuration(task)) {
            task.setId(getIdOfNewTask());
            tasks.put(task.getId(), task);
            prioritizedList.add(task);
        } else {
            throw new DefaultTasksException("Время выполнения задачи пересекается с " +
                    "выполнением другой задачи, задача не создана.");
        }
    }

    @Override
    public void addEpic(Epic epic) {
        epic.setStatus(TaskStatus.NEW);
        epic.setSubtasksInEpic(new HashMap<>());

        if (isValidForDuration(epic)) {
            epic.setId(getIdOfNewTask());
            epics.put(epic.getId(), epic);
            prioritizedList.add(epic);
        } else {
            throw new DefaultTasksException("Время выполнения задачи пересекается с " +
                    "выполнением другой задачи, задача не создана.");
        }
    }

    @Override
    public void addSubtask(Subtask subtask) {
        Integer epicId = subtask.getIdOfEpic();
        if (epics.get(epicId) != null) {
            subtask.setStatus(TaskStatus.NEW);
            if (isValidForDuration(subtask)) {
                subtask.setId(getIdOfNewTask());
                prioritizedList.remove(epics.get(epicId));
                epics.get(epicId).getSubtasksInEpic().put(subtask.getId(), subtask);
                subtasks.put(subtask.getId(), subtask);
                prioritizedList.add(subtask);

                epics.get(epicId).getDuration();
                epics.get(epicId).getStartTime();
                prioritizedList.add(epics.get(epicId));
            } else {
                throw new DefaultTasksException("Время выполнения задачи пересекается с " +
                        "выполнением другой задачи, задача не создана.");
            }
        } else {
            throw new DefaultTasksException("Эпика с таким id не существует.");
        }
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
        List<Task> listOfTasks = new ArrayList<>();
        for (int id : tasks.keySet()) {
            listOfTasks.add(tasks.get(id));
        }
        for (int id : epics.keySet()) {
            listOfTasks.add(epics.get(id));
        }
        for (int id : subtasks.keySet()) {
            listOfTasks.add(subtasks.get(id));
        }
        return listOfTasks;
    }

    @Override
    public void deleteAllTasks() {
        tasks.clear();
        epics.clear();
        subtasks.clear();
        historyManager.removeAll();
        prioritizedList.clear();
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
        Task oldTask = tasks.get(task.getId());
        prioritizedList.removeIf(t -> t.getId() == task.getId());

        if (task.getStatus() == null) {
            task.setStatus(TaskStatus.NEW);
        }

        if (!isValidForDuration(task)) {
            prioritizedList.add(oldTask);
            return;
        }
        tasks.put(task.getId(), task);
        prioritizedList.add(task);
    }

    @Override
    public void putEpic(Epic epic) {
        Epic oldEpic = epics.get(epic.getId());
        prioritizedList.removeIf(t -> t.getId() == epic.getId());
        try {
            if (oldEpic != null && oldEpic.getSubtasksInEpic() != null && !oldEpic.getSubtasksInEpic().isEmpty()) {
                epic.setSubtasksInEpic(oldEpic.getSubtasksInEpic());
            } else {
                epic.setSubtasksInEpic(new HashMap<>());
            }
        } catch (Exception exc) {
            exc.printStackTrace();
        }

        epic.getStartTime();
        epic.getDuration();
        this.putEpicStatus(epic);

//        if (!isValidForDuration(epic)) {
//            prioritizedList.add(oldEpic);
//            return;
//        }

        epics.put(epic.getId(), epic);
        prioritizedList.add(epic);
    }

    @Override
    public void putSubtask(Subtask subtask) {
        Subtask oldSubtask = subtasks.get(subtask.getId());

        Epic epic = epics.get(subtask.getIdOfEpic());
        if (epic != null) {
            prioritizedList.removeIf(t -> t.getId() == subtask.getId());
            prioritizedList.removeIf(t -> t.getId() == epic.getId());

            if (subtask.getStatus() == null) {
                subtask.setStatus(TaskStatus.NEW);
            }

            if (!isValidForDuration(subtask)) {
                prioritizedList.add(oldSubtask);
                return;
            }

            epic.getSubtasksInEpic().put(subtask.getId(), subtask);
            subtasks.put(subtask.getId(), subtask);

            epic.getStartTime();
            epic.getDuration();
            this.putEpicStatus(epic);

            prioritizedList.add(epic);
            prioritizedList.add(subtask);
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
    public void putEpicStatus(Epic epic) {
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
            epic.setStatus(TaskStatus.NEW);
            System.out.println("Статус эпика " + epic.getTitle() + " всё ещё " + epic.getStatus());
        }
    }

    @Override
    public void removeTaskForId(int id) {
        Map<Integer, Task> collect =
                historyManager.getHistory().stream().collect(Collectors.toMap(Task::getId, Function.identity()));
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

            Map<Integer, Subtask> subtaskMap = epic.getSubtasksInEpic();
            for (int idSubtask : subtaskMap.keySet()) {
                subtasks.remove(idSubtask);
                if (collect.containsKey(idSubtask)) {
                    historyManager.remove(idSubtask);
                }
            }
            epics.remove(id);
        }

        prioritizedList.removeIf(t -> t.getId() == id);
    }
}
