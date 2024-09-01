package manager;

import model.Epic;
import model.Task;
import model.Subtask;

import java.util.List;


public interface TaskManager {


    Task getTask(int id);

    Epic getEpic(int id);

    Subtask getSubtask(int id);

    void createTask(Task task);

    void createEpic(Epic epic);

    void createSubtask(Subtask subtask);

    void updateTask(Task task);

    void updateSubtask(Subtask subtask);

    void updateEpic(Epic epic);

    List<Task> getAllTasks();

    List<Task> getAllEpics();

    List<Task> getAllSubtasks();

    void deleteTaskById(int id);

    void deleteSubtaskById(int id);

    void deleteEpicById(int id);

    void deleteAllTasks();

    void deleteAllSubtask();

    void deleteAllEpic();

    List<Subtask> getSubtasksByEpic(Epic epic);

    boolean isTimeOverlap(Task task);

    //Удаление задач
    void removeAll();
}
