import exception.ManagerSaveException;
import main.Status;
import model.Epic;
import model.Subtask;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.FileBackedTaskManager;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTaskManagerTest {

    private FileBackedTaskManager manager;


    @BeforeEach
    public void setUp() {
        try {
            Files.deleteIfExists(Paths.get("TaskManagerTestFile.txt"));
            Files.createFile(Paths.get("TaskManagerTestFile.txt"));
            // Инициализация manager, укажите нужный конструктор
            manager = new FileBackedTaskManager("TaskManagerTestFile.txt");
        } catch (IOException e) {
            e.printStackTrace(); // Обработка исключения
        }
    }


    @Test
    void saveAndLoadTest() throws ManagerSaveException {
        String testFileName = "test_file.csv";
        deleteIfExists(testFileName);

        FileBackedTaskManager manager = new FileBackedTaskManager(testFileName);
        Epic epic = new Epic(1, "Эпик 1", "Описание эпика", Status.NEW);
        manager.addEpic(epic);
        Subtask subtask = new Subtask(2, "Подзадача 1", "Описание подзадачи", Status.NEW, epic.getId());
        manager.addSubtask(subtask);
        manager.save();

        FileBackedTaskManager newManager = new FileBackedTaskManager(testFileName);
        newManager.load();

        checkEpic(epic, newManager.getEpic(epic.getId()));
        checkSubtask(subtask, newManager.getSubtask(subtask.getId()));

        if (!new File(testFileName).delete()) {
            System.err.println("Ошибка при удалении файла: " + testFileName);
        }
    }

    private void deleteIfExists(String fileName) {
        File file = new File(fileName);
        if (file.exists()) {
            file.delete();
        }
    }

    private void checkEpic(Epic expected, Epic actual) {
        assertNotNull(actual);
        assertEquals(expected.getName(), actual.getName());
        assertEquals(expected.getDescription(), actual.getDescription());
        assertEquals(expected.getStatus(), actual.getStatus());
    }

    private void checkSubtask(Subtask expected, Subtask actual) {
        assertNotNull(actual);
        assertEquals(expected.getName(), actual.getName());
        assertEquals(expected.getDescription(), actual.getDescription());
        assertEquals(expected.getStatus(), actual.getStatus());
        assertEquals(expected.getEpicId(), actual.getEpicId());
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

    @AfterEach
    public void tearDown() {
        try {
            Files.deleteIfExists(Paths.get("TaskManagerTestFile.txt"));
            Files.deleteIfExists(Paths.get("test_file.csv"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}