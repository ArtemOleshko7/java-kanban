package main;

import java.util.*;

public class Epic extends Task {
    private ArrayList<Integer> subtaskIds = new ArrayList<>();
    private Map<Integer, Subtask> subtasks = new HashMap<>();

    public Epic(String name, String description, Status status) {
        super(null, name, description, status, TaskType.EPIC_TASK); // ID будет присвоен позже
        this.subtaskIds = new ArrayList<>(); // Инициализация списка подзадач
    }

    public ArrayList<Integer> getSubtaskIds() {
        return subtaskIds;
    }

    public void addSubtaskIds(int id, Subtask subtask) {
        if (subtask == null) {
            throw new IllegalArgumentException("Подзадача не может быть null.");
        }
        if (!subtaskIds.contains(id)) {
            subtaskIds.add(id);
            subtasks.put(id, subtask);
        } else {
            throw new IllegalArgumentException("Подзадача с таким ID уже добавлена.");
        }
    }

    @Override
    public String toString() {
        return "main.Epic{" +
                "subtaskIDs=" + subtaskIds +
                ", description='" + getDescription() + '\'' +
                ", ID=" + getId() +
                ", name='" + getName() + '\'' +
                ", status=" + getStatus() +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Epic epic = (Epic) o;
        return Objects.equals(subtaskIds, epic.subtaskIds);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), subtaskIds);
    }

}