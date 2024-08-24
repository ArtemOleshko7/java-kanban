import exception.ManagerSaveException;
import manager.*;
import model.Epic;
import model.Task;
import model.TaskStatus;
import model.Subtask;

import java.io.File;
import java.time.Duration;
import java.time.LocalDateTime;

import static manager.FileBackedTaskManager.loadFromFile;

public class Main {
    public static void main(String[] args) {
        File savedTasksFile = new File("savedTasks.txt");
        FileBackedTaskManager fileBackedTaskManager = null;

        try {
            fileBackedTaskManager = loadFromFile(savedTasksFile);
        } catch (ManagerSaveException e) {
            System.out.println("Ошибка загрузки задач из файла: " + e.getMessage());
        }

        TaskManager inMemoryTaskManager = Managers.getDefault();

        Task task = new Task("run", "running", TaskStatus.NEW);
        inMemoryTaskManager.createTask(task);
        Task task2 = new Task("Chek", "Chek work", TaskStatus.NEW);
        inMemoryTaskManager.createTask(task2);

        // Обновленный код создания эпика с новым параметрами
        LocalDateTime startTime = LocalDateTime.now();
        Duration duration = Duration.ofHours(2);

        Epic epic = new Epic("Move", "Move", TaskStatus.NEW, 1, duration, startTime);
        Subtask subtask = new Subtask("F", "f", TaskStatus.NEW, epic);
        Subtask subtask2 = new Subtask("ff", "fff", TaskStatus.IN_PROGRESS, epic);
        inMemoryTaskManager.createEpic(epic);
        inMemoryTaskManager.createSubtask(subtask);
        inMemoryTaskManager.createSubtask(subtask2);

        Epic epic1 = new Epic("Study", "studying", TaskStatus.NEW, 2);
        inMemoryTaskManager.createEpic(epic1);
        Subtask subtask3 = new Subtask("read", "reading", TaskStatus.DONE, epic1);
        inMemoryTaskManager.createSubtask(subtask3);

        System.out.println("Список задач:" + inMemoryTaskManager.getAllTasks());
        System.out.println("Список эпиков:" + inMemoryTaskManager.getAllEpics());
        System.out.println("Список подзадач:" + inMemoryTaskManager.getAllSubtasks());
        System.out.println("");

        // Вывод информации
        System.out.println(inMemoryTaskManager.getTask(task.getId()));
        System.out.println(inMemoryTaskManager.getEpic(epic.getId()));
        System.out.println(inMemoryTaskManager.getSubtask(subtask.getId()));
        System.out.println("История " + inMemoryTaskManager.getHistory());
        System.out.println("Размер " + inMemoryTaskManager.getHistory().size());

        task.setStatus(TaskStatus.DONE);
        subtask2.setStatus(TaskStatus.DONE);
        subtask.setStatus(TaskStatus.DONE);
        inMemoryTaskManager.updateSubtask(subtask);
        inMemoryTaskManager.updateSubtask(subtask2);

        System.out.println(inMemoryTaskManager.getTask(task.getId()));
        System.out.println(inMemoryTaskManager.getEpic(epic.getId()));
        System.out.println(inMemoryTaskManager.getSubtask(subtask.getId()));

        System.out.println(inMemoryTaskManager.getHistory());
        System.out.println("Размер " + inMemoryTaskManager.getHistory().size());

        task.setNameTask("NoRun");
        Task task12 = new Task("Studydf", "Studyingdf", TaskStatus.NEW);

        // Сохранение задач в файл
        if (fileBackedTaskManager != null) {
            fileBackedTaskManager.createTask(task);
            fileBackedTaskManager.createTask(task2);
            Epic epic4 = new Epic("Move", "Move");
            fileBackedTaskManager.createEpic(epic4);
            Subtask subtask4 = new Subtask("read", "reading", TaskStatus.DONE, epic4);
            fileBackedTaskManager.createSubtask(subtask4);
            subtask4.setStatus(TaskStatus.NEW);
        }
    }
}