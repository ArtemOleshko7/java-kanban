import exception.ManagerSaveException;
import manager.FileBackedTaskManager;
import manager.InMemoryHistoryManager;
import manager.Managers;
import manager.TaskManager;
import model.Epic;
import model.Subtask;
import model.Task;
import model.TaskStatus;

import java.io.File;
import java.time.Duration;
import java.time.LocalDateTime;

import static manager.FileBackedTaskManager.loadFromFile;

public class Main {
    public static void main(String[] args) {
        InMemoryHistoryManager historyManager = new InMemoryHistoryManager();
        File savedTasksFile = new File("savedTasks.txt");
        FileBackedTaskManager fileBackedTaskManager;

        try {
            fileBackedTaskManager = loadFromFile(savedTasksFile);
        } catch (ManagerSaveException e) {
            System.out.println("Ошибка загрузки задач из файла: " + e.getMessage());
            fileBackedTaskManager = new FileBackedTaskManager(savedTasksFile); // Создаем новый менеджер
        }

        TaskManager inMemoryTaskManager = Managers.getDefault();

        Task task = new Task("run", "running", TaskStatus.NEW);
        inMemoryTaskManager.createTask(task);
        Task task2 = new Task("Chek", "Chek work", TaskStatus.NEW);
        inMemoryTaskManager.createTask(task2);

        // Обновленный код создания эпика и подзадач
        LocalDateTime startTime = LocalDateTime.now();
        Duration duration = Duration.ofHours(2);

        Epic epic = new Epic("Move", "Move", TaskStatus.NEW, 1, duration, startTime);
        inMemoryTaskManager.createEpic(epic);

        Subtask subtask = new Subtask("F", "f", TaskStatus.NEW, epic, Duration.ofHours(1)); // Добавлено Duration
        Subtask subtask2 = new Subtask("ff", "fff", TaskStatus.IN_PROGRESS, epic, Duration.ofHours(1)); // Добавлено Duration
        inMemoryTaskManager.createSubtask(subtask);
        inMemoryTaskManager.createSubtask(subtask2);

        Epic epic1 = new Epic("Study", "studying", TaskStatus.NEW, 2);
        inMemoryTaskManager.createEpic(epic1);
        Subtask subtask3 = new Subtask("read", "reading", TaskStatus.DONE, epic1, Duration.ofHours(1)); // Добавлено Duration
        inMemoryTaskManager.createSubtask(subtask3);

        System.out.println("Список задач:" + inMemoryTaskManager.getAllTasks());
        System.out.println("Список эпиков:" + inMemoryTaskManager.getAllEpics());
        System.out.println("Список подзадач:" + inMemoryTaskManager.getAllSubtasks());
        System.out.println("");

        // Вывод информации
        System.out.println(inMemoryTaskManager.getTask(task.getId()));
        System.out.println(inMemoryTaskManager.getEpic(epic.getId()));
        System.out.println(inMemoryTaskManager.getSubtask(subtask.getId()));
        System.out.println("История " + historyManager.getHistory());
        System.out.println("Размер " + historyManager.getHistory().size());

        task.setStatus(TaskStatus.DONE);
        subtask2.setStatus(TaskStatus.DONE);
        subtask.setStatus(TaskStatus.DONE);
        inMemoryTaskManager.updateSubtask(subtask);
        inMemoryTaskManager.updateSubtask(subtask2);

        System.out.println(inMemoryTaskManager.getTask(task.getId()));
        System.out.println(inMemoryTaskManager.getEpic(epic.getId()));
        System.out.println(inMemoryTaskManager.getSubtask(subtask.getId()));

        System.out.println(historyManager.getHistory());
        System.out.println("Размер " + historyManager.getHistory().size());

        task.setNameTask("NoRun");
        Task task12 = new Task("Studydf", "Studyingdf", TaskStatus.NEW);

        // Сохранение задач в файл
        fileBackedTaskManager.createTask(task);
        fileBackedTaskManager.createTask(task2);
        Epic epic4 = new Epic("Move", "Move");
        fileBackedTaskManager.createEpic(epic4);
        Subtask subtask4 = new Subtask("read", "reading", TaskStatus.DONE, epic4, Duration.ofHours(1));
        fileBackedTaskManager.createSubtask(subtask4);


    }
}