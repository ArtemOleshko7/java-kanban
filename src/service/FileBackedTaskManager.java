package service;

import exception.ManagerSaveException;
import main.Status;
import model.Epic;
import model.Subtask;
import model.Task;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;


public class FileBackedTaskManager extends InMemoryTaskManager {
    private final InMemoryTaskManager taskManager;
    public final File taskManagerFile;

    public FileBackedTaskManager(HistoryManager historyManager, String fileName) throws IOException {
        super(historyManager);
        this.taskManagerFile = new File(fileName);
        this.taskManager = new InMemoryTaskManager(historyManager); // Инициализация taskManager
        Path path = Path.of(fileName);
        if (!Files.exists(path)) {
            Files.createFile(path);
        }
        loadFromFile(); // Загружаем данные из файла при создании
    }

    public FileBackedTaskManager(String fileName) throws IOException  {
        this(new InMemoryHistoryManager(), fileName);
    }

    private static final int ID_INDEX = 0; // ID находится на индексе 0
    private static final int TYPE_INDEX = 1; // Тип задачи на индексе 1
    private static final int NAME_INDEX = 2; // Имя на индексе 2
    private static final int STATUS_INDEX = 3; // Статус на индексе 3
    private static final int DESCRIPTION_INDEX = 4; // Описание на индексе 4
    private static final int SUBTASK_ID_INDEX = 5; // Для SUB_TASK и EPIC_TASK

    public static Task taskFromString(String line, InMemoryTaskManager taskManager) {
        System.out.println("Обработка строки задачи: " + line);

        String[] parts = line.split(",");

        int id = Integer.parseInt(parts[ID_INDEX].trim());
        String type = parts[TYPE_INDEX].trim();
        String name = parts[NAME_INDEX].trim();
        String description = parts[DESCRIPTION_INDEX].trim();
        Status status;

        try {
            status = Status.valueOf(parts[STATUS_INDEX].trim());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Некорректный статус: " + parts[STATUS_INDEX]);
        }

        switch (type) {
            case "EPIC_TASK":
                Epic epic = new Epic(id, taskManager, name, description, status);
                if (parts.length > SUBTASK_ID_INDEX) {
                    for (int i = SUBTASK_ID_INDEX; i < parts.length; i++) {
                        int subtaskId = Integer.parseInt(parts[i].trim());
                        Subtask subtask = taskManager.getSubtask(subtaskId);
                        if (subtask != null) {
                            epic.addSubtaskId(subtaskId);
                        } else {
                            System.out.println("Подзадача с ID " + subtaskId + " не найдена для Эпика " + id);
                        }
                    }
                } else {
                    System.out.println("Эпик ID " + id + " создан без подзадач.");
                }
                return epic;

            case "SUB_TASK":
                if (parts.length < 5) {
                    throw new IllegalArgumentException("Неверное количество параметров для Subtask");
                }
                int epicId = Integer.parseInt(parts[SUBTASK_ID_INDEX].trim());
                Epic parentEpic = taskManager.getEpic(epicId);

                if (parentEpic == null) {
                    throw new IllegalArgumentException("Эпик с ID " + epicId + " не найден для подзадачи");
                }

                Subtask subtask = new Subtask(id, name, description, status, epicId); // создаем Subtask с существующим ID
                taskManager.createSubtask(name, description, status, epicId); // сохраняем подзадачу через менеджер
                parentEpic.addSubtaskId(subtask.getId()); // Добавляем ID подзадачи к эпику
                return subtask;

            case "TASK":
                return new Task(taskManager, name, description, status);

            default:
                throw new IllegalArgumentException("Неизвестный тип задачи: " + type);
        }
    }

