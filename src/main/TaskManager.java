package main;

import java.util.*;

public interface TaskManager {

    Task addTask(Task task);
    void updateTask(Task task);
    Task getTask(int idCounter);
    List<Task> getAllTasks();
    void deleteTask(int idCounter);
    void deleteAllTasks();
    void addSubtask(Subtask subtask);
    void updateSubtask(Subtask subtask);
    Subtask getSubtask(int idCounter);
    List<Subtask> getAllSubtasks();
    List<Subtask> getAllSubtasksInEpic(int idCounter);
    void deleteSubtask(int idCounter);
    void deleteAllSubtasks();
    void addEpic(Epic epic);
    void updateEpic(Epic epic);
    Epic getEpic(int idCounter);
    List<Epic> getAllEpics();
    void deleteEpic(int idCounter);
    void deleteAllEpics();
    List<Task> getHistory();
}

