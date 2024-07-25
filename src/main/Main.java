package main;

import exception.ManagerSaveException;
import model.Subtask;
import model.Task;
import service.InMemoryTaskManager;
import service.Managers;
import service.TaskManager;

public class Main {
    public static void main(String[] args) {
        InMemoryTaskManager taskManager = (InMemoryTaskManager) Managers.getDefault(); // Приводим к нужному типу

        try {
            // Создание задач
            Task task1 = new Task(InMemoryTaskManager.generateId(), "task1", "desc1", Status.NEW);
            Task task2 = new Task(InMemoryTaskManager.generateId(), "task2", "desc2", Status.NEW);

            // Создание эпиков и подзадач
            int epicId1 = taskManager.createEpic("Epic1", "descrEpic1", Status.NEW);
            Subtask subtask1 = new Subtask(InMemoryTaskManager.generateId(), "SubTask1", "descSub1", Status.NEW, epicId1);
            Subtask subtask2 = new Subtask(InMemoryTaskManager.generateId(), "SubTask2", "descSub2", Status.NEW, epicId1);
            Subtask subtask3 = new Subtask(InMemoryTaskManager.generateId(), "SubTask3", "descSub3", Status.NEW, epicId1);

            // Сохранение задач
            taskManager.createTask(task1.getName(), task1.getDescription(), task1.getStatus());
            taskManager.createTask(task2.getName(), task2.getDescription(), task2.getStatus());

            // Сохранение подзадач, исправляем вызовы:
            taskManager.createSubtask(subtask1.getName(), subtask1.getDescription(), subtask1.getStatus(), epicId1);
            taskManager.createSubtask(subtask2.getName(), subtask2.getDescription(), subtask2.getStatus(), epicId1);
            taskManager.createSubtask(subtask3.getName(), subtask3.getDescription(), subtask3.getStatus(), epicId1);

            // Вывод всех задач
            System.out.println(taskManager.getAllTasks());
            System.out.println(taskManager.getAllEpics());
            System.out.println(taskManager.getAllSubtasks());

            // Получаем задачи по идентификаторам
            System.out.println(taskManager.getTask(task2.getId()));
            System.out.println(taskManager.getEpic(epicId1));
            System.out.println(taskManager.getSubtask(subtask1.getId()));
            System.out.println(taskManager.getHistory());

            // Удаляем задачу
            taskManager.deleteTask(task2.getId());
            System.out.println(taskManager.getHistory());

            // Проверяем оставшиеся элементы
            System.out.println(taskManager.getSubtask(subtask1.getId()));
            System.out.println(taskManager.getEpic(epicId1));
            System.out.println(taskManager.getHistory());

            // Удаляем эпик
            taskManager.deleteEpic(epicId1);
            System.out.println(taskManager.getHistory());

        } catch (ManagerSaveException e) {
            System.err.println("Ошибка управления задачами: " + e.getMessage());
        }
    }
}