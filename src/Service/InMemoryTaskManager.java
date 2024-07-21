package Service;

import exception.ManagerSaveException;
import Model.Epic;
import Model.Subtask;
import Model.Task;
import main.Status;

import java.util.*;

public class InMemoryTaskManager implements TaskManager {
    protected final Map<Integer, Task> tasks;
    protected final Map<Integer, Subtask> subtasks;
    protected final Map<Integer, Epic> epics;
    protected HistoryManager historyManager;
    int idCounter = 0;

    public InMemoryTaskManager(HistoryManager historyManager) {
        this.tasks = new HashMap<>();
        this.subtasks = new HashMap<>();
        this.epics = new HashMap<>();
        this.historyManager = historyManager != null ? historyManager : Managers.getDefaultHistory();
    }

    public InMemoryTaskManager() {
        this(Managers.getDefaultHistory());  // Вызов другого конструктора с null
    }

    public int generateId() {
        return idCounter++;
    }

    @Override
    public void addTask(Task task) {
        task.setId(generateId());
        tasks.put(task.getId(), task);
    }

    @Override
    public int createTask(Task task) throws ManagerSaveException {
        idCounter++;
        task.setId(idCounter);
        tasks.put(task.getId(), task);
        return idCounter;
    }

    @Override
    public void updateTask(int id, Task task) {
        if (tasks.containsKey(task.getId())) {
            tasks.put(task.getId(), task);
        } else {
            System.out.println("Задача не найдена");
        }
    }

    @Override
    public Task getTask(int id) {
        Task task = tasks.get(id);
        if (task != null) {
            historyManager.add(task);
        }
        return task;
    }

    @Override
    public List<Task> getAllTasks() {
        if (tasks.isEmpty()) {
            System.out.println("Задач нет");
            return Collections.emptyList();
        }
        return new ArrayList<>(tasks.values());
    }

    @Override
    public void deleteTask(int idCounter) {
        if (tasks.containsKey(idCounter)) {
            tasks.remove(idCounter);
        } else {
            System.out.println("Задача не найдена");
        }
    }

    @Override
    public void deleteAllTask() {
        tasks.clear();
    }

    @Override
    public void addSubtask(Subtask subtask) {
        if (subtask == null) {
            throw new IllegalArgumentException("Подзадача должна быть связана с эпиком.");
        }

        int subtaskId = generateId();
        subtask.setId(subtaskId);

        Epic epic = epics.get(subtask.getEpicId());
        if (epic != null) {
            subtasks.put(subtaskId, subtask);
            epic.addSubtaskIds(subtaskId, subtask);
            updateStatusEpic(epic);
        } else {
            throw new NoSuchElementException("Такого эпика нет");
        }
    }

    @Override
    public int createSubtask(Subtask subtask) throws ManagerSaveException {
        if (subtask == null) {
            throw new ManagerSaveException("Подзадача не может быть null.");
        }
        idCounter++;
        subtask.setId(idCounter);
        subtasks.put(subtask.getId(), subtask);
        return idCounter;
    }

    @Override
    public void updateSubtask(int id, Subtask subtask) throws ManagerSaveException {
        if (subtask == null) {
            throw new ManagerSaveException("Подзадача не может быть null.");
        }
        if (subtasks.containsKey(id)) {
            subtask.setId(id); // Устанавливаем ID перед обновлением
            subtasks.put(id, subtask);

            Epic epic = epics.get(subtask.getEpicId());
            updateStatusEpic(epic);

        } else {
            System.out.println("Подзадачи с ID " + id + " не существует.");
        }
    }

    public Subtask getSubtask(int id) {
        Subtask subtask = subtasks.get(id);
        if (subtask != null) {
            historyManager.add(subtask);
        }
        return subtask;
    }