    @Override
    public void save() throws ManagerSaveException {
        try {
            // Проверяем, существует ли файл. Если нет, создаем его.
            if (!taskManagerFile.exists()) {
                taskManagerFile.createNewFile();
            }

            // Открываем файл для записи
            try (Writer writer = new FileWriter(taskManagerFile, false)) {
                // Сначала сохраняем задачи
                for (Task task : tasks.values()) {
                    writer.write(toString(task) + "\n");
                }

                // Затем сохраняем эпики
                for (Epic epic : epics.values()) {
                    writer.write(toString(epic) + "\n");
                    // Сохраняем также подзадачи, принадлежащие этому эпику
                    for (Integer subtaskId : epic.getSubtaskIds()) {
                        Subtask subtask = subtasks.get(subtaskId);
                        if (subtask != null) {
                            writer.write(toString(subtask) + "\n");
                        }
                    }
                }
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Error saving to file: " + taskManagerFile + ". " + e.getMessage());
        }
    }

    public void loadFromFile() throws ManagerSaveException {
        System.out.println("Загрузка данных из файла: " + taskManagerFile.getAbsolutePath());
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
            System.out.println("Создана задача: " + task); // Отладка

            if (task instanceof Epic) {
                Epic epic = (Epic) task;
                if (epicIds.contains(epic.getId())) {
                    throw new ManagerSaveException("Эпик с ID " + epic.getId() + " уже существует.");
                }
                epicIds.add(epic.getId());
                epics.put(epic.getId(), epic); // Добавляем в список эпиков
                System.out.println("Добавлен эпик: " + epic); // Отладка
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
                    epics.get(subtask.getEpicId()).addSubtaskId(subtask.getId()); // Добавляем ID подзадачи

                    // Отладка подзадачи
                    System.out.println("Добавлена подзадача: " + subtask +
                            " к эпику с ID: " + subtask.getEpicId());
                }
            }
        }
        // Обновляем статусы всех эпиков после загрузки
        for (Epic epic : epics.values()) {
            updateStatusEpic(epic);
            System.out.println("Обновлен статус эпика: " + epic); // Отладка
        }
    }

    private List<String> readLinesFromFile() throws ManagerSaveException {
        List<String> lines = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(taskManagerFile, StandardCharsets.UTF_8))) {
            String line;
            while ((line = br.readLine()) != null) {
                lines.add(line);
            }
        } catch (FileNotFoundException e) {
            throw new ManagerSaveException("File not found: " + taskManagerFile);
        } catch (IOException e) {
            throw new ManagerSaveException("Error reading file: " + e.getMessage());
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
        if (task == null) {
            throw new IllegalArgumentException("Задача не может быть null");
        }

        super.addTask(task); // Вызов родительского метода для добавления задачи

        // Сохранение состояния после добавления задачи
        try {
            save();
        } catch (ManagerSaveException e) {
            System.out.println("Ошибка при сохранении данных: " + e.getMessage());
        }
    }


    @Override
    public void addSubtask(Subtask subtask) {
        if (subtask == null) {
            System.out.println("Ошибка: подзадача не может быть null.");
            return;
        }

        subtask.setId(generateId());
        System.out.println("Добавляем подзадачу с ID: " + subtask.getId());
        subtasks.put(subtask.getId(), subtask);

        Epic epicTask = epics.get(subtask.getEpicId());
        if (epicTask != null) {
            epicTask.addSubtaskId(subtask.getId());
        } else {
            System.out.println("Ошибка: Эпик с ID " + subtask.getEpicId() + " не найден.");
        }

        System.out.println("Подзадача добавлена. Текущий размер коллекции подзадач: " + subtasks.size());

        // Сохранение состояния
        try {
            save();
        } catch (ManagerSaveException e) {
            System.out.println("Ошибка при сохранении данных: " + e.getMessage());
        }
    }


    @Override
    public void addEpic(Epic epic) {
        if (epic != null) {
            // Убедитесь, что ID уже установлен при создании эпика
            System.out.println("Добавляем эпик с ID: " + epic.getId());
            epics.put(epic.getId(), epic);
            System.out.println("Эпик добавлен. Текущий размер коллекции эпиков: " + epics.size());

            try {
                save();
            } catch (ManagerSaveException e) {
                System.out.println("Ошибка при сохранении данных: " + e.getMessage());
            }
        } else {
            System.out.println("Ошибка: эпик не может быть null.");
        }
    }


    @Override
    public int createTask(String name, String description, Status status) throws ManagerSaveException {
        if (name == null || description == null || status == null) {
            throw new IllegalArgumentException("Параметры не могут быть null.");
        }

        Task task = new Task(this, name, description, status); // Создаем новый экземпляр Task
        super.addTask(task); // Добавляем задачу через родительский метод

        // Сохранение состояния после создания новой задачи
        try {
            save();
        } catch (ManagerSaveException e) {
            System.out.println("Ошибка при сохранении данных: " + e.getMessage());
            throw e; // Бросаем исключение для дальнейшей обработки
        }

        return task.getId(); // Возвращаем ID созданной задачи
    }

    @Override
    public int createSubtask(String name, String description, Status status, int epicId) throws ManagerSaveException {
        if (name == null || description == null || status == null) {
            throw new ManagerSaveException("Параметры не могут быть null.");
        }

        Epic epicTask = epics.get(epicId);
        if (epicTask == null) {
            throw new ManagerSaveException("Ошибка: Эпик с ID " + epicId + " не найден.");
        }

        int newId = generateId();
        Subtask subtask = new Subtask(newId, name, description, status, epicId);
        subtasks.put(newId, subtask);

        // Сохранение состояния после создания новой подзадачи
        try {
            save();
        } catch (ManagerSaveException e) {
            System.out.println("Ошибка при сохранении данных: " + e.getMessage());
        }

        return newId;
    }


    @Override
    public int createEpic(String name, String description, Status status) throws ManagerSaveException {
        if (name == null || description == null || status == null) {
            throw new ManagerSaveException("Параметры не могут быть null.");
        }

        int newId = generateId(); // Генерируем ID перед созданием эпика
        Epic epic = new Epic(newId, taskManager, name, description, status);

        // Используем метод addEpic для добавления
        addEpic(epic);

        return newId; // Возвращаем ID созданного эпика
    }

    @Override
    public void deleteAllTask() {
        tasks.clear();
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
    public void deleteTask(int idCounter) {
        if (tasks.remove(idCounter) == null) {
            throw new IllegalArgumentException("Задача с ID " + idCounter + " не найдена");
        }
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
    public void deleteSubtask(int subtaskId) {
        Subtask subtask = subtasks.remove(subtaskId);
        if (subtask == null) {
            throw new IllegalArgumentException("Подзадача с ID " + subtaskId + " не найдена");
        }

        Epic epic = epics.get(subtask.getEpicId());
        if (epic != null) {
            epic.getSubtaskIds().remove(Integer.valueOf(subtaskId));
            updateStatusEpic(epic);
        } else {
            throw new IllegalArgumentException("Эпик с ID " + subtask.getEpicId() + " не найден");
        }

        // Добавляем вызов метода сохранения состояния
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
    public void deleteEpic(int idCounter) {
        Epic epic = epics.remove(idCounter);
        if (epic == null) {
            throw new IllegalArgumentException("Эпик с ID " + idCounter + " не найден");
        }

        // Удаляем все подзадачи, связанные с эпиком
        epic.getSubtaskIds().forEach(subtasks::remove);

        // Добавляем вызов метода сохранения состояния
        save();
    }
}


