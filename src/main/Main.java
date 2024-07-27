package main;

import exception.ManagerSaveException;
import model.Subtask;
import model.Task;
import service.InMemoryTaskManager;
import service.Managers;

public class Main {
    public static void main(String[] args) {
        InMemoryTaskManager taskManager = (InMemoryTaskManager) Managers.getDefault();

        try {
            // Создание задач с использованием метода управления
            int taskId1 = taskManager.createTask("task1", "desc1", Status.NEW);
            int taskId2 = taskManager.createTask("task2", "desc2", Status.NEW);

            // Создание эпиков
            int epicId1 = taskManager.createEpic("Epic1", "descrEpic1", Status.NEW);

            // Создание подзадач через менеджер
            taskManager.createSubtask("SubTask1", "descSub1", Status.NEW, epicId1);
            taskManager.createSubtask("SubTask2", "descSub2", Status.NEW, epicId1);
            taskManager.createSubtask("SubTask3", "descSub3", Status.NEW, epicId1);

            // Вывод всех задач
            System.out.println(taskManager.getAllTasks());
            System.out.println(taskManager.getAllEpics());
            System.out.println(taskManager.getAllSubtasks());

            // Получаем задачи по идентификаторам
            System.out.println(taskManager.getTask(taskId2));
            System.out.println(taskManager.getEpic(epicId1));

            // Можно получить ID подзадач через getAllSubtasks() и проверить их
            System.out.println(taskManager.getHistory());

            // Удаляем задачу
            taskManager.deleteTask(taskId2);
            System.out.println(taskManager.getHistory());

            // Проверяем оставшиеся элементы
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