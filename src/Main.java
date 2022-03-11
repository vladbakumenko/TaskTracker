import manager.Managers;
import manager.TaskManager;

public class Main {
    public static void main(String[] args) {
        TaskManager manager = Managers.getDefault();

        manager.createTask("Задача 1", "Описание задачи 1");
        manager.createTask("Задача 2", "Описание задачи 2");
        manager.createTask("Задача 3", "Описание задачи 3");
        manager.createTask("Задача 4", "Описание задачи 4");
        manager.createTask("Задача 5", "Описание задачи 5");

        manager.createEpic("Эпик 1", "Описание эпика 1");
        manager.createEpic("Эпик 2", "Описание эпика 2");

        manager.createSubtaskOfEpic("Эпик 1", "Подзадача 1", "Описание подзадачи 1");
        manager.createSubtaskOfEpic("Эпик 1", "Подзадача 2", "Описание подзадачи 2");
        manager.createSubtaskOfEpic("Эпик 1", "Подзадача 3", "Описание подзадачи 3");

        manager.createSubtaskOfEpic("Эпик 2", "Подзадача 1", "Описание подзадачи 1");
        manager.createSubtaskOfEpic("Эпик 2", "Подзадача 2", "Описание подзадачи 2");

        manager.createEpic("Эпик 3", "Описание эпика 3");

        manager.createSubtaskOfEpic("Эпик 3", "Подзадача 1", "Описание подзадачи 1");

        System.out.println(manager.getAllTasks());

        manager.getTaskForId(1);
        manager.getTaskForId(2);
        manager.getTaskForId(3);
        manager.getTaskForId(4);
        manager.getTaskForId(5);
        manager.getTaskForId(6);
        manager.getTaskForId(7);
        manager.getTaskForId(8);
        manager.getTaskForId(9);
        manager.getTaskForId(10);
        manager.getTaskForId(11);
        manager.getTaskForId(12);

        System.out.println(manager.getHistoryManager().getHistory());
    }
}
