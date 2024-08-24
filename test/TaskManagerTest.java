import manager.TaskManager;
import org.junit.jupiter.api.BeforeEach;
import model.Epic;
import model.Subtask;
import model.Task;
import model.TaskStatus;

import java.io.File;
import java.io.IOException;

public abstract class TaskManagerTest<T extends TaskManager> {
    protected T manager;
    protected File file;
    protected Task task;
    protected Epic epic;
    protected Subtask subtask;

    @BeforeEach
    void setUp() throws IOException {
        file = new File("data.csv");
        task = new Task("Sample Task", "Description", TaskStatus.NEW, 1);
        epic = new Epic("Epic", "Epic Description", TaskStatus.IN_PROGRESS, 2);
        subtask = new Subtask("Subtask", "Subtask Description", TaskStatus.DONE, epic, 3);
    }
}