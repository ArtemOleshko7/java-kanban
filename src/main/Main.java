package main;

import java.util.*;

public class Main {

    public static void main(String[] args) {
        TaskManager taskManager = Managers.getDefault();

        try {
            Task task1 = new Task(1, "task1", "desc1", Status.NEW, TaskType.TASK);
            Task task2 = new Task(2, "task2", "desc2", Status.NEW, TaskType.TASK);
            Epic epic1 = new Epic("Epic1", "descrEpic1", Status.NEW);
            Subtask subtask1 = new Subtask("SubTask1", "descSub1", Status.NEW, epic1.getId());
            Subtask subtask2 = new Subtask("SubTask2", "descSub2", Status.NEW, epic1.getId());
            Subtask subtask3 = new Subtask("SubTask3", "descSub3", Status.NEW, epic1.getId());
            Epic epic2 = new Epic("Epic2", "descrEpic2", Status.NEW);

            taskManager.createTask(task1);
            taskManager.createTask(task2);
            taskManager.createEpic(epic1);
            taskManager.createSubtask(subtask1);
            taskManager.createSubtask(subtask2);
            taskManager.createSubtask(subtask3);
            taskManager.createEpic(epic2);

            System.out.println(taskManager.getAllTasks());
            System.out.println(taskManager.getAllEpics());
            System.out.println(taskManager.getAllSubtasks());

            // Получаем задачи по идентификаторам
            System.out.println(taskManager.getTask(2));
            System.out.println(taskManager.getEpic(3));
            System.out.println(taskManager.getEpic(3));
            System.out.println(taskManager.getEpic(3));
            System.out.println(taskManager.getTask(2));
            System.out.println(taskManager.getHistory());

            // Удаляем задачу
            taskManager.deleteTask(2);
            System.out.println(taskManager.getHistory());

            System.out.println(taskManager.getSubtask(4));
            System.out.println(taskManager.getEpic(3));
            System.out.println(taskManager.getHistory());

            // Удаляем эпик
            taskManager.deleteEpic(3);
            System.out.println(taskManager.getHistory());

        } catch (ManagerSaveException e) {
            System.err.println("Ошибка управления задачами: " + e.getMessage());
        }
    }
}