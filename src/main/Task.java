package main;

import java.util.*;

public class Task {
    protected int id;
    protected String name;
    protected String description;
    protected Status status;
    protected final TaskType type;

    public Task(Integer id, String name, String description, Status status, TaskType type) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Имя не может быть пустым.");
        }
        if (description == null) {
            throw new IllegalArgumentException("Описание не может быть null.");
        }
        if (status == null) {
            throw new IllegalArgumentException("Статус не может быть null.");
        }

        this.id = id != null ? id : 0; // Присваиваем 0, если id не был передан
        this.name = name;
        this.description = description;
        this.status = status;
        this.type = TaskType.TASK;
    }

    public Task() {
        this.id = 0;
        this.name = "";
        this.description = "";
        this.status = Status.NEW; // Предположим, статус по умолчанию - NEW
        this.type = TaskType.TASK;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
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

    public TaskType getType() {
        return type;
    }

    @Override
    public String toString() {
        return "Task{" +
                "type=" + type +
                ", id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                '}';
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