package manager;

import model.Epic;
import model.Subtask;
import model.Task;

import java.util.List;


public interface TaskManager {
    //Добавление новых задач
    void addTask(Task task);

    void addEpic(Epic epic);

    void addSubtask(Subtask subtask);

    //Обновление существующих задач
    void updateTask(Task task);

    void updateEpic(Epic epic);

    void updateSubtask(Subtask subtask);

    //Получение списков задач
    List<Task> getAllTasks();

    List<Task> getTasks();

    List<Epic> getEpics();

    List<Subtask> getSubtasks();

    //Удаление задач
    void removeAll();

    void removeAllTasks();

    void removeAllSubtasks();

    void removeAllEpics();

    //Получение по идентификатору
    Task getTaskById(int taskId);

    Subtask getSubtaskById(int subtaskId);

    Epic getEpicById(int epicId);

    //Удаление по идентификатору
    void removeTask(int id);

    void removeSubtask(int id);

    void removeEpic(int id);

    //Получение списка всех подзадач определённого эпика
    List<Subtask> getSubtasksOfEpic(int id);

}