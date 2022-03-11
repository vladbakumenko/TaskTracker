package manager;

import tasks.Task;

import java.util.LinkedList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {

    private final static int HISTORY_LIMIT = 10;
    private List<Task> viewHistory = new LinkedList<>();

    @Override
    public void add(Task task) {
        viewHistory.add(task);
    }

    @Override
    public List<Task> getHistory() {
        System.out.println("История последних просмотренных задач:");
        while (viewHistory.size() > HISTORY_LIMIT) {
            viewHistory.remove(0);
        }
        return viewHistory;
    }

}
