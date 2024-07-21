import Exception.ManagerSaveException;
import Model.Epic;
import Model.Subtask;
import Model.Task;
import Service.FileBackedTaskManager;
import Service.Managers;
import main.Status;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

class FileBackedTaskManagerTest {

    private FileBackedTaskManager manager;


    @BeforeEach
    public void beforeEach() {
        manager = new FileBackedTaskManager(Managers.getDefaultHistory(), "TaskManagerTestFile.txt");
    }


    @Test
    void saveAndLoadTest() {
        Task task1 = new Task(null, "task1", "Description1", Status.NEW);
        Epic epic1 = new Epic("epic1", "EpicDescription1", Status.NEW);
        Subtask subTask1 = new Subtask("subTask1", "SubTaskDescription1", Status.IN_PROGRESS, epic1.getId());

        try {
            manager.createTask(task1);
            manager.createEpic(epic1);
            manager.createSubtask(subTask1);


        } catch (ManagerSaveException e) {
            fail(e.getMessage());
        }


        try {
            manager.loadFromFile();
        } catch (ManagerSaveException e) {
            fail(e.getMessage());
        }

        assertEquals(1, manager.getAllTasks().size());
        assertEquals(1, manager.getAllEpics().size());
        assertEquals(1, manager.getAllSubtasks().size());
    }

    @Test
    void saveEmptyFileTest() {


        try {
            manager.save();

        } catch (ManagerSaveException e) {
            fail(e.getMessage());
        }

        try {
            manager.loadFromFile();
        } catch (ManagerSaveException e) {
            fail(e.getMessage());
        }

        // Assert that data was loaded correctly
        assertEquals(0, manager.getAllTasks().size());
        assertEquals(0, manager.getAllEpics().size());
        assertEquals(0, manager.getAllSubtasks().size());
    }
}