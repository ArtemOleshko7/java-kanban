package model;

import main.Status;
import main.TaskType;
import service.InMemoryTaskManager;

public class Subtask extends Task {

    private Integer epicId;

    public Subtask(String name, String description, Status status, Integer epicId) {
        this(InMemoryTaskManager.generateId(), name, description, status, epicId); // Вызов с использованием сгенерированного ID
        this.epicId = epicId;
    }

    public Subtask(Integer id, String name, String description, Status status, Integer epicId) {
        super(id, name, description, status);
        this.epicId = epicId;
    }

    @Override
    public TaskType getType() {
        return TaskType.SUB_TASK;
    }

    public Integer getEpicId() {
        return epicId;
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
            throw new IllegalStateException("Неверный переход статуса для подзадачи.");
        }
    }

    @Override
    public String toString() {
        return String.format("SubTask{\nEpic Id=%d, Status=%s, Id=%d, Name='%s', Description='%s'}\n",
                epicId, super.getStatus(), super.getId(), super.getName(), super.getDescription());
    }
}