package model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

public class Subtask extends Task {
    private Epic epic;


    public Subtask(String nameTask, String descriptionTask, TaskStatus status, Epic epic) {
        super(nameTask, descriptionTask, status);
        this.epic = epic;
    }

    public Subtask(String nameTask, String descriptionTask, TaskStatus status, Epic epic, int id) {
        super(nameTask, descriptionTask, status, id);
        this.epic = epic;
    }

    // Конструктор с полным набором параметров
    public Subtask(String nameTask, String descriptionTask, TaskStatus status, Epic epic, int id, LocalDateTime startTime, Duration duration) {
        super(nameTask, descriptionTask, status, id, startTime, duration);
        this.epic = epic;
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
                "epic=" + epic +
                ", " + super.toString() +
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