    @Override
    public List<Subtask> getAllSubtasks() {
        if (subtasks.isEmpty()) {
            System.out.println("Подзадач нет");
            return Collections.emptyList();
        }
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public List<Subtask> getAllSubtasksInEpic(int idCounter) {
        if (epics.containsKey(idCounter)) {
            List<Subtask> subtasksNew = new ArrayList<>();
            Epic epic = epics.get(idCounter);
            for (int i = 0; i < epic.getSubtaskIds().size(); i++) {
                subtasksNew.add(subtasks.get(epic.getSubtaskIds().get(i)));
            }
            return subtasksNew;
        } else {
            return Collections.emptyList();
        }
    }

    @Override
    public void deleteSubtask(int idCounter) {
        Subtask subtask = subtasks.get(idCounter);
        if (subtask != null) {
            Epic epic = epics.get(subtask.getEpicId());
            epic.getSubtaskIds().remove((Integer) subtask.getId());
            updateStatusEpic(epic);
            subtasks.remove(idCounter);
        } else {
            System.out.println("Такой подзадачи нет");
        }
    }

    @Override
    public void deleteAllSubtasks() {
        subtasks.clear();
        for (Epic epic : epics.values()) {
            epic.getSubtaskIds().clear();
            updateStatusEpic(epic);
        }
    }

    @Override
    public void addEpic(Epic epic) {
        int epicId = generateId();
        epic.setId(epicId);
        epics.put(epicId, epic);
    }

    @Override
    public int createEpic(Epic epic) throws ManagerSaveException {
        if (epic == null) {
            throw new ManagerSaveException("Эпик не может быть null.");
        }
        idCounter++;
        epic.setId(idCounter);
        epics.put(epic.getId(), epic);
        return idCounter;
    }

    @Override
    public void updateEpic(int id, Epic epic) throws ManagerSaveException {
        if (epic == null) {
            throw new ManagerSaveException("Эпик не может быть null.");
        }
        if (epics.containsKey(id)) {
            epic.setId(id); // Устанавливаем ID перед обновлением
            epics.put(id, epic);

            updateStatusEpic(epic); // Обновляем статус эпика

        } else {
            throw new NoSuchElementException("Эпика с ID " + id + " не существует.");
        }
    }

    @Override
    public Epic getEpic(int id) {
        Epic epic = epics.get(id);
        if (epic != null) {
            historyManager.add(epic);
        }
        return epic;
    }

    @Override
    public List<Epic> getAllEpics() {
        if (epics.isEmpty()) {
            System.out.println("Эпиков нет");
            return Collections.emptyList();
        }
        return new ArrayList<>(epics.values());
    }

    @Override
    public void deleteEpic(int idCounter) {
        Epic epic = epics.get(idCounter);
        if (epic != null) {
            for (Integer subtaskID : epic.getSubtaskIds()) {
                subtasks.remove(subtaskID);
            }
            epics.remove(idCounter);
        } else {
            System.out.println("Эпика нет");
        }
    }

    @Override
    public void deleteAllEpics() {
        subtasks.clear();
        epics.clear();
    }

    private void updateStatusEpic(Epic epic) {
        if (epics.containsKey(epic.getId())) {
            if (epic.getSubtaskIds().isEmpty()) {
                epic.setStatus(Status.NEW);
            } else {
                List<Subtask> subtasksNew = new ArrayList<>();
                int countDone = 0;
                int countNew = 0;

                for (int i = 0; i < epic.getSubtaskIds().size(); i++) {
                    subtasksNew.add(subtasks.get(epic.getSubtaskIds().get(i)));
                }
                for (Subtask subtask : subtasksNew) {
                    if (subtask.getStatus() == Status.DONE) {
                        countDone++;
                    }
                    if (subtask.getStatus() == Status.NEW) {
                        countNew++;
                    }
                    if (subtask.getStatus() == Status.IN_PROGRESS) {
                        epic.setStatus(Status.IN_PROGRESS);
                        return;
                    }
                }
                if (countDone == epic.getSubtaskIds().size()) {
                    epic.setStatus(Status.DONE);
                } else if (countNew == epic.getSubtaskIds().size()) {
                    epic.setStatus(Status.NEW);
                } else {
                    epic.setStatus(Status.IN_PROGRESS);
                }
            }
        } else {
            System.out.println("Эпик не найден");
        }
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

}