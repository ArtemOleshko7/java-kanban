import java.util.*;

public class TaskManager {
    private HashMap<Integer, Task> tasks = new HashMap<>();
    private HashMap<Integer, Subtask> subtasks = new HashMap<>();
    private HashMap<Integer, Epic> epics = new HashMap<>();
    private int idCounter = 0;

    public int generateId() {
        return idCounter++;
    }

    public Task addTask(Task task) {
        task.setId(generateId());
        tasks.put(task.getId(), task);
        return task;
    }

    public void updateTask(Task task) {
        if (tasks.containsKey(task.getId())) {
            tasks.put(task.getId(), task);
        } else {
            System.out.println("Задача не найдена");
        }
    }

    public Task getTask(int idCounter) {
        return tasks.get(idCounter);
    }

    public List<Task> getAllTasks() {
        if (tasks.isEmpty()) {
            System.out.println("Задач нет");
            return Collections.emptyList();
        }
        return new ArrayList<>(tasks.values());
    }

    public void deleteTask(int idCounter) {
        if (tasks.containsKey(idCounter)) {
            tasks.remove(idCounter);
        } else {
            System.out.println("Задача не найдена");
        }
    }

    public void deleteAllTasks() {
        tasks.clear();
    }

    public void addSubtask(Subtask subtask) {
        int subtaskId = generateId();
        subtask.setId(subtaskId);
        Epic epic = epics.get(subtask.getEpicId());
        if (epic != null) {
            subtasks.put(subtaskId, subtask);
            epic.setSubtaskIds(subtaskId);
            updateStatusEpic(epic);
        } else {
            System.out.println("Такого эпика нет");
        }
    }

    public void updateSubtask(Subtask subtask) {
        if (subtasks.containsKey(subtask.getId())) {
            subtasks.put(subtask.getId(), subtask);
            Epic epic = epics.get(subtask.getEpicId());
            updateStatusEpic(epic);
        } else {
            System.out.println("Такой подзадачи нет");
        }
    }

    public Subtask getSubtask(int idCounter) {
        return subtasks.get(idCounter);
    }

    public List<Subtask> getAllSubtasks() {
        if (subtasks.isEmpty()) {
            System.out.println("Подзадач нет");
            return Collections.emptyList();
        }
        return new ArrayList<>(subtasks.values());
    }

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

    public void deleteAllSubtasks() {
        subtasks.clear();
        for (Epic epic : epics.values()) {
            epic.getSubtaskIds().clear();
            updateStatusEpic(epic);
        }
    }

    public void addEpic(Epic epic) {
        int epicId = generateId();
        epic.setId(epicId);
        epics.put(epicId, epic);
    }

    public void updateEpic(Epic epic) {
        if (epics.containsKey(epic.getId())) {
            epics.put(epic.getId(), epic);
            updateStatusEpic(epic);
        } else {
            System.out.println("Такого эпика нет");
        }
    }

    public Epic getEpic(int idCounter) {
        return epics.get(idCounter);
    }

    public List<Epic> getAllEpics() {
        if (epics.isEmpty()) {
            System.out.println("Эпиков нет");
            return Collections.emptyList();
        }
        return new ArrayList<>(epics.values());
    }

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

    public void deleteAllEpics() {
        subtasks.clear();
        epics.clear();
    }

    public void updateStatusEpic(Epic epic) {
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
}
