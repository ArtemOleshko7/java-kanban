package main;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class FileBackedTaskManager extends InMemoryTaskManager {

    private static final FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager(Managers.getDefaultHistory());
    private final File taskManagerFile;

    public FileBackedTaskManager(HistoryManager historyManager) {
        super(historyManager);
        this.taskManagerFile = new File("TaskManagerFile.txt");
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
            writer.append("id,type,name,status,description,epic" + "\n");

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

    public void load() throws ManagerSaveException {
        fileBackedTaskManager.tasks.clear();
        fileBackedTaskManager.subtasks.clear();
        fileBackedTaskManager.epics.clear();
        fileBackedTaskManager.historyManager.removeAll();

        try (BufferedReader br = new BufferedReader(new FileReader(fileBackedTaskManager.taskManagerFile, StandardCharsets.UTF_8));) {

            List<String> list = new ArrayList<>();
            while (br.ready()) {
                list.add(br.readLine());
            }

            for (int i = 1; i < list.size(); i++) {
                if (list.get(i).isBlank()) {
                    break;
                }
                taskFromString(list.get(i));
            }


        } catch (IOException e) {
            throw new ManagerSaveException(e.getMessage());
        }
    }

    private static void taskFromString(String value) {
        String[] params = value.split(",");
        int parsedId;
        try {
            parsedId = Integer.parseInt(params[0]);


            switch (params[1]) {
                case "TASK":
                    fileBackedTaskManager.tasks.put(parsedId,
                            new Task(parsedId, params[2], params[4], Status.valueOf(params[3]), TaskType.valueOf(params[1])));
                    break;
                case "EPIC":
                    fileBackedTaskManager.epics.put(parsedId, new Epic(params[2], params[4], Status.valueOf(params[3])));
                    break;
                case "SUBTASK":
                    Subtask sbFromFile = new Subtask(parsedId, params[2], params[4], Status.valueOf(params[3]), Integer.parseInt(params[5]));
                    fileBackedTaskManager.epics.get(Integer.parseInt(params[5])).addSubtaskIds(parsedId, sbFromFile);
                    fileBackedTaskManager.subtasks.put(parsedId, sbFromFile);

                    break;

            }
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("Недостаточно введенных параметров о задаче. Проверьте содержимое файла.");
            throw new ArrayIndexOutOfBoundsException();
        } catch (NumberFormatException e) {
            System.out.println("Неверный формат введенных данных.");
            e.printStackTrace();
        }
    }


    private String toString(Task task) {
        return String.format("%d,%S,%s,%S,%s", task.getId(), task.getType(), task.getName(), task.getStatus(), task.getDescription());
    }

    private String toString(Subtask task) {
        return String.format("%d,%S,%s,%s,%s,%s", task.getId(), task.getType(), task.getName(), task.getStatus(), task.getDescription(), task.getEpicId());
    }

    private String toString(Epic task) {
        return String.format("%d,%S,%s,%S,%s", task.getId(), task.getType(), task.getName(), task.getStatus(), task.getDescription());
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
}