package service;

import exception.ManagerSaveException;
import main.Status;
import model.Epic;
import model.Subtask;
import model.Task;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;


public class FileBackedTaskManager extends InMemoryTaskManager {

    private final File taskManagerFile;

    public FileBackedTaskManager(HistoryManager historyManager, String fileName) {
        super(historyManager);
        this.taskManagerFile = new File(fileName);
    }

    public FileBackedTaskManager(String fileName) {
        this(new InMemoryHistoryManager(), fileName);
    }

    private static final int ID_INDEX = 1; // Начинаем с 1
    private static final int TYPE_INDEX = 2;
    private static final int NAME_INDEX = 3;
    private static final int DESCRIPTION_INDEX = 4;
    private static final int STATUS_INDEX = 5;
    private static final int SUBTASK_ID_INDEX = 6; // Для SUB_TASK и EPIC_TASK

    public static Task taskFromString(String line, InMemoryTaskManager taskManager) {
        System.out.println("Parsing line: " + line);
        String[] parts = line.split(",");

        if (parts.length < 6) {
            throw new IllegalArgumentException("Недостаточно параметров в строке: " + line);
        }

        int id = Integer.parseInt(parts[ID_INDEX]);
        String type = parts[TYPE_INDEX].trim();
        String name = parts[NAME_INDEX];
        String description = parts[DESCRIPTION_INDEX];
        Status status;

        try {
            status = Status.valueOf(parts[STATUS_INDEX]);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Некорректный статус: " + parts[STATUS_INDEX]);
        }

        switch (type) {
            case "EPIC_TASK":
                Epic epic = new Epic(id, name, description, status);
                for (int i = SUBTASK_ID_INDEX; i < parts.length; i++) {
                    int subtaskId = Integer.parseInt(parts[i]);
                    Subtask subtask = taskManager.getSubtask(subtaskId);
                    if (subtask != null) {
                        epic.addSubtaskIds(subtaskId, subtask);
                    } else {
                        throw new IllegalArgumentException("Подзадача с ID " + subtaskId + " не найдена.");
                    }
                }
                return epic;

            case "SUB_TASK":
                if (parts.length != 7) { // Изменено на 7 для Subtask
                    throw new IllegalArgumentException("Неверное количество параметров для Subtask");
                }
                return new Subtask(id, name, description, status, Integer.parseInt(parts[SUBTASK_ID_INDEX]));

            case "TASK": // Добавлено для обработки обычной задачи
            default: // Обработка по умолчанию для других типов
                return new Task(id, name, description, status);
        }
    }


