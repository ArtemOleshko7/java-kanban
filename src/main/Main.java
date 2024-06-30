package main;

import java.util.*;

public class Main {

    public static void main(String[] args) {
        Managers managers = new Managers();
        TaskManager inMemoryTaskManager = managers.getDefault();
        HistoryManager inMemoryHistoryManager = managers.getDefaultHistory();
        Task task = new Task("run", "running", Status.NEW);
        inMemoryTaskManager.addTask(task);

        inMemoryTaskManager.addTask(new Task("Задача 1 без подзадач", "main.Task 1 without subtasks",
                Status.NEW));
        inMemoryTaskManager.addTask(new Task("Задача 2 без подзадач", "main.Task 2 without subtasks",
                Status.NEW));
        inMemoryTaskManager.addEpic(new Epic("Эпик с двумя подзадачами", "main.Epic with 2 subtasks",
                Status.NEW));
        inMemoryTaskManager.addSubtask(new Subtask("Подзадача 1 к Эпику с двумя подзадачами",
                "main.Subtask 1 in main.Epic with 2 subtasks", Status.NEW, 2));
        inMemoryTaskManager.addSubtask(new Subtask("Подзадача 2 к Эпику с двумя подзадачами",
                "main.Subtask 2 in main.Epic with 2 subtasks", Status.NEW, 2));

        inMemoryTaskManager.addEpic(new Epic("Эпик с одной подзадачей", "main.Epic with 1 subtask",
                Status.NEW));
        inMemoryTaskManager.addSubtask(new Subtask("Подзадача 1 к Эпику с одной подзадачей",
                "main.Subtask 1 in main.Epic with 1 subtask", Status.NEW, 5));

        System.out.println("Список Задач:");
        List<Task> taskList = inMemoryTaskManager.getAllTasks();
        System.out.println(taskList);
        System.out.println(" ");

        System.out.println("Список Подзадач:");
        List<Subtask> subtasksList = inMemoryTaskManager.getAllSubtasks();
        System.out.println(subtasksList);
        System.out.println(" ");

        System.out.println("Список Эпиков:");
        List<Epic> epicsList = inMemoryTaskManager.getAllEpics();
        System.out.println(epicsList);
        System.out.println(" ");

        Task task1 = inMemoryTaskManager.getTask(0);
        task1.setStatus(Status.IN_PROGRESS);
        inMemoryTaskManager.updateTask(task1);

        Task task2 = inMemoryTaskManager.getTask(1);
        task2.setStatus(Status.DONE);
        inMemoryTaskManager.updateTask(task2);

        Subtask subtask1 = inMemoryTaskManager.getSubtask(3);
        subtask1.setStatus(Status.IN_PROGRESS);
        inMemoryTaskManager.updateSubtask(subtask1);
        Epic epic1 = inMemoryTaskManager.getEpic(2);
        inMemoryTaskManager.updateEpic(epic1);

        Subtask subtask2 = inMemoryTaskManager.getSubtask(4);
        subtask2.setStatus(Status.DONE);
        inMemoryTaskManager.updateSubtask(subtask2);
        epic1 = inMemoryTaskManager.getEpic(2);
        inMemoryTaskManager.updateEpic(epic1);

        Subtask subtask3 = inMemoryTaskManager.getSubtask(6);
        subtask3.setStatus(Status.DONE);
        inMemoryTaskManager.updateSubtask(subtask3);
        Epic epic2 = inMemoryTaskManager.getEpic(5);
        inMemoryTaskManager.updateEpic(epic2);

        System.out.println("Список Эпиков после изменения статусов:");
        List<Epic> epicsListAfterStatusChange = inMemoryTaskManager.getAllEpics();
        System.out.println(epicsListAfterStatusChange);
        System.out.println(" ");

        System.out.println("Список Задач после изменения статусов:");
        List<Task> taskListAfterStatusChange = inMemoryTaskManager.getAllTasks();
        System.out.println(taskListAfterStatusChange);
        System.out.println(" ");

        System.out.println("Список Подзадач после изменения статусов:");
        List<Subtask> subtasksListAfterStatusChange = inMemoryTaskManager.getAllSubtasks();
        System.out.println(subtasksListAfterStatusChange);
        System.out.println(" ");

        inMemoryTaskManager.deleteTask(1);
        inMemoryTaskManager.deleteEpic(2);

        System.out.println("Список Эпиков после удаления одного Эпика:");
        List<Epic> epicsListAfterDelete = inMemoryTaskManager.getAllEpics();
        System.out.println(epicsListAfterDelete);
        System.out.println(" ");

        System.out.println("Список Задач после удаления одной задачи:");
        List<Task> taskListAfterDelete = inMemoryTaskManager.getAllTasks();
        System.out.println(taskListAfterDelete);
        System.out.println(" ");

        System.out.println("Список Подзадач после удаления одного Эпика:");
        List<Subtask> subtasksListAfterDelete = new ArrayList<>(inMemoryTaskManager.getAllSubtasks());
        System.out.println(subtasksListAfterDelete);
        System.out.println(" ");
    }
}