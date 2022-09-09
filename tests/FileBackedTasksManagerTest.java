import manager.FileBackedTasksManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FileBackedTasksManagerTest extends TaskManagerTest<FileBackedTasksManager> {

    FileBackedTasksManager fileBackedTasksManager;

    void loadFromFile() {
        fileBackedTasksManager = FileBackedTasksManager.loadFromFile(new File(".\\TaskTrackerData"));
    }

    @BeforeEach
    public void beforeEach() {
        taskManager = new FileBackedTasksManager(new File(".\\TaskTrackerData"));
        createTestTasks();
        taskManager.getTaskForId(1);
        taskManager.getTaskForId(5);
        taskManager.getTaskForId(3);
    }

    @Test
    void shouldSaveAndLoadCorrectInformation() {
        loadFromFile();

        assertEquals(taskManager.getAllTasks(), fileBackedTasksManager.getAllTasks());

        assertEquals(taskManager.getHistoryManager().getHistory(),
                fileBackedTasksManager.getHistoryManager().getHistory());
    }

    @Test
    void shouldEmptyStoragesWhenEmptyListOfTasks() {
        taskManager.deleteAllTasks();

        loadFromFile();

        assertEquals(taskManager.getAllTasks(), fileBackedTasksManager.getAllTasks());

        assertEquals(taskManager.getHistoryManager().getHistory(),
                fileBackedTasksManager.getHistoryManager().getHistory());
    }

    @Test
    void shouldLoadEmptyHistoryIfHistoryStorageEmpty() {
        taskManager.getHistoryManager().removeAll();
        taskManager.save();

        loadFromFile();

        assertTrue(fileBackedTasksManager.getHistoryManager().getHistory().isEmpty());
    }

    @Test
    void shouldLoadCorrectInformationWhenSubtasksIsEmpty() {
        taskManager.removeTaskForId(subtask1.getId());
        taskManager.removeTaskForId(subtask2.getId());
        taskManager.putEpicStatus(epic1);

        loadFromFile();

        assertEquals(taskManager.getAllTasks(), fileBackedTasksManager.getAllTasks());

        assertEquals(taskManager.getHistoryManager().getHistory(),
                fileBackedTasksManager.getHistoryManager().getHistory());
    }

}