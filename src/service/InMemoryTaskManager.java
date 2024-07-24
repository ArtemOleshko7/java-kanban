package service;

import exception.ManagerSaveException;
import main.Status;
import model.Epic;
import model.Subtask;
import model.Task;

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
        if (task == null) {
            throw new IllegalArgumentException("Задача не может быть null");
        }
        task.setId(generateId());
        System.out.println("Добавляем задачу с ID: " + task.getId());

        tasks.put(task.getId(), task);
        System.out.println("Задача добавлена. Текущий размер коллекции: " + tasks.size());
    }

    @Override
    public int createTask(Task task) throws ManagerSaveException {
        if (task == null) {
            throw new IllegalArgumentException("Задача не может быть null.");
        }

        int newId = generateId();
        task.setId(newId);
        tasks.put(newId, task);

        // Логирование
        if (!tasks.containsKey(task.getId())) {
            System.err.println("Ошибка: задача не добавлена в коллекцию!");
        } else {
            System.out.println("Задача успешно добавлена в коллекцию. ID: " + task.getId());
        }

        return task.getId();
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
        // Обновляем статусы всех эпиков, так как задачи могли измениться
        for (Epic epic : epics.values()) {
            updateStatusEpic(epic);
        }
    }

    @Override
    public void addSubtask(Subtask subtask) {
        if (subtask == null) {
            System.out.println("Ошибка: подзадача не может быть null.");
            return; // Уходим из метода, если подзадача null
        }

        int subtaskId = generateId();
        subtask.setId(subtaskId);

        Epic epic = epics.get(subtask.getEpicId());
        if (epic != null) {
            subtasks.put(subtaskId, subtask); // Добавляем подзадачу
            epic.addSubtaskIds(subtaskId, subtask); // Добавляем ID подзадачи в эпик
            updateStatusEpic(epic); // Обновление статуса эпика
            System.out.println("Подзадача добавлена. ID: " + subtaskId);
        } else {
            System.out.println("Ошибка: эпик с ID " + subtask.getEpicId() + " не найден.");
        }
    }

    @Override
    public int createSubtask(Subtask subtask) throws ManagerSaveException {
        if (subtask == null) {
            throw new ManagerSaveException("Подзадача не может быть null.");
        }

        int newId = generateId();
        subtask.setId(newId);
        subtasks.put(newId, subtask);

        return newId;
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
    public void deleteSubtask(int subtaskId) {
        Subtask subtask = subtasks.remove(subtaskId);

        if (subtask != null) {
            Epic epic = epics.get(subtask.getEpicId());
            if (epic != null) {
                epic.getSubtaskIds().remove(Integer.valueOf(subtaskId));
                updateStatusEpic(epic); // Обновление статуса эпика
            } else {
                System.out.println("Ошибка: эпик с ID " + subtask.getEpicId() + " не найден.");
            }
        } else {
            System.out.println("Ошибка: подзадача с ID " + subtaskId + " не найдена.");
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
    public void addEpic(Epic epic) throws ManagerSaveException {
        if (epic == null) {
            throw new ManagerSaveException("Эпик не может быть null.");
        }

        int epicId = generateId();
        epic.setId(epicId);
        epics.put(epicId, epic); // Добавляем эпик
    }

    @Override
    public int createEpic(Epic epic) throws ManagerSaveException {
        if (epic == null) {
            throw new ManagerSaveException("Эпик не может быть null.");
        }

        int newId = generateId();
        epic.setId(newId);
        epics.put(newId, epic);

        return newId;
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
        // Прежде чем удалить эпики, обновляем статусы, если это необходимо
        for (Epic epic : epics.values()) {
            updateStatusEpic(epic);
        }
        // Очищаем коллекции эпиков и подзадач
        subtasks.clear();
        epics.clear();
    }

    public void updateStatusEpic(Epic epic) {
        List<Integer> subtaskIds = epic.getSubtaskIds();

        if (subtaskIds.isEmpty()) {
            epic.setStatus(Status.NEW);
            return;
        }

        int countDone = 0;
        int countInProgress = 0;
        int countNew = 0;

        for (int subtaskId : subtaskIds) {
            Subtask subtask = subtasks.get(subtaskId);
            Status subtaskStatus = subtask.getStatus();

            switch (subtaskStatus) {
                case DONE:
                    countDone++;
                    break;
                case IN_PROGRESS:
                    countInProgress++;
                    break;
                case NEW:
                    countNew++;
                    break;
            }
        }

        if (countDone == subtaskIds.size()) {
            epic.setStatus(Status.DONE);
        } else if (countNew == subtaskIds.size()) {
            epic.setStatus(Status.NEW);
        } else {
            epic.setStatus(Status.IN_PROGRESS);
        }
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

}