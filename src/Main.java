import manager.Manager;
import tasks.Epic;
import tasks.Task;

public class Main {
    public static void main(String[] args) {
        Manager manager = new Manager();

        Task task1 = new Task("Задача 1", "Описание задачи 1", manager.getIdOfNewTask());
        Task task2 = new Task("Задача 2", "Описание задачи 2", manager.getIdOfNewTask());

        Epic epic1 = new Epic("Эпик 1", "Описание эпика 1", manager.getIdOfNewTask());
        Epic epic2 = new Epic("Эпик 2", "Описание эпика 2", manager.getIdOfNewTask());

        manager.createSubtaskOfEpic(epic1, "Сабтаск 1", "Описание сабтаск 1");
        manager.createSubtaskOfEpic(epic1, "Сабтаск 2", "Описание сабтаск 2");
        manager.createSubtaskOfEpic(epic2, "Сабтаск 3", "Описание сабтаск 3");

        manager.addTaskInMap(task1);
        manager.addTaskInMap(task2);
        manager.addEpicInMap(epic1);
        manager.addEpicInMap(epic2);

        System.out.println(manager.getAllTasks());

        task1.setStatus("IN_PROGRESS");
        task2.setStatus("DONE");

        epic1.getSubtasksInEpic().get(5).setStatus("DONE");
        epic1.getSubtasksInEpic().get(6).setStatus("DONE");
        epic2.getSubtasksInEpic().get(7).setStatus("IN_PROGRESS");

        manager.updateTask(task1);
        manager.updateTask(task2);

        manager.updateSubtask(epic1.getSubtasksInEpic().get(5));
        manager.updateSubtask(epic1.getSubtasksInEpic().get(6));
        manager.updateSubtask(epic2.getSubtasksInEpic().get(7));

        manager.updateEpicStatus(epic1);
        manager.updateEpicStatus(epic2);

        System.out.println(manager.getTaskForId(2));

        System.out.println(manager.getAllTasks());

        manager.removeTaskForId(1);
        manager.removeTaskForId(4);

        System.out.println(manager.getAllTasks());

        manager.deleteAllTasks();

        System.out.println(manager.getAllTasks());
    }
}