    public void save() throws ManagerSaveException {
        try (Writer writer = new FileWriter(taskManagerFile, false)) {
            for (Task task : tasks.values()) {
                writer.write(toString(task) + "\n");
            }
            for (Epic epic : epics.values()) {
                writer.write(toString(epic) + "\n");
            }
            for (Subtask subtask : subtasks.values()) {
                writer.write(toString(subtask) + "\n");
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Error saving to file: " + taskManagerFile + ". " + e.getMessage());
        }
    }

    public void loadFromFile() throws ManagerSaveException {
        load();
    }

    private void updateIdAfterLoad(Map<Integer, Task> tasks, Map<Integer, Subtask> subtasks, Map<Integer, Epic> epics) {
        // Собираем все ключи (ID) из трех карт
        List<Integer> allIds = new ArrayList<>();
        allIds.addAll(tasks.keySet());
        allIds.addAll(subtasks.keySet());
        allIds.addAll(epics.keySet());

        // Получаем максимальный ID
        int maxId = allIds.stream()
                .reduce(0, Integer::max);

        // Устанавливаем idCounter на следующий доступный ID
        idCounter = maxId + 1; // Следующий ID будет на 1 больше максимального
    }

    public void load() throws ManagerSaveException {
        tasks.clear();
        subtasks.clear();
        epics.clear();
        historyManager.removeAll();

        List<String> lines = readLinesFromFile();
        System.out.println("Загруженные строки: " + lines);

        Set<Integer> taskIds = new HashSet<>();
        Set<Integer> epicIds = new HashSet<>();

        for (String line : lines) {
            Task task = taskFromString(line, this); // Передаем текущий менеджер задач

            if (task instanceof Epic) {
                Epic epic = (Epic) task;
                if (epicIds.contains(epic.getId())) {
                    throw new ManagerSaveException("Эпик с ID " + epic.getId() + " уже существует.");
                }
                epicIds.add(epic.getId());
                epics.put(epic.getId(), epic); // Добавляем в список эпиков
            } else {
                if (taskIds.contains(task.getId())) {
                    throw new ManagerSaveException("Задача с ID " + task.getId() + " уже существует.");
                }
                taskIds.add(task.getId());
                tasks.put(task.getId(), task); // Добавляем в список задач

                if (task instanceof Subtask) {
                    Subtask subtask = (Subtask) task;
                    if (!epicIds.contains(subtask.getEpicId())) {
                        throw new ManagerSaveException("Подзадача с ID " + subtask.getId() +
                                " не принадлежит существующему эпике.");
                    }
                    subtasks.put(subtask.getId(), subtask); // Добавляем в список подзадач
                    epics.get(subtask.getEpicId()).addSubtaskIds(subtask.getId(), subtask); // Добавляем подзадачу в эпик
                }
            }
        }

        // Обновляем статусы всех эпиков после загрузки
        for (Epic epic : epics.values()) {
            updateStatusEpic(epic);
        }
    }

    private List<String> readLinesFromFile() throws ManagerSaveException {
        List<String> lines = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(taskManagerFile, StandardCharsets.UTF_8))) {
            String line;
            while ((line = br.readLine()) != null) {
                lines.add(line);
            }
        } catch (IOException e) {
            throw new ManagerSaveException(e.getMessage());
        }
        return lines;
    }


    private String toString(Task task) {
        return String.format("%d,%s,%s,%s,%s",
                task.getId(),
                task.getType(),
                task.getName(),
                task.getStatus().name(),  // Преобразование статуса в строку
                task.getDescription());
    }

    private String toString(Subtask task) {
        return String.format("%d,%s,%s,%s,%s,%d",
                task.getId(),
                task.getType(),
                task.getName(),
                task.getStatus().name(),  // Преобразование статуса в строку
                task.getDescription(),
                task.getEpicId());
    }

    private String toString(Epic task) {
        return String.format("%d,%s,%s,%s,%s,%s",
                task.getId(),
                task.getType(),
                task.getName(),
                task.getStatus().name(),  // Преобразование статуса в строку
                task.getDescription(),
                task.getSubtaskIds().toString()); // Добавляем подзадачи
    }

    @Override
    public void addTask(Task task) {
        if (task != null) {
            task.setId(generateId());
            System.out.println("Добавляем задачу с ID: " + task.getId());

            // Используем setTask для добавления задачи
            setTask(task);

            System.out.println("Задача добавлена. Текущий размер коллекции: " + tasks.size());

            try {
                save();
            } catch (ManagerSaveException e) {
                System.out.println("Ошибка при сохранении данных: " + e.getMessage());
            }
        } else {
            System.out.println("Ошибка: задача не может быть null.");
        }
    }

    @Override
    public void addSubtask(Subtask subtask) {
        if (subtask != null) {
            subtask.setId(generateId()); // Генерация ID для подзадачи
            System.out.println("Добавляем подзадачу с ID: " + subtask.getId());

            // Используем setTask для добавления подзадачи
            setTask(subtask);

            System.out.println("Подзадача добавлена. Текущий размер коллекции подзадач: " + subtasks.size());

            try {
                save(); // Сохранение после добавления подзадачи
            } catch (ManagerSaveException e) {
                System.out.println("Ошибка при сохранении данных: " + e.getMessage());
            }
        } else {
            System.out.println("Ошибка: подзадача не может быть null.");
        }
    }

    @Override
    public void addEpic(Epic epic) {
        if (epic != null) {
            epic.setId(generateId()); // Генерация ID для эпика
            System.out.println("Добавляем эпик с ID: " + epic.getId());

            // Используем setTask для добавления эпика
            setTask(epic);

            System.out.println("Эпик добавлен. Текущий размер коллекции эпиков: " + epics.size());

            try {
                save(); // Сохранение после добавления эпика
            } catch (ManagerSaveException e) {
                System.out.println("Ошибка при сохранении данных: " + e.getMessage());
            }
        } else {
            System.out.println("Ошибка: эпик не может быть null.");
        }
    }


    @Override
    public int createTask(Task task) throws ManagerSaveException {
        if (task == null) {
            throw new IllegalArgumentException("Ошибка: задача не может быть null.");
        }
        int id = super.createTask(task);
        save();
        return id;
    }

    @Override
    public int createSubtask(Subtask subTask) throws ManagerSaveException {
        if (subTask == null) {
            throw new IllegalArgumentException("Ошибка: подзадача не может быть null.");
        }
        int id = super.createSubtask(subTask);
        save();
        return id;
    }

    @Override
    public int createEpic(Epic epic) throws ManagerSaveException {
        if (epic == null) {
            throw new IllegalArgumentException("Ошибка: эпик не может быть null.");
        }
        int id = super.createEpic(epic);
        save();
        return id;
    }

    @Override
    public void deleteAllTask() throws ManagerSaveException {
        tasks.clear();
        save();
    }

    @Override
    public Task getTask(int id) throws ManagerSaveException {
        Task result = super.getTask(id);
        if (result == null) {
            throw new NoSuchElementException("Задача с id " + id + " не найдена.");
        }
        return result;
    }

    @Override
    public void updateTask(int id, Task task) throws ManagerSaveException {
        if (task == null) {
            throw new IllegalArgumentException("Ошибка: задача не может быть null.");
        }
        super.updateTask(id, task);
        save();
    }

    @Override
    public void deleteTask(int id) throws ManagerSaveException {
        super.deleteTask(id);
        save();
    }

    @Override
    public void deleteAllSubtasks() throws ManagerSaveException {
        super.deleteAllSubtasks();
        save();
    }

    @Override
    public Subtask getSubtask(int id) {
        Subtask result = super.getSubtask(id);
        if (result == null) {
            throw new NoSuchElementException("Подзадача с id " + id + " не найдена.");
        }
        return result;
    }

    @Override
    public void updateSubtask(int id, Subtask subTask) throws ManagerSaveException {
        if (subTask == null) {
            throw new IllegalArgumentException("Ошибка: подзадача не может быть null.");
        }
        super.updateSubtask(id, subTask);
        save();
    }

    @Override
    public void deleteSubtask(int id) throws ManagerSaveException {
        super.deleteSubtask(id);
        save();
    }

    @Override
    public void deleteAllEpics() throws ManagerSaveException {
        super.deleteAllEpics();
        save();
    }

    @Override
    public Epic getEpic(int id) {
        Epic result = super.getEpic(id);
        if (result == null) {
            throw new NoSuchElementException("Эпик с id " + id + " не найден.");
        }
        return result;
    }

    @Override
    public void updateEpic(int id, Epic epic) throws ManagerSaveException {
        super.updateEpic(id, epic);
        save();
    }

    @Override
    public void deleteEpic(int id) throws ManagerSaveException {
        super.deleteEpic(id);
        save();
    }

    protected void setTask(Task task) {
        tasks.put(task.getId(), task); // Добавляем задачу в общую коллекцию

        if (task instanceof Epic) {
            epics.put(task.getId(), (Epic) task);
        } else if (task instanceof Subtask) {
            Subtask subtask = (Subtask) task;
            subtasks.put(subtask.getId(), subtask);

            Epic epicTask = epics.get(subtask.getEpicId());
            if (epicTask != null) {
                epicTask.addSubtaskIds(subtask.getId(), subtask);
            }
        }
    }
}


