package model;

import main.Status;
import main.TaskType;
import service.InMemoryTaskManager;

public class Subtask extends Task {

    private Integer epicId;

    public Subtask(InMemoryTaskManager taskManager, String name, String description, Status status, Integer epicId) {
        super(taskManager, name, description, status); // Это вызовет конструктор с InMemoryTaskManager
        this.epicId = epicId;
    }

    public Subtask(Integer id, String name, String description, Status status, Integer epicId) {
        super(name, description, status); // Это вызовет конструктор без InMemoryTaskManager
        this.epicId = epicId;
        setId(id); // Устанавливаем ID
    }

    @Override
    public TaskType getType() {
        return TaskType.SUB_TASK;
    }

    public Integer getEpicId() {
        return epicId;
    }

    @Override
    public String toString() {
        return String.format("SubTask{\nEpic Id=%d, Status=%s, Id=%d, Name='%s', Description='%s'}\n",
                epicId, super.getStatus(), super.getId(), super.getName(), super.getDescription());
    }
}