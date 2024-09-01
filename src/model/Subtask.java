package model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

public class Subtask extends Task {
    private Epic epic;


    public Subtask(String nameTask, String descriptionTask, TaskStatus status, Epic epic, Duration duration) {
        super(nameTask, descriptionTask, status);
        this.epic = epic;
        if (duration == null) {
            throw new IllegalArgumentException("Subtask duration cannot be null");
        }
        this.duration = duration;
    }

    public Subtask(String nameTask, String descriptionTask, TaskStatus status, Epic epic, int id) {
        super(nameTask, descriptionTask, status, id);
        this.epic = epic;
    }

    public Subtask(String nameTask, String descriptionTask, TaskStatus status, Epic epic, int id, LocalDateTime startTime, Duration duration) {
        super(nameTask, descriptionTask, status, id, startTime, duration);
        this.epic = epic;
        if (duration == null) {
            throw new IllegalArgumentException("Subtask duration cannot be null");
        }
    }

    public void setEpic(Epic epic) {
        this.epic = epic;
    }

    public Epic getEpic() {
        return epic;
    }

    @Override
    public String toString() {
        return "Subtask{" +
                "name='" + getNameTask() + '\'' +
                ", description='" + getDescriptionTask() + '\'' +
                ", status=" + getStatus() +
                ", epicId=" + (epic != null ? epic.getId() : null) +
                '}';
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), epic);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Subtask)) return false;
        if (!super.equals(obj)) return false;

        Subtask subtask = (Subtask) obj;
        return Objects.equals(epic, subtask.epic);
    }
}
