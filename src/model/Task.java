package model;

import main.Status;
import main.TaskType;

import java.util.*;

public class Task {
    protected Integer id;
    protected String name;
    protected String description;
    protected Status status;

    public Task(Integer id, String name, String description, Status status) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Имя не может быть пустым.");
        }
        if (description == null) {
            throw new IllegalArgumentException("Описание не может быть null.");
        }
        if (status == null) {
            throw new IllegalArgumentException("Статус не может быть null.");
        }

        this.id = id != null ? id : -1; // Присваиваем -1, если id не был передан
        this.name = name;
        this.description = description;
        this.status = status;
    }

    public Task() {
        this.id = -1;
        this.name = "";
        this.description = "";
        this.status = Status.NEW; // Предположим, статус по умолчанию - NEW
    }

    public TaskType getType() { return TaskType.TASK; }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name != null ? name : "";
    }

    public String getDescription() {
        return description;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public void updateStatus(Status newStatus) {
        if (newStatus == null) {
            throw new IllegalArgumentException("Статус не может быть null.");
        }

        if (this.status == Status.NEW && newStatus == Status.IN_PROGRESS) {
            this.status = newStatus;
        } else if (this.status == Status.IN_PROGRESS && newStatus == Status.DONE) {
            this.status = newStatus;
        } else {
            throw new IllegalStateException("Неверный переход статуса.");
        }
    }

    @Override
    public String toString() {
        return String.format("Task{id=%d, name='%s', description='%s', status=%s}",
                id, name, description, status);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return id == task.id && Objects.equals(description, task.description) && Objects.equals(name, task.name)
                && status == task.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(description, id, name, status);
    }
}