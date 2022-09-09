package manager;

import java.io.File;
import java.io.IOException;

public class Managers {

    public static TaskManager getDefault(String url) throws IOException {
        return HTTPTaskManager.load(url);
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

    public static TaskManager getFileBackedTasksManager(File file) {
        FileBackedTasksManager fileBackedTasksManager = FileBackedTasksManager.loadFromFile(file);
        return fileBackedTasksManager;
    }
}
