import exception.ManagerSaveException;
import main.Status;
import model.Epic;
import model.Subtask;
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
        // Проверяем, существует ли файл, и удаляем его, если да
        File testFile = new File(testFileName);
        if (testFile.exists()) {
            testFile.delete();
        }

        FileBackedTaskManager manager = new FileBackedTaskManager(testFileName);
        Epic epic = new Epic(1, "Эпик 1", "Описание эпика", Status.NEW);
        manager.addEpic(epic);
        Subtask subtask = new Subtask(1, "Подзадача 1", "Описание подзадачи", Status.NEW, epic.getId());
        manager.addSubtask(subtask);
        manager.save();

        FileBackedTaskManager newManager = new FileBackedTaskManager(testFileName);
        newManager.load();

        Epic loadedEpic = newManager.getEpic(epic.getId());
        Subtask loadedSubtask = newManager.getSubtask(subtask.getId());

        assertNotNull(loadedEpic);
        assertEquals(epic.getName(), loadedEpic.getName());
        assertEquals(epic.getDescription(), loadedEpic.getDescription());
        assertEquals(epic.getStatus(), loadedEpic.getStatus());

        assertNotNull(loadedSubtask);
        assertEquals(subtask.getName(), loadedSubtask.getName());
        assertEquals(subtask.getDescription(), loadedSubtask.getDescription());
        assertEquals(subtask.getStatus(), loadedSubtask.getStatus());
        assertEquals(loadedEpic.getId(), loadedSubtask.getEpicId());

        // Очистка: удаляем тестовый файл
        if (!testFile.delete()) {
            System.err.println("Ошибка при удалении файла: " + testFileName);
        }
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