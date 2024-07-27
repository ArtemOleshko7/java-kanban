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
    void saveAndLoadTest() throws ManagerSaveException, IOException {
        String testFileName = "test_file.csv";
        File testFile = new File(testFileName);
        if (testFile.exists() && !testFile.delete()) {
            System.err.println("Не удалось удалить существующий файл: " + testFileName);
        }

        FileBackedTaskManager manager = new FileBackedTaskManager(testFileName);

        // Создаем Epic с передачей экземпляра manager
        Epic epic = new Epic(1, manager, "Эпик 1", "Описание эпика", Status.NEW);
        manager.addEpic(epic);

        Subtask subtask = new Subtask(2, "Подзадача 1", "Описание подзадачи", Status.NEW, epic.getId());
        manager.addSubtask(subtask);
        manager.save(); // Сохраняем состояние менеджера в файл

        FileBackedTaskManager newManager = new FileBackedTaskManager(testFileName);
        newManager.load(); // Загружаем задачи из файла

        // Проверяем, что загруженные данные соответствуют оригинальным
        assertEquals(epic.getId(), newManager.getEpic(epic.getId()).getId());
        assertEquals(epic.getName(), newManager.getEpic(epic.getId()).getName());
        assertEquals(epic.getDescription(), newManager.getEpic(epic.getId()).getDescription());
        assertEquals(epic.getStatus(), newManager.getEpic(epic.getId()).getStatus());

        assertEquals(subtask.getId(), newManager.getSubtask(subtask.getId()).getId());
        assertEquals(subtask.getName(), newManager.getSubtask(subtask.getId()).getName());
        assertEquals(subtask.getDescription(), newManager.getSubtask(subtask.getId()).getDescription());
        assertEquals(subtask.getStatus(), newManager.getSubtask(subtask.getId()).getStatus());
        assertEquals(subtask.getEpicId(), newManager.getSubtask(subtask.getId()).getEpicId());

        // Удаляем тестовый файл после завершения теста
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