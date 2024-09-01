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
    HistoryManager historyManager = Managers.getDefaultHistory();
    private static int id = 0;

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

    private <T extends Task> T getTaskById(Map<Integer, T> map, int id) {
        T task = map.get(id);
        if (task != null) {
            historyManager.add(task);
        }
        return task;
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

    //Получение списков задач
    @Override
    public List<Task> getAllTasks() {
        List<Task> allTasks = new ArrayList<>();
        allTasks.addAll(tasks.values());
        allTasks.addAll(epics.values());
        allTasks.addAll(subtasks.values());
        return allTasks;
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
        List<Integer> subtaskIds = epic.getSubtaskOfEpicIDs();

        if (subtaskIds.isEmpty()) {
            return Collections.emptyList(); // Возвращаем пустой список, если подзадач нет
        }

        List<Subtask> subtasksList = new ArrayList<>();
        for (Integer id : subtaskIds) {
            if (subtasks.containsKey(id)) {
                subtasksList.add(subtasks.get(id));
            }
        }
        return subtasksList;
    }

    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(prioritizedTasks);
    }

    // Проверка на пересечение времени
    public boolean isTimeOverlap(Task newTask) {
        return prioritizedTasks.stream()
                .filter(task -> task.getId() != newTask.getId())  // Исключаем саму новую задачу
                .filter(task -> task.getStartTime() != null && newTask.getStartTime() != null) // Проверка на null
                .anyMatch(task -> task.getStartTime().isBefore(newTask.getEndTime()) &&
                        newTask.getStartTime().isBefore(task.getEndTime())); // Проверка пересечения
    }

    @Override
    public void createTask(Task task) {
        // Проверка, что задача не null
        if (task == null) {
            throw new IllegalArgumentException("Task cannot be null");
        }

        // Проверка на пересечение времени
        if (isTimeOverlap(task)) {
            throw new IllegalArgumentException("Task time overlaps with an existing task");
        }

        // Генерация уникального идентификатора
        int newId = generatedId();  // Увеличиваем ID для новой задачи
        task.setId(newId);

        // Добавление задачи в коллекции
        tasks.put(task.getId(), task);
        prioritizedTasks.add(task);

        // Добавление задачи в менеджер истории
        historyManager.add(task);
    }

    @Override
    public void createSubtask(Subtask subtask) {
        // Проверка на null
        if (subtask == null) {
            throw new IllegalArgumentException("Subtask cannot be null");
        }

        // Проверка, что продолжительность не null
        if (subtask.getDuration() == null) {
            throw new IllegalArgumentException("Subtask duration cannot be null");
        }

        // Проверка на пересечение времени
        if (isTimeOverlap(subtask)) {
            throw new IllegalArgumentException("Subtask time overlaps with an existing task");
        }

        // Получение эпика
        Epic epic = epics.get(subtask.getEpic().getId());
        if (epic == null) {
            throw new IllegalArgumentException("Epic not found for the given subtask");
        }

        // Генерация уникального идентификатора
        int newId = generatedId();
        subtask.setId(newId);

        // Добавление подзадачи в коллекции
        subtasks.put(newId, subtask);
        epic.addSubtask(subtask);

        // Обновление времени эпика
        updateEpicTime(epic.getId());

        // Добавление подзадачи в prioritizedTasks
        prioritizedTasks.add(subtask);

        // Проверка и добавление подзадачи в менеджер истории
        historyManager.add(subtask);
        epic.calculateStatus();
    }

    @Override
    public void createEpic(Epic epic) {
        // Проверка на null
        if (epic == null) {
            throw new IllegalArgumentException("Epic cannot be null");
        }

        // Генерация уникального идентификатора
        int newId = generatedId();
        epic.setId(newId);

        // Добавление эпика в коллекцию
        epics.put(newId, epic);
    }

    @Override
    public void updateTask(Task task) {
        if (task == null) {
            throw new IllegalArgumentException("Task cannot be null");
        }

        Task existingTask = tasks.get(task.getId());
        if (existingTask != null) {
            // Проверка на пересечение времени с другими задачами
            if (isTimeOverlap(task)) {
                throw new IllegalArgumentException("Task time overlaps with an existing task");
            }

            // Обновление задачи
            prioritizedTasks.remove(existingTask);
            tasks.put(task.getId(), task);
            prioritizedTasks.add(task);
        } else {
            throw new IllegalArgumentException("Task with given ID does not exist");
        }
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        // Проверка, что подзадача не null
        if (subtask == null) {
            throw new IllegalArgumentException("Subtask cannot be null");
        }

        // Обновление подзадачи, если она существует
        Subtask savedSubtask = subtasks.get(subtask.getId());
        if (savedSubtask != null) {
            // Проверка на пересечение времени
            if (isTimeOverlap(subtask)) {
                throw new IllegalArgumentException("Subtask time overlaps with an existing task");
            }

            // Получение эпика и проверка его существования
            Epic epic = savedSubtask.getEpic();
            if (epic == null || !epics.containsKey(epic.getId())) {
                throw new IllegalArgumentException("Epic not found for the given subtask");
            }

            // Удаляем старую подзадачу из эпика, добавляем обновленную
            epic.getSubtaskOfEpicIDs().remove(savedSubtask);
            epic.addSubtask(subtask);
            epic.calculateStatus(); // Обновляем статус эпика

            // Сохраняем обновленные данные
            subtasks.put(subtask.getId(), subtask);

        } else {
            // Добавляем новую подзадачу, если её не существует
            subtasks.put(subtask.getId(), subtask);
            // Также необходимо добавить её в соответствующий эпик
            Epic epic = subtask.getEpic();
            if (epic != null && epics.containsKey(epic.getId())) {
                epic.addSubtask(subtask);
                epic.calculateStatus(); // Обновляем статус эпика
            }
        }

        // Добавление подзадачи в prioritizedTasks
        prioritizedTasks.add(subtask);
    }

    @Override
    public void updateEpic(Epic epic) {
        // Проверка, что эпик не null
        if (epic == null) {
            throw new IllegalArgumentException("Epic cannot be null");
        }

        // Обновление эпика, если он существует
        Epic savedEpic = epics.get(epic.getId());
        if (savedEpic != null) {
            // Обновляем данные эпика
            savedEpic.setNameTask(epic.getNameTask());
            savedEpic.setDescriptionTask(epic.getDescriptionTask());

            // Пересчитываем статус эпика
            savedEpic.calculateStatus();

            // Сохраняем обновленные данные
            epics.put(savedEpic.getId(), savedEpic);
        } else {
            // Если эпика не существует, добавляем новый
            epics.put(epic.getId(), epic);
        }
    }



    public void deleteTaskById(int id) {
        System.out.println("Attempting to delete Task with ID: " + id);
        if (tasks.containsKey(id)) {
            final Task task = tasks.remove(id);
            System.out.println("Deleted Task ID: " + id + " from tasks.");
            prioritizedTasks.remove(task);
            System.out.println("Removed Task ID: " + id + " from prioritizedTasks.");
            historyManager.remove(id);
            System.out.println("Removed Task ID: " + id + " from historyManager.");
        } else {
            System.out.println("Task with ID " + id + " does not exist.");
        }

        // Отладочный вывод состояния коллекций
        System.out.println("Current Tasks after deletion: " + tasks.keySet());
        System.out.println("Current Prioritized Tasks after deletion: " + prioritizedTasks);
        System.out.println("Current History after deletion: " + historyManager.getHistory()); // предположим, что есть метод getHistory()
    }

    @Override
    public void deleteSubtaskById(int id) {
        Subtask subtask = subtasks.remove(id);
        if (subtask != null) {
            prioritizedTasks.remove(subtask);
            historyManager.remove(id);

            Epic epic = subtask.getEpic();
            if (epic != null) {
                epic.removeSubtask(subtask);
                updateEpicTime(epic.getId());
                epic.calculateStatus();
            }
            System.out.println("Subtask with ID " + id + " has been removed from subtasks.");
        } else {
            System.out.println("Subtask with ID " + id + " does not exist.");
        }
    }

    @Override
    public void deleteEpicById(int id) {
        if (epics.containsKey(id)) {
            Epic epic = epics.remove(id);
            if (epic != null) {
                List<Subtask> subtasksToRemove = getSubtasksByEpic(epic); // Получаем подзадачи эпика
                for (Subtask subtask : subtasksToRemove) {
                    historyManager.remove(subtask.getId());
                    subtasks.remove(subtask.getId()); // Удаляем подзадачу из Map
                    System.out.println("Subtask with ID " + subtask.getId() + " removed from history and subtasks.");
                }
                System.out.println("Epic with ID " + id + " has been removed from epics.");
            }
        } else {
            System.out.println("Epic with ID " + id + " does not exist.");
        }
    }


    @Override
    public void deleteAllTasks() {
        tasks.values().forEach(task -> {
            historyManager.remove(task.getId());
            prioritizedTasks.remove(task);
        });
        tasks.clear();
        System.out.println("All tasks have been removed.");
    }

    @Override
    public void deleteAllSubtask() {
        for (Subtask subtask : new ArrayList<>(subtasks.values())) {
            Epic epic = subtask.getEpic();
            if (epic != null) {
                epic.removeSubtask(subtask);
                epic.calculateStatus();
                // Обновление времени эпика после удаления подзадачи
                updateEpicTime(epic.getId());
            }
            prioritizedTasks.remove(subtask);
            historyManager.remove(subtask.getId());
        }
        subtasks.clear();
        System.out.println("All subtasks have been removed.");
    }

    @Override
    public void deleteAllEpic() {
        for (Epic epic : new ArrayList<>(epics.values())) {
            if (historyManager.getHistory().contains(epic)) {
                historyManager.remove(epic.getId());
            }
            prioritizedTasks.remove(epic); // Удаляем эпик из приоритетных задач

            List<Subtask> subtasksToRemove = getSubtasksByEpic(epic); // Получаем подзадачи эпика
            for (Subtask subtask : subtasksToRemove) {
                prioritizedTasks.remove(subtask); // Убираем подзадачу из списка приоритетных задач
                historyManager.remove(subtask.getId());
            }
        }
        epics.clear();
        subtasks.clear();
        System.out.println("All epics and their subtasks have been removed.");
    }

    //Удаление задач
    @Override
    public void removeAll() {
        historyManager.clearHistory();
        prioritizedTasks.clear();
        tasks.clear();
        subtasks.clear();
        epics.clear();
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
            epic.setDuration(Duration.ZERO); // Устанавливаем нулевую длительность
        } else {
            for (Subtask subtask : subtasks) {
                Duration duration = subtask.getDuration();

                if (duration != null) {
                    totalDuration = totalDuration.plus(duration);

                    if (startTime == null || subtask.getStartTime() != null && subtask.getStartTime().isBefore(startTime)) {
                        startTime = subtask.getStartTime();
                    }
                    if (endTime == null || subtask.getEndTime() != null && subtask.getEndTime().isAfter(endTime)) {
                        endTime = subtask.getEndTime();
                    }
                } else {
                    System.out.println("Subtask " + subtask.getId() + " has null duration");
                }
            }
        }

        epic.setDuration(totalDuration);
        epic.setStartTime(startTime);
        epic.setEndTime(endTime);
    }


}
