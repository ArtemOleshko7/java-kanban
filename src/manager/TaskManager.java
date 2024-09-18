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

    boolean isTimeOverlap(Task task);

    //Получение задач
    Task getTaskById(int taskId);

    Subtask getSubtaskById(int subtaskId);

    Epic getEpicById(int epicId);

    List<Task> getAllTasks();

    List<Task> getTasks();

    List<Epic> getEpics();

    List<Subtask> getSubtasks();

    List<Task> getPrioritizedTasks();

    //Удаление задач
    void removeAll();

    void removeAllTasks();

    void removeAllSubtasks();

    void removeAllEpics();

    void removeTask(int id);

    void removeSubtask(int id);

    void removeEpic(int id);

    List<Subtask> getSubtasksOfEpic(int id);

    public void updateEpicStatus(int epicId);
}