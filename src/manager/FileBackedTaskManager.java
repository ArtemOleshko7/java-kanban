package manager;

import exception.ManagerSaveException;
import model.Epic;
import model.Subtask;
import model.Task;
import model.TaskStatus;

import java.io.*;
import java.util.List;
import java.util.Map;

public class FileBackedTaskManager extends InMemoryTaskManager implements TaskManager {
    private File fileWithSavedTasks;
    private static final String COLUMN_DESIGNATIONS = "id,type,name,status,description,epic";

    public FileBackedTaskManager(File fileWithSavedTasks) {
        this.fileWithSavedTasks = fileWithSavedTasks;
    }

    public File getFileWithSavedTasks() {
        return fileWithSavedTasks;
    }

    private void save() throws ManagerSaveException {
        try (Writer fileWriter = new FileWriter(fileWithSavedTasks.getPath())) {
            fileWriter.write(COLUMN_DESIGNATIONS);

            // Обработка всех задач, включая эпики и сабтаски
            for (Task task : getAllTasks()) {
                fileWriter.write("\n" + toString(task));
            }

            for (Task task : getAllEpics()) {
                fileWriter.write("\n" + toString(task));
            }

            for (Task task : getAllSubtasks()) {
                fileWriter.write("\n" + toString(task));
            }

        } catch (IOException e) {
            throw new ManagerSaveException();
        }
    }


    public static FileBackedTaskManager loadFromFile(File file) throws ManagerSaveException {
        FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager(file);
        int id = 0;

        try (BufferedReader fileReader = new BufferedReader(new FileReader(file.getPath()))) {
            String line;
            // Пропустим заголовок
            fileReader.readLine();

            while ((line = fileReader.readLine()) != null) {
                if (fileBackedTaskManager.getNameClass(line).equals("Task")) {
                    Task task = fileBackedTaskManager.fromStringTask(line);
                    fileBackedTaskManager.tasks.put(task.getId(), task);
                    id = Math.max(id, task.getId());
                } else if (fileBackedTaskManager.getNameClass(line).equals("Subtask")) {
                    Subtask subtask = fileBackedTaskManager.fromStringSubtask(line);
                    fileBackedTaskManager.subtasks.put(subtask.getId(), subtask);
                    id = Math.max(id, subtask.getId());
                } else if (fileBackedTaskManager.getNameClass(line).equals("Epic")) {
                    Epic epic = fileBackedTaskManager.fromStringEpic(line);
                    fileBackedTaskManager.epics.put(epic.getId(), epic);
                    id = Math.max(id, epic.getId());
                }
            }
        } catch (IOException e) {
            throw new ManagerSaveException();
        }

        fileBackedTaskManager.setIdCounter(id);
        return fileBackedTaskManager;
    }

    public String toString(Task task) {
        String stringTask;
        if (task.getClass().getSimpleName().equals("Task")) {
            stringTask = String.format("%d,%s,%s,%s,%s", task.getId(), TypeClass.TASK, task.getNameTask(),
                    task.getStatus(), task.getDescriptionTask());
        } else if (task.getClass().getSimpleName().equals("Subtask")) {
            stringTask = String.format("%d,%s,%s,%s,%s,%d", task.getId(), TypeClass.SUBTASK, task.getNameTask(),
                    task.getStatus(), task.getDescriptionTask(), task.getEpic().getId());
        } else {
            stringTask = String.format("%d,%s,%s,%s,%s", task.getId(), TypeClass.EPIC, task.getNameTask(),
                    task.getStatus(), task.getDescriptionTask());
        }
        return stringTask;
    }

    public Task fromStringTask(String value) {
        String[] infoAboutTask = value.split(",");
        return new Task(infoAboutTask[2], infoAboutTask[4], getTaskStatus(infoAboutTask[3]),
                Integer.parseInt(infoAboutTask[0]));
    }

    public Subtask fromStringSubtask(String value) {
        String[] infoAboutTask = value.split(",");
        return new Subtask(infoAboutTask[2], infoAboutTask[4], getTaskStatus(infoAboutTask[3]),
                getEpicWithoutAddInHistory(Integer.parseInt(infoAboutTask[5])), Integer.parseInt(infoAboutTask[0]));
    }

    public Epic fromStringEpic(String value) {
        String[] infoAboutTask = value.split(",");
        return new Epic(infoAboutTask[2], infoAboutTask[4], getTaskStatus(infoAboutTask[3]),
                Integer.parseInt(infoAboutTask[0]));
    }

    public String getNameClass(String str) {
        String[] infoAboutTask = str.split(",");
        try {
            TypeClass taskType = TypeClass.valueOf(infoAboutTask[1].toUpperCase()); // Приведение к верхнему регистру
            return switch (taskType) {
                case TASK -> "Task";
                case SUBTASK -> "Subtask";
                case EPIC -> "Epic";
            };
        } catch (IllegalArgumentException e) {
            return "Unknown"; // Возвращение "Unknown" для неизвестных типов
        }
    }

    public TaskStatus getTaskStatus(String status) {
        try {
            return TaskStatus.valueOf(status);
        } catch (IllegalArgumentException e) {
            return TaskStatus.IN_PROGRESS; // Если статус неизвестен, возвращаем статус по умолчанию
        }
    }

    public Epic getEpicWithoutAddInHistory(int id) {
        List<Task> epicList = getAllEpics();
        // Проверьте, что индекс в пределах допустимого диапазона
        if (id >= 0 && id < epicList.size()) {
            return (Epic) epicList.get(id); // Приведение типа, если это безопасно
        }
        return null; // Или выбросьте исключение, если эпик не найден
    }

    @Override
    public Task createTask(Task task) {
        super.createTask(task);
        save();
        return task;
    }

    @Override
    public Epic createEpic(Epic epic) {
        super.createEpic(epic);
        save();
        return epic;
    }

    @Override
    public Subtask createSubtask(Subtask subtask) {
        super.createSubtask(subtask);
        save();
        return subtask;
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        super.updateSubtask(subtask);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void deleteAllEpic() {
        super.deleteAllEpic();
        save();
    }

    @Override
    public void deleteAllSubtask() {
        super.deleteAllSubtask();
        save();
    }

    @Override
    public void deleteAllTasks() {
        super.deleteAllTasks();
        save();
    }


}
