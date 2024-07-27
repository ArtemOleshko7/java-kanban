package model;

import main.Status;
import main.TaskType;

import java.util.Objects;

public class Task {
    protected Integer id;
    protected String name;
    protected String description;
    protected Status status;

    public Task(int id, String name, String description, Status status) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Имя не может быть пустым.");
        }
        if (description == null) {
            throw new IllegalArgumentException("Описание не может быть null.");
        }
        if (status == null) {
            throw new IllegalArgumentException("Статус не может быть null.");
        }

        this.id = id; // Устанавливаем переданный ID
        this.name = name;
        this.description = description;
        this.status = status;
    }

    public Task(String name, String description, Status status) {
        this(-1, name, description, status);
    }

    public TaskType getType() {
        return TaskType.TASK;
    }

    public int getId() {
        return id;
    }

    public void setId(Integer id) {
        if (this.id != null) { // Используем null для проверки
            throw new IllegalStateException("ID уже установлен и не может быть изменен.");
        }
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

    @Override
    public String toString() {
        return String.format("Task{id=%s, name='%s', description='%s', status=%s}",
                id, name, description, status); // Изменено %d на %s, так как id может быть null
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