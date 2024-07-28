import service.FileBackedTaskManager;
import model.Epic;
import model.Subtask;
import model.Task;
import model.TaskStatus;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import static service.FileBackedTaskManager.loadFromFile;
import static org.junit.jupiter.api.Assertions.assertEquals;


class FileBackedTaskManagerTest {
    @Test
    void shouldSaveAndLoadEmptyFileSuccessfully() {
        try {
            File file = File.createTempFile("fileForTest", ".txt", new File(
                    "C:\\Users\\I\\desktop\\java-kanban(NewTry)\\test\\resource"));

            FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager(file);
            FileBackedTaskManager fileBackedTaskManager1 = loadFromFile(file);
            assertEquals(fileBackedTaskManager.getFileWithSavedTasks(), file, "Файл не сохранен");
            assertEquals(fileBackedTaskManager.getFileWithSavedTasks().length(), 0, "Файл не пустой");
            assertEquals(fileBackedTaskManager1.getFileWithSavedTasks(), file, "Файл не загружен");
            assertEquals(fileBackedTaskManager1.getFileWithSavedTasks().length(), 0, "Файл не пустой");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    void shouldSaveAndLoadTasksSuccessfully() {
        try {
            File directory = new File("C:\\Users\\I\\desktop\\java-kanban(NewTry)\\test\\resource");
            if (!directory.exists()) {
                directory.mkdirs(); // Создание всех недостающих директорий
            }
            File file1 = File.createTempFile("fileForTest", ".txt", directory);
            FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager(file1);
            Task task = new Task("run", "running", TaskStatus.NEW);
            fileBackedTaskManager.createTask(task);
            Epic epic = new Epic("Move", "Move");
            Subtask subtask = new Subtask("House", "Buy new house", TaskStatus.NEW, epic);
            fileBackedTaskManager.createEpic(epic);
            fileBackedTaskManager.createSubtask(subtask);
            BufferedReader fileReader = new BufferedReader(new FileReader(file1.getPath()));
            String line = fileReader.readLine();
            while (fileReader.ready()) {
                line = fileReader.readLine();
                if (fileBackedTaskManager.getNameClass(line).equals("Task")) {
                    assertEquals(task, fileBackedTaskManager.fromStringTask(line),
                            "Задача не сохранилась в файл");
                } else if (fileBackedTaskManager.getNameClass(line).equals("Subtask")) {
                    assertEquals(subtask, fileBackedTaskManager.fromStringSubtask(line),
                            "Задача не сохранилась в файл");
                } else {
                    assertEquals(epic, fileBackedTaskManager.fromStringEpic(line),
                            "Задача не сохранилась в файл");
                }
            }
            fileReader.close();
            FileBackedTaskManager fileBackedTaskManager1 = loadFromFile(file1);
            assertEquals(task, fileBackedTaskManager1.getTask(1), "Задачи не загрузились");
            assertEquals(epic, fileBackedTaskManager1.getEpic(2), "Задачи не загрузились");
            assertEquals(subtask, fileBackedTaskManager1.getSubtask(3), "Задачи не загрузились");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}