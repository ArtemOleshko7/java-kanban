package manager;

import model.Epic;
import model.Subtask;
import model.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;


public class InMemoryTaskManager implements TaskManager {
    protected Map<Integer, Task> tasks;
    protected Map<Integer, Epic> epics;
    protected Map<Integer, Subtask> subtasks;
    private final Set<Task> prioritizedTasks = new TreeSet<>((task1, task2) -> {
        if (task1.getStartTime() == null && task2.getStartTime() == null) {
            return Integer.compare(task1.getId(), task2.getId());
        }
        if (task1.getStartTime() == null) {
            return 1;
        }
        if (task2.getStartTime() == null) {
            return -1;
        }
        return task1.getStartTime().compareTo(task2.getStartTime());
    });
    protected HistoryManager historyManager = Managers.getDefaultHistory();
    private int id = 0;

    public InMemoryTaskManager() {
        this.tasks = new HashMap<>();
        this.epics = new HashMap<>();
        this.subtasks = new HashMap<>();
    }

    private int generatedId() {
        return ++id;
    }

    public void setIdCounter(int count) {
        id = count;
    }

    public int getIdCounter() {
        return id;
    }

    private <T extends Task> T getTaskById(Map<Integer, T> map, int id) {
        T task = map.get(id);
        if (task != null) {
            historyManager.add(task);
        }
        return task;
    }

    //Проверка на пересечение времени
    public boolean isTimeOverlap(Task newTask) {
        return prioritizedTasks.stream()
                .anyMatch(task -> {
                    if (task.getId() == newTask.getId()) {
                        return false;
                    }
                    if (task.getStartTime() == null || newTask.getStartTime() == null) {
                        return false;
                    }
                    return task.getStartTime().isBefore(newTask.getEndTime()) &&
                            newTask.getStartTime().isBefore(task.getEndTime());
                });
    }

    @Override
    public void createTask(Task task) {
        // Проверка на пересечение времени
        if (isTimeOverlap(task)) {
            throw new IllegalArgumentException("Task time overlaps with an existing task");
        }

        // Генерация уникального идентификатора
        int newId = generatedId();
        task.setId(newId);

        // Добавление задачи в коллекции
        tasks.put(task.getId(), task);
        prioritizedTasks.add(task);

    }

    @Override
    public void createSubtask(Subtask subtask) {
        // Проверка на пересечение времени
        if (isTimeOverlap(subtask)) {
            throw new IllegalArgumentException("Subtask time overlaps with an existing task");
        }

        // Получение эпика
        Epic epic = epics.get(subtask.getEpic().getId());
        if (epic == null) {
            throw new IllegalArgumentException("Epic not found for the given subtask");
        }

        // Установка уникального идентификатора
        subtask.setId(generatedId());

        // Добавление подзадачи в эпик и обновление статуса
        epic.addSubtask(subtask);
        epic.calculateStatus();

        // Сохранение подзадачи
        subtasks.put(subtask.getId(), subtask);

    }

    @Override
    public void createEpic(Epic epic) {
        // Проверка, что эпик не null
        if (epic == null) {
            throw new IllegalArgumentException("Epic cannot be null");
        }

        // Генерация уникального идентификатора
        int newId = generatedId();
        epic.setId(newId);

        // Добавление эпика в коллекцию
        epics.put(epic.getId(), epic);

    }

    @Override
    public void updateTask(Task task) {
        final Task savedTask = tasks.get(task.getId());
        if (savedTask == null) {
            return;
        }
        if (isTimeOverlap(task)) {
            throw new IllegalArgumentException("Task time overlaps with an existing task");
        }
        tasks.put(task.getId(), task);
        prioritizedTasks.remove(savedTask);
        prioritizedTasks.add(task);
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        final Subtask savedSubtask = subtasks.get(subtask.getId());
        if (savedSubtask == null) {
            return; // Подзадача не найдена, выходим
        }

        // Проверка на пересечение времени
        if (isTimeOverlap(subtask)) {
            throw new IllegalArgumentException("Subtask time overlaps with an existing task");
        }

        // Обновляем подзадачу в списке подзадач эпика
        Epic epic = savedSubtask.getEpic();
        Epic savedEpic = epics.get(epic.getId());
        if (savedEpic == null) {
            throw new IllegalArgumentException("Epic not found for the given subtask");
        }

        // Удаляем старую подзадачу и добавляем обновленную
        savedEpic.getSubTasks().remove(savedSubtask);
        savedEpic.addSubtask(subtask);
        savedEpic.calculateStatus(); // Обновляем статус эпика

        // Сохраняем обновленные данные
        subtasks.put(subtask.getId(), subtask);
    }

