package service;

import exception.ManagerSaveException;
import model.Epic;
import model.Subtask;
import model.Task;
import main.Status;
import main.TaskType;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class FileBackedTaskManager extends InMemoryTaskManager {

    private final File taskManagerFile;
    private static final String CSV_HEADER = "id,type,name,status,description,epic";

    private String taskData;

    public FileBackedTaskManager(HistoryManager historyManager, String fileName) {
        super(historyManager);
        this.taskManagerFile = new File(fileName);
        createFile();
    }

    private void createFile() {
        try {
            if (taskManagerFile.createNewFile()) {
                System.out.println("Файл создан: " + taskManagerFile.getName());
            } else {
                System.out.println("Файл уже существует: " + taskManagerFile.getName());
            }
        } catch (IOException e) {
            System.err.println("Ошибка при создании файла: " + e.getMessage());
        }
    }


    public void save() throws ManagerSaveException {
        try (Writer writer = new FileWriter(taskManagerFile, false);) {
            writer.append(CSV_HEADER).append("\n");

            for (Task task : tasks.values()) {
                writer.write(toString(task) + "\n");
            }
            for (Epic task : epics.values()) {
                writer.write(toString(task) + "\n");
            }
            for (Subtask task : subtasks.values()) {
                writer.write(toString(task) + "\n");
            }

            writer.append(" " + "\n");


        } catch (IOException e) {
            throw new ManagerSaveException(e.getMessage());
        }
    }

    public void loadFromFile() throws ManagerSaveException {
        load();
    }

    private void updateIdAfterLoad(Map<Integer, Task> tasks, Map<Integer, Subtask> subtasks, Map<Integer, Epic> epics) {
        int maxId = 0;
        maxId = Math.max(maxId, tasks.keySet().stream().max(Integer::compareTo).orElse(0));
        maxId = Math.max(maxId, subtasks.keySet().stream().max(Integer::compareTo).orElse(0));
        maxId = Math.max(maxId, epics.keySet().stream().max(Integer::compareTo).orElse(0));
        idCounter = maxId + 1;
    }

    public void load() throws ManagerSaveException {
        tasks.clear();
        subtasks.clear();
        epics.clear();
        historyManager.removeAll();

        List<String> lines = readLinesFromFile();

        for (String line : lines) {
            taskFromString(line, this);
        }

        updateIdAfterLoad(tasks, subtasks, epics);
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

    private void updateIdCounter() {
        int maxId = Collections.max(Stream.concat(tasks.keySet().stream(),
                        Stream.concat(subtasks.keySet().stream(), epics.keySet().stream()))
                .collect(Collectors.toList()));
        idCounter = Math.max(maxId + 1, idCounter);
    }


    private static void taskFromString(String value, InMemoryTaskManager taskManager) {
        String[] params = value.split(",");

        if (params.length < 6) {
            System.out.println("Недостаточно введенных параметров о задаче. Проверьте содержимое файла.");
            return;
        }

        try {
            final int ID_INDEX = 0;
            final int TASK_TYPE_INDEX = 1;
            final int NAME_INDEX = 2;
            final int STATUS_INDEX = 3;
            final int DESCRIPTION_INDEX = 4;
            final int EPIC_ID_INDEX = 5;

            int parsedId = Integer.parseInt(params[ID_INDEX]);
            TaskType taskType = TaskType.valueOf(params[TASK_TYPE_INDEX].trim().toUpperCase());
            String name = params[NAME_INDEX].trim();
            String description = params[DESCRIPTION_INDEX].trim();
            Status status = Status.valueOf(params[STATUS_INDEX].trim().toUpperCase());

            switch (taskType) {
                case TASK:
                    taskManager.tasks.put(parsedId, new Task(parsedId, name, description, status));
                    break;
                case EPIC_TASK:
                    taskManager.epics.put(parsedId, new Epic(name, description, status));
                    break;
                case SUB_TASK:
                    int epicId = Integer.parseInt(params[EPIC_ID_INDEX]);
                    Subtask subtask = new Subtask(parsedId, name, description, status, epicId);
                    taskManager.epics.get(epicId).addSubtaskIds(parsedId, subtask);
                    taskManager.subtasks.put(parsedId, subtask);
                    break;
                default:
                    System.out.println("Неизвестный тип задачи: " + taskType);
                    break;
            }
        } catch (NumberFormatException e) {
            System.out.println("Неверный формат введенных данных: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.out.println("Неверный статус задачи: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Ошибка обработки задачи: " + e.getMessage());
        }
    }


    private String toString(Task task) {
        return String.format("%d,%s,%s,%s,%s", task.getId(), task.getType(), task.getName(), task.getStatus(), task.getDescription());
    }

    private String toString(Subtask task) {
        return String.format("%d,%s,%s,%s,%s,%s", task.getId(), task.getType(), task.getName(), task.getStatus(), task.getDescription(), task.getEpicId());
    }

    private String toString(Epic task) {
        return String.format("%d,%s,%s,%s,%s", task.getId(), task.getType(), task.getName(), task.getStatus(), task.getDescription());
    }

    @Override
    public int createTask(Task task) throws ManagerSaveException {
        int id = super.createTask(task);
        save();
        return id;
    }

    @Override
    public int createSubtask(Subtask subTask) throws ManagerSaveException {
        int id = super.createSubtask(subTask);
        save();
        return id;
    }

    @Override
    public int createEpic(Epic epic) throws ManagerSaveException {
        int id = super.createEpic(epic);
        save();
        return id;
    }

    @Override
    public void deleteAllTask() throws ManagerSaveException {
        tasks.clear();
    }

    @Override
    public Task getTask(int id) throws ManagerSaveException {
        Task result = super.getTask(id);
        save();
        return result;
    }

    @Override
    public void updateTask(int id, Task task) throws ManagerSaveException {
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
    public Subtask getSubtask(int id) throws ManagerSaveException {
        Subtask result = super.getSubtask(id);
        save();
        return result;
    }

    @Override
    public void updateSubtask(int id, Subtask subTask) throws ManagerSaveException {
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
    public Epic getEpic(int id) throws ManagerSaveException {
        Epic result = super.getEpic(id);
        save();
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


