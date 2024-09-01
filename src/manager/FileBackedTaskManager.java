package manager;

import exception.ManagerSaveException;
import model.Epic;
import model.Subtask;
import model.Task;
import model.TaskStatus;

import java.io.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

public class FileBackedTaskManager extends InMemoryTaskManager implements TaskManager {
    private File fileWithSavedTasks;
    private static final String COLUMN_DESIGNATIONS = "id,type,name,status,description,epic,startTime,duration";

    public FileBackedTaskManager(File fileWithSavedTasks) {
        this.fileWithSavedTasks = fileWithSavedTasks;
    }

    public File getFileWithSavedTasks() {
        return fileWithSavedTasks;
    }

    private void save() throws ManagerSaveException {
        try (Writer fileWriter = new FileWriter(fileWithSavedTasks.getPath())) {
            fileWriter.write(COLUMN_DESIGNATIONS);

            // Сначала записываем эпики, затем сабтаски и в конце обычные задачи
            for (Task task : getAllEpics()) {
                fileWriter.write("\n" + toString(task));
            }

            for (Task task : getAllSubtasks()) {
                fileWriter.write("\n" + toString(task));
            }

            for (Task task : getAllTasks()) {
                fileWriter.write("\n" + toString(task));
            }

        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка сохранения данных: " + e.getMessage(), e);
        }
    }

    public String toString(Task task) {
        if (task instanceof Subtask) {
            Subtask subtask = (Subtask) task;
            return String.format("%d,%s,%s,%s,%s,%d,%s,%s", subtask.getId(), TypeClass.SUBTASK,
                    subtask.getNameTask(), subtask.getStatus(), subtask.getDescriptionTask(),
                    subtask.getEpic().getId(), subtask.getStartTime(), subtask.getDuration());
        } else if (task instanceof Epic) {
            return String.format("%d,%s,%s,%s,%s,%s,%s", task.getId(), TypeClass.EPIC,
                    task.getNameTask(), task.getStatus(), task.getDescriptionTask(),
                    task.getStartTime(), task.getDuration());
        } else { // Task
            return String.format("%d,%s,%s,%s,%s,%s,%s", task.getId(), TypeClass.TASK,
                    task.getNameTask(), task.getStatus(), task.getDescriptionTask(),
                    task.getStartTime(), task.getDuration());
        }
    }

    public static FileBackedTaskManager loadFromFile(File file) throws ManagerSaveException {
        FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager(file);
        int id = 0;

        try (BufferedReader fileReader = new BufferedReader(new FileReader(file))) {
            String line;
            fileReader.readLine(); // Пропустим заголовок

            while ((line = fileReader.readLine()) != null && !line.trim().isEmpty()) {
                String taskType = fileBackedTaskManager.getNameClass(line);
                switch (taskType) {
                    case "Task":
                        Task task = fileBackedTaskManager.fromStringTask(line);
                        fileBackedTaskManager.tasks.put(task.getId(), task);
                        id = Math.max(id, task.getId());
                        break;
                    case "Subtask":
                        Subtask subtask = fileBackedTaskManager.fromStringSubtask(line);
                        fileBackedTaskManager.subtasks.put(subtask.getId(), subtask);
                        id = Math.max(id, subtask.getId());
                        break;
                    case "Epic":
                        Epic epic = fileBackedTaskManager.fromStringEpic(line);
                        fileBackedTaskManager.epics.put(epic.getId(), epic);
                        id = Math.max(id, epic.getId());
                        break;
                    default:
                        throw new ManagerSaveException("Некорректный формат данных в файле.");
                }
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка чтения файла: " + e.getMessage(), e);
        }

        fileBackedTaskManager.setIdCounter(id);
        return fileBackedTaskManager;
    }

    public Task fromStringTask(String value) {
        String[] infoAboutTask = value.split(",");
        return new Task(infoAboutTask[2], infoAboutTask[4], getTaskStatus(infoAboutTask[3]),
                Integer.parseInt(infoAboutTask[0]),                      // Парсинг id
                LocalDateTime.parse(infoAboutTask[5]),                   // Парсинг startTime
                Duration.parse(infoAboutTask[6]));                       // Парсинг duration
    }

    public Subtask fromStringSubtask(String value) {
        String[] infoAboutTask = value.split(",");
        return new Subtask(infoAboutTask[2], infoAboutTask[4], getTaskStatus(infoAboutTask[3]),
                getEpicWithoutAddInHistory(Integer.parseInt(infoAboutTask[5])),
                Integer.parseInt(infoAboutTask[0]),                     // Парсинг id
                LocalDateTime.parse(infoAboutTask[6]),                  // Парсинг startTime
                Duration.parse(infoAboutTask[7]));                      // Парсинг duration
    }

    public Epic fromStringEpic(String value) {
        String[] infoAboutTask = value.split(",");
        int id = Integer.parseInt(infoAboutTask[0]); // id
        TaskStatus status = getTaskStatus(infoAboutTask[3]);
        String nameTask = infoAboutTask[2];
        String descriptionTask = infoAboutTask[4];
        LocalDateTime startTime = LocalDateTime.parse(infoAboutTask[5]);
        Duration duration = Duration.parse(infoAboutTask[6]);

        return new Epic(nameTask, descriptionTask, status, id, duration, startTime);
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
        // Проверяем, что индекс в пределах допустимого диапазона
        if (id >= 0 && id < epicList.size()) {
            return (Epic) epicList.get(id); // Приведение типа, если это безопасно
        }
        return null; // Или выбросьте исключение, если эпик не найден
    }

    @Override
    public void createTask(Task task) {
        super.createTask(task);
        save();
    }

    @Override
    public void createEpic(Epic epic) {
        super.createEpic(epic);
        save();
    }

    @Override
    public void createSubtask(Subtask subtask) {
        super.createSubtask(subtask);
        save();
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
