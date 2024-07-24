package model;

import main.Status;
import main.TaskType;

import java.util.ArrayList;
import java.util.Objects;

public class Epic extends Task {
    private final ArrayList<Integer> subtaskIds = new ArrayList<>();
    private final TaskType type = TaskType.EPIC_TASK;

    public Epic(Integer id, String name, String description, Status status) {
        super(id, name, description, status);
    }

    @Override
    public TaskType getType() {
        return this.type;
    }

    public ArrayList<Integer> getSubtaskIds() {
        return subtaskIds;
    }

    public void addSubtaskIds(int id, Subtask subtask) {
        if (subtask == null) {
            throw new IllegalArgumentException("Подзадача не может быть null.");
        }
        if (id <= 0) {
            throw new IllegalArgumentException("ID подзадачи должен быть больше нуля.");
        }
        if (!subtaskIds.contains(id)) {
            subtaskIds.add(id);
        } else {
            throw new IllegalArgumentException("Подзадача с таким ID уже добавлена.");
        }

    }


    @Override
    public String toString() {
        return String.format("Epic{id=%d, name='%s', description='%s', status=%s, subtaskIds=%s}",
                getId(), getName(), getDescription(), getStatus(), subtaskIds);
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