package service;

import exception.ManagerSaveException;
import model.Epic;
import model.Subtask;
import model.Task;

import java.util.List;

public interface TaskManager {

    void addTask(Task task) throws ManagerSaveException;

    int createTask(Task task) throws ManagerSaveException;

    void updateTask(int id, Task task) throws ManagerSaveException;

    Task getTask(int idCounter) throws ManagerSaveException;

    List<Task> getAllTasks() throws ManagerSaveException;

    void deleteTask(int idCounter);

    void deleteAllTask();

    void addSubtask(Subtask subtask);

    int createSubtask(Subtask subtask) throws ManagerSaveException;

    void updateSubtask(int id, Subtask subtask) throws ManagerSaveException;

    Subtask getSubtask(int idCounter) throws ManagerSaveException;

    List<Subtask> getAllSubtasks();

    List<Subtask> getAllSubtasksInEpic(int idCounter);

    void deleteSubtask(int idCounter);

    void deleteAllSubtasks();

    void addEpic(Epic epic);

    int createEpic(Epic epic) throws ManagerSaveException;

    void updateEpic(int id, Epic epic) throws ManagerSaveException;

    Epic getEpic(int idCounter) throws ManagerSaveException;

    List<Epic> getAllEpics();

    void deleteEpic(int idCounter);

    void deleteAllEpics() throws ManagerSaveException;

    List<Task> getHistory();
}