    @Override
    public void updateEpic(Epic epic) {
        final Epic savedEpic = epics.get(epic.getId());
        if (savedEpic == null) {
            return; // Эпик не найден, выходим
        }

        // Обновляем данные эпика
        savedEpic.setNameTask(epic.getNameTask());
        savedEpic.setDescriptionTask(epic.getDescriptionTask());

        // Пересчитываем статус эпика, если необходимо
        savedEpic.calculateStatus();

        // Сохраняем обновленные данные
        epics.put(savedEpic.getId(), savedEpic);
    }

    @Override
    public Task getTask(int id) {
        return getTaskById(tasks, id);
    }

    @Override
    public Epic getEpic(int id) {
        return getTaskById(epics, id);
    }

    @Override
    public Subtask getSubtask(int id) {
        return getTaskById(subtasks, id);
    }

    @Override
    public List<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public List<Task> getAllSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public List<Task> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public List<Subtask> getSubtasksByEpic(Epic epic) {
        return epic.getSubTasks();
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    @Override
    public void deleteTaskById(int id) {
        if (historyManager.getHistory().contains(tasks.get(id))) {
            historyManager.remove(id);
        }
        tasks.remove(id);
    }

    @Override
    public void deleteSubtaskById(int id) {
        if (historyManager.getHistory().contains(subtasks.get(id))) {
            historyManager.remove(id);
        }
        Subtask removeSubtask = getSubtask(id);
        Epic epic = removeSubtask.getEpic();
        Epic epicSaved = epics.get(epic.getId());
        epicSaved.removeSubtask(removeSubtask); // Обновленный вызов метода
        subtasks.remove(id);
    }

    @Override
    public void deleteEpicById(int id) {
        if (historyManager.getHistory().contains(epics.get(id))) {
            historyManager.remove(id);
        }
        Epic epic = getEpic(id);
        List<Subtask> subTasks = epic.getSubTasks();
        for (Subtask element : subTasks) {
            if (historyManager.getHistory().contains(element)) {
                historyManager.remove(element.getId());
            }
            subtasks.remove(element.getId());
        }
        epics.remove(id);
    }

    @Override
    public void deleteAllTasks() {
        for (Task task : tasks.values()) {
            if (historyManager.getHistory().contains(task)) {
                historyManager.remove(task.getId());
            }
        }
        tasks.clear();
    }

    @Override
    public void deleteAllSubtask() {
        for (Subtask subtasks : subtasks.values()) {
            Epic epic = subtasks.getEpic();
            Epic epicSaved = epics.get(epic.getId());
            epicSaved.removeSubtask(subtasks); // Обновленный вызов метода
            epicSaved.calculateStatus(); // Вызов для обновления статуса эпика
            epics.put(epicSaved.getId(), epicSaved);
            if (historyManager.getHistory().contains(subtasks)) {
                historyManager.remove(subtasks.getId());
            }
        }
        subtasks.clear();
    }

    @Override
    public void deleteAllEpic() {
        for (Epic epic : epics.values()) {
            if (historyManager.getHistory().contains(epic)) {
                historyManager.remove(epic.getId());
            }
        }
        for (Subtask subtasks : subtasks.values()) {
            if (historyManager.getHistory().contains(subtasks)) {
                historyManager.remove(subtasks.getId());
            }
        }
        epics.clear();
        subtasks.clear();
    }

    private void updateEpicTime(int id) {
        Epic epic = epics.get(id);

        if (epic == null) {
            throw new IllegalArgumentException("Epic with ID " + id + " does not exist");
        }

        Duration totalDuration = Duration.ZERO;
        LocalDateTime startTime = null;
        LocalDateTime endTime = null;

        List<Subtask> subtasks = getSubtasksByEpic(epic);

        if (subtasks.isEmpty()) {
            epic.setStartTime(null);
            epic.setEndTime(null);
        } else {
            for (Subtask subtask : subtasks) {
                totalDuration = totalDuration.plus(subtask.getDuration());
                if (startTime == null || subtask.getStartTime().isBefore(startTime)) {
                    startTime = subtask.getStartTime();
                }
                if (endTime == null || subtask.getEndTime().isAfter(endTime)) {
                    endTime = subtask.getEndTime();
                }
            }
        }

        epic.setDuration(totalDuration);
        epic.setStartTime(startTime);
        epic.setEndTime(endTime);
    }


}
