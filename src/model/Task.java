package model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

public class Task {
    private String nameTask;
    private String descriptionTask;
    protected TaskStatus status;
    private int id;
    protected LocalDateTime startTime;
    protected Duration duration;

    public Task(String nameTask, String descriptionTask, TaskStatus status) {
        this.nameTask = nameTask;
        this.descriptionTask = descriptionTask;
        this.status = status;
    }

    public Task(String nameTask, String descriptionTask, TaskStatus status, int id) {
        this.nameTask = nameTask;
        this.descriptionTask = descriptionTask;
        this.status = status;
        this.id = id;
        this.startTime = null;
        this.duration = Duration.ZERO; // Значение по умолчанию
    }

    // Конструктор с полным набором полей
    public Task(String nameTask, String descriptionTask, TaskStatus status, int id, LocalDateTime startTime, Duration duration) {
        this.nameTask = nameTask;
        this.descriptionTask = descriptionTask;
        this.status = status;
        this.id = id;
        this.startTime = startTime;
        this.duration = duration;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public String getNameTask() {
        return nameTask;
    }

    public String getDescriptionTask() {
        return descriptionTask;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setNameTask(String nameTask) {
        this.nameTask = nameTask;
    }

    public void setDescriptionTask(String descriptionTask) {
        this.descriptionTask = descriptionTask;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public Epic getEpic() {
        return null;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public void setStartTime(int y, int m, int d, int hh, int mm) {
        this.startTime = LocalDateTime.of(y, m, d, hh, mm);
    }

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public void setDuration(long minutes) {
        this.duration = Duration.ofMinutes(minutes);
    }


    public LocalDateTime getEndTime() {
        return (startTime != null) ? startTime.plus(duration) : null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Task)) return false;
        Task task = (Task) o;
        return id == task.id &&
                Objects.equals(nameTask, task.nameTask) &&
                Objects.equals(descriptionTask, task.descriptionTask) &&
                status == task.status &&
                Objects.equals(startTime, task.startTime) &&
                Objects.equals(duration, task.duration);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nameTask, descriptionTask, status, id, startTime, duration);
    }

    @Override
    public String toString() {
        return "Task{" +
                "nameTask='" + nameTask + '\'' +
                ", descriptionTask='" + descriptionTask + '\'' +
                ", status=" + status +
                ", id=" + id +
                ", startTime=" + startTime +
                ", duration=" + duration +
                '}';
    }

}
